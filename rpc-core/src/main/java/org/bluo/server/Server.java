package org.bluo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.MessageDecoder;
import org.bluo.common.MessageEncoder;
import org.bluo.common.RpcInvocation;
import org.bluo.common.RpcProtocol;
import org.bluo.serialize.jackson.JacksonSerialize;


/**
 * 服务端
 *
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class Server {
    private static final ServerBootstrap serverBootstrap = new ServerBootstrap();

    public static void runServer() {
        log.info("启动服务器中..");
        serverBootstrap.group(new NioEventLoopGroup());
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageDecoder());
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.debug("获取到数据: {}", msg);
                        RpcInvocation rpcInvocation = (RpcInvocation) msg;
                        rpcInvocation.setResult("ok");
                        byte[] serialize = new JacksonSerialize().serialize(rpcInvocation);
                        RpcProtocol rpcProtocol = new RpcProtocol();
                        rpcProtocol.setContentLength(serialize.length);
                        rpcProtocol.setContent(serialize);
                        ctx.writeAndFlush(rpcProtocol);
                    }
                });
            }
        });
        serverBootstrap.bind(6636).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    log.info("服务器启动成功");
                } else {
                    log.error("服务器启动失败");
                }
            }
        });
    }

    public static void main(String[] args) {
        runServer();
    }
}
