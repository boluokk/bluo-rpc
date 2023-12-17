package org.bluo.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.bluo.serializer.Serializer;

import java.util.List;

import static org.bluo.constants.RpcConstants.MAGIC_NUMBER;

/**
 * 解码
 *
 * @author boluo
 * @date 2023/11/30
 */
public class MessageDecoder extends ByteToMessageDecoder {
    /**
     * 基本长度 = 魔数 + 内容长度
     */
    private static final int BASE_LENGTH = Short.BYTES + Integer.BYTES;

    private Serializer serializer;

    @Override
    protected void decode(ChannelHandlerContext ctx,
                          ByteBuf byteBuf,
                          List<Object> list) throws Exception {
        if (byteBuf.readableBytes() > BASE_LENGTH) {
            // 魔数判定
            if (byteBuf.readShort() != MAGIC_NUMBER) {
                ctx.close();
                return;
            }
            // 内容长度判定
            int length = byteBuf.readInt();
            if (length > byteBuf.readableBytes()) {
                ctx.close();
                return;
            }
            // 读取内容
            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            list.add(serializer.deserialize(body, RpcInvocation.class));
        }
    }

    public MessageDecoder(Serializer serializer) {
        this.serializer = serializer;
    }
}
