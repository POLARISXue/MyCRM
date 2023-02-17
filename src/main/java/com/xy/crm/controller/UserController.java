package com.xy.crm.controller;

import com.xy.crm.base.BaseController;
import com.xy.crm.base.ResultInfo;
import com.xy.crm.exceptions.ParamsException;
import com.xy.crm.model.UserModel;
import com.xy.crm.query.UserQuery;
import com.xy.crm.service.UserService;
import com.xy.crm.utils.AssertUtil;
import com.xy.crm.utils.LoginUserUtil;
import com.xy.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseBody
    public ResultInfo userLogin(String userName , String password){
        ResultInfo resultInfo = new ResultInfo();
        UserModel userModel = userService.userLogin(userName,password);
        resultInfo.setResult(userModel);
        return resultInfo;
    }

    @PostMapping("/updatePassword")
    @ResponseBody
    public ResultInfo updatePassword(HttpServletRequest request,String oldPassword , String newPassword,String confirmPassword){
        return userService.updateUserPassword(LoginUserUtil.releaseUserIdFromCookie(request),oldPassword,newPassword,confirmPassword);
    }

    @RequestMapping("/toPasswordPage")
    public String toPassword(){
        return "user/password";
    }

    @RequestMapping("/queryAllSales")
    @ResponseBody
    public List<Map<String,Object>> queryAllSales(){
        return userService.queryAllSales();
    }

    @RequestMapping("/list")
    @ResponseBody
    public Map<String,Object> queryUserList(UserQuery userQuery){
        return userService.queryByParamsForTable(userQuery);
    }

    @PostMapping("/add")
    @ResponseBody
    public ResultInfo addUser(User user){
        userService.addUser(user);
        return success("用户添加成功");
    }

    @PostMapping("/update")
    @ResponseBody
    public ResultInfo updateUser(User user){
        userService.updateUser(user);
        return success("用户修改成功");
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultInfo deleteUserByIds(Integer[] ids){
        userService.deleteUserByIds(ids);
        return  success("用户删除成功");
    }

    @RequestMapping("/openAddOrUpdateUserPage")
    public String openAddOrUpdateUser(Integer id, Model model){
        if (id != null){
            User user = userService.selectByPrimaryKey(id);
            AssertUtil.isTrue(user==null,"您修改的用户已不存在");
            model.addAttribute("UserInfo",user);
        }
        return "/user/add_update";
    }

    @RequestMapping("/index")
    public String index(){
        return "/user/user";
    }
}
