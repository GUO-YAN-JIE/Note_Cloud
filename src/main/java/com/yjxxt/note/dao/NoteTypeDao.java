package com.yjxxt.note.dao;

import com.yjxxt.note.po.NoteType;
import com.yjxxt.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class NoteTypeDao {
    public List<NoteType> findTypeListByUserId(Integer userId){

        String sql = "select typeId,typeName,userId from tb_note_type where userId=?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        List<NoteType> list = BaseDao.queryRows(sql,params,NoteType.class);
        return list;
    }

    public long findNoteCountByTypeId(String typeId) {
        String sql = "select count(1) from tb_note where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        long count = (long) BaseDao.findsinglevalue(sql,params);
        return count;
    }

    public int deleteTypeId(String typeId) {
        String sql = "delete from tb_note_type where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeId);
        int rows = BaseDao.executeUpdate(sql,params);
        return rows;
    }
//验证类型名是否唯一（返回1表示成功，返回0表示失败）
    public Integer checkTypeName(String typeName, Integer userId, String typeId) {
        String sql = "select * from tb_note_type where userId=? and typeName= ?";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(typeName);
        //执行查询操作
        NoteType noteType = (NoteType) BaseDao.queryRow(sql,params,NoteType.class);
        //如果对象为空，表示可用
        if (noteType == null){
            return 1;
        }else {
            //如果是修改操作，则需要判断是否是当前记录本身
            if (typeId.equals(noteType.getTypeId().toString())){
                return 1;
            }
            return 0;
        }
    }
//添加，返回主键
    public Integer addType(String typeName, Integer userId) {
        Integer key = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
//得到数据库连接
            connection = DBUtil.getConnection();
            String sql = "insert into tb_note_type (typeName,userId) values (?,?)";
            preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            //设置参数
            preparedStatement.setString(1,typeName);
            preparedStatement.setInt(2,userId);
            //执行更新，返回受影响的行数
            int rows = preparedStatement.executeUpdate();
            //判断
            if(rows>0){
                //获取返回主键的结果集
                resultSet = preparedStatement.getGeneratedKeys();
                //得到主键的值
                if (resultSet.next()){
                    key = resultSet.getInt(1);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭资源
            DBUtil.close(resultSet,preparedStatement,connection);
        }
        return key;
    }

    public Integer updateType(String typeName, String typeId) {
        String sql = "update tb_note_type set typeName = ? where typeId = ?";
        List<Object> params = new ArrayList<>();
        params.add(typeName);
        params.add(typeId);
        //调用BaseDao
        int rows = BaseDao.executeUpdate(sql,params);
        return rows;
    }
}
