package com.example.netty_rpc;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @BelongsProject :cloud-demo
 * @BelongsPackage :im
 * @Author: crazyPang
 * @CreateTime: 2023-05-06  11:11
 * @Description: todo 忘记了 待会删
 */

@Component
public class ImServer {
//    public static void main(String[] args) {
//        start();
//    }

    public static ConfigurableApplicationContext appContext;
    public static final Map<String,Channel> USERS = new ConcurrentHashMap<>(1024);
    public static final ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void start(ConfigurableApplicationContext applicationContext){
        appContext = applicationContext;
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap  bootstrap= new ServerBootstrap();
        bootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //添加http编码解码器
                        socketChannel.pipeline().addLast(new HttpServerCodec())
                                //支持大数据流
                                .addLast(new ChunkedWriteHandler())
                                //http 请求聚合
                                .addLast(new HttpObjectAggregator(1024*64))
                                //为websocket做
                                .addLast(new WebSocketServerProtocolHandler("/"));
                                //

                    }
                });
        ChannelFuture future = bootstrap.bind(19990);

    }
}
