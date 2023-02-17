package com.xy.crm.aspect;

import com.xy.crm.annoation.RequiredPermission;
import com.xy.crm.exceptions.AuthException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {

    @Autowired
    private HttpSession session;

    @Around(value = "@annotation(com.xy.crm.annoation.RequiredPermission)")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = null;
        List<String> permissions = (List<String>) session.getAttribute("permissions");

        if (null == permissions || permissions.size() <1){
            throw new AuthException();
        }

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        RequiredPermission requiredPermission = methodSignature.getMethod().getDeclaredAnnotation(RequiredPermission.class);

        if (!(permissions.contains(requiredPermission.code()))){
            throw new AuthException();
        }



        result=proceedingJoinPoint.proceed();
        return result;
    }

}
