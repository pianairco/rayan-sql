package ir.piana.rayan.data.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mj.rahmati on 12/16/2019.
 */
@SuppressWarnings("Duplicates")
public class InsertDefCreator {
    private static void fillInsertColumns(Map map, InsertDef insertDef) {
        List<Map<String, String>> columnDefs = (List<Map<String, String>>)map.get("columns");
        List<InsertColumnDef> columns = new ArrayList<>();
        for(Map<String, String> columnDef : columnDefs) {
            String name = columnDef.get("name");
            String value = columnDef.get("value");
            InsertColumnDef insertColumnDef = new InsertColumnDef(name, value);
            columns.add(insertColumnDef);
        }
        insertDef.columns = columns;
    }

    private static void fillParams(Map map, InsertDef insertDef) {
        List<Map<String, String>> paramDefs = (List<Map<String, String>>)map.get("params");
        Map<String, SQLParamDef> paramMap = new LinkedHashMap<>();
        List<SQLParamDef> params = new ArrayList<>();
        if(paramDefs != null) {
            for (Map<String, String> paramDef : paramDefs) {
                String name = paramDef.get("name");
                String type = paramDef.get("type");
                String key = paramDef.get("key");
                String defaultValue = paramDef.get("default-value");
                SQLParamDef selectSQLParamDef = new SQLParamDef(name, type, key, defaultValue);
                paramMap.put(name, selectSQLParamDef);
                params.add(selectSQLParamDef);
            }
        }
        insertDef.paramMap = paramMap;
        insertDef.params = params;
    }

    private static List<SQLSelectAttribute> getSelectAttribute(List<Map<String, String>> selectAttributes) {
        List<SQLSelectAttribute> sqlSelectAttributes = new ArrayList<>();
        if(selectAttributes != null) {
            for (Map<String, String> selectAttribute : selectAttributes) {
                String attrName = selectAttribute.get("attribute-name");
                String queryName = selectAttribute.get("query-name");
                sqlSelectAttributes.add(new SQLSelectAttribute(attrName, queryName));
            }
        }
        return sqlSelectAttributes;
    }

    private static List<SQLExecuteIf> getExecuteIfs(List<Map<String, String>> executeIfAttributes) {
        List<SQLExecuteIf> sqlExecuteIfs = new ArrayList<>();
        if(executeIfAttributes != null) {
            for (Map<String, String> map : executeIfAttributes) {
                String queryForInt = map.get("query-for-int");
                String evaluation = map.get("evaluation");
                String target = map.get("target");
                sqlExecuteIfs.add(new SQLExecuteIf(queryForInt, evaluation, target));
            }
        }
        return sqlExecuteIfs;
    }

    private static void fillInsertQuery(InsertDef insertDef) {
        QueryElements queryElements = QueryElements.getQueryElements(insertDef);
        StringBuffer insertBuffer = new StringBuffer(queryElements.insert);
        insertDef.insertQuery = insertBuffer.toString();
    }



    public static InsertDef createSQLDef(Map map) {
        InsertDef insertDef = new InsertDef();
        insertDef.name = (String)map.get("source-name");
        insertDef.queryPattern = (String)map.get("query-pattern");
        insertDef.sqlExecuteIfs = getExecuteIfs((List<Map<String, String>>)map.get("execute-if"));
        insertDef.befores = getSelectAttribute((List<Map<String, String>>)map.get("before"));
        insertDef.afters = getSelectAttribute((List<Map<String, String>>)map.get("after"));
        if(insertDef.afters == null)
            insertDef.afters = new ArrayList<>();
        fillInsertColumns(map, insertDef);
        fillParams(map, insertDef);
        fillInsertQuery(insertDef);
        return insertDef;
    }

    private static class QueryElements {
        String insert;

        private QueryElements(String insert) {
            this.insert = insert;
        }

        public static QueryElements getQueryElements(InsertDef insertDef) {
            String queryPattern = insertDef.queryPattern;
            Pattern pattern = Pattern.compile("#(.*?)#");
            Matcher matcher = pattern.matcher(queryPattern);
            String insert = null;
            if (matcher.find()) {
                String group = matcher.group();
                String[] split = queryPattern.split(Pattern.quote(group));
                insert = split[0];
            } else {
                insert = queryPattern;
            }
            return new QueryElements(insert);
        }
    }
}
