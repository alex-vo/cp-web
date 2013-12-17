package controller;

import ejb.AuthorizationBeanRemote;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import remote.RemotingManager;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/20/13
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class AuthorizationController {
    //TODO put into .properties
    public static final String CALLBACK_URL = "http://localhost:9090/cp-web/dropboxAuthComplete";

    @RequestMapping("/welcome")
    public String printWelcome(ModelMap model) {
        return "hello";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam("login") String login, @RequestParam("password") String password, HttpSession httpSession){
        try {
            RemotingManager remotingManager = new RemotingManager();
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            Long userId = bean.login(login, password);
            if(userId != null && userId > 0){
                httpSession.setAttribute("user", userId);
                return "redirect:/app";
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/welcome";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/welcome";
    }

    @RequestMapping("/addDropbox")
    public String addDropbox(HttpSession httpSession){
        try {
            //TODO make static
            RemotingManager remotingManager = new RemotingManager();
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            String dropboxUrl = bean.getDropboxAuthLink((Long) httpSession.getAttribute("user"));
            if(dropboxUrl != null){
                return "redirect:" + dropboxUrl + "&oauth_callback=" + CALLBACK_URL; //TODO move this to core
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "player";
    }

    @RequestMapping("dropboxAuthComplete")
    public String dropboxAuthComplete(HttpSession httpSession){
        try {
            //TODO make static
            RemotingManager remotingManager = new RemotingManager();
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            Boolean retrievedToken = bean.retrieveDropboxAccessToken((Long) httpSession.getAttribute("user"));
            if(retrievedToken){
                //TODO add success message
                return "redirect:app";
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        //TODO add error message
        return "redirect:app";
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
            return "redirect:registerForm";
        }
        try {
            //TODO make static
            RemotingManager remotingManager = new RemotingManager();
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            Boolean registered = bean.registerUser(login, password);
            if(registered){
                //TODO add success message
                return "redirect:welcome";
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        //TODO add error
        return "redirect:welcome";
    }
}
