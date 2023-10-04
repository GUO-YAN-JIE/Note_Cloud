package com.yjxxt.note.filter;

import cn.hutool.core.util.StrUtil;
import com.yjxxt.note.util.DBUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@WebFilter("/*")
public class EncodeingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //处理POST请求(GET不受影响）
        request.setCharacterEncoding("UTF-8");
        //得到请求类型（GET|POST）
        String method = request.getMethod();
        //处理GET请求，判断tomcat版本
        if("GET".equalsIgnoreCase(method)){//忽略大小写比较
            //得到服务器版本
            String serverInfo = request.getServletContext().getServerInfo();
            //截取字符串，得到具体的版本号
            String version = serverInfo.substring(serverInfo.lastIndexOf("/")+1,serverInfo.indexOf("."));
            if(version!=null && Integer.parseInt(version)<8){
                MyMapper myrequest = new MyMapper(request);
                //放行资源
                filterChain.doFilter(request,response);
                return;
            }
        }
        //放行
        filterChain.doFilter(request,response);
    }
    /*
    在内部类中修改编码格式
    1.定义内部类（类的本质是request对象）
    2.继承HttpServletRequestWrapper包装类
    3.重写getParameter方法
     */
    class MyMapper extends HttpServletRequestWrapper{
        //定义成员变量HttpServletRequest对象（提升构造器中request对象的作用域）
        private HttpServletRequest request;
        //带参构造，可以得到需要处理的request对象
        public MyMapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public String getParameter(String name) {
            //获取参数（乱码的参数值）
            String value = request.getParameter(name);
            //判断参数值是否为空
            if (StrUtil.isBlank(value)) {
                return value;
            }
            try {
                value = new String(value.getBytes("ISO-8859-1"), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return value;
        }
        }
}
