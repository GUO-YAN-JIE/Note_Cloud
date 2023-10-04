package com.yjxxt.note.web;

import cn.hutool.core.util.StrUtil;
import com.yjxxt.note.po.Note;
import com.yjxxt.note.po.NoteType;
import com.yjxxt.note.po.User;
import com.yjxxt.note.service.NoteService;
import com.yjxxt.note.service.NoteTypeService;
import com.yjxxt.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/note")
public class NoteServlet extends HttpServlet {
    private NoteService noteService = new NoteService();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置首页导航栏高亮
        req.setAttribute("menu_page","note");
        //得到用户行为
        String actionName = req.getParameter("actionName");
        if ("view".equals(actionName)){
            noteView(req,resp);
        } else if ("addOrUpdate".equals(actionName)) {
            addOrUpdate(req,resp);
        }else if ("detail".equals(actionName)) {
            noteDetail(req,resp);
        }else if ("delete".equals(actionName)) {
            noteDelete(req,resp);
        }
    }

    private void noteDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String noteId = req.getParameter("noteId");
        Integer code = noteService.deleteNote(noteId);
        resp.getWriter().write(code+"");
        resp.getWriter().close();
    }

    private void noteDetail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String noteId =req.getParameter("noteId");
        Note note = noteService.noteDetail(noteId);
        req.setAttribute("note",note);
        req.setAttribute("changePage","note/detail.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);

    }


    private void addOrUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String typeId = req.getParameter("typeId");
        String title = req.getParameter("title");
        String content = req.getParameter("content");
        //获取经纬度
        String lon = req.getParameter("lon");
        String lat = req.getParameter("lat");
        //修改操作需要接收noteId
        String noteId = req.getParameter("noteId");
        ResultInfo<Note> resultInfo = noteService.addOrUpdate(typeId,title,content,noteId,lon,lat);
        if (resultInfo.getCode()==1){
            resp.sendRedirect("index.jsp");
        }else {
            req.setAttribute("resultInfo",resultInfo);
            //请求转发跳转到
            String url = "note?actionName=view";
            //如果是修改操作，需要传递noteId
            if (!StrUtil.isBlank(noteId)){
                url+="&noteId="+noteId;
            }
            req.getRequestDispatcher(url).forward(req,resp);
        }
    }

    private void noteView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //修改
        //得到要修改的云记ID
        String noteId = req.getParameter("noteId");
        //通过noteId查询云记对象
        Note note = noteService.noteDetail(noteId);
        //将note对象设置到请求域中
        req.setAttribute("noteInfo",note);


        //获取用户id对应的类型列表
        User user = (User) req.getSession().getAttribute("user");
        List<NoteType> typeList = new NoteTypeService().findTypeList(user.getUserId());
        req.setAttribute("typeList",typeList);
        req.setAttribute("changePage","note/view.jsp");
        req.getRequestDispatcher("index.jsp").forward(req,resp);
    }
}
