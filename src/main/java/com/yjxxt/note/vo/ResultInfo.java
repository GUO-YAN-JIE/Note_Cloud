package com.yjxxt.note.vo;

import lombok.Getter;
import lombok.Setter;

//封装返回结果的类（状态码）
@Setter
@Getter
public class ResultInfo<T> {
    private Integer code;//状态码，成功=1，失败=0
    private String msg;//提示信息
    private T result;//返回对象
}
