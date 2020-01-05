package ir.rayan.dev.data.sql;

/**
 * Created by mj.rahmati on 12/28/2019.
 */
public class SQLSelectAttribute {
    String attributeName;
    String queryName;

    public SQLSelectAttribute() {
    }

    public SQLSelectAttribute(String attributeName, String queryName) {
        this.attributeName = attributeName;
        this.queryName = queryName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getQueryName() {
        return queryName;
    }

    void setQueryName(String queryName) {
        this.queryName = queryName;
    }
}
