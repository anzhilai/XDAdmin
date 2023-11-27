package com.anzhilai.admin.task;

import com.anzhilai.core.base.BaseUser;
import com.anzhilai.core.base.XController;
import com.anzhilai.core.base.XException;
import com.anzhilai.core.base.XInterceptor;
import com.anzhilai.core.database.SqlCache;
import com.anzhilai.core.framework.GlobalValues;
import com.anzhilai.core.toolkit.RequestUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.List;

@Repository
@XInterceptor(priority = 1)
public class RightsInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = Logger.getLogger(RightsInterceptor.class);


    //处理请求之前拦截(可以在这做访问过快的拦截,也可以在这里做是否需要登录的拦截)
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();
        String token = RequestUtil.GetParameter(request, BaseUser.F_GatherTOKEN);
        BaseUser user = BaseUser.GetUserByToken(token);
        GlobalValues.SetSessionUser(user);
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = ((HandlerMethod) handler);
            Class<?> beanType = handlerMethod.getBeanType();//类名
            Method method = handlerMethod.getMethod();//方法名
            boolean login = true;
            if (SqlCache.urlRightMap.containsKey(url)) {
                login = SqlCache.urlRightMap.get(url);
            } else {
                XController methodProps = method.getAnnotation(XController.class);//取出接口方法的注解
                XController controllerProps = beanType.getAnnotation(XController.class);
                if (methodProps != null) {
                    if (XController.LoginState.Default.equals(methodProps.isLogin()) && controllerProps != null) {
                        login = !XController.LoginState.No.equals(controllerProps.isLogin());
                    } else {
                        login = !XController.LoginState.No.equals(methodProps.isLogin());
                    }
                } else if (controllerProps != null) {
                    login = !XController.LoginState.No.equals(controllerProps.isLogin());
                }
            }
            if (login) {
                if (user == null) {
                    log.error(RequestUtil.GetClientIpAddress(request) + url + "访问失败");
                    throw new XException("用户没有权限,请重新登录");
                } else {
                    if (user.IsLock()) {
                        throw new XException("用户已锁定,请联系管理员");
                    }
                    List<String> list = user.GetApiList();
                    if (list != null && list.size() > 0) {//API权限判断
                        String apiId = beanType.getName() + "#" + method.getName();
                        if (!list.contains(apiId)) {
                            throw new XException("该请求没有权限，不能调用API");
                        }
                    }
                }
            } else {
                log.info(RequestUtil.GetClientIpAddress(request) + " notneedlogin " + url);
            }
        }
        return super.preHandle(request, response, handler);
    }
}
