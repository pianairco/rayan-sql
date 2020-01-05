package ir.rayan.dev.data.sql;

import java.util.*;

/**
 * Created by mj.rahmati on 12/16/2019.
 */
@SuppressWarnings("Duplicates")
public class UpdateDefCreator {
    private final static String UPDATE = "update ";
    private final static String SET = " set ";
    private final static String WHERE = " where ";

    private static void fillSources(Map map, UpdateDef updateDef) {
        List<Map<String, String>> sourceDefs = (List<Map<String, String>>)map.get("sources");
        Map<String, UpdateSourceDef> sourceMap = new LinkedHashMap<>();
        List<UpdateSourceDef> sources = new ArrayList<>();
        for(Map<String, String> sourceDef : sourceDefs) {
            String name = sourceDef.get("name");
            String alias = sourceDef.get("alias");
            UpdateSourceDef selectSourceDef = new UpdateSourceDef(name, alias);
            sourceMap.put(alias, selectSourceDef);
            sources.add(selectSourceDef);
        }
        updateDef.sourceMap = sourceMap;
        updateDef.sources = sources;
    }

    private static void fillUpdateColumns(Map map, UpdateDef updateDef) {
        List<Map<String, String>> columnDefs = (List<Map<String, String>>)map.get("columns");
        List<UpdateColumnDef> columns = new ArrayList<>();
        for(Map<String, String> columnDef : columnDefs) {
            String statement = columnDef.get("statement");
            UpdateColumnDef updateColumnDef = new UpdateColumnDef(statement);
            columns.add(updateColumnDef);
        }
        updateDef.columns = columns;
    }

    private static void fillWheres(Map map, UpdateDef updateDef) {
        List<Map<String, String>> whereDefs = (List<Map<String, String>>)map.get("wheres");
        List<SQLWhereDef> wheres = new ArrayList<>();
        if(whereDefs != null) {
            for (Map<String, String> whereDef : whereDefs) {
                String clause = whereDef.get("clause");
                String conjunction = whereDef.get("conjunction");
                String force = whereDef.get("force-if-null");
                wheres.add(new SQLWhereDef(clause, conjunction, force != null && force.equalsIgnoreCase("true")));
            }
        }
        updateDef.wheres = wheres;
    }

    private static void fillParams(Map map, UpdateDef updateDef) {
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
        updateDef.paramMap = paramMap;
        updateDef.params = params;
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

    private static void fillUpdateQuery(UpdateDef updateDef) {
        QueryElements queryElements = QueryElements.getQueryElements(updateDef);
        StringBuffer updateBuffer = new StringBuffer(UPDATE);
        String[] splited = queryElements.update.split(",");
        for (String s : splited)
            updateBuffer.append(updateDef.sourceMap.get(s.trim()).name + " " + s.trim() + ", ");
        updateBuffer.deleteCharAt(updateBuffer.length() - 2);
        updateBuffer.append(SET);
        updateDef.updateQuery = updateBuffer.toString();

//        Pattern pattern = Pattern.compile("\\$(.*?)\\$");
//        for(UpdateColumnDef updateColumnDef : updateDef.columns) {
//            if (updateColumnDef.statement.contains("\\$")) {
//                Matcher matcher = pattern.matcher(updateColumnDef.statement);
//                while (matcher.find()) {
//
//                }
//            } else {
//
//            }
//        }

        List<Map.Entry<String[], SQLWhereDef>> whereParts = new ArrayList<>();
        if(!queryElements.where.isEmpty()) {
            for(SQLWhereDef SQLWhereDef : updateDef.wheres) {
                if(SQLWhereDef.clause.contains("$")) {
                    String[] splitted = SQLWhereDef.clause.split("\\$", 3);
                    String[] wherePart = new String[splitted.length + splitted.length / 3];
                    for(int i = 0; i < splitted.length / 3; i++) {
                        wherePart[i * 4] = splitted[i * 3];
                        wherePart[i * 4 + 1] = splitted[i * 3 + 1];
                        wherePart[i * 4 + 2] = splitted[i * 3 + 2];
                        wherePart[i * 4 + 3] = SQLWhereDef.conjunction;
                        if(updateDef.paramMap.containsKey(splitted[i + 1]))
                            whereParts.add(new AbstractMap.SimpleEntry(wherePart, SQLWhereDef));
                    }
                } else {
                    String[] wherePart = new String[] {SQLWhereDef.clause, "", "", SQLWhereDef.conjunction };
                    whereParts.add(new AbstractMap.SimpleEntry<>(wherePart, SQLWhereDef));
                }
            }
            updateDef.whereParts = whereParts;
        }
    }

    public static UpdateDef createSQLDef(Map map) {
        UpdateDef updateDef = new UpdateDef();
        updateDef.name = (String)map.get("source-name");
        updateDef.queryPattern = (String)map.get("query-pattern");
        updateDef.befores = getSelectAttribute((List<Map<String, String>>)map.get("before"));
        updateDef.afters = getSelectAttribute((List<Map<String, String>>)map.get("after"));
        fillSources(map, updateDef);
        fillUpdateColumns(map, updateDef);
        fillParams(map, updateDef);
        fillWheres(map, updateDef);
        fillUpdateQuery(updateDef);
        return updateDef;
    }

    private static class QueryElements {
        String update;
        String set;
        String where;

        private QueryElements(String update, String set, String where) {
            this.update = update;
            this.set = set;
            this.where = where;
        }

        public static QueryElements getQueryElements(UpdateDef updateDef) {
            String queryPattern = updateDef.queryPattern;
            String update = queryPattern.substring(UPDATE.length(), queryPattern.indexOf(SET));
            String from = "";
            if(queryPattern.contains(WHERE))
                from = queryPattern.substring(UPDATE.length() + update.length() + SET.length() - 1, queryPattern.indexOf(WHERE) + 1);
            else
                from = queryPattern.substring(UPDATE.length() + update.length() + SET.length() - 1);
            String where = "";
            if(updateDef.wheres.size() > 0 && queryPattern.contains(WHERE)) {
                where = queryPattern.substring(UPDATE.length() + update.length() + SET.length() + from.length() + WHERE.length() - 2);
            }
            return new QueryElements(update, from, where);
        }
    }
}
