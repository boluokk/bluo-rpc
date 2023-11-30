package org.bluo.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.bluo.protocol.RpcProtocol;

/**
 * @author boluo
 * @date 2023/11/30
 */
public class MessageEncoder extends MessageToByteEncoder<RpcProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol message,
                          ByteBuf byteBuf) throws Exception {
    
    }
}
