package top.crazy.loadBalance;

import org.omg.CORBA.PUBLIC_MEMBER;
import top.crazy.common.HostNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  22:20
 * @Description: TODO
 */
public class LoadBalance {

    //
    public static Map<String,Integer> SERVICE_LOAD_BALANCE_MAP = new HashMap<String,Integer>();
    public static Map<String,Map<String,Integer>>  WEIGHT_SERVICE_NAME_HOSTNAME = new HashMap<>();
    public static Random randomConstructor = new Random();
    public static HostNode LoadBalanceCal(String serviceName, List<HostNode> nodeList){
        //获得serviceName的负载均衡政策
        Integer loadBalanceInteger = SERVICE_LOAD_BALANCE_MAP.get(serviceName);
        if(null==loadBalanceInteger){
            loadBalanceInteger = 1;
        }

        if(nodeList.size()<=0){
            return null;
        }

        switch (LoadBalanceType.match(loadBalanceInteger)){
            case RANDOM: return randomExecute(nodeList);
            case WEIGHT: return weightExecute(serviceName,nodeList);
            default:
                return randomExecute(nodeList);
        }

    }

    public static HostNode randomExecute(List<HostNode> nodeList){
        if(nodeList.size()<=0){
            return null;
        }
        return nodeList.get(randomConstructor.nextInt()% nodeList.size());
    }



    public static HostNode weightExecute(String serviceName,List<HostNode> nodeList){
        if(nodeList.size()<=0){
            return null;
        }
        Map<String, Integer> hostNameWeight = LoadBalance.WEIGHT_SERVICE_NAME_HOSTNAME.get(serviceName);
        if(hostNameWeight.size()==1){
            return nodeList.get(0);
        }
        int sumWeight = 0;
        for (HostNode currentHostNode: nodeList ){
            Integer curWeight = hostNameWeight.get(currentHostNode.getHostName());
            sumWeight+=curWeight;
        }
        int index = randomConstructor.nextInt() % sumWeight;
        int temp = 0;
        HostNode targetHostNode = nodeList.get(0);
        for (HostNode currentHostNode: nodeList ){
            temp+=hostNameWeight.get(currentHostNode.getHostName());
            if (index>temp){
                targetHostNode = currentHostNode;
            }else {
                return targetHostNode;
            }
        }
        return nodeList.get(randomConstructor.nextInt()% nodeList.size());
    }
}
