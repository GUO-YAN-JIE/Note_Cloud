package com.yjxxt.note.web;

import com.yjxxt.note.po.Note;
import com.yjxxt.note.po.User;
import com.yjxxt.note.service.NoteService;
import com.yjxxt.note.util.JSONUtil;
import com.yjxxt.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    private NoteService noteService = new NoteService();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置首页导航栏的高亮值
        req.setAttribute("menu_page","report");
        //得到用户行为
        String actionName = req.getParameter("actionName");
        //判断用户行为
        if ("info".equals(actionName)){
            //进入报表页面
            reportInfo(req,resp);
        }else if ("month".equals(actionName)){
            queryNoteCountByMonth(req,resp);
        }else if ("location".equals(actionName)){
            queryNoteLonAndLat(req,resp);
        }
    }

    private void queryNoteLonAndLat(HttpServletRequest req, HttpServletResponse resp) {
        User user = (User) req.getSession().getAttribute("user");
        //ajax
        ResultInfo<List<Note>> resultInfo = noteService.queryNoteLonAndLat(user.getUserId());
        JSONUtil.toJSON(resp,resultInfo);
    }

    //通过月份查询对应的云记数量
    private void queryNoteCountByMonth(HttpServletRequest req, HttpServletResponse resp) {
//获取userId
        User user = (User) req.getSession().getAttribute("user");
        //ajax
        ResultInfo<Map<String,Object>> resultInfo = noteService.queryNoteCountByMonth(user.getUserId());
        JSONUtil.toJSON(resp,resultInfo);

    }

    //进入报表页面
    private void reportInfo(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    //设置动态包含的页面值
        req.setAttribute("changePage","report/info.jsp");
        //请求转发跳转到index.jsp
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }
}
