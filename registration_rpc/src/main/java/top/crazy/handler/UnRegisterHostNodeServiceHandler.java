package top.crazy.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import top.crazy.common.HostNode;
import top.crazy.common.RegisterMessage;
import top.crazy.common.RegisterRespMessage;
import top.crazy.common.RegisterRespMessageType;

import java.util.List;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  22:05
 * @Description: 将指定的 service and hostName 删除
 */
public class UnRegisterHostNodeServiceHandler {
    public static void execute(ChannelHandlerContext channelHandlerContext, RegisterMessage registerMessage){

        //获得 服务名称
        String serviceName = registerMessage.getServiceName();

        //定义resp
        RegisterRespMessage registerRespMessage = new RegisterRespMessage();
        //获得 HostNode 节点
        HostNode hostNode = new HostNode(registerMessage.getHostName(), registerMessage.getHost(), registerMessage.getPort(),registerMessage.getLoadBalanceWeight());

        //获得 list<hostName>
        List<HostNode> hostNodes = NettyRegisterHandler.SERVICE_HOST_NODE_MAP.get(serviceName);

        for(HostNode currentHostNode:hostNodes){
            if (currentHostNode.getHostName().equals(hostNode.getHostName())){
                //todo 删除redis 缓存
                hostNodes.remove(currentHostNode);
                registerRespMessage.setMessage("成功将服务："+serviceName+" hostName "+hostNode.getHostName()+" 从注册中心下线");
                registerRespMessage.setType(RegisterRespMessageType.SUCCESS.getType());
                registerRespMessage.setResult(null);
                channelHandlerContext.writeAndFlush(JSON.toJSONString(registerRespMessage));
            }
        }
    }
}
