package com.xy.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xy.crm.base.BaseService;
import com.xy.crm.dao.SaleChanceMapper;
import com.xy.crm.enums.DevResult;
import com.xy.crm.enums.StateStatus;
import com.xy.crm.query.SaleChanceQuery;
import com.xy.crm.utils.AssertUtil;
import com.xy.crm.utils.PhoneUtil;
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
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;

    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery){
        Map<String,Object> result = new HashMap<String,Object>();
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        PageInfo<SaleChance> pageInfo =new PageInfo<>(saleChanceMapper.selectByParams(saleChanceQuery));
        result.put("count",pageInfo.getTotal());
        result.put("data",pageInfo.getList());
        result.put("code",0);
        result.put("msg","success");
        return result;
    }

    //添加营销机会
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance){
        //参数校验
        checkAddParams(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //默认值注入
        if (StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
            saleChance.setAssignTime(new Date());
        }else {
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }
        if (saleChance.getIsValid() == null ){saleChance.setIsValid(1);}
        saleChance.setUpdateDate(new Date());
        saleChance.setCreateDate(new Date());
        AssertUtil.isTrue(insertSelective(saleChance) < 1,"营销机会数据添加失败!");
    }

    /**
     * 营销机会数据更新
     * 1.参数校验
     * id:记录必须存在
     * customerName:⾮空
     * linkMan:⾮空
     * linkPhone:⾮空，11位⼿机号
     * 2. 设置相关参数值
     * updateDate:系统当前时间
     * 原始记录 未分配 修改后改为已分配(由分配⼈决定)
     * state 0->1
     * assginTime 系统当前时间
     * devResult 0-->1
     * 原始记录 已分配 修改后 为未分配
     * state 1-->0
     * assignTime 待定 null
     * devResult 1-->0
     * 3.执⾏更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance){

        checkUpdateParams(saleChance.getId(),saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());

        saleChance.setUpdateDate(new Date());
        SaleChance old = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(old==null,"待更新记录已不存在");


        //修改前未分配，修改后有分配
        if (StringUtils.isBlank(old.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        //修改前有分配，修改后未分配
        }else if ( StringUtils.isNotBlank(old.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())){
            saleChance.setAssignMan(null);
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setAssignTime(null);
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }else if (StringUtils.isNotBlank(old.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
            //判断修改前后分配的人是否一致
            //如果一致
             if (old.getAssignMan().equals(saleChance.getAssignMan())){
                 saleChance.setAssignTime(old.getAssignTime());
             }else {
                 saleChance.setAssignTime(new Date());
             }
        }

        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1 ,"更新失败");

    }

    /**
     * 营销机会数据删除
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //参数校验
        AssertUtil.isTrue((ids==null || ids.length <1 ),"未选择想要删除的数据");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) != ids.length,"营销机会数据删除失败");
    }

    /**
     * 更新营销机会开发状态的结果
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceResult(Integer id,Integer devResult){
        checkUpdateSaleChanceResultParams(id,devResult);
        SaleChance saleChance = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null == saleChance,"待更新记录不存在！");
        saleChance.setDevResult(devResult);
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) != 1,"开发状态更新失败");
    }


    /**
     * 添加营销机会-参数校验
     *      * id:记录必须存在
     *      * customerName:⾮空
     *      * linkMan:⾮空
     *      * linkPhone:⾮空，11位⼿机号
     * @param id
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkUpdateParams(Integer id,String customerName,String linkMan,String linkPhone) {
        AssertUtil.isTrue(id==null,"用户已登出");
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入客户名!");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人!");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入联系电话!");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"电话格式不正确");
    }

    /**
     * 添加营销机会数据-参数校验
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkAddParams(String customerName,String linkMan,String linkPhone){
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入客户名!");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人!");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入联系电话!");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"电话格式不正确");
    }

    /**
     * 更新营销机会开发结果-参数校验
     * @param id
     * @param devResult
     */
    private void checkUpdateSaleChanceResultParams(Integer id,Integer devResult){
        AssertUtil.isTrue(id==null,"未查询到目标记录的id！");
        AssertUtil.isTrue(devResult==null,"系统异常，未找到需要更新的状态！");
    }

}
