package ir.rayan.dev.data.sql;

/**
 * Created by mj.rahmati on 12/16/2019.
 */
public enum SQLType {
    UNKNOWN(""),
    SELECT("select"),
    UPDATE("update"),
    INSERT("insert"),
    DELETE("delete");

    private String name;

    SQLType(String name) {
        this.name = name;
    }

    public static SQLType fromName(String name) {
        for(SQLType sqlType : SQLType.values()) {
            if(sqlType.name.equals(name))
                return sqlType;
        }
        return UNKNOWN;
    }
}
