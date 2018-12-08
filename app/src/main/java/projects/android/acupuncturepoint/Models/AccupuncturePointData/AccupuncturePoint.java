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
    @SerializedName("operation")
    @Expose
    private String operation;
    @SerializedName("deportment")
    @Expose
    private String deportment;
    @SerializedName("fire")
    @Expose
    private String fire;
    @SerializedName("refer")
    @Expose
    private String refer;
    @SerializedName("x")
    @Expose
    private String x;
    @SerializedName("y")
    @Expose
    private String y;
    @SerializedName("delta")
    @Expose
    private String delta;
    @SerializedName("imgPos")
    @Expose
    private String imgPos;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("imgLink")
    @Expose
    private String imgLink;
    private final static long serialVersionUID = -1713689204839883490L;

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

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getDeportment() {
        return deportment;
    }

    public void setDeportment(String deportment) {
        this.deportment = deportment;
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

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getDelta() {
        return delta;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }

    public String getImgPos() {
        return imgPos;
    }

    public void setImgPos(String imgPos) {
        this.imgPos = imgPos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }
}
