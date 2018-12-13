package projects.android.acupuncturepoint.Models.WebHTML;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaiThuoc {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("link")
    @Expose
    private String link;
    private final static long serialVersionUID = 2179238832155633551L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
