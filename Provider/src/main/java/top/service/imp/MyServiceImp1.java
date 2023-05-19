package top.service.imp;

import top.service.MyService;
import top.crazy.anno.RpcService;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :com.service.imp
 * @Author: crazyPang
 * @CreateTime: 2023-05-14  12:21
 * @Description: 接口MyService的实现类 作为 实现类
 */



@RpcService
public class MyServiceImp1 implements MyService {
    @Override
    public String function1(String a) {
        System.out.println("function 1 have been use");
        return "aaa"+a+"bbb";
    }
}
