package top.crazy.server;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import top.crazy.anno.RpcService;
import top.crazy.common.RegisterMessage;
import top.crazy.common.RegisterMessageType;
import top.crazy.common.RegisterRespMessage;
import top.crazy.common.RegisterRespMessageType;
import top.crazy.handler.NettyServerHandler;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.server
 * @Author: crazyPang
 * @CreateTime: 2023-05-14  16:33
 * @Description: TODO
 */

@Component
public class NettyPrcServer {

    @Autowired
    NettyServerHandler nettyServerHandler;

    @Value("${rpc.provider.port}")
    Integer port;

    @Value("${rpc.hostname}")
    public String hostName;

    public void start(){
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap bootstrap= new ServerBootstrap();
        bootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        //添加http编码解码器
                        socketChannel.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(nettyServerHandler);
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind(port).sync();

            System.out.println("Provider服务器启动成功");
//            future.channel().closeFuture().sync();
//            boss.shutdownGracefully();
//            worker.shutdownGracefully();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
