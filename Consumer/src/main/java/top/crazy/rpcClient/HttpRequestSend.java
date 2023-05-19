package top.crazy.rpcClient;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


import top.crazy.common.*;
import top.crazy.handler.NettyRegistrationClientHandler;
import top.crazy.loadBalance.LoadBalance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.rpcClient
 * @Author: crazyPang
 * @CreateTime: 2023-05-18  16:57
 * @Description: 作为发送类的使用
 */

@Configuration
@Component
public class HttpRequestSend {

    Logger logger = LoggerFactory.getLogger(HttpRequestSend.class);

    @Value("${rpc.consumer.registration.hostnode.cache.savetime}")
    Integer cacheSaveTime;

    @Autowired
    NettyRegistrationClientHandler nettyRegistrationClientHandler;

    @Autowired
    NettyRegistrationClient nettyRegistrationClient;

    public Object send(Invocation invocation, String serviceName){
        List<HostNode> nodeList = NettyRegistrationClientHandler.SERVICE_NAME_LIST_HOST_NODE.get(serviceName);
        HostNode serviceHostNode = getHostNode(serviceName);
        logger.debug("发起rpc调用请求 service:{} hostname:{} host:{} port:{} ",serviceName,serviceHostNode.getHostName(),serviceHostNode.getHost(),serviceHostNode.getPort());
        HttpClient httpClient = new HttpClient();
        String result = httpClient.send(serviceHostNode.getHost(), Integer.valueOf(serviceHostNode.getPort()) , invocation);
        return result;
    }

    public void updateService(String serviceName){

        List<HostNode> serviceHostNodes = NettyRegistrationClientHandler.SERVICE_NAME_LIST_HOST_NODE.get(serviceName);
        RegisterMessage registerMessage = new RegisterMessage();
        registerMessage.setRegisterMessageType(RegisterMessageType.GET_HOST_NODE.getType());
        registerMessage.setServiceName(serviceName);
        registerMessage.setMessage("请求服务节点 "+serviceName);
        try {
            String result =  (String) nettyRegistrationClient.pullRegistrationHostNode(JSON.toJSONString(registerMessage));
            RegisterRespMessage registerRespMessage = JSON.parseObject(result, RegisterRespMessage.class);
            if (null==registerRespMessage.getResult()){
                throw new RuntimeException("没有找到服务："+serviceName);
            }
            String hostNodesString =(String) registerRespMessage.getResult();
            List<HostNode> hostNodes = JSON.parseArray(hostNodesString, HostNode.class);
            serviceHostNodes = hostNodes;
            for (HostNode curHostNode:serviceHostNodes) {
                curHostNode.setExpireTime(LocalDateTime.now().plusSeconds(cacheSaveTime));
            }
            logger.debug("更新节点 service:{} sum ",serviceName,serviceHostNodes.size());
            NettyRegistrationClientHandler.SERVICE_NAME_LIST_HOST_NODE.put(serviceName,serviceHostNodes);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (RuntimeException runtimeException){
            logger.error("没有找到服务：{}",serviceName);
            return ;
        }
    }

    /**
     * @description: 根据服务名获取一个节点 并且 完成 过期校验
     * @author: crazyPang
     * @date: 2023/5/19 19:10
     * @param: [serviceName]
     * @return: top.crazy.common.HostNode
    **/
    public HostNode getHostNode(String serviceName){


        List<HostNode> nodeList = NettyRegistrationClientHandler.SERVICE_NAME_LIST_HOST_NODE.get(serviceName);
        while (nodeList==null||nodeList.size()<=0){
            updateService(serviceName);
            nodeList = NettyRegistrationClientHandler.SERVICE_NAME_LIST_HOST_NODE.get(serviceName);
            System.out.println("尝试更新服务"+serviceName);
        }
        //负载均衡计算
        HostNode hostNode = LoadBalance.LoadBalanceCal(serviceName, nodeList);
        if(null == hostNode){
            updateService(serviceName);
            hostNode = getHostNode(serviceName);
        }
        if (null!=hostNode){
            if (! hostNode.getExpireTime().isAfter(LocalDateTime.now())){
                updateService(serviceName);
                nodeList = NettyRegistrationClientHandler.SERVICE_NAME_LIST_HOST_NODE.get(serviceName);
                hostNode = LoadBalance.LoadBalanceCal(serviceName, nodeList);
            }
        }
        return hostNode;
    }



}
