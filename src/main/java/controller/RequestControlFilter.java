package controller;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/21/13
 * Time: 7:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestControlFilter implements Filter {

    private Set<String> localAddresses = new HashSet<String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            localAddresses.add(InetAddress.getLocalHost().getHostAddress());
            for (InetAddress inetAddress : InetAddress.getAllByName("localhost")) {
                localAddresses.add(inetAddress.getHostAddress());
            }
        } catch (IOException e) {
            throw new ServletException("Unable to lookup local addresses");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        String uri = ((HttpServletRequest) servletRequest).getRequestURI();
        if(!uri.endsWith("/register") && !uri.endsWith("/registerForm") && !uri.endsWith("/welcome")
                && !uri.endsWith("/login") && !localAddresses.contains(servletRequest.getRemoteAddr())
                && ((HttpServletRequest) servletRequest).getSession().getAttribute("user") == null){
            ((HttpServletResponse)servletResponse).sendRedirect("welcome");
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
