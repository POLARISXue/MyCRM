package com.xy.crm.dao;

import com.xy.crm.base.BaseMapper;
import com.xy.crm.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper  extends BaseMapper<User,Integer> {
    public User queryUserByUserName(String userName);

    //查询所有的销售人员
    public List<Map<String,Object>> queryALLSales();

}