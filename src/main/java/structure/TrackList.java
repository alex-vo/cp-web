package structure;

import commons.CloudFile;

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
    private String errorMessage;
    private List<CloudFile> songs;

    public TrackList(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public TrackList(List<CloudFile> data){
        this.songs = data;
    }

    public List<CloudFile> getSongs() {
        return songs;
    }

    public void setSongs(List<CloudFile> songs) {
        this.songs = songs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
