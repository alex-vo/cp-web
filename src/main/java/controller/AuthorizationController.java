package controller;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
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
    public static final String DROPBOX_ACCESS_TOKEN = "https://api.dropbox.com/1/oauth/access_token";
    public static final String DROPBOX_REQUEST_TOKEN = "https://api.dropbox.com/1/oauth/request_token";
    public static final String CALLBACK_URL = "http://localhost:8080/CloudPlayer/dropboxAuthComplete";

    @RequestMapping("/welcome")
    public String printWelcome(ModelMap model) {
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
        User currentUser = (User)httpSession.getAttribute("user");
        try{
            getDropboxToken(DROPBOX_REQUEST_TOKEN, currentUser, new BasicHeader("Authorization",
                "OAuth oauth_version=\"1.0\", oauth_signature_method=\"PLAINTEXT\", " +
                "oauth_consumer_key=\"" + APP_KEY + "\", oauth_signature=\"" + APP_SECRET + "&\""));
            return "redirect:https://www.dropbox.com/1/oauth/authorize?oauth_token=" + currentUser.getDropboxToken()
                    + "&oauth_callback=" + CALLBACK_URL;
        }catch (Exception e){
            e.printStackTrace();
        }
        return "player";
    }

    @RequestMapping("dropboxAuthComplete")
    public String dropboxAuthComplete(HttpSession httpSession){
        User currentUser = (User) httpSession.getAttribute("user");
        getDropboxToken(DROPBOX_ACCESS_TOKEN, currentUser, new BasicHeader("Authorization", "OAuth oauth_version=\"1.0\", " +
                "oauth_signature_method=\"PLAINTEXT\", oauth_consumer_key=\"" + APP_KEY + "\", oauth_token=\""
                + currentUser.getDropboxToken() + "\", oauth_signature=\"" + APP_SECRET + "&"
                + currentUser.getDropboxSecret() + "\""));
        return "redirect:app";
    }

    private void getDropboxToken(String url, User user, Header header){
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);
        post.addHeader(header);

        try {
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    String responseString = IOUtils.toString(instream);
                    System.out.println(responseString);
                    String secret = responseString.substring(responseString.indexOf("oauth_token_secret=") + 19,
                            responseString.indexOf('&'));
                    String token = responseString.substring(responseString.indexOf("oauth_token=") + 12,
                            responseString.indexOf("&uid") > 0 ? responseString.indexOf("&uid") : responseString.length());
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction transaction = session.beginTransaction();
                    user.setDropboxToken(token);
                    user.setDropboxSecret(secret);
                    session.update(user);
                    transaction.commit();
                    session.close();
                } finally {
                    instream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/registerForm")
    public String registerForm(){
        return "registerForm";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam("login") String login,
                           @RequestParam("password") String password,
                           @RequestParam("password_repeat") String passwordRepeat){
        if(!password.equals(passwordRepeat) || password.length() < 5 || login.length() < 5){
            //TODO add error
            return "redirect:welcome";
        }

        return "redirect:welcome";
    }
}
