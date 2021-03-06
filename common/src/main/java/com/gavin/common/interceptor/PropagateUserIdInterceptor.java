package com.gavin.common.interceptor;

import com.gavin.common.config.interceptor.CustomWebMvcConfiguration;
import com.gavin.common.constants.RequestHeaderConstants;
import com.gavin.common.context.CustomHystrixContext;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * added to registry on {@link CustomWebMvcConfiguration}
 */
@Slf4j
public class PropagateUserIdInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.initializeContext();
        }

        String userId = request.getHeader(RequestHeaderConstants.X_USER_ID);
        if (userId != null) {
            CustomHystrixContext.getInstance().setUserId(userId);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            HystrixRequestContext.getContextForCurrentThread().shutdown();
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

}
