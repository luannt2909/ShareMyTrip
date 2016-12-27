package Model;

/**
 * Created by Nguyen Tan Luan on 11/9/2016.
 */

public class NavDrawerItem {
    private String showNotify;
    private String title;
    private int icon;

    public NavDrawerItem() {
    }

    public NavDrawerItem(String showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public NavDrawerItem(String showNotify, String title, int icon) {
        this.showNotify = showNotify;
        this.title = title;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getShowNotify() {
        return showNotify;
    }

    public void setShowNotify(String showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
