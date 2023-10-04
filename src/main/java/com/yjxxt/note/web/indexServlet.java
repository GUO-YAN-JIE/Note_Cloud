package com.yjxxt.note.web;

import com.yjxxt.note.po.Note;
import com.yjxxt.note.po.User;
import com.yjxxt.note.service.NoteService;
import com.yjxxt.note.util.Page;
import com.yjxxt.note.vo.NoteVo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/index")
public class indexServlet extends HttpServlet{
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("menu_page","index");
        //得到用户行为
        String actionName = req.getParameter("actionName");
        //将用户行为设置到作用域中（为了条件查询时也能做到分页查询，分页导航中需要获取）
        req.setAttribute("actionName",actionName);
        //判断用户行为
        if("searchTitle".equals(actionName)){
            //得到查询条件：标题
            String title = new String(req.getParameter("title").getBytes("ISO-8859-1"), "UTF-8");
            // 将查询条件设置到request请求域中（查询条件的回显）
            req.setAttribute("title", title);
            //标题查询
            noteList(req,resp,title,null,null);
        }else if ("searchDate".equals(actionName)){//日期查询
            //得到查询条件：日期
            String date = new String(req.getParameter("date").getBytes("ISO-8859-1"), "UTF-8");
            // 将查询条件设置到request请求域中（查询条件的回显）
            req.setAttribute("date", date);
            noteList(req,resp,null,date,null);
        }else if ("searchType".equals(actionName)){//类型查询
            //得到查询条件：类型
            String typeId = req.getParameter("typeId");
            // 将查询条件设置到request请求域中（查询条件的回显）
            req.setAttribute("typeId", typeId);
            noteList(req,resp,null,null,typeId);
        }
        else {
            //不做条件查询
            //分页查询云记列表
            noteList(req,resp,null,null,null);
        }
        //设置首页动态包含的页面值
        req.setAttribute("changePage","note/list.jsp");
        //请求转发跳转到index.jsp
        req.getRequestDispatcher("index.jsp").forward(req,resp);
        //得到用户行为（判断是什么条件查询：标题查询、日期查询、类型查询）
    }

    private void noteList(HttpServletRequest req, HttpServletResponse resp, String title, String date, String typeId) {
        String pageNum = req.getParameter("pageNum");
        String pageSize = req.getParameter("pageSize");
        User user = (User) req.getSession().getAttribute("user");
        Page<Note> page = new NoteService().findNoteListByPage(pageNum,pageSize,user.getUserId(),title,date,typeId);
        req.setAttribute("page",page);
        //通过日期分组查询当前登录用户下的云记数量
        List<NoteVo> dateInfo = new NoteService().findNoteCountByDate(user.getUserId());
        //设置集合存放在session作用域中
        req.getSession().setAttribute("dateInfo",dateInfo);
        //通过类型分组查询当前登录用户下的云记数量
        List<NoteVo> typeInfo = new NoteService().findNoteCountByType(user.getUserId());
        //设置集合存放在session作用域中
        req.getSession().setAttribute("typeInfo",typeInfo);
    }
}
