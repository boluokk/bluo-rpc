package org.bluo.router.hash;

/**
 * 一致性hash
 *
 * @author boluo
 * @date 2023/12/02
 */

import cn.hutool.core.util.ObjectUtil;
import org.bluo.common.ServiceWrapper;
import org.bluo.router.RouterAbs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性hash
 *
 * @author boluo
 */

public class ConsistentHashRouter extends RouterAbs {
    private ConsistentHash consistentHash;

    @Override
    public ServiceWrapper select(List<ServiceWrapper> services) {
        if (ObjectUtil.isEmpty(consistentHash)) {
            consistentHash = new ConsistentHash(services, 160);
        }
        return null;
    }

    class ConsistentHash {
        private final SortedMap<Integer, ServiceWrapper> circle = new TreeMap<>();
        private final List<ServiceWrapper> nodes = new ArrayList<>();
        private final int numberOfReplicas;

        public ConsistentHash(List<ServiceWrapper> nodes, int numberOfReplicas) {
            this.numberOfReplicas = numberOfReplicas;
            this.nodes.addAll(nodes);
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

        public void removeNode(String node) {
            for (int i = 0; i < numberOfReplicas; i++) {
                int hash = getHash(node + i);
                circle.remove(hash);
            }
        }

        public ServiceWrapper getNode(String key) {
            if (circle.isEmpty()) {
                return null;
            }
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
