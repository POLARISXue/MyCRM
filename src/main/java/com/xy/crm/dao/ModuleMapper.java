package com.xy.crm.dao;

import com.xy.crm.base.BaseMapper;
import com.xy.crm.model.ModuleTreeModel;
import com.xy.crm.vo.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

    //查询所有的资源树形列表
    public List<ModuleTreeModel> queryAllModules();

    //查询所有的资源
    public List<Module> queryModuleList();
    //通过模块名与层级查询
    Module queryModuleByModuleNameAndGrade(@Param("moduleName") String moduleName ,@Param("grade") Integer grade);
    //通过层级与url查询
    Module queryModuleByGradeAndUrl(@Param("url") String url, @Param("grade") Integer grade);
    //通过权限码查询
    Module queryModuleByOptValue(String optValue);
    //查询指定资源是否存在子记录
    Integer queryModuleByParentId(Integer id);
}