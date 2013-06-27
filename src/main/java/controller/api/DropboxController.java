package controller.api;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import org.springframework.web.bind.annotation.RequestMapping;
import persistence.User;

import javax.servlet.http.HttpSession;
/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 13.25.6
 * Time: 20:47
 * To change this template use File | Settings | File Templates.
 */
public class DropboxController {

    // TODO separate file
    final static private String APP_KEY = "vw5zvh4m1fr72kw";
    final static private String APP_SECRET = "0wjwqy7k55y0lzi";

    // Define AccessType for DropboxAPI object
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER; //TODO another type
    private DropboxAPI mDBApi;


    // get all music list from specific folder
    // parameters: path, localization,
    @RequestMapping("/api/getList")
    public String app(HttpSession httpSession){

        User user = (User)httpSession.getAttribute("user");

        System.out.println("DropboxController.app1");

        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        WebAuthSession session = new WebAuthSession(appKeys, ACCESS_TYPE);
        mDBApi = new DropboxAPI<WebAuthSession>(session);

        return "player";
    }

    @RequestMapping("/api/getLink")
    public String app2(){
        System.out.println("DropboxController.app2");

        return "player";
    }

    // get mp3 by file path
}
