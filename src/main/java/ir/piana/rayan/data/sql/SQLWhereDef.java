package ir.piana.rayan.data.sql;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SQLWhereDef {
    String clause;
    String conjunction;
    boolean force;

    public SQLWhereDef(String clause, String conjunction) {
        this(clause, conjunction, false);
    }

    public SQLWhereDef(String clause, String conjunction, boolean force) {
        this.clause = clause;
        this.conjunction = conjunction != null ? conjunction : "";
        this.force = force;
    }

    public String getClause() {
        return clause;
    }

    void setClause(String clause) {
        this.clause = clause;
    }

    public String getConjunction() {
        return conjunction;
    }

    void setConjunction(String conjunction) {
        this.conjunction = conjunction;
    }

    public boolean isForce() {
        return force;
    }

    void setForce(boolean force) {
        this.force = force;
    }
}
