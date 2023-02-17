package com.xy.crm.controller;

import com.xy.crm.annoation.RequiredPermission;
import com.xy.crm.base.BaseController;
import com.xy.crm.base.ResultInfo;
import com.xy.crm.query.SaleChanceQuery;
import com.xy.crm.service.SaleChanceService;
import com.xy.crm.utils.CookieUtil;
import com.xy.crm.utils.LoginUserUtil;
import com.xy.crm.vo.SaleChance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/sale_chance")
public class SaleChanceController extends BaseController {

    @Autowired
    private SaleChanceService saleChanceService;

    @RequiredPermission(code = "1010")
    @RequestMapping("/index")
    public String index(){
        return "saleChance/sale_chance";
    }

    @RequiredPermission(code = "101001")
    @RequestMapping("/list")
    @ResponseBody
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery,Integer flag,HttpServletRequest request){
        if (flag != null && flag == 1){
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }
        return saleChanceService.querySaleChanceByParams(saleChanceQuery);
    }

    @RequiredPermission(code = "101002")
    @RequestMapping("/add")
    @ResponseBody
    public ResultInfo addSaleChance(HttpServletRequest request, SaleChance saleChance){
        String userName = CookieUtil.getCookieValue(request,"userName");
        saleChance.setCreateMan(userName);
        saleChanceService.addSaleChance(saleChance);
        return success("数据添加成功");
    }

    @RequestMapping("/addOrUpdateSaleChancePage")
    public String addOrUpdateSaleChancePage(Integer id , Model model){
        if (id != null){
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
            model.addAttribute("saleChance",saleChance);
        }
        return "saleChance/add_update";
    }

    @RequiredPermission(code = "101004")
    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateSalChance( SaleChance saleChance){
        saleChanceService.updateSaleChance(saleChance);
        return success("营销机会数据更新成功!");
    }

    @RequiredPermission(code = "101003")
    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        saleChanceService.deleteBatch(ids);
        return success("营销机会数据删除成功");
    }

    @PostMapping("/updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer DevResult){

        saleChanceService.updateSaleChanceResult(id,DevResult);

        return success("营销机会开发状态修改成功!");
    }


}
