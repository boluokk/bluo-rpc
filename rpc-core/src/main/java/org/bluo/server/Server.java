package org.bluo.server;

import cn.hutool.core.util.ObjectUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.bluo.cache.ServerCache;
import org.bluo.common.MessageDecoder;
import org.bluo.common.MessageEncoder;
import org.bluo.common.ServiceWrapper;
import org.bluo.config.ConfigLoader;
import org.bluo.config.ServerConfig;
import org.bluo.register.Register;
import org.bluo.serialize.Serialize;
import org.bluo.spi.ExtraLoader;

import java.util.LinkedHashMap;

import static org.bluo.cache.CachePool.extraLoader;
import static org.bluo.cache.ServerCache.register;
import static org.bluo.cache.ServerCache.serverConfig;


/**
 * 服务端
 *
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class Server {
    private ServerBootstrap serverBootstrap = new ServerBootstrap();

    public ChannelFuture runServer() {
        log.info("启动服务器中..");
        initConfiguration();
        serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup());
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageDecoder());
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new ServerChannelInboundHandler());
            }
        });
        return serverBootstrap.bind(6636).addListener(channelFuture -> {
            if (channelFuture.isSuccess()) {
                ServiceWrapper serviceWrapper = new ServiceWrapper();
                serviceWrapper.setPort(serverConfig.getServerPort());
                serviceWrapper.setDomain("127.0.0.1");
                register.register(serverConfig.getApplicationName(), serviceWrapper);
                Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
                log.info("服务器启动成功");
            } else {
                log.error("服务器启动失败");
            }
        });
    }

    public void stopServer() {
        ServiceWrapper serviceWrapper = new ServiceWrapper();
        serviceWrapper.setDomain("127.0.0.1");
        serviceWrapper.setPort(serverConfig.getServerPort());
        register.unRegister(serverConfig.getApplicationName(), serviceWrapper);
        serverBootstrap.group().shutdownGracefully();
        log.info("服务器关闭成功");
    }

    public void initConfiguration() {
        // 加载扩展类
        try {
            ServerConfig serverConfig = ConfigLoader.loadServerProperties();
            extraLoader.loadExtension(Serialize.class);
            extraLoader.loadExtension(Register.class);
            LinkedHashMap<String, Class> registerClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Register.class.getName());
            LinkedHashMap<String, Class> serializeClass = ExtraLoader.EXTENSION_LOADER_CLASS_CACHE.get(Serialize.class.getName());

            // 注册中心
            Class regCls = registerClass.get(serverConfig.getRegisterType());
            if (ObjectUtil.isEmpty(regCls)) {
                throw new RuntimeException("注册中心类型不存在");
            }
            register = (Register) regCls.newInstance();
            // 序列化
            Class serCls = serializeClass.get(serverConfig.getServerSerialize());
            if (ObjectUtil.isEmpty(serCls)) {
                throw new RuntimeException("序列化类型不存在");
            }
            ServerCache.serialize = (Serialize) serCls.newInstance();
            ServerCache.serverConfig = serverConfig;
        } catch (Exception e) {
            log.error("加载扩展类失败", e);
        }
    }
}
