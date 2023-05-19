package top.crazy.rpcClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import top.crazy.handler.NettyRegistrationClientHandler;

import java.util.concurrent.*;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.rpcClient
 * @Author: crazyPang
 * @CreateTime: 2023-05-18  21:40
 * @Description: TODO
 */

@Component
@ConditionalOnProperty(
        prefix = "rpc",
        name = "mode-registration",
        havingValue = "true",
        matchIfMissing = false
)
public class NettyRegistrationClient  {

    Logger log = LoggerFactory.getLogger(NettyPrcClient.class);
    @Autowired
    NettyRegistrationClientHandler nettyRegistrationClientHandler;

    ExecutorService executorService = Executors.newCachedThreadPool();

    static Bootstrap bootstrap;

    @Value("${rpc.mode-registration}")
    boolean modeRegistration;

    /**
     * @description:连接注册中心
     * @author: crazyPang
     * @date: l 15:56
     **/

    public void start(String ip,Integer port) throws Exception {
        if(!modeRegistration){
            return;
        }
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap= new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //添加http编码解码器
                        socketChannel.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(nettyRegistrationClientHandler);
                    }
                });
        try {
            this.bootstrap= bootstrap;
            ChannelFuture future = connect(ip, port);
            System.out.println("消费者启动成功,注册中心连接成功 host："+ip+" port："+port);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public Object pullRegistrationHostNode(String msg)throws InterruptedException , ExecutionException {
        nettyRegistrationClientHandler.setReqMsg(msg);
        Future submit = executorService.submit(nettyRegistrationClientHandler);
        Object o = submit.get();
        return o;
    }


    public static ChannelFuture connect(String ip, Integer port) {
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        ChannelFuture channelFuture = bootstrap.connect(ip, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    System.out.println(Thread.currentThread().getName() + ">>>>>连接" + ip + ":" + port + "服务端超时，1秒后重试……");
                    Thread.sleep(1000);
                    connect(ip, port);
                }
            }
        });
        return channelFuture;
    }
}
