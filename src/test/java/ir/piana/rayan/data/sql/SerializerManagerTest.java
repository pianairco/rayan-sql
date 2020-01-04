package ir.piana.rayan.data.sql;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import ir.piana.rayan.data.model.TestTableEntity;
import ir.piana.rayan.data.serializer.RayanJsonDeserializer;
import ir.piana.rayan.data.serializer.RayanJsonSerializer;
import oracle.sql.TIMESTAMP;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Created by mj.rahmati on 12/11/2019.
 */
public class SerializerManagerTest {
    @Test
    public void test() throws Exception {
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
        mapper.registerModule(simpleModule);
        TestTableEntity testTableEntity = new TestTableEntity();
        testTableEntity.setId(1l);
        testTableEntity.setColumnA("a");
        testTableEntity.setColumnB(1);
        testTableEntity.setColumnC(false);
        testTableEntity.setColumnD(new Timestamp(System.currentTimeMillis()));

        String jsonResult = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(testTableEntity);

        TestTableEntity entity = mapper.readValue(jsonResult, TestTableEntity.class);
        System.out.println(entity.getColumnA());
    }
}
