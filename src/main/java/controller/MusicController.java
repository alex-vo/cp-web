package controller;

import bo.TrackList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 6/21/13
 * Time: 4:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
public class MusicController {
    @RequestMapping("/app")
    public String app(){
        return "player";
    }

    @RequestMapping("/getTrackList")
    public @ResponseBody TrackList getTrackList(){
        TrackList trackList = new TrackList();
        List<String> songs = new ArrayList<String>();
        songs.add("Kalimba.mp3");
        songs.add("Maid with the Flaxen Hair.mp3");
        trackList.songs = songs;
        return trackList;
    }

    @RequestMapping("/getSongSrc")
    @ResponseBody
    public String getSongSrc(@RequestParam("name") String name){
        if("Kalimba.mp3".equals(name)){
            return "https://dl.dropboxusercontent.com/1/view/gnjd5g96fp8hbw6/Kalimba.mp3";
        }
        if("Maid with the Flaxen Hair.mp3".equals(name)){
            return "https://dl.dropboxusercontent.com/1/view/jx28pqwdck0bg4q/Maid%20with%20the%20Flaxen%20Hair.mp3";
        }
        return "";
    }
}
