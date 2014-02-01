package structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 7/20/13
 * Time: 2:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrackList {
    private List<Track> songs;

    public TrackList(List<String[]> rawData){
        this.songs = new ArrayList<Track>();
        for(String[] currentSongData : rawData){
            this.songs.add(new Track(currentSongData));
        }
    }

    public List<Track> getSongs() {
        return songs;
    }

    public void setSongs(List<Track> songs) {
        this.songs = songs;
    }
}
