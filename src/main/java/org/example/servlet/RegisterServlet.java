package org.example.servlet;

import org.example.dao.UserDAO;
import org.example.exception.AppException;
import org.example.model.Response;
import org.example.model.User;
import org.example.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *用户注册类
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    static class Request{
        public String name;
        public String password;
        public String nickName;
        public String signature;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        Response response = new Response();
        try {
            Request request = Util.deserialize(req.getInputStream(), Request.class);
            UserDAO userDAO = new UserDAO();
            User existUser = userDAO.queryByName(request.name);
            if (existUser != null) {
                throw new AppException("用户已存在！");
            } else {
                User u = new User();
                u.setName(request.name);
                u.setPassword(request.password);
                u.setNickName(request.nickName);
                u.setSignature(request.signature);
                userDAO.addUser(u);
                response.setOk(true);
                response.setReason("用户注册成功！");
            }
        } catch (Exception e) {
            response.setOk(false);
            response.setReason(e.getMessage());
        } finally {
            resp.getWriter().println(Util.serialize(response));
        }
    }
}
