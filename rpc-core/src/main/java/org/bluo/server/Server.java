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
import org.bluo.register.redis.RedisRegister;


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
                redisRegister.register("test", "127.0.0.1", 6636);
                Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
                log.info("服务器启动成功");
            } else {
                log.error("服务器启动失败");
            }
        });
    }

    public void stopServer() {
        serverBootstrap.config().group().shutdownGracefully();
    }

    public void stopPre(ChannelFuture serverChannelFuture) {
        serverChannelFuture.channel().closeFuture().addListener(future -> {
            if (future.isSuccess()) {
                redisRegister.unRegister("test", "127.0.0.1", 6636);
                log.info("服务器关闭成功");
            }
        });
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.stopPre(server.runServer());
    }
}
