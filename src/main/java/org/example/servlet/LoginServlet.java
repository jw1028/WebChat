package org.example.servlet;

import org.example.dao.UserDAO;
import org.example.exception.AppException;
import org.example.model.User;
import org.example.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    //检测登录状态接口：页面初始化时执行
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        //返回的数据，还是用户信息
        User u = new User();
        //获取当前请求的session，并再获取用户信息，如果获取不到，返回ok:false
        HttpSession session = req.getSession(false);
        if(session != null){
            User get = (User) session.getAttribute("user");
            if(get != null){
                //已经登录，并获取到用户信息
                u = get;
                u.setOk(true);
                resp.getWriter().println(Util.serialize(u));
                return;
            }
        }
        u.setOk(false);//其实不用设置，该字段为boolean，默认就是false
        u.setReason("用户未登陆");
        //3 返回响应数据: 从响应对象获取输出流，打印输出到响应体body
        resp.getWriter().println(Util.serialize(u));
    }

    //登录接口
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        //响应的数据：根据接口文档，user类中都包含了约定的字段
        User u = new User();
        try{
            //1 解析请求数据: 根据接口文档，需要使用反序列化操作
            User input = Util.deserialize(req.getInputStream(), User.class);
            //2 业务处理: 数据库验证账号密码，如果验证通过，创建session，保存用户信息
            //根据账号查询用户
            User query = UserDAO.queryByName(input.getName());
            if(query == null){
                throw new AppException("用户不存在");
            }
            if(!query.getPassword().equals(input.getPassword())){
                throw new AppException("账号或密码错误");
            }
            //账号密码验证成功
            HttpSession session = req.getSession();
            session.setAttribute("user", query);
            u = query;
            //构造操作成功的正常返回数据：ok:true, 业务字段
            u.setOk(true);
        }catch (Exception e){
            e.printStackTrace();
            //构造操作失败的错误信息：ok:false, reason:错误信息
            u.setOk(false);
            //自定义异常，自己抛，为中文信息，可以给用户看
            if(e instanceof AppException){
                u.setReason(e.getMessage());
            }else{//非自定义异常，英文信息，转一下
                u.setReason("未知的错误，请联系管理员");
            }
        }
        //3 返回响应数据: 从响应对象获取输出流，打印输出到响应体body
        resp.getWriter().println(Util.serialize(u));
    }
}
