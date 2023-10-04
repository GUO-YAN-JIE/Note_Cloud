package com.yjxxt.note.dao;

import cn.hutool.core.util.StrUtil;
import com.yjxxt.note.po.Note;
import com.yjxxt.note.vo.NoteVo;

import java.util.ArrayList;
import java.util.List;

public class NoteDao {
    public int addOrUpdate(Note note) {
        String sql = "";
        List<Object> params = new ArrayList<>();
        params.add(note.getTypeId());
        params.add(note.getTitle());
        params.add(note.getContent());
        //判断noteId是否为空
        if (note.getNoteId()==null){
            sql = "insert into tb_note (typeId,title,content,pubTime,lon,lat) values (?,?,?,now(),?,?)";
            params.add(note.getLon());
            params.add(note.getLat());
        }else {
            sql = "update tb_note set typeId = ?,title = ?,content = ? where noteId = ?";
            params.add(note.getNoteId());
        }
        int row = BaseDao.executeUpdate(sql,params);
        System.out.println(row);
        return row;
    }
//查询当前用户云记数量
    public Long findNoteCount(Integer userId, String title, String date, String typeId) {
        String sql = "select count(1) from tb_note n inner join tb_note_type t on n.typeId=t.typeId where userId=?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        //判断条件查询的参数是否为空
        if (!StrUtil.isBlank(title)){
            sql += " and title like concat('%',?,'%') ";
            params.add(title);
        }else if (!StrUtil.isBlank(date)){
            sql += " and DATE_FORMAT(pubTime,'%Y年%m月') = ? ";
            params.add(date);
        }else if (!StrUtil.isBlank(typeId)){//注意同名属性要写表名.typeId
            sql += " and n.typeId = ? ";
            params.add(typeId);
        }
        Long noteCount = (Long) BaseDao.findsinglevalue(sql,params);
        return noteCount;
    }

    public List<Note> findNoteListByPage(Integer userId, Integer index, Integer pageSize, String title, String date, String typeId) {
        String sql = "select noteId,title,pubTime from tb_note n join tb_note_type t on n.typeId=t.typeId where userId=? ";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        //判断条件查询的参数是否为空
        if (!StrUtil.isBlank(title)){
            sql += " and title like concat('%',?,'%') ";
            params.add(title);
        }else if (!StrUtil.isBlank(date)){
            sql += " and DATE_FORMAT(pubTime,'%Y年%m月') = ? ";
            params.add(date);
        }else if (!StrUtil.isBlank(typeId)){
            sql += " and n.typeId = ? ";
            params.add(typeId);
        }
        //拼接分页的sql语句（limit语句需要写在sql语句最后）
        sql += " order by pubTime desc limit ?,?";
        params.add(index);
        params.add(pageSize);
        List<Note> noteList = BaseDao.queryRows(sql,params,Note.class);
        return noteList;
    }

    public List<NoteVo> findNoteCountByDate(Integer userId) {
        String sql = "SELECT COUNT(1) noteCount,DATE_FORMAT(pubTime,'%Y年%m月') groupName FROM tb_note n JOIN tb_note_type t ON n.typeId=t.typeId WHERE userId=? GROUP BY DATE_FORMAT(pubTime,'%Y年%m月') ORDER BY DATE_FORMAT(pubTime,'%Y年%m月') DESC";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteVo> list = BaseDao.queryRows(sql,params, NoteVo.class);
        return list;
    }

    public List<NoteVo> findNoteCountByType(Integer userId) {
        String sql = "SELECT count(noteId) noteCount, t.typeId,t.typeName groupName FROM tb_note n RIGHT JOIN tb_note_type t ON n.typeId=t.typeId WHERE userId=? GROUP BY t.typeId ORDER BY count(noteId) DESC";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteVo> list = BaseDao.queryRows(sql,params, NoteVo.class);
        return list;
    }

    public Note noteDetail(String noteId) {
        String sql = "select noteId,title,content,pubTime,typeName,n.typeId from tb_note n " +
                " inner join tb_note_type t on n.typeId=t.typeId where noteId = ?";
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        Note note = (Note) BaseDao.queryRow(sql,params,Note.class);
        return note;
    }

    public int deleteNoteById(String noteId) {
        String sql = "delete from tb_note where noteId= ?";
        List<Object> params = new ArrayList<>();
        params.add(noteId);
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }

    public List<Note> queryNoteList(Integer userId) {
        String sql = "select lon,lat from tb_note n join tb_note_type t on n.typeId=t.typeId where userId=?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<Note> list = BaseDao.queryRows(sql,params,Note.class);
        return list;
    }
}
