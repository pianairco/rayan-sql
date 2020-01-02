package ir.piana.rayan.data.sql;

import java.util.*;

/**
 * Created by mj.rahmati on 12/16/2019.
 */
@SuppressWarnings("Duplicates")
public class SelectDefCreator {
    private final static String SELECT = "select ";
    private final static String FROM = " from ";
    private final static String WHERE = " where ";
    private final static String GROUP_BY = " group by ";
    private final static String ORDER_BY = " order by ";

    private static void fillSources(Map map, SelectDef selectDef) {
        List<Map<String, String>> sourceDefs = (List<Map<String, String>>)map.get("sources");
        sourceDefs = sourceDefs != null ? sourceDefs : new ArrayList<>();
        Map<String, SelectSourceDef> sourceMap = new LinkedHashMap<>();
        List<SelectSourceDef> sources = new ArrayList<>();
        for(Map<String, String> sourceDef : sourceDefs) {
            String name = sourceDef.get("name");
            String alias = sourceDef.get("alias");
            String type = sourceDef.get("type");
            SelectSourceDef selectSourceDef = new SelectSourceDef(name, alias, type);
            sourceMap.put(alias, selectSourceDef);
            sources.add(selectSourceDef);
        }
        selectDef.sourceMap = sourceMap;
        selectDef.sources = sources;
    }

    private static void fillSelectColumns(Map map, SelectDef selectDef) {
        List<Map<String, String>> columnDefs = (List<Map<String, String>>)map.get("columns");
        List<SelectColumnDef> columns = new ArrayList<>();
        Map<String, SelectColumnDef> columnMap = new LinkedHashMap<>();
        for(Map<String, String> columnDef : columnDefs) {
            String name = columnDef.get("name");
            String alias = columnDef.get("as");
            String type = columnDef.get("type");
            SelectColumnDef selectColumnDef = new SelectColumnDef(name, alias != null ? alias : name.substring(name.indexOf(".") + 1), type);
            columnMap.put(selectColumnDef.as, selectColumnDef);
            columns.add(selectColumnDef);
        }
        selectDef.columnMap = columnMap;
        selectDef.columns = columns;
    }

    private static void fillWheres(Map map, SelectDef selectDef) {
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
        selectDef.wheres = wheres;
    }

    private static void fillOrders(Map map, SelectDef selectDef) {
        List<Map<String, String>> orderDefs = (List<Map<String, String>>)map.get("orders");
        List<SelectOrderDef> orders = new ArrayList<>();
        if(orderDefs != null) {
            for (Map<String, String> orderDef : orderDefs) {
                String by = orderDef.get("by");
                String order = orderDef.get("order");
                orders.add(new SelectOrderDef(by, order));
            }
        }
        selectDef.orders = orders;
    }

    private static void fillGroups(Map map, SelectDef selectDef) {
        List<String> groupDefs = (List<String>)map.get("groups");
        selectDef.groups = groupDefs == null ? new ArrayList<>() : groupDefs;
    }

    private static void fillParams(Map map, SelectDef selectDef) {
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
        selectDef.paramMap = paramMap;
        selectDef.params = params;
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

    private static void fillSelectQuery(SelectDef selectDef) {
        QueryElements queryElements = QueryElements.getQueryElements(selectDef);
        StringBuffer selectBuffer = new StringBuffer(SELECT);
        if(!queryElements.select.isEmpty()) {
            for (SelectColumnDef selectColumnDef : selectDef.columns) {
                selectBuffer.append(selectColumnDef.name + " " + selectColumnDef.as + ", ");
            }
            selectBuffer.deleteCharAt(selectBuffer.length() - 2);
        }
        selectDef.fromString = new StringBuffer(FROM).append(queryElements.from).toString();
        List<Map.Entry<String[], SQLWhereDef>> whereParts = new ArrayList<>();
        if(!queryElements.where.isEmpty()) {
            for(SQLWhereDef SQLWhereDef : selectDef.wheres) //noinspection Duplicates
            {
                if(SQLWhereDef.clause.contains("$")) {
                    String[] splitted = SQLWhereDef.clause.split("\\$", 3);
                    String[] wherePart = new String[splitted.length + splitted.length / 3];
                    for(int i = 0; i < splitted.length / 3; i++) {
                        wherePart[i * 4] = splitted[i * 3];
                        wherePart[i * 4 + 1] = splitted[i * 3 + 1];
                        wherePart[i * 4 + 2] = splitted[i * 3 + 2];
                        wherePart[i * 4 + 3] = SQLWhereDef.conjunction;
                        if(selectDef.paramMap.containsKey(splitted[i + 1]))
                            whereParts.add(new AbstractMap.SimpleEntry(wherePart, SQLWhereDef));
                    }
                } else {
                    String[] wherePart = new String[] {SQLWhereDef.clause, "", "", SQLWhereDef.conjunction };
                    whereParts.add(new AbstractMap.SimpleEntry<>(wherePart, SQLWhereDef));
                }
            }
            selectDef.whereParts = whereParts;
        }

        List<Map.Entry<Map.Entry<String, Boolean>, Map.Entry<String, Boolean>>> orderParts = new ArrayList<>();
        if(!queryElements.orderBy.isEmpty()) {
            for (SelectOrderDef selectOrderDef : selectDef.orders) {
                AbstractMap.SimpleEntry<String, Boolean> byPart = null;
                if(selectOrderDef.by.contains("$")) {
                    byPart = new AbstractMap.SimpleEntry<String, Boolean>(selectOrderDef.by.split("\\$", 3)[1], true);
                } else {
                    byPart = new AbstractMap.SimpleEntry<String, Boolean>(selectOrderDef.by, false);
                }
                AbstractMap.SimpleEntry<String, Boolean> orderPart = null;
                if(selectOrderDef.order.contains("$")) {
                    orderPart = new AbstractMap.SimpleEntry<String, Boolean>(selectOrderDef.order.split("\\$", 3)[1], true);
                } else {
                    orderPart = new AbstractMap.SimpleEntry<String, Boolean>(selectOrderDef.order, false);
                }
                orderParts.add(new AbstractMap.SimpleEntry<Map.Entry<String, Boolean>, Map.Entry<String, Boolean>>(byPart, orderPart));
            }
        }
        selectDef.orderParts = orderParts;

        selectDef.leftQueryString = selectBuffer/*.append(fromBuffer)*/.toString();
    }

    public static SelectDef createSQLDef(Map map) {
        SelectDef selectDef = new SelectDef();
        selectDef.name = (String)map.get("source-name");
        selectDef.queryPattern = (String)map.get("query-pattern");
        selectDef.selectType = (String)map.get("select-type");
        selectDef.selectType = selectDef.selectType == null || selectDef.selectType.isEmpty() ? "result-set" : selectDef.selectType;
        selectDef.sqlExecuteIfs = getExecuteIfs((List<Map<String, String>>)map.get("execute-if"));
        selectDef.befores = getSelectAttribute((List<Map<String, String>>)map.get("before"));
        selectDef.afters = getSelectAttribute((List<Map<String, String>>)map.get("after"));
        fillSources(map, selectDef);
        fillSelectColumns(map, selectDef);
        fillParams(map, selectDef);
        fillWheres(map, selectDef);
        fillGroups(map, selectDef);
        fillOrders(map, selectDef);
        fillSelectQuery(selectDef);
        return selectDef;
    }

    private static class QueryElements {
        String select;
        String from;
        String where;
        String groupBy;
        String orderBy;

        private QueryElements(String select, String from, String where, String groupBy, String orderBy) {
            this.select = select;
            this.from = from;
            this.where = where;
            this.groupBy = groupBy;
            this.orderBy = orderBy;
        }

        public static QueryElements getQueryElements(SelectDef selectDef) {
            String queryPattern = selectDef.queryPattern;
            String select = queryPattern.substring(SELECT.length(), queryPattern.indexOf(" from "));
            String from = "";
            if(queryPattern.contains(WHERE))
                from = queryPattern.substring(SELECT.length() + select.length() + FROM.length() - 1, queryPattern.indexOf(WHERE) + 1);
            else if(queryPattern.contains(GROUP_BY))
                from = queryPattern.substring(SELECT.length() + select.length() + FROM.length() - 1, queryPattern.indexOf(GROUP_BY) + 1);
            else if(queryPattern.contains(ORDER_BY))
                from = queryPattern.substring(SELECT.length() + select.length() + FROM.length() - 1, queryPattern.indexOf(ORDER_BY) + 1);
            else
                from = queryPattern.substring(SELECT.length() + select.length() + FROM.length() - 1);
            String where = "";
            if(selectDef.wheres.size() > 0 && queryPattern.contains(WHERE)) {
                if(queryPattern.contains(GROUP_BY))
                    where = queryPattern.substring(SELECT.length() + select.length() + FROM.length() + from.length() + WHERE.length() - 2, queryPattern.indexOf(GROUP_BY) + 1);
                else if(queryPattern.contains(ORDER_BY))
                    where = queryPattern.substring(SELECT.length() + select.length() + FROM.length() + from.length() + WHERE.length() - 2, queryPattern.indexOf(ORDER_BY) + 1);
                else
                    where = queryPattern.substring(SELECT.length() + select.length() + FROM.length() + from.length() + WHERE.length() - 2);
            }
            String groupBy = "";
            if(selectDef.groups.size() > 0 && queryPattern.contains(GROUP_BY)) {
                if(queryPattern.contains(ORDER_BY))
                    groupBy = queryPattern.substring(SELECT.length() + select.length() + FROM.length() + from.length() + (!queryPattern.contains(WHERE) ? 0 : WHERE.length()) + where.length() + GROUP_BY.length() - 3, queryPattern.indexOf(ORDER_BY));
                else
                    groupBy = queryPattern.substring(SELECT.length() + select.length() + FROM.length() + from.length() + (!queryPattern.contains(WHERE) ? 0 : WHERE.length()) + where.length() + GROUP_BY.length() - 3);
            }
            String orderBy = "";
            if(selectDef.orders.size() > 0 && queryPattern.contains(ORDER_BY)) {
                orderBy = queryPattern.substring(SELECT.length() + select.length() + FROM.length() + from.length() + (!queryPattern.contains(WHERE) ? 0 : WHERE.length()) + where.length() + (!queryPattern.contains(GROUP_BY) ? 0 : GROUP_BY.length()) + groupBy.length() + ORDER_BY.length() - 4);
            }
            return new QueryElements(select, from, where, groupBy, orderBy);
        }
    }
}
