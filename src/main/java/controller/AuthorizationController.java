package controller;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import persistence.HibernateUtil;
import persistence.User;

import javax.servlet.http.HttpSession;
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

    @RequestMapping(value = "/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/welcome";
    }
}
