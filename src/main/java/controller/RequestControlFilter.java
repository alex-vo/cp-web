package controller;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import persistence.HibernateUtil;
import persistence.User;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/21/13
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestControlFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();
        if(!uri.endsWith("/welcome") && !uri.endsWith("/login") &&
                ((HttpServletRequest) servletRequest).getSession().getAttribute("user") == null){
            ((HttpServletResponse)servletResponse).sendRedirect("welcome");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
