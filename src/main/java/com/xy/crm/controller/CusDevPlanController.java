package com.xy.crm.controller;

import com.xy.crm.base.BaseController;
import com.xy.crm.base.ResultInfo;
import com.xy.crm.query.CusDevPlanQuery;
import com.xy.crm.service.CusDevPlanService;
import com.xy.crm.service.SaleChanceService;
import com.xy.crm.vo.CusDevPlan;
import com.xy.crm.vo.SaleChance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/cus_dev_plan")
public class CusDevPlanController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;

    @Autowired
    private CusDevPlanService cusDevPlanService;

    /**
     * 首页
     * @return
     */
    @RequestMapping("/index")
    public String index(){
        return "/cusDevPlan/cus_dev_plan";
    }

    /**
     * 打开开发编辑页面
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("/toCusDevPlanPage")
    public String toCusDevPlanPage(Integer id , HttpServletRequest request){
        //根据ID获取营销机会数据
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
        //
        request.setAttribute("saleChance",saleChance);
        return "/cusDevPlan/cus_dev_plan_data";
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String,Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery){
        return cusDevPlanService.queryCusDevPlanByParams(cusDevPlanQuery);
    }

    @RequestMapping("/add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setMsg("添加成功");
        return resultInfo;
    }

    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan){
        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("计划项更新成功!");
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id){
        cusDevPlanService.deleteCusDevPlan(id);
        return success("删除成功!");
    }

    @RequestMapping("/addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id,Integer sId,HttpServletRequest request){
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        request.setAttribute("cusDevPlan",cusDevPlan);
        request.setAttribute("sId",sId);
        return "/cusDevPlan/add_update";
    }
}
