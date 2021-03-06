package org.mvnsearch.spring.boot.dubbo.listener;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.listener.InvokerListenerAdapter;

import java.util.*;

/**
 * dubbo client invoker listener
 *
 * @author linux_china
 */
@Activate
public class ConsumerSubscribeListener extends InvokerListenerAdapter {
    public static Set<Class> subscribedInterfaces = new HashSet<>();
    public static Map<String, Set<String>> connections = new HashMap<>();

    @Override
    public void referred(Invoker<?> invoker) throws RpcException {
        Class<?> subscribeInterface = invoker.getInterface();
        subscribedInterfaces.add(subscribeInterface);
        String subscribeInterfaceCanonicalName = subscribeInterface.getCanonicalName();
        if (!connections.containsKey(subscribeInterfaceCanonicalName)) {
            connections.put(subscribeInterfaceCanonicalName, new HashSet<>());
        }
        connections.get(subscribeInterfaceCanonicalName).add(invoker.getUrl().toString());
    }

    @Override
    public void destroyed(Invoker<?> invoker) {
        Class<?> subscribedInterface = invoker.getInterface();
        subscribedInterfaces.remove(subscribedInterface);
        String subscribedInterfaceCanonicalName = subscribedInterface.getCanonicalName();
        if (connections.containsKey(subscribedInterfaceCanonicalName)) {
            connections.get(subscribedInterfaceCanonicalName).remove(invoker.getUrl().toString());
        }
    }
}
