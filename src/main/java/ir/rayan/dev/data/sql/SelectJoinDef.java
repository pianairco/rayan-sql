package ir.rayan.dev.data.sql;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SelectJoinDef {
    String type;
    String left;
    String right;
    String on;

    public SelectJoinDef() {
    }

    public SelectJoinDef(String type, String left, String right, String on) {
        this.type = type;
        this.left = left;
        this.right = right;
        this.on = on;
    }

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    public String getLeft() {
        return left;
    }

    void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    void setRight(String right) {
        this.right = right;
    }

    public String getOn() {
        return on;
    }

    void setOn(String on) {
        this.on = on;
    }
}
