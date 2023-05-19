package top.crazy.server;

import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @BelongsProject :netty_rpc
 * @BelongsPackage :top.crazy.server
 * @Author: crazyPang
 * @CreateTime: 2023-05-18  20:52
 * @Description: TODO
 */
@Component
public class MyDispatcherServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        new HttpServerHandler().handle(req,resp);
    }
}
