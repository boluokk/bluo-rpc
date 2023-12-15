package org.bluo.serializer.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bluo.serializer.Serializer;

/**
 * @author boluo
 * @date 2023/11/30
 */
public class JacksonSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) throws Exception {
        return new ObjectMapper().writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return new ObjectMapper().readValue(bytes, clazz);
    }
}
