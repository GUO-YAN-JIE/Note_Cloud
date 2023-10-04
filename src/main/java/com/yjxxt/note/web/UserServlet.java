package com.yjxxt.note.web;

import com.yjxxt.note.po.User;
import com.yjxxt.note.service.UserService;
import com.yjxxt.note.util.DBUtil;
import com.yjxxt.note.vo.ResultInfo;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/*
*
1. 获取参数 （姓名、密码）
2. 调用Service层的方法，返回ResultInfo对象
3. 判断是否登录成功
        如果失败
            将resultInfo对象设置到request作用域中
            请求转发跳转到登录页面
        如果成功
            将用户信息设置到session作用域中
            判断用户是否选择记住密码（rem的值是1）
* */
@WebServlet("/user")
@MultipartConfig
public class UserServlet extends HttpServlet {
    private UserService userService = new UserService();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置首页导航高亮
        req.setAttribute("menu_page","user");
        String actionName = req.getParameter("actionName");
        if("login".equals(actionName)){
            //用户登录
            userLogin(req,resp);
        }else if("logout".equals(actionName)){
            //用户退出
            userLogout(req,resp);
        } else if ("userCenter".equals(actionName)) {
            userCenter(req,resp);
        } else if ("userHead".equals(actionName)) {
            userHead(req,resp);
        } else if ("checkNick".equals(actionName)) {
            checkNick(req,resp);
        } else if ("updateUser".equals(actionName)) {
            updateUser(req,resp);
        }

    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //调用Service层的方法，传递request对象作为参数，返回resultInfo对象
        ResultInfo<User> resultInfo = userService.updateUser(req);
        req.setAttribute("resultInfo",resultInfo);
        req.getRequestDispatcher("user?actionName=userCenter").forward(req,resp);
    }

    private void checkNick(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //验证昵称唯一性
        String nick = req.getParameter("nick");
        User user = (User) req.getSession().getAttribute("user");
        Integer code = userService.checkNick(nick,user.getUserId());
        System.out.println(code);
        resp.getWriter().write(code+"");
        resp.getWriter().close();
    }

    private void userHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String head = req.getParameter("imageName");
        String path = req.getServletContext().getRealPath("/WEB-INF/upload/");
        File file = new File(path+"/"+head);
        String pic = head.substring(head.lastIndexOf(".")+1);
        //通过不同图片后缀，设置不同响应类型
        if("png".equalsIgnoreCase(pic)){
            resp.setContentType("/image/png");
        } else if ("jpg".equalsIgnoreCase(pic)||"jpeg".equalsIgnoreCase(pic)) {
            resp.setContentType("/image/jpg");
        } else if ("gif".equalsIgnoreCase(pic)) {
            resp.setContentType("/image/gif");
        }
        //将图片拷贝给浏览器
        FileUtils.copyFile(file,resp.getOutputStream());
    }

    private void userCenter(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("changePage","user/info.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }

    private void userLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //销毁session对象
        req.getSession().invalidate();
        //删除cookie对象
        Cookie cookie = new Cookie("user",null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        //重定向跳转到登录页面
        resp.sendRedirect("login.jsp");
    }

    private void userLogin(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
        String uname = request.getParameter("uname");
        String upwd = request.getParameter("upwd");

        ResultInfo<User> resultInfo = userService.UserLogin(uname,upwd);
        //判断是否登录成功
        if(resultInfo.getCode()==1){
            request.getSession().setAttribute("user",resultInfo.getResult());
            //判断用户是否记住密码
            String rem = request.getParameter("rem");
            if("1".equals(rem)){
                Cookie cookie = new Cookie("user",uname+"-"+upwd);
                cookie.setMaxAge(3*24*60*60);
                //响应给客户端
                response.addCookie(cookie);
            }else {
                Cookie cookie = new Cookie("user",null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
            response.sendRedirect("index");
        }else {
            request.setAttribute("resultInfo",resultInfo);
            request.getRequestDispatcher("login.jsp").forward(request,response);
        }
    }
}
