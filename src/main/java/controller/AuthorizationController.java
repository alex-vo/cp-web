package controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String printWelcome(ModelMap model, @RequestParam("page") String page) {
        System.out.println(page);
        model.addAttribute("message", "Spring 3 MVC Hello World");
        return "hello";

    }

}
