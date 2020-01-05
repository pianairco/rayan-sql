package ir.rayan.dev.data.sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class UpdateDef {
    String name;
    String queryPattern;
    String updateQuery;
    List<SQLSelectAttribute> befores;
    List<SQLSelectAttribute> afters;
    Map<String, UpdateSourceDef> sourceMap;
    List<UpdateSourceDef> sources;
    List<UpdateColumnDef> columns;
    List<SQLWhereDef> wheres;
    List<Map.Entry<String[], SQLWhereDef>> whereParts;
    Map<String, SQLParamDef> paramMap;
    List<SQLParamDef> params;

    public UpdateDef() {
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

    public Map<String, UpdateSourceDef> getSourceMap() {
        return Collections.unmodifiableMap(sourceMap);
    }

    public List<UpdateSourceDef> getSources() {
        return Collections.unmodifiableList(sources);
    }

    public UpdateSourceDef getSelectSourceDef(String sourceName) {
        return sourceMap.get(sourceName);
    }

    public Set<String> getSourceNames() {
        return sourceMap.keySet();
    }

    public List<UpdateColumnDef> getColumns() {
        return Collections.unmodifiableList(columns);
    }

//    public Map<String, UpdateColumnDef> getColumnMap() {
//        return Collections.unmodifiableMap(columnMap);
//    }
//
//    public UpdateColumnDef getColumn(String columnName) {
//        return columnMap.get(columnName);
//    }
//
//    public Set<String> getColumnNames() {
//        return this.columnMap.keySet();
//    }

    public List<SQLWhereDef> getWheres() {
        return wheres;
    }

    void setWheres(List<SQLWhereDef> wheres) {
        this.wheres = wheres;
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
