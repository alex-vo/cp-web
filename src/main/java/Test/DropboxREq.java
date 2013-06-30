package Test;

import com.dropbox.client2.session.AccessTokenPair;
import components.Dropbox;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: vanstr
 * Date: 13.30.6
 * Time: 22:26
 * Test
 */
public class DropboxREq {


    public static void main(String[] args) {

        // user access token
        String ACCESS_TOKEN_KEY = "9wxd6qles4nsrw4";
        String ACCESS_TOKEN_SECRET = "r7cu66vih4xj5p";

        AccessTokenPair accessTokenPair = new AccessTokenPair(ACCESS_TOKEN_KEY, ACCESS_TOKEN_SECRET);
        Dropbox drop = new Dropbox(accessTokenPair);
        ArrayList<String> files = drop.getFileList("/", true, "mp3");
        System.out.println(files.toString());
        System.out.println(drop.getFileLink(files.get(1)));

    }
}
