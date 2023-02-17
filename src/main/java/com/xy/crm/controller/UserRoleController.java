package com.xy.crm.controller;

import com.xy.crm.base.BaseController;
import com.xy.crm.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserRoleController extends BaseController {

    @Autowired
    private UserRoleService userRoleService;


}
