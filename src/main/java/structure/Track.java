package structure;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 1/23/14
 * Time: 12:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Track {
    private Integer cloudId;
    private String name;
    private String url;
    private String id;
    //TODO expiration time

    public Track(String[] rawData){
        this.cloudId = Integer.parseInt(rawData[0]);
        this.name = rawData[1];
        this.url = rawData[2];
        this.id = rawData[3];
    }

    public Integer getCloudId() {
        return cloudId;
    }

    public void setCloudId(Integer cloudId) {
        this.cloudId = cloudId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
