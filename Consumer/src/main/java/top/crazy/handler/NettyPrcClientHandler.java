package top.crazy.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import top.crazy.common.HostNode;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-14  16:04
 * @Description: TODO
 */
@Data
@Component
@ConditionalOnProperty(
        prefix = "rpc",
        name = "mode-registration",
        havingValue = "false",
        matchIfMissing = false
)
public class NettyPrcClientHandler extends SimpleChannelInboundHandler<String>  implements  Callable {

    ChannelHandlerContext context;
    Map<String, HostNode>  SERVICE_NAME_HOST_NODE = new HashMap<>(128);
    private String reqMsg;
    private String serviceName;
    private String respMsg;

    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        respMsg = s;
        //唤醒等待线程
        notify();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
    }

    /**
     * @description:给服务端发送消息
     * @author: crazyPang
     * @date: 2023/5/15 16:19
     * @param: []
     * @return: java.lang.Object
    **/
    @Override
    public synchronized Object call() throws Exception {
        context.writeAndFlush(reqMsg);
        //将线程置于等待状态
        wait();
        return respMsg;
    }

    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    public ChannelHandlerContext getCtx() {
        return context;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.context = ctx;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }
}
