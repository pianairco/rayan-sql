package ir.piana.rayan.data.sql;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SelectOrderDef {
    String by;
    String order;

    public SelectOrderDef(String by) {
        this(by, "asc");
    }

    public SelectOrderDef(String by, String order) {
        this.by = by;
        this.order = order;
    }

    public String getBy() {
        return by;
    }

    void setBy(String by) {
        this.by = by;
    }

    public String getOrder() {
        return order;
    }

    void setOrder(String order) {
        this.order = order;
    }
}
