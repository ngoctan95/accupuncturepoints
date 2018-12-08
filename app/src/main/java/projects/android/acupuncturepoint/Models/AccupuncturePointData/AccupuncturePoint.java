package projects.android.acupuncturepoint.Models.AccupuncturePointData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccupuncturePoint {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("anotherName")
    @Expose
    private String anotherName;
    @SerializedName("attributes")
    @Expose
    private String attributes;
    @SerializedName("position")
    @Expose
    private String position;
    @SerializedName("dissection")
    @Expose
    private String dissection;
    @SerializedName("deportment")
    @Expose
    private String deportment;
    @SerializedName("treatment")
    @Expose
    private String treatment;
    @SerializedName("fire")
    @Expose
    private String fire;
    @SerializedName("refer")
    @Expose
    private String refer;
    @SerializedName("img")
    @Expose
    private String img;
    @SerializedName("x")
    @Expose
    private Integer x;
    @SerializedName("y")
    @Expose
    private Integer y;
    @SerializedName("delta")
    @Expose
    private Integer delta;
    @SerializedName("id")
    @Expose
    private Integer id;
    private final static long serialVersionUID = -4109717450935967805L;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnotherName() {
        return anotherName;
    }

    public void setAnotherName(String anotherName) {
        this.anotherName = anotherName;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDissection() {
        return dissection;
    }

    public void setDissection(String dissection) {
        this.dissection = dissection;
    }

    public String getDeportment() {
        return deportment;
    }

    public void setDeportment(String deportment) {
        this.deportment = deportment;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getFire() {
        return fire;
    }

    public void setFire(String fire) {
        this.fire = fire;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
