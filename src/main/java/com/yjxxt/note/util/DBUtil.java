package com.yjxxt.note.util;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {
    //得到配置文件对象
    private static Properties properties = new Properties();
    //加载配置文件
    static {
        try {
            InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties");
            properties.load(in);
            Class.forName(properties.getProperty("jdbcName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取数据库连接
    public static Connection getConnection() {
       Connection connection = null;
        try {
            String DBUrl = properties.getProperty("dbUrl");
            String DBName = properties.getProperty("dbName");
            String DBPwd = properties.getProperty("dbPwd");
            connection = DriverManager.getConnection(DBUrl,DBName,DBPwd);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
    //关闭资源
    public static void close(ResultSet resultSet, PreparedStatement preparedStatement,Connection connection) {

            try {
                if(resultSet !=null) {
                    resultSet.close();
                }
                if(preparedStatement !=null){
                    preparedStatement.close();
                }
                if(connection !=null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }
