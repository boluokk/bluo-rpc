package org.bluo.router.hash;

/**
 * 一致性hash
 *
 * @author boluo
 * @date 2023/12/02
 */

import cn.hutool.core.util.ObjectUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bluo.common.ServiceWrapper;
import org.bluo.router.RouterAbs;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一致性hash
 *
 * @author boluo
 */
@Slf4j
public class ConsistentHashRouter extends RouterAbs {
    private ConsistentHash consistentHash;
    private static final int LEN = 160;
    private ReentrantLock lock = new ReentrantLock();

    @SneakyThrows
    @Override
    public ServiceWrapper select(List<ServiceWrapper> services) {
        // 未初始化
        if (ObjectUtil.isNull(consistentHash) || ObjectUtil.isEmpty(consistentHash.nodes.size())) {
            synchronized (this) {
                if (ObjectUtil.isNull(consistentHash) || ObjectUtil.isEmpty(consistentHash.nodes.size())) {
                    consistentHash = new ConsistentHash(services, LEN);
                    return consistentHash.getNode(String.valueOf(InetAddress.getLocalHost()));
                }
            }
        }

        try {
            lock.lock();
            ArrayList<ServiceWrapper> nodes = consistentHash.nodes;
            // 增加节点
            for (ServiceWrapper service : services) {
                if (!nodes.contains(service)) {
                    nodes.add(service);
                    consistentHash.addNode(service);
                }
            }
            // 移除节点
            for (ServiceWrapper node : nodes) {
                if (!services.contains(node)) {
                    nodes.remove(node);
                    consistentHash.removeNode(node);
                }
            }
        } finally {
            lock.unlock();
        }

        return consistentHash.getNode(String.valueOf(InetAddress.getLocalHost()));
    }

    class ConsistentHash {
        private final SortedMap<Integer, ServiceWrapper> circle = new TreeMap<>();
        private final ArrayList<ServiceWrapper> nodes = new ArrayList<>();
        private final int numberOfReplicas;

        public ConsistentHash(List<ServiceWrapper> nodes, int numberOfReplicas) {
            this.numberOfReplicas = numberOfReplicas;
            nodes.addAll(nodes);
            for (ServiceWrapper node : nodes) {
                addNode(node);
            }
        }

        public void addNode(ServiceWrapper node) {
            for (int i = 0; i < numberOfReplicas; i++) {
                int hash = getHash(node.getUrl() + i);
                circle.put(hash, node);
            }
        }

        public void removeNode(ServiceWrapper node) {
            for (int i = 0; i < numberOfReplicas; i++) {
                int hash = getHash(node.getUrl() + i);
                circle.remove(hash);
            }
        }

        public ServiceWrapper getNode(String key) {
            int hash = getHash(key);
            if (!circle.containsKey(hash)) {
                SortedMap<Integer, ServiceWrapper> tailMap = circle.tailMap(hash);
                hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
            }
            return circle.get(hash);
        }

        private int getHash(String input) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(input.getBytes());
                byte[] digest = md.digest();
                return byteArrayToInt(digest);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Unable to find MD5 algorithm");
            }
        }

        private int byteArrayToInt(byte[] bytes) {
            int value = 0;
            for (int i = 0; i < Math.min(bytes.length, 4); i++) {
                value |= (bytes[i] & 0xFF) << (8 * i);
            }
            return value;
        }
    }
}
