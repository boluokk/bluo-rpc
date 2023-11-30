package org.bluo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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
    private static Bootstrap bootstrap = new Bootstrap();

    public static ChannelFuture getChannelFuture() throws InterruptedException {
        return connect(new InetSocketAddress("127.0.0.1", 6636));
    }

    private static ChannelFuture connect(InetSocketAddress address) throws InterruptedException {
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                channel.pipeline().addLast(new MessageEncoder());
                channel.pipeline().addLast(new MessageDecoder());
                channel.pipeline().addLast(new ChannelInboundHandler());
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(address).sync();
        if (channelFuture.isSuccess()) {
            log.debug("服务器连接成功: {} - {}", address.getHostName(), address.getPort());
        } else {
            log.debug("服务器连接失败: {} - {}", address.getHostName(), address.getPort());
        }
        return channelFuture;
    }

    public static void sendMessage(RpcProtocol rpcProtocol) throws InterruptedException {
        ChannelFuture channelFuture = getChannelFuture();
        channelFuture.channel().writeAndFlush(rpcProtocol);
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.test();
    }

    public void test() {
        RpcProtocol rpcProtocol = new RpcProtocol();
        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setClassName("org.bluo.server.HelloService");
        rpcInvocation.setMethodName("hello");
        try {
            byte[] body = new JacksonSerialize().serialize(rpcInvocation);
            rpcProtocol.setContentLength(body.length);
            rpcProtocol.setContent(body);
            sendMessage(rpcProtocol);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
