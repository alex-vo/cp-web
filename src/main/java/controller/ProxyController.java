package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 14.29.1
 * Time: 20:52
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class ProxyController {

    @Value("#{localProperties['proxy.allowed.domains'].split(',')}")
    private List<String> allowedDomains;

    final static Logger logger = LoggerFactory.getLogger(ProxyController.class);

    private HttpURLConnection con;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @RequestMapping("/crossDomain")
    public void crossDomainRequest(HttpServletRequest clientRequest, HttpServletResponse webResponse) {

        request = clientRequest;
        response = webResponse;

        try {

            if (request.getMethod().equalsIgnoreCase("POST")) {
                throw new RuntimeException("POST method not supported");
            }

            URL url = getWebUrlFromQuery();

            checkIsItAllowedURL(url);

            retransmitClientRequestToWeb(url);

            retransmitWebResponseToClient();

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkIsItAllowedURL(URL url) throws MalformedURLException {
        String urlDomain = url.getHost();

        logger.debug("allowedUrlDomains: " + allowedDomains);
        logger.debug("requestedUrlDomain:" + urlDomain);

        for (String value : allowedDomains) {
            if (value.equalsIgnoreCase(urlDomain)) {
                return;
            }
        }
        throw new MalformedURLException("URL domain: " + url + " not allowed");
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
        logger.info("Request link >" + url.toString());

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
