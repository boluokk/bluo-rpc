package org.bluo.router.round;

import org.bluo.common.ServiceWrapper;
import org.bluo.router.RouterAbs;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 轮询
 *
 * @author boluo
 * @date 2023/12/03
 */
public class RoundRouter extends RouterAbs {

    private volatile int index = 0;
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public ServiceWrapper select(List<ServiceWrapper> services) {
        return services.get(getNextIndex(services.size()));
    }

    private int getNextIndex(int size) {
        try {
            lock.lock();
            if (index + 1 >= size) {
                index = 0;
            }
            return index++;
        } finally {
            lock.unlock();
        }
    }
}