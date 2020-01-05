package ir.rayan.dev.data.sql;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SQLParamDef {
    String name;
    String type;
    String key;
    String defaultValue;

    public SQLParamDef(String name, String type, String key) {
        this(name, type, key, null);
    }

    public SQLParamDef(String name, String type, String key, String defaultValue) {
        this.name = name;
        this.type = type;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
