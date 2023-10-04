package com.yjxxt.note.dao;

import com.yjxxt.note.po.User;
import com.yjxxt.note.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/* 通过用户名查询用户对象， 返回用户对象
           1. 获取数据库连接
           2. 定义sql语句
           3. 预编译
           4. 设置参数
           5. 执行查询，返回结果集
           6. 判断并分析结果集
           7. 关闭资源

*/
public class UserDao {
    public User queryUserByName(String uname){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = new User();
        try {
            connection = DBUtil.getConnection();
            String sql = "select * from tb_user where uname=?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,uname);
            resultSet = preparedStatement.executeQuery();
            System.out.println(resultSet);
            if (resultSet.next()) {
                user.setUserId(resultSet.getInt("userId"));
                user.setUname(uname);
                user.setHead(resultSet.getString("head"));
                user.setMood(resultSet.getString("mood"));
                user.setNick(resultSet.getString("nick"));
                user.setUpwd(resultSet.getString("upwd"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DBUtil.close(resultSet,preparedStatement,connection);
        }
        return user;
    }

    public User queryUserByNickAndUserId(String nick, Integer userId) {
        String sql = "select * from tb_user where nick = ? and userId != ?";
        List<Object> params = new ArrayList<>();
        params.add(nick);
        params.add(userId);
        //调用BaseDao的查询方法
        User user = (User) BaseDao.queryRow(sql,params,User.class);
        return user;
    }

    public int updateUser(User user) {
        String sql = "update tb_user set nick = ?,mood = ?,head = ? where userId = ?";
        List<Object> params = new ArrayList<>();
        params.add(user.getNick());
        params.add(user.getMood());
        params.add(user.getHead());
        params.add(user.getUserId());
        int row = BaseDao.executeUpdate(sql,params);
        return row;
    }
}
