package controller;

import com.dropbox.client2.session.AccessTokenPair;
import components.Dropbox;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/21/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MusicController {

    // get all music list from specific folder
    // parameters: path, localization,
    @RequestMapping("/api/getMusicList")
    public String getMusicList(HttpSession httpSession) {

        // harcoded while DB not ready
        // user access token
        String ACCESS_TOKEN_KEY = "9wxd6qles4nsrw4";
        String ACCESS_TOKEN_SECRET = "r7cu66vih4xj5p";

        AccessTokenPair accessTokenPair = new AccessTokenPair(ACCESS_TOKEN_KEY, ACCESS_TOKEN_SECRET);
        Dropbox drop = new Dropbox(accessTokenPair);
        ArrayList<String> files = drop.getFileList("/", true, "mp3");
        System.out.println(files.toString());

        return "player";
    }

    // TODO how to pass file path -> RESTfull api
    // Not realized
    @RequestMapping("/api/getLink")
    public String getFileLink() {

        return "player";
    }
}
