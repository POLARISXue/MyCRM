package com.xy.crm.service;

import com.xy.crm.base.BaseService;
import com.xy.crm.dao.ModuleMapper;
import com.xy.crm.dao.PermissionMapper;
import com.xy.crm.dao.RoleMapper;
import com.xy.crm.query.RoleQuery;
import com.xy.crm.utils.AssertUtil;
import com.xy.crm.vo.Permission;
import com.xy.crm.vo.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class RoleService extends BaseService<Role,Integer> {

    @Resource
    private RoleMapper roleMapper;
    @Resource
    private PermissionMapper permissionMapper;
    @Resource
    private ModuleMapper moduleMapper;


    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        checkAddRoleParams(role.getRoleName());
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        role.setIsValid(1);
        AssertUtil.isTrue(roleMapper.insertSelective(role) != 1,"角色添加失败");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role){
        checkUpdateRoleParams(role.getId(),role.getRoleName());
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)!=1,"角色更新失败");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRoleByIds(Integer[] ids){
        AssertUtil.isTrue(ids == null || ids.length < 1,"为找到选中的角色id信息");
        AssertUtil.isTrue(roleMapper.deleteBatch(ids)!=ids.length,"角色记录删除失败");
    }

    private void checkAddRoleParams(String roleName){
        AssertUtil.isTrue(roleName == null || roleName.equals(' '),"角色名称不能为空");
        Role role = roleMapper.queryRoleByRoleName(roleName);
        AssertUtil.isTrue(role!=null && role.getIsValid()==1 ,"角色已存在");
    }

    private void checkUpdateRoleParams(Integer id,String roleName){
        AssertUtil.isTrue(roleName == null || roleName.equals(' '),"角色名称不能为空");
        Role role = roleMapper.queryRoleByRoleName(roleName);
        AssertUtil.isTrue(role!=null && role.getIsValid()==1 && !role.getId().equals(id),"角色已存在");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId, Integer[] mIds) {
        Integer count = permissionMapper.countPermissionByRoleId(roleId);
        if (count > 0){
            permissionMapper.deletePermissionByRoleId(roleId);
        }
        if (mIds != null && mIds.length>0){
            List<Permission> permissionList = new ArrayList<>();

            for(Integer mId : mIds ){
                Permission permission = new Permission();
                permission.setModuleId(mId);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mId).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());

                permissionList.add(permission);
            }

            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList)!=permissionList.size(),"角色授权失败");
        }
    }
}
