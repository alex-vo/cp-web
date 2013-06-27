package controller;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/21/13
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
// TODO: im, create separate package, deny use API controllers
public class RequestControlFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        // TODO: IM, change access architecture
        //      1. welcome, about ...
        // if( user logged in ) {
        //      2. player, logout,  ..
        // } else{
        //      3. help, login, ...
        // }

        if( ((HttpServletRequest) servletRequest).getSession().getAttribute("user") != null ){
            // logged in
            String uri = ((HttpServletRequest) servletRequest).getRequestURI();
            if(!uri.endsWith("/welcome") && !uri.endsWith("/login") &&
                    ((HttpServletRequest) servletRequest).getSession().getAttribute("user") == null){
                System.out.println("RequestControlFilter.doFilter 2");
                ((HttpServletResponse)servletResponse).sendRedirect("welcome");
                System.out.println("RequestControlFilter.doFilter 4");
            }
        } else{
            System.out.println("RequestControlFilter.doFilter 3");
            // not logged in
            ((HttpServletResponse)servletResponse).sendRedirect("welcome");
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
