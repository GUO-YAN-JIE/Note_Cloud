package com.yjxxt.note.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.yjxxt.note.dao.UserDao;
import com.yjxxt.note.po.User;
import com.yjxxt.note.vo.ResultInfo;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;

public class UserService {
    private UserDao userDao = new UserDao();
    public ResultInfo<User> UserLogin(String uname,String upwd){
        // 数据回显：当登录实现时，将登录信息返回给页面显示
        ResultInfo<User> resultInfo = new ResultInfo<>();
        User u = new User();
        u.setUname(uname);
        u.setUpwd(upwd);
        resultInfo.setResult(u);
        //判断非空
        if(StrUtil.isBlank(uname)||StrUtil.isBlank(upwd)){
            resultInfo.setMsg("用户名或密码不能为空！");
            resultInfo.setCode(0);
            return resultInfo;
        }
        //不为空，通过用户名查找对象

        User user = userDao.queryUserByName(uname);
        //判断用户对象是否为空
        if(user==null||!uname.equals(user.getUname())){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户不存在！");
            return resultInfo;
        }
        //用户对象不为空则比较密码
        upwd = DigestUtil.md5Hex(upwd);
        if (!upwd.equals(user.getUpwd())){
            resultInfo.setMsg("密码错误！");
            resultInfo.setCode(0);
            return resultInfo;
        }
        resultInfo.setCode(1);
        resultInfo.setResult(user);
        return resultInfo;
    }


    public Integer checkNick(String nick, Integer userId) {
        //验证昵称的唯一性
        if(StrUtil.isBlank(nick)){
            return 0;
        }
        //调用Dao层，通过用户ID和昵称查询用户对象
        User user = userDao.queryUserByNickAndUserId(nick,userId);
        //判断用户对象存在
        if(user == null){
            return 1;
        }else {
            return 0;
        }

    }

    public ResultInfo<User> updateUser(HttpServletRequest req) {
        ResultInfo<User> resultInfo = new ResultInfo<>();
        String nick = req.getParameter("nick");
        String mood = req.getParameter("mood");
        if(StrUtil.isBlank(nick)){
            resultInfo.setCode(0);
            resultInfo.setMsg("用户昵称不能为空！");
            return resultInfo;
        }
        //从session作用域中获取用户对象
        User user = (User) req.getSession().getAttribute("user");
        //设置头像和心情
        user.setNick(nick);
        user.setMood(mood);
        //实现文件上传
        try {
            Part part = req.getPart("img");
            String header = part.getHeader("Content-Disposition");
            System.out.println(header);
            //获取具体的请求头对应的值
            String str = header.substring(header.lastIndexOf("=")+2);
            System.out.println(str);
            //获取上传的文件名
            String fileName = str.substring(0,str.length()-1);
            if (!StrUtil.isBlank(fileName)){
                //更新头像
                user.setHead(fileName);
                //获取项目根路径（真实路径）
                String filePath = req.getServletContext().getRealPath("/WEB-INF/upload/");
                //上传文件到指定目录
                part.write(filePath+"/"+fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            e.printStackTrace();
        }
        int row = userDao.updateUser(user);
        //判断受影响的行数
        if (row>0){
            resultInfo.setCode(1);
            //更新session中的用户对象
            req.getSession().setAttribute("user",user);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败！");
            return resultInfo;
        }
        return resultInfo;
    }
}
