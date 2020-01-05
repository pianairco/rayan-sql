package ir.rayan.dev.data.sql;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class UpdateColumnDef {
    String statement;

    public UpdateColumnDef(String statement) {
        this.statement = statement;
    }

    public String getStatement() {
        return statement;
    }

    void setStatement(String statement) {
        this.statement = statement;
    }
}
