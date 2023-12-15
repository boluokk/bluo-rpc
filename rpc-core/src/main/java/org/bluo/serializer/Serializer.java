package org.bluo.serializer;

/**
 * @author boluo
 * @date 2023/11/30
 */
public interface Serializer {
    byte[] serialize(Object obj) throws Exception;

    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;
}
