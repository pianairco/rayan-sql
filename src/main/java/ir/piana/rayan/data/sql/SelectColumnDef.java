package ir.piana.rayan.data.sql;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SelectColumnDef {
    String name;
    String as;
    String type;

    public SelectColumnDef() {
    }

    public SelectColumnDef(String name, String type) {
        this(name, name, type);
    }

    public SelectColumnDef(String name, String as, String type) {
        this.name = name;
        if(as == null || as.isEmpty())
            this.as = name;
        else
            this.as = as;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getAs() {
        return as;
    }

    void setAs(String as) {
        if(as != null && !as.isEmpty())
            this.as = as;
    }

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }
}
