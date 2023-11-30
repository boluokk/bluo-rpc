package org.bluo.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码
 *
 * @author boluo
 * @date 2023/11/30
 */
public class MessageEncoder extends MessageToByteEncoder<RpcProtocol> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext,
                          RpcProtocol message,
                          ByteBuf byteBuf) throws Exception {
        byteBuf.writeShort(message.getMagicNumber());
        byteBuf.writeInt(message.getContentLength());
        byteBuf.writeBytes(message.getContent());
    }
}
