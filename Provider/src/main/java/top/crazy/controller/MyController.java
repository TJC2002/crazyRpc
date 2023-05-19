package top.crazy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import top.crazy.anno.RpcController;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.controller
 * @Author: crazyPang
 * @CreateTime: 2023-05-18  13:33
 * @Description: TODO
 */

@Controller
@RequestMapping("/abc")
@RpcController()
public class MyController {
    @RequestMapping("/request")
    public String myFunction(){
        return "myfunction";
    }
}
