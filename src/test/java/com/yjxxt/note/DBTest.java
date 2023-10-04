package com.yjxxt.note;

import com.yjxxt.note.dao.BaseDao;
import com.yjxxt.note.dao.UserDao;
import com.yjxxt.note.po.User;
import com.yjxxt.note.util.DBUtil;
import org.slf4j.Logger;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DBTest {
    private Logger logger = LoggerFactory.getLogger(DBUtil.class);
    @Test
    public void testQueryUserByName(){
            //使用日志
        logger.info("在{}时获取数据库连接",DBUtil.getConnection());
        UserDao userDao = new UserDao();
        User user = userDao.queryUserByName("admin");
        System.out.println(user);
        System.out.println(userDao.queryUserByNickAndUserId("lisi", 1));
        List list = BaseDao.queryRows("select * from tb_user",null,User.class);
        System.out.println(list);
    }


}
