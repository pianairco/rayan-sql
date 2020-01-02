package ir.piana.rayan.data.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.Column;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

/**
 * Created by mj.rahmati on 1/2/2020.
 */
public class RayanObjectDeserializer<T> /*extends JsonDeserializer<T> */{
//    @Override
//    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//        return null;ObjectCodec oc = jsonParser.getCodec();
////                        ((ObjectMapper)oc).valueToTree(jsonParser.getValueAsString());
//        JsonNode snode = oc.readTree(jsonParser);
//        ObjectNode node = ((ObjectMapper) oc).readValue(snode.asText(), ObjectNode.class);
//        Method[] methods = TestTableEntity.class.getDeclaredMethods();
//        TestTableEntity testTableEntity = new TestTableEntity();
//        for(Method method : methods) {
//            Column column = null;
//            if((column = method.getAnnotation(Column.class)) != null) {
//                try {
//                    Method setdMethod = null;
//                    Class<?> parameterType = method.getReturnType();
//                    if (parameterType == String.class) {
//                        setdMethod = getSetMethod(method, String.class);
//                        setdMethod.invoke(testTableEntity, node.get(column.name()).asText());
//                    } else if (parameterType == Timestamp.class) {
//                        setdMethod = getSetMethod(method, Timestamp.class);
//                        Timestamp timestamp = Timestamp.valueOf(node.get(column.name()).asText());
//                        setdMethod.invoke(testTableEntity, timestamp);
//                    } else if (parameterType == Integer.class){
//                        setdMethod = getSetMethod(method, Integer.class);
//                        int i = node.get(column.name()).asInt();
//                        setdMethod.invoke(testTableEntity, i);
//                    } else if (parameterType == int.class){
//                        setdMethod = getSetMethod(method, int.class);
//                        int i = node.get(column.name()).asInt();
//                        setdMethod.invoke(testTableEntity, i);
//                    } else if (parameterType == Boolean.class){
//                        setdMethod = getSetMethod(method, Boolean.class);
//                        Boolean i = node.get(column.name()).asBoolean();
//                        setdMethod.invoke(testTableEntity, i);
//                    } else if (parameterType == boolean.class){
//                        setdMethod = getSetMethod(method, boolean.class);
//                        boolean i = node.get(column.name()).asBoolean();
//                        setdMethod.invoke(testTableEntity, i);
//                    }
//                    else if (parameterType == Long.class || parameterType == long.class){
//                        setdMethod = getSetMethod(method, Long.class);
//                        long l = node.get(column.name()).asLong();
//                        setdMethod.invoke(testTableEntity, l);
//                    }
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return testTableEntity;
//    }

//    public static Method getSetMethod(Method method, Class parameterType) throws NoSuchMethodException {
//        Method setdMethod = null;
//        if (method.getName().startsWith("get")) {
//            setdMethod = TestTableEntity.class.getDeclaredMethod("set".concat(method.getName().substring(3)), parameterType);
//        } else if (method.getName().startsWith("is")) {
//            setdMethod = TestTableEntity.class.getDeclaredMethod("set".concat(method.getName().substring(2)), parameterType);
//        }
//        return setdMethod;
//    }
}
