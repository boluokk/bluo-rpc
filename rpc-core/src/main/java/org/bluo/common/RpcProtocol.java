package org.bluo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.bluo.constants.RpcConstants.MAGIC_NUMBER;

/**
 * 协议
 *
 * @author boluo
 * @date 2023/11/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcProtocol {
    /**
     * 魔数
     */
    private short magicNumber = MAGIC_NUMBER;
    /**
     * 内容长度
     */
    private int contentLength;
    /**
     * 内容
     */
    private byte[] content;
}
