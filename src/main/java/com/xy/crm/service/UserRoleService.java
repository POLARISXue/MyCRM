package com.xy.crm.service;

import com.xy.crm.base.BaseService;
import com.xy.crm.dao.UserRoleMapper;
import com.xy.crm.vo.UserRole;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserRoleService extends BaseService<UserRole,Integer> {

    @Resource
    private UserRoleMapper userRoleMapper;


}
