package top.crazy.handler;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import top.crazy.anno.RpcService;
import top.crazy.common.*;
import top.crazy.server.NettyRegisterClient;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.handler
 * @Author: crazyPang
 * @CreateTime: 2023-05-14  13:00
 * @Description: TODO
 */

@Component
@Data
@ChannelHandler.Sharable//设置通道共享
public class NettyServerHandler extends SimpleChannelInboundHandler<String>  {


    @Value("${rpc.provider.host}")
    public String host;

    @Value("${rpc.provider.port}")
    public String port;

    @Value("${rpc.hostname}")
    public String hostName;

    /**
     * @description: 接受客户端请求 并重新写回去
     * @author: crazyPang
     * @date: 2023/5/14 15:46
     * @param: [channelHandlerContext, s]
     * @return: void
    **/
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        //请求对象
        Invocation invocation = JSON.parseObject(s, Invocation.class);
        RpcResult rpcResult = new RpcResult();
        rpcResult.setReqId(invocation.getReqId());
        try {
            Object result = handler(invocation);
            String resultJson = JSON.toJSONString(result);
            rpcResult.setResult(resultJson);
        }catch (Exception e){
            e.printStackTrace();
            rpcResult.setMessage(e.getMessage());
            rpcResult.setCode(400);
        }
        rpcResult.setMessage("成功");
        rpcResult.setCode(200);
        channelHandlerContext.writeAndFlush(JSON.toJSONString(rpcResult));
    }



    /**
     * @description: 对于收到的请求 处理invocation
     * @author: crazyPang
     * @date: 2023/5/16 20:03
     * @param: [invocation]
     * @return: java.lang.Object
    **/
    public Object handler(Invocation invocation) throws InvocationTargetException {
        Object serviceBean = SERVICE_INSTANCE_MAP.get(invocation.getInterfaceName());
        if(serviceBean==null){
            throw new RuntimeException("服务端没有找到服务");
        }
        //反射执行
        FastClass proxyClass = FastClass.create(serviceBean.getClass());
        FastMethod method = proxyClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
        return method.invoke(serviceBean,invocation.getParameters());
    }


    /**
     * @description: 服务与代理对象的map 作为缓存
     * @author: crazyPang
     * @date: 2023/5/16 20:02
    **/
    public static Map<String,Object> SERVICE_INSTANCE_MAP =  new HashMap<>();


}
