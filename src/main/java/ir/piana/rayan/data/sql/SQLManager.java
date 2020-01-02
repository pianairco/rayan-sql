package ir.piana.rayan.data.sql;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SQLManager {
    private SQLModelManager modelManager;
    private static SQLManager sqlManager;
    private DataSource dataSource;

    private Object getValue(String key, ParameterProvider parameterProvider) {
        Object obj = null;
        if(parameterProvider != null)
            obj = parameterProvider.get(key);
//        if(obj == null)
//            return "";
        return obj;
    }

    private SQLManager(SQLModelManager modelManager, DataSource dataSource) {
        this.modelManager = modelManager;
        this.dataSource = dataSource;
    }

    public static SQLManager createSQLManager(SQLModelManager sqlModelManager, DataSource dataSource) {
        if(sqlManager == null)
            sqlManager = new SQLManager(sqlModelManager, dataSource);
        return sqlManager;
    }

    public static SQLManager getSQLManager() {
        if(sqlManager != null)
            return sqlManager;
        return null;
    }

    public SQLModelManager getSQLModelManager() {
        return modelManager;
    }

    public String createQuery(String sourceName, ParameterProvider parameterProvider) {
        if(modelManager.hasSelectDef(sourceName))
            return createSelectQuery(sourceName, parameterProvider);
        else if(modelManager.hasInsertDef(sourceName))
            return createInsertQuery(sourceName, parameterProvider);
        else if(modelManager.hasUpdateDef(sourceName))
            return createUpdateQuery(sourceName, parameterProvider);
        return null;
    }

    public String createSelectQuery(String sourceName, ParameterProvider parameterProvider) {
        SelectDef selectDef = modelManager.getSelectDef(sourceName);

        StringBuffer wheresBuffer = new StringBuffer("");
        String lastConjuction = null;
        if(selectDef.whereParts == null) {

        } else {
            for (Map.Entry<String[], SQLWhereDef> wherePart : selectDef.whereParts) {
                StringBuffer whereBuffer = new StringBuffer("");
                for (int i = 0; i < wherePart.getKey().length - 1; i++) {
                    if (i % 2 == 0)
                        whereBuffer.append(wherePart.getKey()[i]);
                    else {
                        String paramName = wherePart.getKey()[i];

                        if (paramName != null && !paramName.isEmpty()) {
                            Object paramValue = getValue(selectDef.paramMap.get(paramName).key, parameterProvider);
                            if ((paramValue == null || ((String)paramValue).isEmpty()) && !wherePart.getValue().force)
                                whereBuffer = new StringBuffer("@");
                            else {
                                if (paramValue instanceof List) {
                                    List list = (List) paramValue;
                                    for (Object obj : list)
                                        whereBuffer.append(obj.toString() + ", ");
                                    whereBuffer.deleteCharAt(whereBuffer.length() - 2);
                                } else {
                                    whereBuffer.append(paramValue);
                                }
                            }
                        } else {
                            whereBuffer.append("");
                        }
                    }
                }
                if(whereBuffer.charAt(0) != '@') {
                    lastConjuction = " " + wherePart.getKey()[wherePart.getKey().length - 1] + " ";
                    whereBuffer.append(lastConjuction);
                    wheresBuffer.append(whereBuffer);
                }
            }
        }
        if(wheresBuffer.length() > 0)
            wheresBuffer = new StringBuffer(" where ").append(wheresBuffer);
        if(lastConjuction != null && !lastConjuction.isEmpty())
            wheresBuffer.delete(wheresBuffer.length() - lastConjuction.length(), wheresBuffer.length()).append(" ");

        StringBuffer fromBuffer = new StringBuffer();
        String from = selectDef.fromString;
        if(!from.isEmpty()) {
            for (SelectSourceDef selectSourceDef : selectDef.sources) {
                if(from.contains(" " + selectSourceDef.alias + " ") || from.endsWith(" " + selectSourceDef.alias)) {
                    if(selectSourceDef.type != null && selectSourceDef.type.equalsIgnoreCase("source"))
                        from = from.replace(" " + selectSourceDef.alias + " ", " ("
                                + this.createSelectQuery(selectSourceDef.name, parameterProvider) + ") "
                                + selectSourceDef.alias + " ");
                    else {
                        if(from.endsWith(" " + selectSourceDef.alias)) {
                            from = from.substring(0, from.length() - selectSourceDef.alias.length());
                            from = from.concat(selectSourceDef.name + " " + selectSourceDef.alias);
                        } else
                            from = from.replace(" " + selectSourceDef.alias + " ", " "
                                    + selectSourceDef.name + " " + selectSourceDef.alias + " ");
                    }
                }

            }
            fromBuffer.append(from);
        }

        StringBuffer rightQueryString = new StringBuffer();
        if(selectDef.orderParts != null && !selectDef.orderParts.isEmpty()) {
            rightQueryString.append(" order by ");
            for (Map.Entry<Map.Entry<String, Boolean>, Map.Entry<String, Boolean>> orderPart : selectDef.orderParts) {
                String by = null;
                if(orderPart.getKey().getValue()) {
                    Object paramValue = getValue(selectDef.paramMap.get(orderPart.getKey().getKey()).key, parameterProvider);
                    if(paramValue == null)
                        continue;
                    by = (String) paramValue;
                } else {
                    by = (String) orderPart.getKey().getKey();
                }
                String order = null;
                if(orderPart.getValue().getValue()) {
                    Object paramValue = getValue(selectDef.paramMap.get(orderPart.getValue().getKey()).key, parameterProvider);
                    if(paramValue == null)
                        continue;
                    order = (String) paramValue;
                } else {
                    order = (String) orderPart.getValue().getKey();
                }
                rightQueryString.append(by + " " + order + ", ");
            }
            if(rightQueryString.toString().equalsIgnoreCase(" order by "))
                rightQueryString = new StringBuffer("");
            else
                rightQueryString.deleteCharAt(rightQueryString.length() - 2);
        }

        if(selectDef.groups != null && !selectDef.groups.isEmpty()) {
            rightQueryString.append(" group by ");
            for (String group : selectDef.groups)
                rightQueryString.append(group).append(", ");
            rightQueryString.deleteCharAt(rightQueryString.length() - 2);
        }

        return selectDef.leftQueryString.concat(fromBuffer.toString()).concat(wheresBuffer.toString())
                .concat(rightQueryString.toString());
    }

    public String createUpdateQuery(String sourceName, ParameterProvider parameterProvider) {
        UpdateDef updateDef = modelManager.getUpdateDef(sourceName);

        StringBuffer columnBuffer = new StringBuffer("");
        for(UpdateColumnDef columnDef : updateDef.columns) {
            Pattern pattern = Pattern.compile("\\$(.*?)\\$");
            String statement = columnDef.statement;
            if (statement.contains("$")) {
                Matcher matcher = pattern.matcher(statement);
                while (matcher.find()) {
                    String group = matcher.group();
                    SQLParamDef sqlParamDef = updateDef.paramMap.get(group.substring(1, group.length() - 1));
                    String[] split = statement.split(Pattern.quote(group));
                    StringBuffer part = new StringBuffer(split[0]).append(getValue(sqlParamDef.key, parameterProvider));
                    columnBuffer.append(part);
                    statement = "";
                    for(int i = 1; i < split.length; i++)
                        statement = statement.concat(split[i]);
                }
                columnBuffer.append(statement).append(", ");
            } else {
                columnBuffer.append(columnDef.statement).append(", ");
            }
        }
        if(!updateDef.columns.isEmpty())
            columnBuffer.deleteCharAt(columnBuffer.length() - 2);

        StringBuffer wheresBuffer = new StringBuffer("");
        String lastConjuction = null;
        if(updateDef.whereParts != null) {
            for (Map.Entry<String[], SQLWhereDef> wherePart : updateDef.whereParts) {
                StringBuffer whereBuffer = new StringBuffer("");
                for (int i = 0; i < wherePart.getKey().length - 1; i++) {
                    if (i % 2 == 0)
                        whereBuffer.append(wherePart.getKey()[i]);
                    else {
                        String paramName = wherePart.getKey()[i];

                        if (paramName != null && !paramName.isEmpty()) {
                            Object paramValue = getValue(updateDef.paramMap.get(paramName).key, parameterProvider);
                            if ((paramValue == null || ((String)paramValue).isEmpty()) && !wherePart.getValue().force)
                                whereBuffer = new StringBuffer("@");
                            else {
                                if (paramValue instanceof List) {
                                    List list = (List) paramValue;
                                    for (Object obj : list)
                                        whereBuffer.append(obj.toString() + ", ");
                                    whereBuffer.deleteCharAt(whereBuffer.length() - 2);
                                } else {
                                    whereBuffer.append(paramValue);
                                }
                            }
                        } else {
                            whereBuffer.append("");
                        }
                    }
                }
                if(whereBuffer.charAt(0) != '@') {
                    lastConjuction = " " + wherePart.getKey()[wherePart.getKey().length - 1] + " ";
                    whereBuffer.append(lastConjuction);
                    wheresBuffer.append(whereBuffer);
                }
            }
        }
        if(wheresBuffer.length() > 0)
            wheresBuffer = new StringBuffer(" where ").append(wheresBuffer);
        if(lastConjuction != null && !lastConjuction.isEmpty())
            wheresBuffer.delete(wheresBuffer.length() - lastConjuction.length(), wheresBuffer.length()).append(" ");

        return updateDef.updateQuery.concat(columnBuffer.toString()).concat(wheresBuffer.toString());
    }

    public String createInsertQuery(String sourceName, ParameterProvider parameterProvider) {
        InsertDef insertDef = modelManager.getInsertDef(sourceName);

        StringBuffer columnBuffer = new StringBuffer("(");
        StringBuffer columnValueBuffer = new StringBuffer(" values (");
        Pattern pattern = Pattern.compile("\\$(.*?)\\$");
        Pattern sharpPattern = Pattern.compile("\\#(.*?)\\#");
        for(InsertColumnDef columnDef : insertDef.columns) {
            columnBuffer.append(columnDef.getName()).append(", ");

            String value = columnDef.value;
            if (value.contains("$")) {
                Matcher matcher = pattern.matcher(value);
                while (matcher.find()) {
                    String group = matcher.group();
                    SQLParamDef sqlParamDef = insertDef.paramMap.get(group.substring(1, group.length() - 1));
                    String[] split = value.split(Pattern.quote(group));
                    if(split.length == 0) {
                        StringBuffer part = new StringBuffer();
                        Object value1 = getValue(sqlParamDef.key, parameterProvider);
                        if(sqlParamDef.type.equals("string"))
                            part.append("'").append(value1).append("'");
                        else
                            part.append(value1);
                        columnValueBuffer.append(part);
                        value = "";
                    } else {
                        StringBuffer part = new StringBuffer(split[0]).append(getValue(sqlParamDef.key, parameterProvider));
                        columnValueBuffer.append(part);
                        value = "";
                        for(int i = 1; i < split.length; i++)
                            value = value.concat(split[i]);
                    }
                }
                columnValueBuffer.append(value).append(", ");
            } else {
                columnValueBuffer.append(columnDef.value).append(", ");
            }
        }
        if(!insertDef.columns.isEmpty()) {
            columnBuffer.deleteCharAt(columnBuffer.length() - 2).append(")");
            columnValueBuffer.deleteCharAt(columnValueBuffer.length() - 2).append(")");
        }

        return insertDef.insertQuery.concat(columnBuffer.toString()).concat(columnValueBuffer.toString());
    }



    public boolean isSelectQuery(String queryName) {
        return modelManager.hasSelectDef(queryName);
    }

    public boolean isPersistDef(String queryName) {
        return modelManager.hasUpdateDef(queryName) || modelManager.hasInsertDef(queryName);
    }

    public boolean equality(int value, SQLExecuteIf sqlExecuteIf) {
        if(sqlExecuteIf.evaluation.equals("="))
            return value == Integer.valueOf(sqlExecuteIf.target);
        else if(sqlExecuteIf.evaluation.equals("<="))
            return value <= Integer.valueOf(sqlExecuteIf.target);
        else if(sqlExecuteIf.evaluation.equals(">="))
            return value >= Integer.valueOf(sqlExecuteIf.target);
        else if(sqlExecuteIf.evaluation.equals("<"))
            return value < Integer.valueOf(sqlExecuteIf.target);
        else if(sqlExecuteIf.evaluation.equals(">"))
            return  value > Integer.valueOf(sqlExecuteIf.target);
        else if(sqlExecuteIf.evaluation.equals("!="))
            return  value != Integer.valueOf(sqlExecuteIf.target);
        return false;
    }


    public List getResultSetAsListOfMap(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList list = new ArrayList(50);
        while (rs.next()){
            Map row = new LinkedHashMap(columns);
            for(int i=1; i<=columns; ++i){
                row.put(md.getColumnName(i),rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

    public List<Map> queryForMap(String query) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Map> map = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            if (resultSet != null)
                map = getResultSetAsListOfMap(resultSet);
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }
        return map == null ? new ArrayList<>() : map;
    }

    public void execBatch(List<String> sqls) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            for(String sql : sqls)
                statement.addBatch(sql);
            int[] ints = statement.executeBatch();
            connection.commit();
        } finally {
            statement.close();
            connection.close();
        }
    }
}
