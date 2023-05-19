package top.crazy.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;
import top.crazy.common.HostNode;
import top.crazy.common.RegisterMessage;
import top.crazy.common.RegisterMessageType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-16  21:20
 * @Description: TODO
 */

@Component
@ChannelHandler.Sharable
public class NettyRegisterHandler extends SimpleChannelInboundHandler<String>  {


    /**
     * @description: 作为缓存
     * @author: crazyPang
     * @date: 2023/5/16 21:42
    **/
    public static Map<String, List<HostNode>> SERVICE_HOST_NODE_MAP = new HashMap<String, List<HostNode>>();
    /**
     * @description: 作为 hostName 与ctx的 对应关系
     * @author: crazyPang
     * @date: 2023/5/19 11:13
    **/
    public static Map<ChannelHandlerContext,String> CTX_HOST_NAME_MAP = new HashMap<ChannelHandlerContext,String>();

    /**
     * @description: HOST_NAME &  LIST <SERVICE_NAME>
     * @author: crazyPang
     * @date: 2023/5/19 11:14
     * @param:
     * @return:
    **/
    public static Map<String, List<String>> HOST_NAME_LIST_SERVICE_NAME_MAP = new HashMap<String, List<String>>();
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        //todo 根据解析后messageType 进行 switch(MessageType)

        RegisterMessage registerMessage = JSON.parseObject(s, RegisterMessage.class);
        System.out.println(registerMessage.toString());

        switch (RegisterMessageType.match(registerMessage.getRegisterMessageType())){
            case REGISTER_HOST_NODE :{
                //TODO 增加register逻辑
                RegisterHostNodeHandler.execute(channelHandlerContext,registerMessage);
                break;
            }
            case LOSS_CONNECT :{
                UnRegisterHostNodeServiceHandler.execute(channelHandlerContext,registerMessage);
                break;
            }
            case GET_HOST_NODE : {
                GetHostNodeHandler.execute(channelHandlerContext,registerMessage);
                break;
            }
            case ERROR : {
                //todo error 怎么写 待会再想
                break;
            }
        }


    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        HostNodeInactiveHandler.execute(ctx);
        //断线处理 todo
    }
}
