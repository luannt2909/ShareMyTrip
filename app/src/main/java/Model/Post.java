package Model;

/**
 * Created by luannguyen on 22/12/2016.
 */

public class Post {
    private String authour;
    private String description;
    private String urlRoad;

    public Post() {
    }

    public Post(String authour, String description, String urlRoad) {
        this.authour = authour;
        this.description = description;
        this.urlRoad = urlRoad;
    }

    public String getAuthour() {
        return authour;
    }

    public void setAuthour(String authour) {
        this.authour = authour;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlRoad() {
        return urlRoad;
    }

    public void setUrlRoad(String urlRoad) {
        this.urlRoad = urlRoad;
    }
}
