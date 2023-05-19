package top.crazy.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import top.crazy.common.HostNode;
import top.crazy.common.RegisterMessage;
import top.crazy.common.RegisterRespMessage;
import top.crazy.common.RegisterRespMessageType;
import top.crazy.loadBalance.LoadBalance;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-19  12:08
 * @Description: TODO
 */
public class HostNodeInactiveHandler {

    public static void execute(ChannelHandlerContext channelHandlerContext){
        //根据ctx 将对应的 SERVICE_HOST_NODE_MAP 删除对应的hostNode
        String hostName = NettyRegisterHandler.CTX_HOST_NAME_MAP.get(channelHandlerContext);
        //定义resp
        RegisterRespMessage registerRespMessage = new RegisterRespMessage();

        //获得 list<hostNode>
        Set<Map.Entry<String, List<HostNode>>> entries = NettyRegisterHandler.SERVICE_HOST_NODE_MAP.entrySet();
        for (Map.Entry<String, List<HostNode>> entry: entries){
            String serviceName = entry.getKey();
            List<HostNode> nodeList = NettyRegisterHandler.SERVICE_HOST_NODE_MAP.get(serviceName);
            Iterator<HostNode> iterator = nodeList.iterator();
            while (iterator.hasNext()){
                HostNode next = iterator.next();
                if (next.getHostName().equals(hostName)){
                    nodeList.remove(next);
                }
            }

        }

        Set<Map.Entry<String, Map<String, Integer>>> entries1 = LoadBalance.WEIGHT_SERVICE_NAME_HOSTNAME.entrySet();
        for (Map.Entry<String, Map<String, Integer>> entry :entries1){
            Map<String, Integer> hostNameWeight = entry.getValue();
            if (hostNameWeight.containsKey(hostName)) {
                hostNameWeight.remove(hostName);
                break;
            }
        }
        registerRespMessage.setMessage("节点断线 hostNode"+hostName+" 所有服务已经删除");
        registerRespMessage.setResult(null);
        //todo 修改返回类型
        registerRespMessage.setType(RegisterRespMessageType.UN_KNOW_WRONG.getType());
        channelHandlerContext.writeAndFlush(JSON.toJSONString(registerRespMessage));
        return;

    }
}
