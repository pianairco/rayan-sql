package ir.rayan.dev.data.sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class InsertDef {
    String name;
    String queryPattern;
    String insertQuery;
    List<SQLExecuteIf> sqlExecuteIfs;
    List<SQLSelectAttribute> befores;
    List<SQLSelectAttribute> afters;
    Map<String, InsertColumnDef> columnMap;
    List<InsertColumnDef> columns;
    Map<String, SQLParamDef> paramMap;
    List<SQLParamDef> params;

    public InsertDef() {
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getQueryPattern() {
        return queryPattern;
    }

    void setQueryPattern(String queryPattern) {
        this.queryPattern = queryPattern;
    }

    public Map<String, InsertColumnDef> getColumnMap() {
        return Collections.unmodifiableMap(columnMap);
    }

    public List<InsertColumnDef> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public InsertColumnDef getColumn(String columnName) {
        return columnMap.get(columnName);
    }

    public Set<String> getColumnNames() {
        return this.columnMap.keySet();
    }

    public Map<String, SQLParamDef> getParamMap() {
        return Collections.unmodifiableMap(paramMap);
    }

    public List<SQLParamDef> getParams() {
        return Collections.unmodifiableList(params);
    }

    public SQLParamDef getSelectParamDef(String paramName) {
        return paramMap.get(paramName);
    }

    public Set<String> getParamNames() {
        return paramMap.keySet();
    }
}
