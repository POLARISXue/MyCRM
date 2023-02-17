package com.xy.crm.dao;

import com.xy.crm.base.BaseMapper;
import com.xy.crm.vo.UserRole;

public interface UserRoleMapper extends BaseMapper<UserRole,Integer> {

    Integer countUserRoleByUserId(Integer userId);

    Integer deleteUserRoleByUserId(Integer userId);
}