package com.xy.crm.controller;

import com.xy.crm.base.BaseController;
import com.xy.crm.service.PermissionService;
import com.xy.crm.service.UserService;
import com.xy.crm.utils.LoginUserUtil;
import com.xy.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @RequestMapping("/index")
    public String index(){ return "index"; }

    @RequestMapping("/welcome")
    public String welcome(){ return "welcome";}

    @RequestMapping("/main")
    public String main(HttpServletRequest request){
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        User user = userService.selectByPrimaryKey(userId);
        request.getSession().setAttribute("user",user);

        List<String> permissions = permissionService.queryUserPermissions(userId);
        request.getSession().setAttribute("permissions",permissions);
        return "main";
    }
}
