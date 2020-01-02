package ir.piana.rayan.data.sql;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SelectSourceDef {
    String name;
    String alias;
    String type;

    public SelectSourceDef() {
    }

    public SelectSourceDef(String name, String type) {
        this(name, name, type);
    }

    public SelectSourceDef(String name, String alias, String type) {
        this.name = name;
        if(alias == null || alias.isEmpty())
            this.alias = name;
        else
            this.alias = alias;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    void setAlias(String alias) {
        if(alias != null && !alias.isEmpty())
            this.alias = alias;
    }

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }
}
