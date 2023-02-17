package com.xy.crm.dao;

import com.xy.crm.base.BaseMapper;
import com.xy.crm.query.RoleQuery;
import com.xy.crm.vo.Role;

import java.util.List;
import java.util.Map;

public interface RoleMapper  extends BaseMapper<Role,Integer> {

    //
    public List<Map<String,Object>> queryAllRoles(Integer userId);

    public Role queryRolesByParams(RoleQuery roleQuery);

    public Role queryRoleByRoleName(String roleName);
}