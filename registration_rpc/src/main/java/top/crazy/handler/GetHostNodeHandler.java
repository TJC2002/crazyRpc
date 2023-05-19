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

import java.util.List;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  22:18
 * @Description: TODO
 */
public class GetHostNodeHandler {

    static Logger logger = LoggerFactory.getLogger(GetHostNodeHandler.class);
    public static void execute(ChannelHandlerContext channelHandlerContext, RegisterMessage registerMessage){
        //获得 服务名称
        String serviceName = registerMessage.getServiceName();

        //定义resp
        RegisterRespMessage registerRespMessage = new RegisterRespMessage();

        //获得 list<hostNode>
        List<HostNode> hostNodes = NettyRegisterHandler.SERVICE_HOST_NODE_MAP.get(serviceName);

        if(null==hostNodes){
            registerRespMessage.setType(RegisterRespMessageType.UN_KNOW_SERVICE_NAME.getType());
            registerRespMessage.setResult(null);
            registerRespMessage.setMessage("没有相关服务 :"+serviceName+" 注册");
            channelHandlerContext.writeAndFlush(JSON.toJSONString(registerRespMessage));
            return;
        }
        /**
         * @description: 负载平衡改为消费者设置 跑路！
        **/
//        HostNode resultHostNode = LoadBalance.LoadBalanceCal(serviceName, hostNodes);
//
//        if(null==resultHostNode){
//            registerRespMessage.setType(RegisterRespMessageType.WITHOUT_HOST_NODE.getType());
//            throw new RuntimeException("获得节点失败");
//        }

        registerRespMessage.setMessage("成功获得服务： "+serviceName+" 共存在节点："+hostNodes.size());
        registerRespMessage.setResult(JSON.toJSONString(hostNodes));
        registerRespMessage.setType(RegisterRespMessageType.SUCCESS.getType());
        logger.info("回复消费者请求服务 service:{} ",serviceName);
        channelHandlerContext.writeAndFlush(JSON.toJSONString(registerRespMessage));
        return;

    }
}
