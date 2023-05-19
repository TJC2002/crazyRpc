package top.crazy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import top.crazy.anno.RpcReference;
import top.crazy.service.MyService;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.controller
 * @Author: crazyPang
 * @CreateTime: 2023-05-15  19:20
 * @Description: TODO
 */

@Controller
public class UserController {

    @RpcReference
    MyService myService;

    @ResponseBody
    @RequestMapping("/abc")
    public String function1(){
        return myService.function1("abc test1");
    }
    @ResponseBody
    @RequestMapping("/abc123")
    public String function2(){
        return "sdfdsf";
    }

}
