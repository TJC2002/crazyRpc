package top.crazy.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-17  00:47
 * @Description: TODO
 */
@Component
@Data
public class NettyRegisterClientHandler extends SimpleChannelInboundHandler<String> implements Callable {
    private String reqMsg;

    private String respMsg;
    private ChannelHandlerContext context;
    @Override
    protected  synchronized  void  channelRead0(ChannelHandlerContext channelHandlerContext, String String) throws Exception {
        channelHandlerContext.writeAndFlush(reqMsg);
        notify();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public  synchronized  Object call() throws Exception {
        context.writeAndFlush(reqMsg);
        //将线程置于等待状态
        wait();
        return respMsg;
    }
}
