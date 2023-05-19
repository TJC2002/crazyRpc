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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import org.springframework.stereotype.Component;
import top.crazy.handler.NettyPrcClientHandler;

import java.util.concurrent.*;


/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.server
 * @Author: crazyPang
 * @CreateTime: 2023-05-14  16:33
 * @Description: 只有在 mode-registration == false 时候启动
 */


@Component
@ConditionalOnProperty(
        prefix = "rpc",
        name = "mode-registration",
        havingValue = "false",
        matchIfMissing = false
)
public class NettyPrcClient {

    Logger log= LoggerFactory.getLogger(NettyPrcClient.class);

    static EventLoopGroup loopGroup = new NioEventLoopGroup();

    ExecutorService executorService = Executors.newCachedThreadPool();

    static Bootstrap bootstrap;

    @Value("${rpc.mode-registration}")
    boolean modeRegistration;

    @Autowired
    NettyPrcClientHandler nettyClientHandler;
    /**
     * @description:
     * @author: crazyPang
     * @date: 2023/5/19 13:38
     * @param: [consumerHost, consumerPort]
     * @return: void
    **/
    public void start(String ip,Integer port) throws Exception {
        if(modeRegistration){
            return;
        }
        Bootstrap bootstrap= new Bootstrap();
        bootstrap.group(loopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //添加http编码解码器
                        socketChannel.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(nettyClientHandler);
                    }
                });

        try {
//            Channel channel = bootstrap.connect(consumerHost,consumerPort).sync().channel();
//            System.out.println("消费者服务器连接成功");
//            this.bootstrap = bootstrap;
//            channel.closeFuture().sync();
//            group.shutdownGracefully();
            ChannelFuture future = connect(ip, port);
            log.debug("NettyPrcClient 启动成功 ip:{},port:{}",ip,port);
        }catch (Exception e){
            loopGroup.shutdownGracefully();
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    public Object send(String msg)throws InterruptedException , ExecutionException {
        nettyClientHandler.setReqMsg(msg);
        Future submit = executorService.submit(nettyClientHandler);
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
