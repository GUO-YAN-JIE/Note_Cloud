package com.yjxxt.note.service;

import cn.hutool.core.util.StrUtil;
import com.yjxxt.note.dao.NoteDao;
import com.yjxxt.note.po.Note;
import com.yjxxt.note.util.Page;
import com.yjxxt.note.vo.NoteVo;
import com.yjxxt.note.vo.ResultInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteService {
    private NoteDao noteDao = new NoteDao();

    public ResultInfo<Note> addOrUpdate(String typeId, String title, String content, String noteId,String lon, String lat) {
        ResultInfo<Note> resultInfo= new ResultInfo<>();

        if(StrUtil.isBlank(typeId)){
            resultInfo.setCode(0);
            resultInfo.setMsg("请选择云记类型！");
            return resultInfo;
        }
        if(StrUtil.isBlank(title)){
            resultInfo.setCode(0);
            resultInfo.setMsg("云记标题不能为空");
            return resultInfo;
        }
        if(StrUtil.isBlank(content)){
            resultInfo.setCode(0);
            resultInfo.setMsg("云记内容不能为空");
            return resultInfo;
        }
        //设置经纬度默认值为北京
        if (lon==null ||"".equals(lon.trim())|| lat ==null||"".equals(lat.trim())){
            lon = "116.404";
            lat = "39.915";
        }
        //设置 note回显对象
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setTypeId(Integer.parseInt(typeId));
        note.setLon(Float.parseFloat(lon));
        note.setLat(Float.parseFloat(lat));
        //判断云记Id是否为空
        if (!StrUtil.isBlank(noteId)){
            note.setNoteId(Integer.parseInt(noteId));
        }
        resultInfo.setResult(note);
        //调用Dao层，添加云记记录，返回受影响的行数
        int row = noteDao.addOrUpdate(note);
        if (row>0){
            resultInfo.setCode(1);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("发布云记失败！");
            resultInfo.setResult(note);
        }
        return resultInfo;
    }

    public Page<Note> findNoteListByPage(String pageNumStr, String pageSizeStr, Integer userId, String title, String date, String typeId) {
        Integer pageNum = 1;//默认是第一页
        Integer pageSize = 5;//默认每页显示5条
        //非空校验，如果分页参数不为空，则设置该值
        if (!StrUtil.isBlank(pageNumStr)){
            pageNum = Integer.parseInt(pageNumStr);
        }
        if (!StrUtil.isBlank(pageSizeStr)){
            pageSize = Integer.parseInt(pageSizeStr);
        }
        //查询当前登陆用户的云记数量
        Long totalCount = noteDao.findNoteCount(userId,title,date,typeId);
        if (totalCount<1){
            return null;
        }
        Page<Note> page = new Page<>(pageNum,pageSize,totalCount);
        //得到数据库中分页查询的开始下标
        Integer index = (pageNum-1)*pageSize;
        //查询当前登陆用户下当前页面的数据列表，返回note集合
        List<Note> notelist = noteDao.findNoteListByPage(userId,index,pageSize,title,date,typeId);
        //将note集合设置到page对象中
        page.setDataList(notelist);
        return page;
    }

    public List<NoteVo> findNoteCountByDate(Integer userId) {
        return noteDao.findNoteCountByDate(userId);
    }

    public List<NoteVo> findNoteCountByType(Integer userId) {
        return noteDao.findNoteCountByType(userId);
    }

    public Note noteDetail(String noteId) {
        // 1. 参数的非空判断
        if (StrUtil.isBlank(noteId)){
            return  null;
        }
        // 2. 调用Dao层的查询，通过noteId查询note对象
        Note note = noteDao.noteDetail(noteId);
        // 3. 返回note对象
        return note;
    }

    public Integer deleteNote(String noteId) {
        if (StrUtil.isBlank(noteId)){
            return 0;
        }
        int rows = noteDao.deleteNoteById(noteId);
        if (rows>0){
            return 1;
        }else {
            return 0;
        }
    }


    public ResultInfo<Map<String, Object>> queryNoteCountByMonth(Integer userId) {
        ResultInfo<Map<String,Object>> resultInfo = new ResultInfo<>();
        //通过月份分类查询云记数量
        List<NoteVo> noteVos = noteDao.findNoteCountByDate(userId);
        // 判断集合是否存在
        if (noteVos != null && noteVos.size() > 0) {
            // 得到月份
            List<String> monthList = new ArrayList<>();
            // 得到云记集合
            List<Integer> noteCountList = new ArrayList<>();

            // 遍历月份分组集合
            for (NoteVo noteVo: noteVos) {
                monthList.add(noteVo.getGroupName());
                noteCountList.add((int)noteVo.getNoteCount());
            }

            // 准备Map对象，封装对应的月份与云记数量
            Map<String, Object> map = new HashMap<>();
            map.put("monthArray", monthList);
            map.put("dataArray", noteCountList);

            // 将map对象设置到ResultInfo对象中
            resultInfo.setCode(1);
            resultInfo.setResult(map);
        }
        return resultInfo;
    }

    public ResultInfo<List<Note>> queryNoteLonAndLat(Integer userId) {
        ResultInfo<List<Note>> resultInfo = new ResultInfo<>();
        //通过用户ID查询云记列表
        List<Note> noteList = noteDao.queryNoteList(userId);
        if (noteList != null && noteList.size() > 0) {
            resultInfo.setCode(1);
            resultInfo.setResult(noteList);
        }
        return resultInfo;
    }
}
