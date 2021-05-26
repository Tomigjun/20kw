package com.chargerlink.iot.common.connector.config;

import com.alibaba.fastjson.JSON;
import com.chargerlink.iot.common.connector.entity.ProtocolInfo;
import com.chargerlink.iot.common.connector.entity.convert.ProtocolConverter;
import com.chargerlink.iot.common.connector.netty.handler.ConnectorLoggingHandler;
import com.chargerlink.iot.common.connector.netty.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PreDestroy;

/**
 * TCP连接配置
 *
 * @author GISirFive
 * @date Create on 2019/11/28 15:38
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(TCPConnectorProperties.class)
@ConditionalOnProperty(name = TCPConnectorProperties.FLAG_PREFIX + ".enable", havingValue = "true")
public class TCPConnectorAutoConfiguration implements InitializingBean {

    @Autowired
    private ApplicationContext context;
    @Autowired
    private NioEventLoopGroup bossGroup;
    @Autowired
    private NioEventLoopGroup workerGroup;
    @Autowired
    private ServerHandler serverHandler;
    @Autowired
    private ConnectorLoggingHandler connectorLoggingHandler;

    private TCPConnectorProperties properties;

    private ChannelFuture channelFuture;
    /**
     * 协议配置信息
     */
    private ProtocolInfo protocolInfo;

    public TCPConnectorAutoConfiguration(TCPConnectorProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            // 协议ID
            if (properties.getProtocolConfig() == null || StringUtils.isBlank(properties.getProtocolConfig().getProtocolId())) {
                throw new NullPointerException("协议ID[protocol-id]不能为空");
            }
            //协议配置文件路径
            if (properties.getProtocolConfig() == null || StringUtils.isBlank(properties.getProtocolConfig().getLocation())) {
                throw new NullPointerException("协议配置文件路径[location]不能为空");
            }
            // 服务端端口号
            if (properties.getNettyConfig() == null || properties.getNettyConfig().getServerPort() == null) {
                throw new NullPointerException("服务端端口号[server-port]不能为空");
            }
            //NOTE 加载协议配置信息
            log.info("加载协议配置信息");
            Resource resource = context.getResource(properties.getProtocolConfig().getLocation());
            if (!resource.exists()) {
                throw new NullPointerException("根据协议配置文件路径，未找到文件");
            }
            protocolInfo = ProtocolConverter.convertToProtocolInfo(resource, properties.getProtocolConfig());
            //尝试验证协议配置信息是否可以正常加载
            ProtocolConverter.convertToMessageDecoder(protocolInfo);
            log.info("协议配置信息加载完成[protocolInfo={}]", JSON.toJSONString(protocolInfo));
        } catch (Exception e) {
            log.error("TCP Connector初始化失败", e);
            SpringApplication.exit(context);
        }

        start();
    }

    /**
     * 启动
     */
    private void start() {
        //NOTE 初始化ServerBootstrap
        log.info("初始化ServerBootstrap");
        ServerBootstrap bootstrap = new ServerBootstrap()
                //配置线程池
                .group(bossGroup, workerGroup)
                //指定server使用的channel类型
                .channel(NioServerSocketChannel.class)
                //指定一个池化的Allocator
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //指定处理客户端的编码/解码类
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        log.info("初始化channel");
                        ChannelPipeline pipeline = channel.pipeline();
                        //设置日志拦截器
                        pipeline.addLast("connectorLoggingHandler", connectorLoggingHandler);
                        //初始化可配置的拦截器
                        pipeline.addLast("messageDecoder", ProtocolConverter.convertToMessageDecoder(protocolInfo));
                        //设置结果拦截器
                        pipeline.addLast("handler", serverHandler);
                        //设置结果编码器
                        pipeline.addLast("encoder", new ByteArrayEncoder());
                    }
                });

        //NOTE 初始化TCP Server
        try {
            log.info("初始化TCP Server");
            channelFuture = bootstrap.bind(properties.getNettyConfig().getServerPort()).sync();
        } catch (InterruptedException e) {
            log.error("TCP Server初始化失败", e);
            SpringApplication.exit(context);
        }
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        if (channelFuture == null) {
            return;
        }
        log.info("开始销毁TCP Server");
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("TCP Server销毁失败", e);
        } finally {
            if (!workerGroup.isShutdown() && !workerGroup.isShuttingDown()) {
                workerGroup.shutdownGracefully();
            }
            if (!bossGroup.isShutdown() && !bossGroup.isShuttingDown()) {
                bossGroup.shutdownGracefully();
            }
        }
    }

    /**
     * 主线程池<br>
     * 负责接收来自客户端的连接请求，并向下分发
     *
     * @return
     */
    @ConditionalOnMissingBean
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup();
    }

    /**
     * 从线程池<br>
     * 负责处理来自客户端的所有的event和IO操作
     *
     * @return
     */
    @ConditionalOnMissingBean
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

}
