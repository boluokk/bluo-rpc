package org.bluo.register.nacos;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.ServiceWrapper;
import org.bluo.register.SimpleRegisterAbstract;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author boluo
 * @date 2023/12/22
 */
@Slf4j
public class NacosRegister extends SimpleRegisterAbstract {

    private static NamingService namingService;

    public NacosRegister(String address, String password) {
        try {
            namingService = NamingFactory.createNamingService(address);
        } catch (Throwable e) {
            log.error("初始化nacos注册中心失败", e);
        }
    }

    @Override
    public void register(String serviceName, ServiceWrapper serviceWrapper) {
        try {
            namingService.registerInstance(serviceName, serviceWrapper.getDomain(), serviceWrapper.getPort());
        } catch (NacosException e) {
            log.error("注册nacos服务失败", e);
        }
    }

    @Override
    public void unRegister(String serviceName, ServiceWrapper serviceWrapper) {
        try {
            namingService.deregisterInstance(serviceName, serviceWrapper.getDomain(), serviceWrapper.getPort());
        } catch (NacosException e) {
            log.error("注销nacos服务失败", e);
        }
    }

    @Override
    public List<ServiceWrapper> getServices(String serviceName) {
        try {
            List<Instance> allInstances = namingService.getAllInstances(serviceName);
            return allInstances.stream()
                    .map(i -> new ServiceWrapper(i.getIp(), i.getPort()))
                    .collect(Collectors.toList());
        } catch (NacosException e) {
            log.error("获取nacos服务失败", e);
        }
        return null;
    }
}
