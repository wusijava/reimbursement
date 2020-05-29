package com.wusi.reimbursement.aop;

import com.google.gson.Gson;
import com.wusi.reimbursement.entity.RequestContext;
import com.wusi.reimbursement.entity.SystemLog;
import com.wusi.reimbursement.entity.User;
import com.wusi.reimbursement.service.SystemLogService;
import com.wusi.reimbursement.utils.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ Description   :  日志记录切面
 * @ Author        :  wusi
 * @ CreateDate    :  2020/5/29$ 11:20$
 */
@Aspect
@Component
public class SysLogAspect {
    @Autowired
    private SystemLogService systemLogService;
    /**
     * 这里我们使用注解的形式
     * 当然，我们也可以通过切点表达式直接指定需要拦截的package,需要拦截的class 以及 method
     * 切点表达式:   execution(...)
     */
    @Pointcut("@annotation(com.wusi.reimbursement.aop.SysLog)")
    public void logPointCut() {}


    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - beginTime;
        try {
            saveLog(point,time);
        } catch (Exception e) {
        }
        return result;
    }
    /**
     * 保存日志
     * @param joinPoint
     * @param time
     */
    private void saveLog(ProceedingJoinPoint joinPoint, long time) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        User se = (User)session.getAttribute("user");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        SystemLog systemLog = new SystemLog();
        systemLog.setExeuTime(time);
        String user = (String)RedisUtil.get("user");
        systemLog.setUser(user);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        systemLog.setCreateTime(new Date());
        SysLog sysLog = method.getAnnotation(SysLog.class);
        if(sysLog != null){
            //注解上的描述
            systemLog.setOperation(sysLog.value());
        }
        //请求的 类名、方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        systemLog.setClassName(className);
        systemLog.setMethodName(methodName);
        //请求的参数
        Object[] args = joinPoint.getArgs();
        try{
            List<String> list = new ArrayList<String>();
            for (Object o : args) {
                list.add(new Gson().toJson(o));
            }
            systemLog.setParams(list.toString());
        }catch (Exception e){ }
        systemLogService.insert(systemLog);
    }

}
