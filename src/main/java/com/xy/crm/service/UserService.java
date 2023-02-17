package com.xy.crm.service;

import com.xy.crm.base.BaseService;
import com.xy.crm.base.ResultInfo;
import com.xy.crm.dao.UserMapper;
import com.xy.crm.dao.UserRoleMapper;
import com.xy.crm.model.UserModel;
import com.xy.crm.utils.AssertUtil;
import com.xy.crm.utils.Md5Util;
import com.xy.crm.utils.PhoneUtil;
import com.xy.crm.utils.UserIDBase64;
import com.xy.crm.vo.User;
import com.xy.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {

    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName,String userPwd){
        checkLoginParams(userName,userPwd);
        User user = userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user == null,"该用户不存在");
        checkLoginPwd(userPwd,user.getUserPwd());
        return buildUserInfo(user);
    }

    /**
     * 修改密码
     * @param id
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public ResultInfo updateUserPassword(Integer id,String oldPassword,String newPassword,String confirmPassword){
        ResultInfo resultInfo = new ResultInfo();

        //参数校验
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword),"原始密码为空");
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"新密码为空");
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword),"新密码确认值为空");
        AssertUtil.isTrue(!newPassword.equals(confirmPassword),"新密码两次输入不一致");
        AssertUtil.isTrue(newPassword.equals(oldPassword),"新密码与旧密码不能一致");
        //根据id获取用户对象
        User user = userMapper.selectByPrimaryKey(id);
        //检查用户是否存在
        AssertUtil.isTrue(user == null ,"修改用户已不存在");
        //检查原始密码是否正确
        checkLoginPwd(oldPassword,user.getUserPwd());
        user.setUserPwd(Md5Util.encode(newPassword));
        int row = userMapper.updateByPrimaryKeySelective(user);
        AssertUtil.isTrue(row < 1,"密码更新失败");
        return resultInfo;

    }

    /**
     * 查询所有销售人员
     * @return
     */
    public List<Map<String,Object>> queryAllSales(){
        return userMapper.queryALLSales();
    }

    /**
     *
     *用户添加
     * * 1.参数校验
     * *  用户名 非空  唯一
     * *  email 非空 格式合法
     * *  手机号 非空 格式合法
     * * 2.设置默认参数
     * * isValid 1
     * * createDate  uddateDate
     * *  userPwd  123456->md5加密
     * * 3.执行添加 判断结果
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        checkAddUserParams(user.getUserName(),user.getEmail(),user.getPhone());
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        AssertUtil.isTrue(insertSelective(user)!=1,"用户添加失败");

        /*用户角色关联*/
        relationUserRole(user.getId(),user.getRoleIds());
    }


    /**用户更新
     * 1.参数校验
     *  id 非空 记录必须存在
     *  用户名 非空  唯一
     *  email 非空 格式合法
     *  手机号 非空 格式合法
     * 2.设置默认参数
     *  uddateDate
     * 3.执行更新 判断结果
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        checkUpdateUserParams(user.getId(),user.getUserName(),user.getEmail(),user.getPhone());
        user.setUpdateDate(new Date());
        AssertUtil.isTrue(updateByPrimaryKeySelective(user)!=1,"用户更新失败");
        /*用户角色关联*/
        relationUserRole(user.getId(),user.getRoleIds());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids){
        //1.参数校验
        AssertUtil.isTrue(ids == null || ids.length<1,"未找到选择删除用户的id信息");
        //2.执行更新操作
        AssertUtil.isTrue(deleteBatch(ids) != ids.length,"用户记录删除失败！");

        for (Integer userId : ids){
            int count = userRoleMapper.countUserRoleByUserId(userId);
            if (count > 0 ){
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"用户角色记录删除失败");
            }
        }
    }


    private void relationUserRole(Integer userId, String roleIds) {
        //通过用户id查询角色记录，判断记录是否存在
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        //如果存在，删除用户对应的角色记录
        if (count > 0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count,"用户角色维护失败！");
        }
        //添加用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)){
            List<UserRole> userRoleList = new ArrayList<>();
            String[] roleIdsArray = roleIds.split(",");

            for (String roleId : roleIdsArray){
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(Integer.valueOf(roleId));
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                userRoleList.add(userRole);
            }

            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList)!=userRoleList.size(),"用户角色维护失败！");
        }
    }

    private void checkLoginParams(String userName, String userPwd) {
        // 判断姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName), "⽤户姓名不能为空！");
        // 判断密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd), "⽤户密码不能为空！");
    }

    private void checkLoginPwd(String userPwd, String upwd) {
        // 数据库中的密码是经过加密的，将前台传递的密码先加密，再与数据库中的密码作⽐较
        userPwd = Md5Util.encode(userPwd);
        // ⽐较密码
        AssertUtil.isTrue(!userPwd.equals(upwd), "⽤户密码不正确！");
    }

    private void checkAddUserParams(String userName,String email,String phone){
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(email == null , "邮件不能为空");
        AssertUtil.isTrue(phone == null , "用户电话不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"电话格式不正确");
        User user =userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user!=null && user.getIsValid()==1 ,"用户名已重复");
    }

    private void checkUpdateUserParams(Integer id,String userName , String email,String phone){
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空");
        AssertUtil.isTrue(email == null , "邮件不能为空");
        AssertUtil.isTrue(phone == null , "用户电话不能为空");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"电话格式不正确");
        AssertUtil.isTrue(id==null,"该用户id获取失败!");
        User user =userMapper.queryUserByUserName(userName);
        AssertUtil.isTrue(user!=null && (!user.getId().equals(id)) && user.getIsValid()==1,"用户名已存在！");

    }




    private UserModel buildUserInfo(User user) {
        UserModel userModel = new UserModel();
        // 设置⽤户信息
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }


}
