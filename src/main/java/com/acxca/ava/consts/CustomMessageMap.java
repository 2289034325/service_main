package com.acxca.ava.consts;

import com.acxca.components.java.consts.BusinessMessageMap;
import com.acxca.components.java.entity.BusinessMessage;

public class CustomMessageMap extends BusinessMessageMap {
    public static final BusinessMessage WORD_NOT_FOUND = new BusinessMessage(201, "没有找到该词");
    public static final BusinessMessage WORD_EXIST = new BusinessMessage(202, "该词已经存在");

    public static final BusinessMessage REQUEST_PARAM_INVALID = new BusinessMessage(203, "参数错误");
}
