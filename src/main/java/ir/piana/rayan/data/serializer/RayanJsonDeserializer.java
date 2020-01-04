package ir.piana.rayan.data.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.omg.CORBA.PRIVATE_MEMBER;

import javax.persistence.Column;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

/**
 * Created by mj.rahmati on 1/2/2020.
 */
public abstract class RayanJsonDeserializer<T> extends JsonDeserializer<T>{

    public T deserialize(Class<T> targetClazz, JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException
            {
        ObjectCodec oc = jsonParser.getCodec();
        JsonNode snode = oc.readTree(jsonParser);
        ObjectNode node = ((ObjectMapper) oc).readValue(snode.toPrettyString(), ObjectNode.class);
        Method[] methods = targetClazz.getDeclaredMethods();
                Object targetObject = null;
                try {
                    targetObject = targetClazz.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                for(Method method : methods) {
            Column column = null;
            if((column = method.getAnnotation(Column.class)) != null) {
                try {
                    Method setMethod = null;
                    Class<?> parameterType = method.getReturnType();
                    if (parameterType == String.class) {
                        setMethod = getSetMethod(targetClazz, method, String.class);
                        setMethod.invoke(targetObject, node.get(column.name()).asText());
                    } else if (parameterType == Timestamp.class) {
                        setMethod = getSetMethod(targetClazz, method, Timestamp.class);
                        Timestamp timestamp = Timestamp.valueOf(node.get(column.name()).asText());
                        setMethod.invoke(targetObject, timestamp);
                    } else if (parameterType == Integer.class){
                        setMethod = getSetMethod(targetClazz, method, Integer.class);
                        int i = node.get(column.name()).asInt();
                        setMethod.invoke(targetObject, i);
                    } else if (parameterType == int.class){
                        setMethod = getSetMethod(targetClazz, method, int.class);
                        int i = node.get(column.name()).asInt();
                        setMethod.invoke(targetObject, i);
                    } else if (parameterType == Boolean.class){
                        setMethod = getSetMethod(targetClazz, method, Boolean.class);
                        Boolean i = node.get(column.name()).asBoolean();
                        setMethod.invoke(targetObject, i);
                    } else if (parameterType == boolean.class){
                        setMethod = getSetMethod(targetClazz, method, boolean.class);
                        boolean i = node.get(column.name()).asBoolean();
                        setMethod.invoke(targetObject, i);
                    }
                    else if (parameterType == Long.class || parameterType == long.class){
                        setMethod = getSetMethod(targetClazz, method, Long.class);
                        long l = node.get(column.name()).asLong();
                        setMethod.invoke(targetObject, l);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return (T)targetObject;
    }
//
    private Method getSetMethod(Class<T> targetClazz, Method method, Class parameterType)
            throws NoSuchMethodException {
        Method setdMethod = null;
        if (method.getName().startsWith("get")) {
            setdMethod = targetClazz.getDeclaredMethod(
                    "set".concat(method.getName().substring(3)), parameterType);
        } else if (method.getName().startsWith("is")) {
            setdMethod = targetClazz.getDeclaredMethod(
                    "set".concat(method.getName().substring(2)), parameterType);
        }
        return setdMethod;
    }

//    @Override
//    public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//        return de;
//    }
}
