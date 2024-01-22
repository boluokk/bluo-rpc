package org.bluo.serializer.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import lombok.extern.slf4j.Slf4j;
import org.bluo.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author boluo
 * @date 2024/01/22
 */
@Slf4j
public class HessianSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream bos = null;
        HessianOutput hessianOutput = null;
        try {
            bos = new ByteArrayOutputStream();
            hessianOutput = new HessianOutput(bos);
            hessianOutput.writeObject(obj);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("序列化失败: {}", e);
        } finally {
            if (hessianOutput != null) hessianOutput.close();
            if (bos != null) bos.close();
        }
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        ByteArrayInputStream bis = null;
        HessianInput hessianInput = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            hessianInput = new HessianInput(bis);
            return (T) hessianInput.readObject();
        } catch (Exception e) {
            log.error("反序列化失败: {}", e);
        } finally {
            if (hessianInput != null) hessianInput.close();
            if (bis != null) bis.close();
        }
        return null;
    }
}
