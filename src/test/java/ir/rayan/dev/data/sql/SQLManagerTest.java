package ir.rayan.dev.data.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ir.rayan.dev.data.model.TestTableEntity;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

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
    public void testHibernateSequence() throws Exception {
        SQLModelManager sqlModelManager = SQLModelManager.getNewInstance(
                SQLManagerTest.class.getResourceAsStream("/query/test-query.json"));

        SQLManager sqlManager = SQLManager.createSQLManager(sqlModelManager, ds);
        ParameterProvider parameterProvider = new ParameterProvider() {
            @Override
            public <T> T get(String paramName) {
                return null;
            }
        };

        Long l = sqlManager.selectValue("select-hibernate-sequence-nextval", parameterProvider);
        System.out.println(l);

//        Assert.assertNotNull(selectQuery);
    }

//    @Test
//    public void test() throws Exception {
//        SQLModelManager sqlModelManager = SQLModelManager.getNewInstance(
//                SQLManagerTest.class.getResourceAsStream("/query/test-query.json"));
//
//        SQLManager sqlManager = SQLManager.createSQLManager(sqlModelManager, ds);
//        ParameterProvider parameterProvider = new ParameterProvider() {
//            @Override
//            public <T> T get(String paramName) {
//                return null;
//            }
//        };
//
//        String selectQuery = sqlManager.createSelectQuery("select-test-table", parameterProvider);
//        System.out.println(selectQuery);
//
//        Connection connection = null;
//        Statement statement = null;
//        ResultSet resultSet = null;
//        try {
//            connection = ds.getConnection();
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery(selectQuery);
//            if (resultSet != null) {
//                List<Map> list = sqlManager.getResultSetAsListOfMap(resultSet);
//                Map map = list.get(0);
//                ObjectMapper mapper = new ObjectMapper();
//                SimpleModule simpleModule = new SimpleModule();
//                simpleModule.addSerializer(TIMESTAMP.class, new JsonSerializer<TIMESTAMP>() {
//                    @Override
//                    public void serialize(TIMESTAMP timestamp, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
//                        jsonGenerator.writeString(timestamp.stringValue());
//                    }
//                });
//                simpleModule.addSerializer(TestTableEntity.class, new RayanJsonSerializer<TestTableEntity>());
//
//                simpleModule.addDeserializer(TestTableEntity.class, new RayanJsonDeserializer<TestTableEntity>() {
//                    @Override
//                    public TestTableEntity deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//                        return this.deserialize(TestTableEntity.class, jsonParser, deserializationContext);
//                    }
//                });
//                simpleModule.addDeserializer(Map.class, new JsonDeserializer<Map>() {
//                    @Override
//                    public Map deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//                        ObjectCodec oc = jsonParser.getCodec();
//                        JsonNode node = oc.readTree(jsonParser);
//                        return null;
//                    }
//                });
//                mapper.registerModule(simpleModule);
//
//                TestTableEntity testTableEntity = new TestTableEntity();
//                testTableEntity.setId(1l);
//                testTableEntity.setColumnA("a");
//                testTableEntity.setColumnB(1);
//                testTableEntity.setColumnC(false);
//                testTableEntity.setColumnD(new Timestamp(System.currentTimeMillis()));
//
//                String jsonResult = mapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(testTableEntity);
//
//                String jsonResult2 = mapper.writerWithDefaultPrettyPrinter()
//                        .writeValueAsString(map);
//
////                TestTableEntity testTableEntity = mapper.readValue()
////                        .writeValueAsString(jsonResult);
//
//                TestTableEntity objectReader = mapper.readValue(jsonResult, TestTableEntity.class);
//                System.out.println(list.size());
//            }
//        } finally {
//            resultSet.close();
//            statement.close();
//            connection.close();
//        }
//
////        Assert.assertNotNull(selectQuery);
//    }

//    @Test
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

        List<TestTableEntity> list = sqlManager.select("select-test-table", parameterProvider);
        System.out.println(list.size());

//        Assert.assertNotNull(selectQuery);
    }

}
