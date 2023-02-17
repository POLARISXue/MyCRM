package com.xy.crm.service;

import com.xy.crm.base.BaseService;
import com.xy.crm.dao.PermissionMapper;
import com.xy.crm.vo.Permission;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class PermissionService extends BaseService<Permission,Integer> {

    @Resource
    private PermissionMapper permissionMapper;


    public List<String> queryUserPermissions(Integer userId){
        return permissionMapper.queryUserPermissions(userId);
    }

}
