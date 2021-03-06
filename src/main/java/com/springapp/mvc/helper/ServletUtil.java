package com.springapp.mvc.helper;

import com.springapp.mvc.commmon.AppConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by ruiqizhang on 11/24/16.
 */
public class ServletUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServletUtil.class);
    public static final int DEFAULT_PORT = 80;
    public static final int MAX_IP_LENGTH = 15;

    public static void clearUserCookie(HttpServletRequest request, HttpServletResponse response) {
//        deleteCookie(request, response, UserConstant.USER_NAME);
//        deleteCookie(request, response, UserConstant.LOGIN_STATUS);
    }


    /**
     * 根据HttpServletRequest获得url
     *
     * @param request 请求
     * @return url
     */
    public static String getUrl(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String url = "http://" + request.getServerName()
                + (request.getServerPort() == DEFAULT_PORT ? "" : ":" + request.getServerPort())
                + request.getContextPath() + request.getRequestURI();
        if (request.getQueryString() != null && !request.getQueryString().isEmpty()) {
            url = url + "?" + HtmlFilterUtil.filterHeaderValue(request.getQueryString());
        }
        return url;
    }

    /**
     * 根据HttpServletRequest获得访问ip
     *
     * @param request 请求
     * @return ip
     */
    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || "".equals(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.split(",")[0].trim().length() > MAX_IP_LENGTH) {
            return "";
        }
        return ip.split(",")[0].trim();
    }

    /**
     * 获取绝对路径
     *
     * @param request
     * @return
     */
    public static String getWebRoot(HttpServletRequest request) {
        String webRoot = "";
        if (request == null) {
            return webRoot;
        }
        if (request.getSession() == null) {
            LOGGER.debug("====>>>>> There is no session");
            return webRoot;
        }
        ServletContext servletContext = request.getSession().getServletContext();
        if (servletContext != null) {
            webRoot = servletContext.getRealPath("/").replaceAll("\\\\", "/");
            LOGGER.debug("====>>>>> The root path of web context is " + webRoot);
        } else {
            LOGGER.debug("====>>>>> There is no servlet context");
        }
        return webRoot;
    }

    public static Cookie getCookie(HttpServletRequest request, String key) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals(key)) {
                return c;
            }
        }
        return null;
    }

    public static boolean writeCookie(HttpServletResponse response, String name, String value, String domain, int time) {
        try {
            Cookie cookie = new Cookie(name, value);
            cookie.setPath("/");
            if (!domain.equals("localhost")) {
                cookie.setDomain(domain);
            }
            if (time > 0) {
                cookie.setMaxAge(time);
            }
            response.addCookie(cookie);
            return true;
        } catch (Exception e) {
            LOGGER.error("write cookie  error, e " + name + "|" + value + "|" + e.getMessage());
            return false;
        }
    }

    public static boolean writeCookie(HttpServletResponse response, String name, String value, int time) {
        return writeCookie(response, name, value, AppConstant.domain, time);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        deleteCookie(request, response, null, name);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String domain, String name) {
        Cookie cookie = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals(name)) {
                    c.setValue(null);
                    c.setMaxAge(0);
                    if (domain == null) {
                        domain = AppConstant.domain;
                    }
                    if (!domain.equals("localhost")) {
                        c.setDomain(domain);
                    }
                    c.setPath("/");
                    cookie = c;
                }
            }
        }
        if (cookie != null) {
            response.addCookie(cookie);
        }
    }

    public static boolean isCrossDomain(HttpServletRequest request) {
        String host = request.getServerName().toLowerCase();
        String referer = request.getHeader("Referer");
        return referer == null || !referer.toLowerCase().startsWith("http://" + host);
    }
}
