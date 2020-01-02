package ir.piana.rayan.data.sql;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class InsertColumnDef {
    String name;
    String value;

    public InsertColumnDef(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }
}
