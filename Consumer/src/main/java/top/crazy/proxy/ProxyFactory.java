package top.crazy.proxy;


import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.crazy.common.Invocation;
import top.crazy.common.RpcResult;
import top.crazy.rpcClient.NettyPrcClient;
import top.crazy.rpcClient.HttpRequestSend;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy
 * @Author: crazyPang
 * @CreateTime: 2023-05-14  00:46
 * @Description: 获取反射对象
 */

@Component
public class ProxyFactory {


    @Value("${rpc.mode-registration}")
    boolean modeRegistration;

    @Autowired
    HttpRequestSend requestSend;

    @Autowired(required = false)
    NettyPrcClient nettyPrcClient;

    public  Map<Class,Object> SERVICE_PROXY = new HashMap<>();
    public  Object getProxy(Class interfaceClass){
        Object proxy = SERVICE_PROXY.get(interfaceClass);
        if(proxy!=null){
            return proxy;
        }
        Object proxyInstance = Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation(interfaceClass.getSimpleName(),method.getName()
                ,method.getParameterTypes(),args);
                invocation.setReqId(UUID.randomUUID().toString());
                if(modeRegistration){
                    return requestSend.send(invocation,interfaceClass.getSimpleName());
                }else {
                    Object send = nettyPrcClient.send(JSON.toJSONString(invocation));
                    RpcResult rpcResult = JSON.parseObject(send.toString(), RpcResult.class);
                    if(rpcResult.getCode().equals("error")){
                        throw new RuntimeException("rpc is ERROR message is"+rpcResult.getMessage());
                    }
                    if(rpcResult.getResult()!=null){
                        return JSON.parseObject(rpcResult.getResult().toString(),method.getReturnType());
                    }
                }
                return null;
            }
        });
        return  proxyInstance;

    }

}
