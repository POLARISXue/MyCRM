package com.xy.crm.service;

import com.xy.crm.base.BaseService;
import com.xy.crm.dao.ModuleMapper;
import com.xy.crm.dao.PermissionMapper;
import com.xy.crm.model.ModuleTreeModel;
import com.xy.crm.utils.AssertUtil;
import com.xy.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询所有树形资源模块
     * @return
     */
    public List<ModuleTreeModel> queryAllModules(Integer roleId){
        List<ModuleTreeModel> moduleTreeModelList = moduleMapper.queryAllModules();
        //查询指定角色已经授权果的资源列表
        List<Integer> permissionIds = permissionMapper.queryRoleHasModuleIdByRoleId(roleId);
        if (permissionIds != null && permissionIds.size()>0){
            moduleTreeModelList.forEach(moduleTreeModel -> {
                if (permissionIds.contains(moduleTreeModel.getId())){
                    moduleTreeModel.setChecked(true);
                }
            });
        }
        return moduleTreeModelList;
    }

    /**
     * 查询资源列表
     * @return
     */
    public Map<String,Object> queryModuleList(){
        Map<String,Object> map = new HashMap<>();
        List<Module> moduleList = moduleMapper.queryModuleList();
        map.put("code",0);
        map.put("msg","");
        map.put("count",moduleList.size());
        map.put("data",moduleList);
        return map;
    }

    /**
     * 1.参数校验
     *  模块名-module_name
     *      非空 同一层级下模块名唯一
     *  url
     *      二级菜单 非空 不可重复
     * 上级菜单-parent_id
     *      一级菜单  null
     *      二级|三级菜单 parent_id 非空 必须存在
     *  层级-grade
     *      非空 0|1|2
     *  权限码 optValue
     *      非空 不可重复
     * 2.参数默认值设置
     *      is_valid create_date update_date
     * 3.执行添加 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module){
        checkAddModuleParams(module);
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());

        AssertUtil.isTrue( moduleMapper.insertSelective(module)!=1,"资源添加失败！");

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module){
        checkUpdateModuleParams(module);
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)!=1,"更新失败");
    }

    private void checkAddModuleParams(Module module) {
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"请输入模块名");
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByModuleNameAndGrade(module.getModuleName(),module.getGrade()),"模块名已存在");

        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade==0||grade==1||grade==2),"模块层级不合法");

        //二级菜单
        if (grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"url地址不能为空");
            AssertUtil.isTrue(null!=moduleMapper.queryModuleByGradeAndUrl(module.getUrl(),grade),"地址不可重复");
        }
        //父级菜单
        if (grade==0){
            module.setParentId(-1);
        }else{
            AssertUtil.isTrue(null == module.getParentId(),"父级菜单不能为空");
            AssertUtil.isTrue(null==moduleMapper.selectByPrimaryKey(module.getParentId()),"父级菜单不存在");
        }

        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码已存在");



    }

    private void checkUpdateModuleParams(Module module){
        AssertUtil.isTrue(module.getId()==null,"未接收到指定记录的id！");
        Module temp = moduleMapper.selectByPrimaryKey(module.getId());
        AssertUtil.isTrue(temp==null,"指定记录已不存在");


        Integer grade = module.getGrade();
        AssertUtil.isTrue(null == grade || !(grade==0||grade==1||grade==2),"模块层级不合法");


        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空");
        temp=moduleMapper.queryModuleByModuleNameAndGrade(module.getModuleName(),grade);
        if (temp != null){
            AssertUtil.isTrue(!(temp.getId()).equals(module.getId()),"同层级下有其他同名模块");
        }


        //二级菜单
        if (grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"url地址不能为空");
            temp=moduleMapper.queryModuleByGradeAndUrl(module.getUrl(),grade);
            if (temp != null){
                AssertUtil.isTrue(!(temp.getId()).equals(module.getId()),"地址不可重复");
            }

        }
//        //父级菜单
//        if (grade==0){
//            module.setParentId(-1);
//        }else{
//            AssertUtil.isTrue(null == module.getParentId(),"父级菜单不能为空");
//            AssertUtil.isTrue(null == moduleMapper.selectByPrimaryKey(module.getParentId()),"父级菜单不存在");
//        }

        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空");
        temp = moduleMapper.queryModuleByOptValue(module.getOptValue());
        if (temp != null){
            AssertUtil.isTrue(!(temp.getId()).equals(module.getId()),"权限码");
        }

        module.setUpdateDate(new Date());


    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModule(Integer id) {
        AssertUtil.isTrue(null == id,"未接收到指定记录的Id");
        Module temp = moduleMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null == temp,"待删除记录不存在");
        Integer count = moduleMapper.queryModuleByParentId(id);
        AssertUtil.isTrue(count>0,"该资源存在子记录，不可删除");
        count=permissionMapper.countPermissionByModuleId(id);
        if (count > 0){
            permissionMapper.deletePermissionByModuleId(id);
        }
        //设置记录无效
        temp.setIsValid((byte) 0);
        temp.setUpdateDate(new Date());

        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp) < 1,"删除资源失败！");
    }
}
