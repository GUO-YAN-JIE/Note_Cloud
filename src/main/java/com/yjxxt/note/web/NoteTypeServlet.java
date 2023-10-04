package com.yjxxt.note.web;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.yjxxt.note.po.NoteType;
import com.yjxxt.note.po.User;
import com.yjxxt.note.service.NoteTypeService;
import com.yjxxt.note.util.JSONUtil;
import com.yjxxt.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/type")
public class NoteTypeServlet extends HttpServlet {
    private NoteTypeService typeService = new NoteTypeService();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("menu_page","type");
        //得到用户行为判断
        String actionName = req.getParameter("actionName");
        if ("list".equals(actionName)){
            //查询类型列表
            typeList(req,resp);
        } else if ("delete".equals(actionName)) {
            deleteType(req,resp);
        } else if ("addOrUpdate".equals(actionName)) {
            addOrUpdate(req,resp);
        }
    }

    private void addOrUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String typeName = req.getParameter("typeName");
        String typeId = req.getParameter("typeId");
        //获取userId
        User user = (User) req.getSession().getAttribute("user");
        ResultInfo<Integer> resultInfo = typeService.addOrUpdate(typeName,user.getUserId(),typeId);
        //JSON
        JSONUtil.toJSON(resp,resultInfo);

    }

    private void deleteType(HttpServletRequest req, HttpServletResponse resp) {
        String typeId = req.getParameter("typeId");
        ResultInfo<NoteType> resultInfo = typeService.deleteType(typeId);
        //将resultInfo对象转换成JSON格式的字符串，响应给ajax函数
        JSONUtil.toJSON(resp,resultInfo);
    }

    private void typeList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置首页导航高亮
        req.setAttribute("menu_page","type");
        User user = (User) req.getSession().getAttribute("user");
        List<NoteType> typeList = typeService.findTypeList(user.getUserId());
        req.setAttribute("typeList",typeList);
        //设置首页动态包含的页面值
        req.setAttribute("changePage","type/list.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }
}
