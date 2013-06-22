package controller;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import persistence.HibernateUtil;
import persistence.User;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/20/13
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class AuthorizationController {
    public static final String APP_KEY = "vw5zvh4m1fr72kw";
    public static final String APP_SECRET = "0wjwqy7k55y0lzi";
    public static final String CALLBACK_URL = "http://localhost:8080/CloudPlayer/dropboxAuthComplete";

    @RequestMapping("/welcome")
    public String printWelcome(ModelMap model) {
        model.addAttribute("message", "Spring 3 MVC Hello World");
        System.out.println("trololo");
        return "hello";

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam("login") String login, @RequestParam("password") String password, HttpSession httpSession){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Query query = session.createQuery("from User where login=:login and password=:password");
        query.setString("login", login);
        query.setString("password", password);
        List<User> list = query.list();
        if(list == null || list.size() < 1){
            return "redirect:/welcome";
        }
        httpSession.setAttribute("user", list.get(0));
        // comment 1 2
        return "redirect:/app";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/welcome";
    }

    @RequestMapping("/addDropbox")
    public String addDropbox(HttpSession httpSession){
        String request = "https://api.dropbox.com/1/oauth/request_token";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(request);

        post.addHeader("Authorization", "OAuth oauth_version=\"1.0\", oauth_signature_method=\"PLAINTEXT\", " +
                "oauth_consumer_key=\"" + APP_KEY + "\", oauth_signature=\"" + APP_SECRET + "&\"");

        try {
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    String responseString = IOUtils.toString(instream);
                    String secret = responseString.substring(19, responseString.indexOf('&'));
                    String token = responseString.substring(responseString.indexOf('&') + 13, responseString.length());
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction transaction = session.beginTransaction();
                    User currentUser = (User)httpSession.getAttribute("user");
                    currentUser.setDropboxToken(token);
                    currentUser.setDropboxSecret(secret);
                    session.update(currentUser);
                    transaction.commit();
                    session.close();
                    return "redirect:https://www.dropbox.com/1/oauth/authorize?oauth_token=" + token
                            + "&oauth_callback=" + CALLBACK_URL;
                } finally {
                    instream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "player";
    }

    @RequestMapping("dropboxAuthComplete")
    public String dropboxAuthComplete(HttpSession httpSession){
        String request = "https://api.dropbox.com/1/oauth/access_token";
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(request);
        User currentUser = (User) httpSession.getAttribute("user");
        post.addHeader("Authorization", "OAuth oauth_version=\"1.0\", oauth_signature_method=\"PLAINTEXT\", " +
                "oauth_consumer_key=\"" + APP_KEY + "\", oauth_token=\"" + currentUser.getDropboxToken() + "\", " +
                "oauth_signature=\"" + APP_SECRET + "&" + currentUser.getDropboxSecret() + "\"");

        try {
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    String responseString = IOUtils.toString(instream);
                    String secret = responseString.substring(19, responseString.indexOf('&'));
                    String token = responseString.substring(responseString.indexOf('&') + 13, responseString.indexOf("&uid"));
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction transaction = session.beginTransaction();
                    currentUser.setDropboxToken(token);
                    currentUser.setDropboxSecret(secret);
                    session.update(currentUser);
                    transaction.commit();
                    session.close();
                    return "redirect:app";
                } finally {
                    instream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:app";
    }
}
