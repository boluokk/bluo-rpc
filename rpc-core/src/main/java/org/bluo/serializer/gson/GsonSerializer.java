package org.bluo.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bluo.serializer.Serializer;

/**
 * @author boluo
 * @date 2024/01/22
 */
public class GsonSerializer implements Serializer {
    private final Gson gson;

    public GsonSerializer() {
        // 创建 Gson 对象，并注册 ClassTypeAdapter
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .create();
    }

    @Override
    public byte[] serialize(Object obj) throws Exception {
        return gson.toJson(obj).getBytes();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        String json = new String(bytes);
        return gson.fromJson(json, clazz);
    }
}
