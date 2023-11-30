package org.bluo.serialize.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bluo.serialize.Serialize;

/**
 * @author boluo
 * @date 2023/11/30
 */
public class JacksonSerialize implements Serialize {
    @Override
    public byte[] serialize(Object obj) throws Exception {
        return new ObjectMapper().writeValueAsBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return new ObjectMapper().readValue(bytes, clazz);
    }
}
