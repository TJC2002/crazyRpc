package top.crazy.loadBalance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.crazy.common.HostNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.loadBalance
 * @Author: crazyPang
 * @CreateTime: 2023-05-19  18:24
 * @Description: TODO
 */

@Component
public class LoadBalance {

    @Value("rpc.consumer.loadbalance")
    static String loadBalanceString;

    static LoadBalanceType loadBalanceType;

    public static Random randomConstructor = new Random();

    public static Map<String, AtomicInteger> SERVICE_INTEGER = new HashMap<>();

    public static HostNode LoadBalanceCal(String serviceName, List<HostNode> nodeList){

        loadBalanceType = LoadBalanceType.STRING_LOAD_BALANCE.get(loadBalanceString);

        //获得serviceName的负载均衡政策
        if(null==loadBalanceType){
            loadBalanceType = LoadBalanceType.RANDOM;
        }
        if(nodeList.size()<=0){
            return null;
        }
        switch (loadBalanceType){
            case RANDOM: return randomExecute(nodeList);
            case ROUND_ROBIN: return roundRobin(serviceName,nodeList);
            case WEIGHT_ROUND_ROBIN:return weightRoundRobinExecute(serviceName,nodeList);
            case WEIGHT_RANDOM:  return weightRandomExecute(serviceName,nodeList);
            case SOURCE_HASH: return sourceHashExecute(serviceName,nodeList);
            default:
                return randomExecute(nodeList);
        }

    }

    public static HostNode roundRobin(String serviceName,List<HostNode> nodeList){
        AtomicInteger atomicInteger = SERVICE_INTEGER.get(serviceName);
        if (null==atomicInteger){
            atomicInteger = new AtomicInteger(0);
            SERVICE_INTEGER.put(serviceName,atomicInteger);
        }
        return nodeList.get(atomicInteger.incrementAndGet()%nodeList.size());

    }
    public static HostNode weightRoundRobinExecute(String serviceName,List<HostNode> nodeList){
        if(nodeList.size()<=0){
            return null;
        }
        if (nodeList.size()==1){
            return nodeList.get(0);
        }
        int sumWeight = 0;
        for (HostNode currentHostNode: nodeList ){
            Integer curWeight = currentHostNode.getWeight();
            sumWeight+=curWeight;
        }
        int index = SERVICE_INTEGER.get(serviceName).incrementAndGet();
        int temp = 0;
        HostNode targetHostNode = nodeList.get(0);
        for (HostNode currentHostNode: nodeList ){
            temp+=currentHostNode.getWeight();
            if (index>temp){
                targetHostNode = currentHostNode;
            }else {
                return targetHostNode;
            }
        }
        return nodeList.get(randomConstructor.nextInt(nodeList.size()));

    }

    public static HostNode randomExecute(List<HostNode> nodeList){
        if(nodeList.size()<=0){
            return null;
        }
        return nodeList.get(randomConstructor.nextInt(nodeList.size()));
    }


    public static HostNode weightRandomExecute(String serviceName,List<HostNode> nodeList){
        if(nodeList.size()<=0){
            return null;
        }
        if (nodeList.size()==1){
            return nodeList.get(0);
        }
        int sumWeight = 0;
        for (HostNode currentHostNode: nodeList ){
            Integer curWeight = currentHostNode.getWeight();
            sumWeight+=curWeight;
        }
        int index = randomConstructor.nextInt( sumWeight);
        int temp = 0;
        HostNode targetHostNode = nodeList.get(0);
        for (HostNode currentHostNode: nodeList ){
            temp+=currentHostNode.getWeight();
            if (index>temp){
                targetHostNode = currentHostNode;
            }else {
                return targetHostNode;
            }
        }
        return nodeList.get(randomConstructor.nextInt(nodeList.size()));
    }


    /**
     * @description: todo 后面再改
     * @author: crazyPang
     * @date: 2023/5/19 20:08
     * @param: [serviceName, nodeList]
     * @return: top.crazy.common.HostNode
    **/
    public static HostNode sourceHashExecute(String serviceName,List<HostNode> nodeList){
        return randomExecute(nodeList);
    }

}
