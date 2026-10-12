package com.zhsx.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        try {
            return Long.valueOf(authentication.getName());
        } catch (NumberFormatException e) {
            // principal 非用户 id（如匿名字符串），防御性处理
            return null;
        }
    }
}
