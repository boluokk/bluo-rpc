package org.bluo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.MessageDecoder;
import org.bluo.common.MessageEncoder;
import org.bluo.common.RpcInvocation;
import org.bluo.common.RpcProtocol;
import org.bluo.serialize.jackson.JacksonSerialize;

import java.net.InetSocketAddress;

/**
 * 用户端
 *
 * @author boluo
 * @date 2023/11/30
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new MessageDecoder());
                channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                        log.debug("获取服务器返回消息: {}", msg);
                        super.channelRead(ctx, msg);
                    }
                });
            }
        });

        Channel channel = bootstrap.connect(new InetSocketAddress("127.0.0.1", 6636)).sync().channel();
        log.debug("消息准备发送！");
        RpcInvocation rpcInvocation = new RpcInvocation();
        RpcProtocol rpcProtocol = new RpcProtocol();
        rpcInvocation.setClassName("org.bluo.service.impl.HelloServiceImpl");
        rpcInvocation.setMethodName("sayHello");
        byte[] serialize = new JacksonSerialize().serialize(rpcInvocation);
        rpcProtocol.setContentLength(serialize.length);
        rpcProtocol.setContent(serialize);
        channel.writeAndFlush(rpcProtocol);
    }
}
