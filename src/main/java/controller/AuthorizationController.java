package controller;

import common.LocalProperties;
import ejb.AuthorizationBeanRemote;
import model.LoginFormModel;
import model.RegisterFormModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import remote.CoreRemotingManager;

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

    private static String DROPBOX_AUTH_LINK = LocalProperties.getProperties().getProperty("dropbox.auth.link");
    private static String DRIVE_AUTH_LINK = LocalProperties.getProperties().getProperty("drive.auth.link");

    @RequestMapping("/welcome")
    public String printWelcome(ModelMap model) {
        if(!model.containsAttribute("loginForm")) model.addAttribute("loginForm", new LoginFormModel());
        return "hello";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@Valid @ModelAttribute("loginForm") LoginFormModel loginForm, BindingResult binding,
                        RedirectAttributes redirectAttributes, HttpSession httpSession){

        String result = "redirect:/welcome";
        if (binding.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginForm", binding);
            redirectAttributes.addFlashAttribute("loginForm", loginForm);
            redirectAttributes.addFlashAttribute("successMessage", "Failed to log in");
            return result;
        }

        CoreRemotingManager remotingManager = null;
        try {
            remotingManager = new CoreRemotingManager();
            AuthorizationBeanRemote bean = remotingManager.getAuthorizationBeanRemote();

            Long userId = bean.login(loginForm.getLogin(), loginForm.getPassword());
            if(userId != null && userId > 0){
                httpSession.setAttribute("user", userId);
                result = "redirect:/app";
            }else{
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to log in");
            }
        } catch (NamingException ne) {
            redirectAttributes.addFlashAttribute("serverErrorMessage", "Failed to connect the server");
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            remotingManager = null;
        }

        return result;
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/welcome";
    }

    @RequestMapping("/addDropbox")
    public String addDropbox(RedirectAttributes redirectAttributes, HttpSession httpSession){
        return "redirect:" + DROPBOX_AUTH_LINK;
    }

    @RequestMapping("/removeDropbox")
    public String removeDropbox(RedirectAttributes redirectAttributes, HttpSession httpSession){

        CoreRemotingManager remotingManager = null;
        try {
            remotingManager = new CoreRemotingManager();
            AuthorizationBeanRemote bean = remotingManager.getAuthorizationBeanRemote();

            Boolean removedDropboxAccount = bean.removeDropboxAcoount((Long) httpSession.getAttribute("user"));
            if(removedDropboxAccount){
                redirectAttributes.addFlashAttribute("successMessage", "Removed Dropbox account");
            }
        } catch (NamingException e) {
            redirectAttributes.addFlashAttribute("serverErrorMessage", "Failed to connect the server");
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return "redirect:app";
    }

    @RequestMapping("dropboxAuthComplete")
    public String dropboxAuthComplete(@RequestParam(value = "code") String code,
                                      RedirectAttributes redirectAttributes,
                                      HttpSession httpSession){

        CoreRemotingManager remotingManager = null;
        try {
            remotingManager = new CoreRemotingManager();
            AuthorizationBeanRemote bean = remotingManager.getAuthorizationBeanRemote();

            if(httpSession.getAttribute("user") != null){
                Boolean retrievedCredentials = bean.retrieveDropboxCredentials((Long) httpSession.getAttribute("user"), code);
                if(retrievedCredentials){
                    redirectAttributes.addFlashAttribute("successMessage", "Added Dropbox account");
                }else{
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to add Dropbox account");
                }
            }else{
                Long userId = bean.authorizeWithDropbox(code);
                if(userId != null){
                    redirectAttributes.addFlashAttribute("successMessage", "Signed in with Dropbox");
                    httpSession.setAttribute("user", userId);
                }else{
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to sign in with Dropbox");
                }
            }
        } catch (NamingException ne) {
            redirectAttributes.addFlashAttribute("serverErrorMessage", "Failed to connect the server");
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return "redirect:app";
    }

    @RequestMapping("/addDrive")
    public String addGDrive(HttpSession httpSession){
        return "redirect:" + DRIVE_AUTH_LINK;
    }

    @RequestMapping("/removeDrive")
    public String removeDrive(RedirectAttributes redirectAttributes, HttpSession httpSession){

        CoreRemotingManager remotingManager = null;
        try {
            remotingManager = new CoreRemotingManager();
            AuthorizationBeanRemote bean = remotingManager.getAuthorizationBeanRemote();

            Boolean removedDriveAccount = bean.removeGDriveAccount((Long) httpSession.getAttribute("user"));
            if(removedDriveAccount){
                redirectAttributes.addFlashAttribute("successMessage", "Removed Google Drive account");
            }
        } catch (NamingException e) {
            redirectAttributes.addFlashAttribute("serverErrorMessage", "Failed to connect the server");
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return "redirect:app";
    }

    @RequestMapping("driveAuthComplete")
    public String driveAuthComplete(@RequestParam(value = "code") String code,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession httpSession){
        CoreRemotingManager remotingManager = null;
        try {
            remotingManager = new CoreRemotingManager();
            AuthorizationBeanRemote bean = remotingManager.getAuthorizationBeanRemote();

            if(httpSession.getAttribute("user") != null){
                Boolean retrievedCredentials = bean.retrieveGDriveCredentials((Long) httpSession.getAttribute("user"), code);
                if(retrievedCredentials){
                    redirectAttributes.addFlashAttribute("successMessage", "Added Google Drive account");
                }else{
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to add Google Drive account");
                }
            }else{
                Long userId = bean.authorizeWithDrive(code);
                if(userId != null){
                    redirectAttributes.addFlashAttribute("successMessage", "Signed in with Google");
                    httpSession.setAttribute("user", userId);
                }else{
                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to sign in with Google");
                }
            }

        } catch (NamingException e) {
            redirectAttributes.addFlashAttribute("serverErrorMessage", "Failed to connect the server");
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
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

        CoreRemotingManager remotingManager = null;
        try {
            remotingManager = new CoreRemotingManager();
            AuthorizationBeanRemote bean = remotingManager.getAuthorizationBeanRemote();

            Boolean registered = bean.registerUser(registerFormModel.getLogin(), registerFormModel.getPassword());
            if(registered){
                redirectAttributes.addFlashAttribute("successMessage", "Registration completed successfully");
            }else{
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to register");
            }
        } catch (NamingException ne) {
            redirectAttributes.addFlashAttribute("serverErrorMessage", "Failed to connect the server");
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return "redirect:welcome";
    }
}
