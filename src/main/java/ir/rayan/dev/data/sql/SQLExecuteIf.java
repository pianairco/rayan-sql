package ir.rayan.dev.data.sql;

/**
 * Created by mj.rahmati on 12/28/2019.
 */
public class SQLExecuteIf {
    String queryForInt;
    String evaluation;
    String target;

    SQLExecuteIf() {
    }

    public SQLExecuteIf(String queryForInt, String evaluation, String target) {
        this.queryForInt = queryForInt;
        this.evaluation = evaluation;
        this.target = target;
    }

    public String getQueryForInt() {
        return queryForInt;
    }

    void setQueryForInt(String queryForInt) {
        this.queryForInt = queryForInt;
    }

    public String getEvaluation() {
        return evaluation;
    }

    void setEvaluation(String evaluation) {
        this.evaluation = evaluation;
    }

    public String getTarget() {
        return target;
    }

    void setTarget(String target) {
        this.target = target;
    }
}
