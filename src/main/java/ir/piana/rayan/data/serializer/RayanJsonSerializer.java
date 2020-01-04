package ir.piana.rayan.data.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.Column;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

/**
 * Created by mj.rahmati on 1/2/2020.
 */
public class RayanJsonSerializer<T> extends JsonSerializer<T> {
    @Override
    public void serialize(T targetObject, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
//                        Field[] fields = testTableEntity.getClass().getFields();
        Method[] methods = targetObject.getClass().getDeclaredMethods();
        StringBuffer stringBuffer = new StringBuffer("{");
        boolean doDelete = false;
        for(Method method : methods) {
            Column column = null;
            if((method.getName().startsWith("get") || method.getName().startsWith("is")) && (column = method.getAnnotation(Column.class)) != null) {
                try {
                    doDelete = true;
                    String left = "\"" + column.name() + "\"";
                    Object object = method.invoke(targetObject);
                    Class<?> returnType = method.getReturnType();
                    String right = null;
                    if(returnType == String.class)
                        right = "\"" + String.format("%s", object) + "\"";
                    else if(returnType == Timestamp.class)
                        right = "\"" + String.format("%s", object) + "\"";
                    else if(returnType == boolean.class || returnType == Boolean.class) {
                        right = String.format("%s", ((Boolean)object) == true ? 1 : 0);
                    } else {
                        right = String.format("%s", object);
                    }
                    if(right != null)
                        stringBuffer.append(left + ": " + right + ", ");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        if(doDelete)
            stringBuffer.deleteCharAt(stringBuffer.length() - 2);
        String s = stringBuffer.append("}").toString();
        jsonGenerator.writeRaw(s);
    }
}
