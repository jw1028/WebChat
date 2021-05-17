package org.example.servlet;

import org.example.model.Response;
import org.example.model.User;
import org.example.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        HttpSession session = req.getSession(false);
        if(session != null) {
            User user = (User) session.getAttribute("user");
            if(user != null) {
                //用户已登录:删除session中保存的用户信息（注销）
                session.removeAttribute("user");
                Response r = new Response();
                r.setOk(true);
                //序列化 返回对象转json格式
                resp.getWriter().println(Util.serialize(r));
                return ;
            }
            //用户未登录
            Response r = new Response();
            r.setReason("用户未登录,不允许操作!");
            resp.getWriter().println(Util.serialize(r));

        }
    }
}
