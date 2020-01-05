package ir.rayan.dev.data.sql;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ir.rayan.dev.data.model.TestTableEntity;
import ir.rayan.dev.data.serializer.RayanJsonDeserializer;
import ir.rayan.dev.data.serializer.RayanJsonSerializer;
import oracle.sql.TIMESTAMP;
import org.junit.Test;

import java.io.IOException;
import java.sql.Timestamp;

/**
 * Created by mj.rahmati on 12/11/2019.
 */
public class SerializerTest {
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
