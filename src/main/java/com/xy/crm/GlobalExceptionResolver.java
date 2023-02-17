package com.xy.crm;

import com.alibaba.fastjson.JSON;
import com.xy.crm.base.ResultInfo;
import com.xy.crm.exceptions.AuthException;
import com.xy.crm.exceptions.NoLoginException;
import com.xy.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 全局异常统一处理类
 */
@Component
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    //处理方法
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        ModelAndView modelAndView = new ModelAndView("error");

        //拦截异常处理
        if (e instanceof NoLoginException){
            modelAndView.setViewName("redirect:/index");
            modelAndView.addObject("code",((NoLoginException) e).getCode());
            modelAndView.addObject("msg",((NoLoginException) e).getMsg());
            return modelAndView;
        }



        modelAndView.addObject("code",500);
        modelAndView.addObject("msg","系统异常，请稍后重试");
        if (o instanceof HandlerMethod){
            //类型转换
            HandlerMethod hm = (HandlerMethod) o;
            //获取方法上声明的@responseBody注解对象
            ResponseBody responseBody = hm.getMethod().getDeclaredAnnotation(ResponseBody.class);

            if (null == responseBody){

                if (e instanceof ParamsException){
                    ParamsException p = (ParamsException) e;
                    modelAndView.addObject("code",p.getCode());
                    modelAndView.addObject("msg",p.getMsg());
                }else if (e instanceof AuthException){
                    AuthException p = (AuthException) e;
                    modelAndView.addObject("code",p.getCode());
                    modelAndView.addObject("msg",p.getMsg());
                }
                return modelAndView;

            }else {

                ResultInfo resultInfo = new ResultInfo();
                resultInfo.setCode(500);
                resultInfo.setMsg("系统异常，请重试！");

                if (e instanceof ParamsException){
                    ParamsException p = (ParamsException) e;
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());
                }else if (e instanceof AuthException){
                    AuthException p = (AuthException) e;
                    resultInfo.setCode(p.getCode());
                    resultInfo.setMsg(p.getMsg());
                }

                //设置响应类型以及编码格式
                httpServletResponse.setContentType("application/json;charset=utf-8");
                PrintWriter writer = null;
                try {
                    writer = httpServletResponse.getWriter();
                    writer.write(JSON.toJSONString(resultInfo));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }finally {
                    if (writer!=null){
                        writer.close();
                    }
                }
                return null;

            }
        }else {
            return modelAndView;
        }



    }

}
