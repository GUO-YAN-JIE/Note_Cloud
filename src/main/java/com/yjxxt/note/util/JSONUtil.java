package com.yjxxt.note.util;

import com.alibaba.fastjson.JSON;
import com.yjxxt.note.vo.ResultInfo;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JSONUtil {
    public static void toJSON(HttpServletResponse response, Object result){
        //将resultInfo对象转换成JSON格式的字符串，响应给ajax函数
        //设置响应类型及编码格式（JSON类型）
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            String json = JSON.toJSONString(result);
            out.write(json);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
