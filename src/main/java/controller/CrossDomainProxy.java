package controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


public class CrossDomainProxy extends HttpServlet {


    @Value("#{localProperties['proxy.alloweddomains']}")
    public String allowedDomains;

    final static Logger logger = LoggerFactory.getLogger(CrossDomainProxy.class);

    private ServletContext servletContext;
    private HttpURLConnection con;
    private HttpServletRequest request;
    private HttpServletResponse response;


    public void init(ServletConfig servletConfig) throws ServletException {
        servletContext = servletConfig.getServletContext();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest clientRequest, HttpServletResponse webResponse) {

        request = clientRequest;
        response = webResponse;

        try {

            if (request.getMethod().equalsIgnoreCase("POST")) {
                throw new RuntimeException("POST method not supported");
            }

            URL url = getWebUrlFromQuery();

            //checkIsItAllowedURL(url);

            retransmitClientRequestToWeb(url);

            retransmitWebResponseToClient();

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkIsItAllowedURL(URL url) throws MalformedURLException {
        String urlDomain = url.getHost();

        logger.debug("checkIsItAllowedURL: "+allowedDomains);

        /*
        logger.debug(allowedDomains.toString());
        for(String value: allowedDomains){
            String allowedDomain = new URL(value).getHost();

            if (allowedDomain.equalsIgnoreCase(urlDomain) ){
                return;
            }
        }
        throw new MalformedURLException("URL: " + url + " not allowed");
        //*/
    }

    private void retransmitWebResponseToClient() throws IOException {
        int statusCode = con.getResponseCode();
        response.setStatus(statusCode);

        updateResponseHeaders();

        updateResponseContent();
    }

    private void retransmitClientRequestToWeb(URL url) throws IOException {

        try {

            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod(request.getMethod());
            con.setDoOutput(false);
            con.setDoInput(true);
            con.setFollowRedirects(true);
            con.setUseCaches(false);

            for (Enumeration e = request.getHeaderNames(); e.hasMoreElements(); ) {
                String headerName = e.nextElement().toString();
                con.setRequestProperty(headerName, request.getHeader(headerName));
                logger.debug(headerName + ": " + request.getHeader(headerName));
            }

            con.connect();

        } catch (IOException e) {
            throw e;
        } finally {
            con.disconnect();
        }

    }

    private void updateResponseHeaders() {
        logger.debug("Set response headers");
        for (Iterator i = con.getHeaderFields().entrySet().iterator(); i.hasNext(); ) {
            Map.Entry mapEntry = (Map.Entry) i.next();
            if (mapEntry.getKey() != null) {
                String headerName = mapEntry.getKey().toString();
                String headerValue = ((List) mapEntry.getValue()).get(0).toString();

                response.setHeader(headerName, headerValue);

                logger.debug(headerName + ": " + headerValue);
            }
        }
        // !!! doesn't set correct content-length without this method
        response.setContentLength(con.getContentLength());
    }

    private URL getWebUrlFromQuery() throws MalformedURLException {
        logger.debug(request.getQueryString());
        String requestLink = request.getQueryString().substring(2);
        URL url = new URL(requestLink);
        logger.info("Fetching >" + url.toString());

        return url;
    }

    private void updateResponseContent() throws IOException {

        BufferedInputStream webToProxyBuf = new BufferedInputStream(con.getInputStream());
        BufferedOutputStream proxyToClientBuf = new BufferedOutputStream(response.getOutputStream());

        int oneByte;
        while ((oneByte = webToProxyBuf.read()) != -1) {
            proxyToClientBuf.write(oneByte);
        }

        proxyToClientBuf.flush();
        proxyToClientBuf.close();

        webToProxyBuf.close();
    }


}
