package ir.piana.rayan.data.sql;

/**
 * Created by mj.rahmati on 12/11/2019.
 */
public class RayanSQLException extends Exception {
    public RayanSQLException(String message) {
        super(message);
    }

    public RayanSQLException(String message, Throwable cause) {
        super(message, cause);
    }
}
