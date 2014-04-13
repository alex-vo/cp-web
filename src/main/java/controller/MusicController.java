package controller;


import com.google.gson.Gson;
import ejb.ContentBeanRemote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import remote.CoreRemotingManager;
import structure.PlayList;
import structure.Song;

import javax.naming.NamingException;
import javax.servlet.http.HttpSession;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/21/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MusicController {

    final static Logger logger = LoggerFactory.getLogger(MusicController.class);

    @RequestMapping("/app")
    public String app(ModelMap model){
        return "player";
    }

    @RequestMapping("/api/getPlayList")
    public @ResponseBody PlayList getPlayList(HttpSession httpSession) {

        CoreRemotingManager remotingManager = null;
        PlayList playList = null;
        try {
            remotingManager = new CoreRemotingManager();
            ContentBeanRemote bean = remotingManager.geContentBeanRemote();

            playList = bean.getPlayList((Long) httpSession.getAttribute("user"));
            for(Song song:playList){
                logger.info("Song:" +song);
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return playList;
    }

    @RequestMapping("/api/saveSongMetadata")
    public @ResponseBody boolean saveSongMetadata(HttpSession httpSession,
                                               @RequestParam(value = "jsonSongObject", required = true) String jsonSongObject) {

        CoreRemotingManager remotingManager = null;

        boolean res = false;
        try {
            remotingManager = new CoreRemotingManager();
            ContentBeanRemote bean = remotingManager.geContentBeanRemote();

            Gson gson = new Gson();

            logger.debug("JsonString parameter:" + jsonSongObject);
            //convert the json string back to object
            Song songObj = gson.fromJson(jsonSongObject, Song.class);

            logger.debug("Json converted java object:" + songObj);

            Long userId = (Long) httpSession.getAttribute("user");
            logger.info(songObj + "  " + userId);
            res = bean.saveSongMetadata(songObj, userId);


        } catch (NamingException ne) {
            ne.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (remotingManager != null) {
                remotingManager.terminate();
            }
        }
        return res;
    }


    @RequestMapping("/api/getLink")
    public @ResponseBody String getFileLink(HttpSession httpSession,
                                            @RequestParam("cloud_id") Integer cloudId,
                                            @RequestParam("file_id") String fileId) {

        CoreRemotingManager remotingManager = null;
        String fileLink = null;
        try {
            remotingManager = new CoreRemotingManager();
            ContentBeanRemote bean = remotingManager.geContentBeanRemote();

            fileLink = bean.getFileSrc((Long) httpSession.getAttribute("user"), cloudId, fileId);
        } catch (NamingException ne) {
            ne.printStackTrace();
            fileLink = "error";
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(remotingManager != null){
                remotingManager.terminate();
            }
        }
        return fileLink;
    }
}
