package ir.rayan.dev.data.serializer;

import ir.rayan.dev.data.persist.Printed;
import sun.misc.Unsafe;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;

import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;

/**
 * Created by mj.rahmati on 1/4/2020.
 */
public class ClassGenerator {
    static JavaCompiler createJavaCompiler() {
        JavaCompiler systemJavaCompiler = null;
        try {
            systemJavaCompiler = ToolProvider.getSystemJavaCompiler();
            if(systemJavaCompiler == null) {
                systemJavaCompiler = (JavaCompiler) Class.forName("com.sun.tools.javac.api.JavacTool").newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return systemJavaCompiler;
    }

    private static Class registerClass(
            String fullClassName,
            String classSource)
            throws NoSuchFieldException,
            IllegalAccessException {
        // A byte array output stream containing the bytes that would be written to the .class file
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + ".java"), SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return classSource;
            }

            @Override
            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };

        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                createJavaCompiler().getStandardFileManager(null, null, null)) {

            @Override
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className,
                                                       JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                return simpleJavaFileObject;
            }
        };

        ToolProvider.getSystemJavaCompiler().getTask(
                null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();

        final byte[] bytes = byteArrayOutputStream.toByteArray();

        // use the unsafe class to load in the class bytes
        final Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        final Unsafe unsafe = (Unsafe) f.get(null);
        final Class aClass = unsafe.defineClass(
                fullClassName, bytes, 0, bytes.length,
                ClassGenerator.class.getClassLoader(),
                null);
        return aClass;
    }

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InstantiationException {

        StringBuffer stringBuffer = new StringBuffer("public class TestClass implements ir.rayan.dev.data.persist.Printed{");
        stringBuffer.append("public void printX() {");
        stringBuffer.append("System.out.println(\"print-x\");");
        stringBuffer.append("}");
        stringBuffer.append("}");

        Class testClass = registerClass("TestClass", stringBuffer.toString());

        Printed p = (Printed)testClass.newInstance();
        p.printX();
    }
}
