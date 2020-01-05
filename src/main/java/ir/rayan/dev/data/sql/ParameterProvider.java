package ir.rayan.dev.data.sql;

/**
 * Created by mj.rahmati on 1/2/2020.
 */
public interface ParameterProvider {
    default <T> T get(String paramName) {
        return null;
    }

    default String getString(String paramName) {
        return null;
    }

    default Byte getByte(String paramName) {
        return null;
    }

    default Short getShort(String paramName) {
        return null;
    }

    default Integer getInt(String paramName) {
        return null;
    }

    default Long getLong(String paramName) {
        return null;
    }

    default Float getFloat(String paramName) {
        return null;
    }

    default Double getDouble(String paramName) {
        return null;
    }

    default Character getChar(String paramName) {
        return null;
    }

    default Boolean getBoolean(String paramName) {
        return null;
    }
}
