package com.xy.crm.dao;

import com.xy.crm.base.BaseMapper;
import com.xy.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    Integer countPermissionByRoleId(Integer roleId);

    void deletePermissionByRoleId(Integer roleId);

    List<Integer> queryRoleHasModuleIdByRoleId(Integer roleId);

    List<String> queryUserPermissions(Integer userId);

    Integer countPermissionByModuleId(Integer id);

    Integer deletePermissionByModuleId(Integer id);
}