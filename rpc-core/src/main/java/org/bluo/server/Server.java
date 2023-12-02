package org.bluo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.MessageDecoder;
import org.bluo.common.MessageEncoder;
import org.bluo.common.ServiceWrapper;
import org.bluo.register.redis.RedisRegister;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * 服务端
 *
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class Server {
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    RedisRegister redisRegister = new RedisRegister();

    public ChannelFuture runServer() {
        log.info("启动服务器中..");
        serverBootstrap.group(new NioEventLoopGroup());
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
                serviceWrapper.setDomain("127.0.0.1");
                serviceWrapper.setPort(6636);
                redisRegister.register("test", serviceWrapper);
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
        serviceWrapper.setPort(6636);
        redisRegister.unRegister("test", serviceWrapper);
        serverBootstrap.group().shutdownGracefully();
        log.info("服务器关闭成功");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.runServer();
    }
}
