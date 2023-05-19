package top.crazy.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import top.crazy.common.Invocation;
import top.crazy.handler.NettyServerHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.server
 * @Author: crazyPang
 * @CreateTime: 2023-05-18  20:53
 * @Description: TODO
 */

@Slf4j
public class HttpServerHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

    public void handle(HttpServletRequest req, HttpServletResponse resp){
        try {
            Invocation invocation = (Invocation) new ObjectInputStream(req.getInputStream()).readObject();
            //todo 处理请求
            Object serviceBean = NettyServerHandler.SERVICE_INSTANCE_MAP.get(invocation.getInterfaceName());
            if(serviceBean==null){
                logger.error("服务端没有找到服务 sername{}",invocation.getInterfaceName());
                throw new RuntimeException("服务端没有找到服务 invocation"+invocation.toString());
            }
            //反射执行
            FastClass proxyClass = FastClass.create(serviceBean.getClass());
            FastMethod method = proxyClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            String result = null;
            result = (String) method.invoke(serviceBean, invocation.getParameters());
            IOUtils.write(result,resp.getOutputStream());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    };
}
