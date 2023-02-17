package com.xy.crm.controller;

import com.xy.crm.base.BaseController;
import com.xy.crm.base.ResultInfo;
import com.xy.crm.query.RoleQuery;
import com.xy.crm.service.RoleService;
import com.xy.crm.utils.AssertUtil;
import com.xy.crm.vo.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @RequestMapping("/queryAllRoles")
    @ResponseBody
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleService.queryAllRoles(userId);
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String,Object> queryRolesByParams(RoleQuery roleQuery){
        return roleService.queryByParamsForTable(roleQuery);
    }

    @RequestMapping("/index")
    public String index(){
        return "/role/role";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addRole(Role role){
        roleService.addRole(role);
        return success("角色添加成功");
    }

    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateRole(Role role){
        roleService.updateRole(role);
        return success("角色更新成功");
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteRole(Integer[] ids){
        roleService.deleteRoleByIds(ids);
        return success("角色删除成功");
    }

    @RequestMapping("/openAddOrUpdateRolePage")
    public String openAddOrUpdateRolePage(Integer id, Model model){
        if (id != null){
            Role role = roleService.selectByPrimaryKey(id);
            AssertUtil.isTrue(role==null,"更新的目标角色已经不存在");
            model.addAttribute("role",role);
        }
        return "/role/add_update";
    }


    @PostMapping("/addGrant")
    @ResponseBody
    public ResultInfo addGrant(Integer roleId,Integer[] mIds){

        roleService.addGrant(roleId,mIds);

        return success("角色授权成功");
    }



}
