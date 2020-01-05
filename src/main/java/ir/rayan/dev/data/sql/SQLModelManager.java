package ir.rayan.dev.data.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mj.rahmati on 12/4/2019.
 */
public class SQLModelManager {
    private Map<String, SelectDef> selectMap;
    private Map<String, UpdateDef> updateMap;
    private Map<String, InsertDef> insertMap;

    public boolean hasSelectDef(String sourceName) {
        return selectMap.containsKey(sourceName);
    }

    public boolean hasUpdateDef(String sourceName) {
        return updateMap.containsKey(sourceName);
    }

    public boolean hasInsertDef(String sourceName) {
        return insertMap.containsKey(sourceName);
    }

    public SelectDef getSelectDef(String sourceName) {
        return selectMap.get(sourceName);
    }

    UpdateDef getUpdateDef(String sourceName) {
        return updateMap.get(sourceName);
    }

    InsertDef getInsertDef(String sourceName) {
        return insertMap.get(sourceName);
    }


    private SQLModelManager(Map<String, SelectDef> selectMap,
                            Map<String, UpdateDef> updateMap,
                            Map<String, InsertDef> insertMap) {
        this.selectMap = selectMap;
        this.updateMap = updateMap;
        this.insertMap = insertMap;
    }


    public static SQLModelManager getNewInstance(InputStream ...sqlInputStreams) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, SelectDef> selectMap = new LinkedHashMap<>();
        Map<String, UpdateDef> updateMap = new LinkedHashMap<>();
        Map<String, InsertDef> insertMap = new LinkedHashMap<>();
        for(InputStream is : sqlInputStreams) {
            String string = IOUtils.toString(is);
            List<Map> modelList = objectMapper.readValue(string, List.class);
            List<Map> displayList = new ArrayList<>();
            for(Map map : modelList) {
                Object displayName = map.get("name");
                if(displayName != null) {
                    displayList.add(map);
                    continue;
                }
                String queryPattern = (String)map.get("query-pattern");
                SQLType sqlType = SQLType.fromName(queryPattern.substring(0, 6));
                if(sqlType == SQLType.SELECT) {
                    SelectDef selectDef = SelectDefCreator.createSQLDef(map);
                    selectMap.put(selectDef.name, selectDef);
                } else if(sqlType == SQLType.UPDATE) {
                    UpdateDef updateDef = UpdateDefCreator.createSQLDef(map);
                    updateMap.put(updateDef.name, updateDef);
                } else if(sqlType == SQLType.INSERT) {
                    InsertDef insertDef = InsertDefCreator.createSQLDef(map);
                    insertMap.put(insertDef.name, insertDef);
                }
            }
        }
        SQLModelManager sqlModelManager = new SQLModelManager(selectMap, updateMap, insertMap);
        return sqlModelManager;
    }
}
