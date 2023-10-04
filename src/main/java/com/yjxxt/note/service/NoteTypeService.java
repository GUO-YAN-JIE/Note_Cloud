package com.yjxxt.note.service;

import cn.hutool.core.util.StrUtil;
import com.yjxxt.note.dao.NoteTypeDao;
import com.yjxxt.note.po.NoteType;
import com.yjxxt.note.vo.ResultInfo;

import java.util.List;

public class NoteTypeService {
    private NoteTypeDao typeDao = new NoteTypeDao();

    public List<NoteType> findTypeList(Integer userId){
        List<NoteType> list = typeDao.findTypeListByUserId(userId);
        return list;
    }

    public ResultInfo<NoteType> deleteType(String typeId) {
        ResultInfo<NoteType> resultInfo = new ResultInfo<>();
        if(StrUtil.isBlank(typeId)){
            resultInfo.setCode(0);
            resultInfo.setMsg("系统异常，请重试!");
            return resultInfo;
        }
        long noteCount = typeDao.findNoteCountByTypeId(typeId);
        if(noteCount>0){
            resultInfo.setCode(0);
            resultInfo.setMsg("该类型存在子记录，不能删除！");
            return resultInfo;
        }
        int rows = typeDao.deleteTypeId(typeId);
        if (rows>0){
            resultInfo.setCode(1);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("删除失败！");
            return resultInfo;
        }
        return resultInfo;
    }

    public ResultInfo<Integer> addOrUpdate(String typeName, Integer userId, String typeId) {
        ResultInfo<Integer> resultInfo = new ResultInfo<>();
        if(StrUtil.isBlank(typeName)){
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称不能为空！");
            return resultInfo;
        }
        //调用Dao层，查询当前登录用户下，类型名称是否唯一，返回0或1
        Integer code = typeDao.checkTypeName(typeName,userId,typeId);
        System.out.println(code);
        if (code == 0){
            resultInfo.setCode(0);
            resultInfo.setMsg("类型名称已存在，请重新输入！");
            return resultInfo;
        }
        //判断类型ID是否为空
        //返回的结果
        Integer key = null;//主键或受影响的行数
        if (StrUtil.isBlank(typeId)){
            //如果为空，添加，返回主键
            key = typeDao.addType(typeName,userId);
        }else {
            //不为空，修改，返回受影响的行数
            key = typeDao.updateType(typeName,typeId);
        }
        //判断 主键/受影响的行数 是否大于0
        if (key>0){
            resultInfo.setCode(1);
            resultInfo.setResult(key);
        }else {
            resultInfo.setCode(0);
            resultInfo.setMsg("更新失败！");
        }
        return resultInfo;
    }
}
