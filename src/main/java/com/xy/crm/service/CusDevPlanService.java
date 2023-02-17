package com.xy.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xy.crm.base.BaseService;
import com.xy.crm.dao.CusDevPlanMapper;
import com.xy.crm.dao.SaleChanceMapper;
import com.xy.crm.query.CusDevPlanQuery;
import com.xy.crm.query.SaleChanceQuery;
import com.xy.crm.utils.AssertUtil;
import com.xy.crm.vo.CusDevPlan;
import com.xy.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {

    @Resource
    private CusDevPlanMapper cusDevPlanMapper;

    @Resource
    private SaleChanceMapper saleChanceMapper;

    /**
     * 查询客户开发计划
     * @param cusDevPlanQuery
     * @return
     */
    public Map<String,Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery){
        Map<String,Object> result = new HashMap<String,Object>();
        PageHelper.startPage(cusDevPlanQuery.getPage(),cusDevPlanQuery.getLimit());
        PageInfo<CusDevPlan> pageInfo =new PageInfo<>(cusDevPlanMapper.selectByParams(cusDevPlanQuery));
        result.put("count",pageInfo.getTotal());
        result.put("data",pageInfo.getList());
        result.put("code",0);
        result.put("msg","success");
        return result;
    }

    /**
     * 添加计划项
     * 1.参数校验
     * 2.设置默认值
     * 3.执行操作
     *
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCusDevPlan(CusDevPlan cusDevPlan){
         checkAddCusDevPlanParams(cusDevPlan.getSaleChanceId(),cusDevPlan.getPlanItem(),cusDevPlan.getPlanDate());
         //设置参数的默认值
         cusDevPlan.setIsValid(1);
         //设置默认创建时间
         cusDevPlan.setCreateDate(new Date());
        //设置修改时间
         cusDevPlan.setUpdateDate(new Date());
         AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan)!=1,"计划添加失败！");
    }

    /**
     * 更新计划项
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        checkUpdateCusDevPlan(cusDevPlan.getId(),
                              cusDevPlan.getSaleChanceId(),
                              cusDevPlan.getPlanItem(),
                              cusDevPlan.getPlanDate());
        cusDevPlan.setUpdateDate(new Date());
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"计划项记录更新失败！");
    }

    /**
     * 删除计划项
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCusDevPlan(Integer id){
        AssertUtil.isTrue(null == id,"待删除记录已不存在！");
        CusDevPlan cusDevPlan = cusDevPlanMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(cusDevPlan == null,"待删除记录已不存在！");
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"计划项删除失败");

    }

    private void checkAddCusDevPlanParams(Integer saleChanceId, String planItem, Date planDate) {
        AssertUtil.isTrue((saleChanceId==null || saleChanceMapper.selectByPrimaryKey(saleChanceId)==null),"目标营销机会已不存在！");
        AssertUtil.isTrue(StringUtils.isBlank(planItem),"计划项目不能为空！");
        AssertUtil.isTrue(planDate==null,"计划时间不能为空！");
    }

    public void checkUpdateCusDevPlan(Integer id,Integer saleChanceId, String planItem, Date planDate){
        AssertUtil.isTrue(id == null || cusDevPlanMapper.selectByPrimaryKey(id) == null,"待更新记录不在！");
        checkAddCusDevPlanParams(saleChanceId,planItem,planDate);
    }


}
