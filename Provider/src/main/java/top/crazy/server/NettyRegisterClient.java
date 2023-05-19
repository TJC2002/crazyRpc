package top.crazy.server;

import com.alibaba.fastjson.JSON;
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
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import top.crazy.anno.RpcService;
import top.crazy.common.RegisterMessage;
import top.crazy.common.RegisterMessageType;
import top.crazy.common.RegisterRespMessage;
import top.crazy.common.RegisterRespMessageType;
import top.crazy.handler.NettyRegisterClientHandler;
import top.crazy.handler.NettyServerHandler;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.server
 * @Author: crazyPang
 * @CreateTime: 2023-05-17  00:46
 * @Description: TODO
 */

@Component
public class NettyRegisterClient  {

    Logger log= LoggerFactory.getLogger(NettyRegisterClient.class);

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    NettyServerHandler nettyServerHandler;


    @Value("${rpc.provider.http.host}")
    public String host;

    @Value("${rpc.provider.http.port}")
    public String port;

    @Value("${rpc.provider.loadbalance.weight}")
    public Integer weight;


    @Value("${rpc.hostname}")
    public String hostName;
    ExecutorService executorService = Executors.newCachedThreadPool();
    private Bootstrap bs;
    @Autowired
    private NettyRegisterClientHandler nettyRegisterClientHandler;

    @Value("${rpc.registration.host}")
    String registrationHost;
    @Value("${rpc.mode-registration}")
    boolean modeRegistration;
    @Value("${rpc.registration.port}")
    Integer registrationPort;

    /**
     * @description:连接服务端
     * @author: crazyPang
     * @date: l 15:56
     * @param: []
     * @return: void
     **/

    public void start()  {
        EventLoopGroup group = new NioEventLoopGroup(2);
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
                                .addLast(nettyRegisterClientHandler);
                    }
                });
        try {

            System.out.println("开启tomcat服务器 hostname"+registrationHost+" port"+registrationPort);
            Channel channel = bootstrap.connect(registrationHost,registrationPort).sync().channel();
            System.out.println("RegisterClient启动成功");
            if(modeRegistration){
                initLocalService();
            }
            else
                initLocalService();
            this.bs = bootstrap;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reconnect(String host, Integer port) {
        bs.remoteAddress(host, port);
        ChannelFuture channelFuture = bs.connect();
        //使用最新的ChannelFuture -> 开启最新的监听器
        channelFuture.addListener((ChannelFutureListener) future -> {
            if (future.cause() != null) {
                log.error(host + ":" + port + "连接失败。。。");
                future.channel().eventLoop().schedule(() -> reconnect(host,port), 3, TimeUnit.SECONDS);
            } else {
                log.info(host + ":" + port + "客户端连接成功。。。");
            }
        });
    }


    public Object send(String msg)throws InterruptedException , ExecutionException {
        nettyRegisterClientHandler.setReqMsg(msg);
        Future submit = executorService.submit(nettyRegisterClientHandler);
        Object o = submit.get();
        return o;
    }

    public  void initLocalService(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        Set<Map.Entry<String, Object>> entries = serviceMap.entrySet();
        for (Map.Entry<String, Object> entry:entries){
            Object serviceBean = entry.getValue();
            if(serviceBean.getClass().getInterfaces().length==0){
                throw new RuntimeException("对外暴露的服务必须实现接口kou");
            }
            Class<?> anInterface = serviceBean.getClass().getInterfaces()[0];
            NettyServerHandler.SERVICE_INSTANCE_MAP.put(anInterface.getSimpleName(),serviceBean);

            //todo 在这里发送 register
            RegisterMessage registerMessage = new RegisterMessage();
            registerMessage.setHost(host);
            registerMessage.setPort(port);
            registerMessage.setHostName(hostName);
            registerMessage.setLoadBalanceWeight(weight);
            registerMessage.setServiceName(anInterface.getSimpleName());
            registerMessage.setRegisterMessageType(RegisterMessageType.REGISTER_HOST_NODE.getType());
            try {
                Object send = this.send(JSON.toJSONString(registerMessage));
                RegisterRespMessage registerRespMessage = JSON.parseObject(send.toString(), RegisterRespMessage.class);
                switch (RegisterRespMessageType.match(registerRespMessage.getType())){
                    case SUCCESS:
                        //todo
                        System.out.println("register service "+anInterface.getSimpleName()+"an respMessage is "+registerRespMessage.getMessage());
                        break;
                    case UN_KNOW_WRONG:
                        //todo
                        System.out.println("register wrong  "+anInterface.getSimpleName()+"an respMessage is "+registerRespMessage.getMessage());
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    public  void initLocalController(){
        System.out.println("ApplicationAware"+ LocalDate.now());
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(Controller.class);
        Set<Map.Entry<String, Object>> entries = serviceMap.entrySet();
        for (Map.Entry<String, Object> entry:entries){
            Object serviceBean = entry.getValue();
//            System.out.println(serviceBean.getClass().getSimpleName());
//            if(serviceBean.getClass().getInterfaces().length==0){
//                throw new RuntimeException("对外暴露的服务必须实现接口kou");
//            }
//            Class<?> anInterface = serviceBean.getClass().getInterfaces()[0];
//            NettyServerHandler.SERVICE_INSTANCE_MAP.put(anInterface.getSimpleName(),serviceBean);

            //todo 在这里发送 register
            RegisterMessage registerMessage = new RegisterMessage();
            registerMessage.setHost(host);
            registerMessage.setPort(port);
            registerMessage.setHostName(hostName);
            registerMessage.setServiceName(serviceBean.getClass().getSimpleName());
            registerMessage.setRegisterMessageType(RegisterMessageType.REGISTER_HOST_NODE.getType());
            try {
                Object send = this.send(JSON.toJSONString(registerMessage));
                RegisterRespMessage registerRespMessage = JSON.parseObject(send.toString(), RegisterRespMessage.class);
                switch (RegisterRespMessageType.match(registerRespMessage.getType())){
                    case SUCCESS:
                        //todo
                        System.out.println("register service "+serviceBean.getClass().getSimpleName()+"an respMessage is "+registerRespMessage.getMessage());
                        break;
                    case UN_KNOW_WRONG:
                        //todo
                        System.out.println("register wrong  "+serviceBean.getClass().getSimpleName()+"an respMessage is "+registerRespMessage.getMessage());
                        break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }


}
