package ru.ifmo.rain.krylov.implementor;

import info.kgeorgiy.java.advanced.implementor.Impler;
import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.lang.reflect.*;

import static java.lang.Class.forName;
import static java.lang.String.format;

public class Implementor implements Impler {

    public static void main(String[] args) {
        Implementor implementor = new Implementor();
        try {
            implementor.implement(forName(args[0]), Paths.get("test"));
        } catch (ImplerException ie) {
            System.err.println("impler exception");
        } catch (ClassNotFoundException cnfe) {
            System.err.println("class not found exception");
        }
    }

    private static final String INDENT = "    ";

    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {

        if (!token.isInterface()) {
            throw new ImplerException("argument isn't interface");
        }

        Path folderPath = root.resolve(token.getPackage() != null ? token.getPackage().getName().replace('.', File.separatorChar) : "");
        Path filePath = folderPath.resolve(token.getSimpleName() + "Impl.java");

        try {
            Files.createDirectories(folderPath);
        } catch (IOException e) {
            throw new ImplerException("couldn't create directories");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {

            //package
            String packageName = token.getPackage().getName();
            writer.write((packageName != null ? format("package %s;", packageName) : "") + "\n\n");

            //title
            String interfaceName = token.getSimpleName();
            String className = interfaceName + "Impl";
            int mod = token.getModifiers();
            if (Modifier.isAbstract(mod)) {
                mod -= Modifier.ABSTRACT;
            }
            if (Modifier.isInterface(mod)) {
                mod -= Modifier.INTERFACE;
            }
            writer.write(format("%s class %s implements %s {\n", Modifier.toString(mod), className, interfaceName));

            //methods
            for (Method method : token.getMethods()) {
                if (method.isDefault()) {
                    continue;
                }
                int modifiers = method.getModifiers();
                if (Modifier.isAbstract(modifiers)) {
                    modifiers -= Modifier.ABSTRACT;
                }
                if (Modifier.isTransient(modifiers)) {
                    modifiers -= Modifier.TRANSIENT;
                }

                //annotations
                writer.write(Arrays.stream(method.getAnnotations()).map(p -> "@" + p.annotationType().getCanonicalName())
                        .collect(Collectors.joining("\n")) + "\n");

                //modifiers
                writer.write(format("\n%s%s %s %s", INDENT, Modifier.toString(modifiers), method.getReturnType().getCanonicalName(), method.getName()));

                //arguments
                writer.write("(" + Arrays.stream(method.getParameters()).map(p -> p.getType().getCanonicalName() + " " + p.getName())
                        .collect(Collectors.joining(", ")) + ")");

                //exceptions
                if (method.getExceptionTypes().length != 0) {
                    writer.write(" throws " + Arrays.stream(method.getExceptionTypes()).map(Class::getCanonicalName).
                            collect(Collectors.joining(", ")));
                }

                //inner code
                writer.write(" {\n");
                if (method.getReturnType() != void.class) {
                    //generating return
                    String ret;
                    if (method.getReturnType() == boolean.class) {
                        ret = "false";
                    } else if (method.getReturnType().isPrimitive()) {
                        ret = "0";
                    } else {
                        ret = "null";
                    }

                    writer.write(format("%s%sreturn %s;", INDENT, INDENT, ret));
                }
                writer.write(format("\n%s}\n", INDENT));
            }
            writer.write("}");
        } catch (IOException e) {
            throw new ImplerException(e.getMessage());
        }
    }
}