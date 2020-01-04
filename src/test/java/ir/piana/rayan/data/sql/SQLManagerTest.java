package ir.piana.rayan.data.sql;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ir.piana.rayan.data.model.TestTableEntity;
import ir.piana.rayan.data.serializer.RayanJsonDeserializer;
import ir.piana.rayan.data.serializer.RayanJsonSerializer;
import oracle.sql.TIMESTAMP;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.Column;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mj.rahmati on 12/11/2019.
 */
public class SQLManagerTest {
    private static HikariDataSource ds;

    @BeforeClass
    public static void beforeClass() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:oracle:thin:@192.168.6.212:1521:devtwo");
        config.setUsername("t2anf04");
        config.setPassword("t");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("allowMultiQueries", "true");
        config.setMaximumPoolSize(10); // this is plenty, the websocket uses 32

        ds = new HikariDataSource(config);
    }

    @Test
    public void test() throws Exception {
        SQLModelManager sqlModelManager = SQLModelManager.getNewInstance(
                SQLManagerTest.class.getResourceAsStream("/query/test-query.json"));

        SQLManager sqlManager = SQLManager.createSQLManager(sqlModelManager, ds);
        ParameterProvider parameterProvider = new ParameterProvider() {
            @Override
            public <T> T get(String paramName) {
                return null;
            }
        };

        String selectQuery = sqlManager.createSelectQuery("select-test-table", parameterProvider);
        System.out.println(selectQuery);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ds.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectQuery);
            if (resultSet != null) {
                List<Map> list = sqlManager.getResultSetAsListOfMap(resultSet);
                Map map = list.get(0);
                ObjectMapper mapper = new ObjectMapper();
                SimpleModule simpleModule = new SimpleModule();
                simpleModule.addSerializer(TIMESTAMP.class, new JsonSerializer<TIMESTAMP>() {
                    @Override
                    public void serialize(TIMESTAMP timestamp, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
                        jsonGenerator.writeString(timestamp.stringValue());
                    }
                });
                simpleModule.addSerializer(TestTableEntity.class, new RayanJsonSerializer<TestTableEntity>());

                simpleModule.addDeserializer(TestTableEntity.class, new RayanJsonDeserializer<TestTableEntity>() {
                    @Override
                    public TestTableEntity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                        return this.deserialize(TestTableEntity.class, jsonParser, deserializationContext);
                    }
                });
                simpleModule.addDeserializer(Map.class, new JsonDeserializer<Map>() {
                    @Override
                    public Map deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
                        ObjectCodec oc = jsonParser.getCodec();
                        JsonNode node = oc.readTree(jsonParser);
                        return null;
                    }
                });
                mapper.registerModule(simpleModule);

                TestTableEntity testTableEntity = new TestTableEntity();
                testTableEntity.setId(1l);
                testTableEntity.setColumnA("a");
                testTableEntity.setColumnB(1);
                testTableEntity.setColumnC(false);
                testTableEntity.setColumnD(new Timestamp(System.currentTimeMillis()));

                String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(testTableEntity);

                String jsonResult2 = mapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(map);

//                TestTableEntity testTableEntity = mapper.readValue()
//                        .writeValueAsString(jsonResult);

                TestTableEntity objectReader = mapper.readValue(jsonResult, TestTableEntity.class);
                System.out.println(list.size());
            }
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
        }

//        Assert.assertNotNull(selectQuery);
    }

    public static Method getSetMethod(Method method, Class parameterType)
            throws NoSuchMethodException {
        Method setdMethod = null;
        if (method.getName().startsWith("get")) {
            setdMethod = TestTableEntity.class.getDeclaredMethod("set".concat(method.getName().substring(3)), parameterType);
        } else if (method.getName().startsWith("is")) {
            setdMethod = TestTableEntity.class.getDeclaredMethod("set".concat(method.getName().substring(2)), parameterType);
        }
        return setdMethod;
    }
}
