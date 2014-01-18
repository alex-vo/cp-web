package controller;

import ejb.AuthorizationBeanRemote;
import model.LoginFormModel;
import model.RegisterFormModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import remote.RemotingManager;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/20/13
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class AuthorizationController {

    @Value("#{localProperties['dropbox. callback.url']}")
    public String DROPBOX_CALLBACK_URL;

    @Value("#{localProperties['drive.redirect.uri']}")
    public String DRIVE_REDIRECT_URI;

    @Value("#{localProperties['jboss.login']}")
    public String JBOSS_LOGIN;

    @Value("#{localProperties['jboss.password']}")
    public String JBOSS_PASSWORD;

    @Value("#{localProperties['jboss.url']}")
    public String JBOSS_URL;

    @RequestMapping("/welcome")
    public String printWelcome(ModelMap model) {
        if(!model.containsAttribute("loginForm")) model.addAttribute("loginForm", new LoginFormModel());
        return "hello";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Valid @ModelAttribute("loginForm") LoginFormModel loginForm, BindingResult binding,
                        RedirectAttributes redirectAttributes, HttpSession httpSession){
        if (binding.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginForm", binding);
            redirectAttributes.addFlashAttribute("loginForm", loginForm);
            redirectAttributes.addFlashAttribute("successMessage", "Failed to log in");
            return "redirect:/welcome";
        }
        try {
            RemotingManager remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            Long userId = bean.login(loginForm.getLogin(), loginForm.getPassword());
            if(userId != null && userId > 0){
                httpSession.setAttribute("user", userId);
                return "redirect:/app";
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("successMessage", "Failed to log in");
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
            RemotingManager remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            String dropboxUrl = bean.getDropboxAuthLink((Long) httpSession.getAttribute("user"));
            if(dropboxUrl != null){
                return "redirect:" + dropboxUrl + "&oauth_callback=" + DROPBOX_CALLBACK_URL; //TODO move this to core
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return "player";
    }

    @RequestMapping("dropboxAuthComplete")
    public String dropboxAuthComplete(RedirectAttributes redirectAttributes, HttpSession httpSession){
        try {
            //TODO make static
            RemotingManager remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            Boolean retrievedToken = bean.retrieveDropboxAccessToken((Long) httpSession.getAttribute("user"));
            if(retrievedToken){
                redirectAttributes.addFlashAttribute("successMessage", "Added Dropbox account");
                return "redirect:app";
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Failed to add Dropbox account");
        return "redirect:app";
    }

    @RequestMapping("/addDrive")
    public String addGDrive(HttpSession httpSession){
        return "redirect:https://accounts.google.com/o/oauth2/auth?redirect_uri="
                + DRIVE_REDIRECT_URI
                + "&response_type=code&client_id=737947483653-ul7ktdr1srcrbe4qt1kkednt8te0qfh9.apps.googleusercontent.com&scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fdrive&approval_prompt=force&access_type=offline";
    }

    @RequestMapping("driveAuthComplete")
    public String driveAuthComplete(@RequestParam(value = "code") String code,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession httpSession){
        try {
            RemotingManager remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            Boolean retrievedCredentials = bean.retrieveGDriveCredentials((Long) httpSession.getAttribute("user"), code);
            if(retrievedCredentials){
                redirectAttributes.addFlashAttribute("successMessage", "Added Google Drive account");
                return "redirect:app";
            }
        } catch (NamingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "redirect:app";
    }

    @RequestMapping("/registerForm")
    public String registerForm(ModelMap model){
        if(!model.containsAttribute("registerForm")) model.addAttribute("registerForm", new RegisterFormModel());
        return "registerForm";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@Valid @ModelAttribute("registerForm") RegisterFormModel registerFormModel,
                           BindingResult binding,
                           RedirectAttributes redirectAttributes){
        if(!registerFormModel.getPassword().equals(registerFormModel.getRepeatPassword())){
            binding.addError(new FieldError("loginForm", "password", registerFormModel.getPassword(), true, null, null, "Passwords must match"));
        }
        if(binding.hasErrors()){
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.registerForm", binding);
            redirectAttributes.addFlashAttribute("registerForm", registerFormModel);
            return "redirect:registerForm";
        }
        try {
            //TODO make static
            RemotingManager remotingManager = new RemotingManager(JBOSS_URL, JBOSS_LOGIN, JBOSS_PASSWORD);
            Context context = remotingManager.getContext();
            AuthorizationBeanRemote bean = (AuthorizationBeanRemote) context
                    .lookup("ejb:/cp-core//AuthorizationBean!ejb.AuthorizationBeanRemote");
            Boolean registered = bean.registerUser(registerFormModel.getLogin(), registerFormModel.getPassword());
            if(registered){
                redirectAttributes.addFlashAttribute("successMessage", "Registration completed successfully");
                return "redirect:/welcome";
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Failed to register");
        return "redirect:welcome";
    }
}
