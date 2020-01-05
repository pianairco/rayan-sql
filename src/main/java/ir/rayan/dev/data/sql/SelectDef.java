package ir.rayan.dev.data.sql;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SelectDef {
    String name;
    String selectType;
    Class entityClazz;
    String queryPattern;
    String fromString;
    String leftQueryString;
    List<SQLExecuteIf> sqlExecuteIfs;
    List<SQLSelectAttribute> befores;
    List<SQLSelectAttribute> afters;
    Map<String, SelectSourceDef> sourceMap;
    List<SelectSourceDef> sources;
    Map<String, SelectColumnDef> columnMap;
    List<SelectColumnDef> columns;
    List<SQLWhereDef> wheres;
    List<Map.Entry<String[], SQLWhereDef>> whereParts;
    List<String> groups;
    List<SelectOrderDef> orders;
    List<Map.Entry<Map.Entry<String, Boolean>, Map.Entry<String, Boolean>>> orderParts;
    Map<String, SQLParamDef> paramMap;
    List<SQLParamDef> params;

    public SelectDef() {
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getSelectType() {
        return selectType;
    }

    void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public String getQueryPattern() {
        return queryPattern;
    }

    void setQueryPattern(String queryPattern) {
        this.queryPattern = queryPattern;
    }

    public Map<String, SelectSourceDef> getSourceMap() {
        return Collections.unmodifiableMap(sourceMap);
    }

    public List<SelectSourceDef> getSources() {
        return Collections.unmodifiableList(sources);
    }

    public SelectSourceDef getSelectSourceDef(String sourceName) {
        return sourceMap.get(sourceName);
    }

    public Set<String> getSourceNames() {
        return sourceMap.keySet();
    }

    public Map<String, SelectColumnDef> getColumnMap() {
        return Collections.unmodifiableMap(columnMap);
    }

    public List<SelectColumnDef> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    public SelectColumnDef getColumn(String columnName) {
        return columnMap.get(columnName);
    }

    public Set<String> getColumnNames() {
        return this.columnMap.keySet();
    }

    public List<SQLWhereDef> getWheres() {
        return wheres;
    }

    void setWheres(List<SQLWhereDef> wheres) {
        this.wheres = wheres;
    }

    public List<String> getGroups() {
        return groups;
    }

    void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<SelectOrderDef> getOrders() {
        return orders;
    }

    void setOrders(List<SelectOrderDef> orders) {
        this.orders = orders;
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
