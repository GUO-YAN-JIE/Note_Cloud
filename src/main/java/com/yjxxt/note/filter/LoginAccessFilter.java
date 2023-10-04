package com.yjxxt.note.filter;

import com.yjxxt.note.po.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/*
非法访问拦截
 */
@WebFilter("/*")
public class LoginAccessFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取访问路径
        String path = request.getRequestURI();//格式：项目路径/资源路径
        //放行登录页面
        if (path.contains("/login.jsp")){
            filterChain.doFilter(request,response);
            return;
        }
        //放行静态资源
        if (path.contains("/statics")){
            filterChain.doFilter(request,response);
            return;
        }
        //放行指定行为（如登录操作actionName=login）
        if (path.contains("/user")){
            String actionName = request.getParameter("actionName");
            if("login".equals(actionName)){
                filterChain.doFilter(request,response);
                return;
            }
        }
        //登录状态（判断session作用域里是否存在user对象）
        User user = (User) request.getSession().getAttribute("user");
        //判断user是否为空
        if(user!=null){
            filterChain.doFilter(request,response);
            return;
        }
        /*
        免登录
         */
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for (Cookie co:cookies){
                if("user".equals(co.getName())){
                    String value = co.getValue();//admin-123456
                    //将字符串分割成数组
                    String[] val = value.split("-");
                    String uname = val[0];
                    String upwd = val[1];
                    //请求转发到url
                    String url = "user?actionName=login&rem=1&uname="+uname+"&upwd="+upwd;
                    request.getRequestDispatcher(url).forward(request,response);
                    return;
                }
            }
        }
        //拦截请求，重定向跳转到登录页面
        response.sendRedirect("login.jsp");
    }

    @Override
    public void destroy() {

    }
}
