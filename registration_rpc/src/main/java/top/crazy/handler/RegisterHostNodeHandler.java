package top.crazy.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.crazy.common.HostNode;
import top.crazy.common.RegisterMessage;
import top.crazy.common.RegisterRespMessage;
import top.crazy.common.RegisterRespMessageType;
import top.crazy.loadBalance.LoadBalance;
import top.crazy.loadBalance.LoadBalanceType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  21:44
 * @Description: TODO
 */
public class RegisterHostNodeHandler {

    static Logger logger = LoggerFactory.getLogger(RegisterHostNodeHandler.class);
    public static void execute(ChannelHandlerContext channelHandlerContext, RegisterMessage registerMessage){
        //获得 服务名称
        String serviceName = registerMessage.getServiceName();
        //定义resp
        RegisterRespMessage registerRespMessage = new RegisterRespMessage();
        //获得 HostNode 节点
        HostNode hostNode = new HostNode(registerMessage.getHostName(), registerMessage.getHost(), registerMessage.getPort(),registerMessage.getLoadBalanceWeight());

        if (!NettyRegisterHandler.CTX_HOST_NAME_MAP.containsKey(channelHandlerContext)) {
            //不存在 hostName 对应的ctx 将 ctx 存进去
            NettyRegisterHandler.CTX_HOST_NAME_MAP.put(channelHandlerContext,registerMessage.getHostName());
        }

        //获得 list<hostNode>
        List<HostNode> hostNodes = NettyRegisterHandler.SERVICE_HOST_NODE_MAP.get(serviceName);
        if (null==hostNodes){
            //新服务
            ArrayList<HostNode> newHostList = new ArrayList<>();
            newHostList.add(hostNode);
            NettyRegisterHandler.SERVICE_HOST_NODE_MAP.put(serviceName,newHostList);
            registerRespMessage.setMessage("成功将 服务："+serviceName+" hostName:"+hostNode.getHostName()+" 成功注册");
            registerRespMessage.setResult(null);
            registerRespMessage.setType(RegisterRespMessageType.SUCCESS.getType());
            channelHandlerContext.writeAndFlush(JSON.toJSON(registerRespMessage));

            //负载均衡取消
//            HashMap<String, Integer> hostNameWeight = new HashMap<>();
//            Integer loadBalanceWeight = registerMessage.getLoadBalanceWeight();
//            if (null==loadBalanceWeight){
//                loadBalanceWeight = 1;
//            }
//            hostNameWeight.put(registerMessage.getHostName(),loadBalanceWeight);
//            LoadBalance.WEIGHT_SERVICE_NAME_HOSTNAME.put(serviceName,hostNameWeight);
            return;
        }

        for(HostNode currentHostNode:hostNodes){
            if (currentHostNode.getHostName().equals(hostNode.getHostName())){
                //具有相同的节点，属于重复添加
                registerRespMessage.setResult(null);
                registerRespMessage.setType(RegisterRespMessageType.SUCCESS.getType());
                registerRespMessage.setMessage("服务："+serviceName+" hostName:"+hostNode.getHostName()+" 还在");
                channelHandlerContext.writeAndFlush(JSON.toJSONString(registerRespMessage));
                return;
            }
        }

        //将hostNodes加入List中 新节点 旧服务 这不得 直接加入hostNodes 并且 获取 权重map
        //todo redis 增加服务
        hostNodes.add(hostNode);

        Integer loadBalanceWeight = registerMessage.getLoadBalanceWeight();
        if (null==loadBalanceWeight){
            loadBalanceWeight = 1;
        }
//        LoadBalance.WEIGHT_SERVICE_NAME_HOSTNAME.get(serviceName).put(registerMessage.getHostName(),loadBalanceWeight);

        //返回成功消息
        registerRespMessage.setMessage("成功将 服务："+serviceName+" hostName:"+hostNode.getHostName()+" 成功注册");
        registerRespMessage.setResult(null);
        registerRespMessage.setType(RegisterRespMessageType.SUCCESS.getType());

        channelHandlerContext.writeAndFlush(JSON.toJSON(registerRespMessage));

    }
}
