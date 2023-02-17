package com.xy.crm.controller;

import com.xy.crm.base.BaseController;
import com.xy.crm.base.ResultInfo;
import com.xy.crm.model.ModuleTreeModel;
import com.xy.crm.service.ModuleService;
import com.xy.crm.vo.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequestMapping("/module")
@Controller
public class ModuleController extends BaseController {

    @Autowired
    private ModuleService moduleService;

    @RequestMapping("/treelist")
    @ResponseBody
    public List<ModuleTreeModel> queryAllModules(Integer roleId){
        return moduleService.queryAllModules(roleId);
    }

    @RequestMapping("/openGrantPage")
    public String openGrantPage(Integer roleId, HttpServletRequest request){
        request.setAttribute("roleId",roleId);
        return "role/grant";
    }

    @RequestMapping("/index")
    public String index(){
        return "module/module";
    }

    /**
     * 资源列表接口
     * @return
     */
    @RequestMapping("/list")
    @ResponseBody
    public Map<String,Object> list(){
        return moduleService.queryModuleList();
    }

    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addModule(Module module){
        moduleService.addModule(module);
        return success("添加资源成功");
    }

    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("添加资源成功");
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModule(id);
        return success("添加删除成功");
    }


    @RequestMapping("/openAddModulePage")
    public String openAddModulePage(Integer grade,Integer parentId,HttpServletRequest request){
        request.setAttribute("grade",grade);
        request.setAttribute("parentId",parentId);
        return "/module/add";
    }

    @RequestMapping("/openUpdateModulePage")
    public String openUpdateModulePage(Integer id, Model model){
        model.addAttribute("module",moduleService.selectByPrimaryKey(id));
        return "/module/update";
    }

}
