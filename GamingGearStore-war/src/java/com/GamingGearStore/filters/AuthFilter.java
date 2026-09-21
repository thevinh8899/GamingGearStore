package com.GamingGearStore.filters;

import com.GamingGearStore.ebeans.Users;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"/faces/admin/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        Users loggedUser = (session != null) ? (Users) session.getAttribute("loggedUser") : null;

        // 1. Kiểm tra nếu chưa đăng nhập -> Chuyển hướng tới trang login
        if (loggedUser == null) {
            res.sendRedirect(req.getContextPath() + "/faces/login.xhtml");
            return;
        }

        // 2. Kiểm tra nếu đã đăng nhập nhưng không phải Admin -> Chuyển hướng về trang chủ
        if (!"admin".equalsIgnoreCase(loggedUser.getRole())) {
            res.sendRedirect(req.getContextPath() + "/faces/index.xhtml");
            return;
        }

        // 3. Nếu là Admin hợp lệ -> Cho phép tiếp tục truy cập
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
