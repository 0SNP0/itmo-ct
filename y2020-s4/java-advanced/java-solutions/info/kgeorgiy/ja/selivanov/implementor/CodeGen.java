package info.kgeorgiy.ja.selivanov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for source code generation
 */
public class CodeGen {

    /**
     * Class generation point of entry
     *
     * @param token the given {@link Class} token
     * @return generated class sourcecode via {@link String}
     * @throws ImplerException when exception occurred during generation
     */
    public static String generate(Class<?> token) throws ImplerException {
        return String.join(
                LINE_SEPARATOR,
                packageName(token), classDeclaration(token),
                String.join(LINE_SEPARATOR, constructors(token)), String.join(LINE_SEPARATOR, methods(token)),
                CURLY_CLOSE_BRACKET
        );
    }

    /**
     * Generates package name for given {@link Class} token
     *
     * @param token the given token
     * @return generated package name
     */
    private static String packageName(Class<?> token) {
        Package p = token.getPackage();
        return p == null ? NOTHING : String.join(
                SPACE, PACKAGE, p.getName(), SEMICOLON, LINE_SEPARATOR
        );
    }

    /**
     * Generates class declaration for given {@link Class} token
     *
     * @param token the given token
     * @return generated class declaration
     */
    private static String classDeclaration(Class<?> token) {
        return String.join(
                SPACE, PUBLIC, CLASS, plusImpl(token),
                token.isInterface() ? IMPLEMENTS : EXTENDS,
                token.getCanonicalName(), CURLY_OPEN_BRACKET
        );
    }

    /**
     * Returns {@link List} of generated constructors
     *
     * @param token the given token
     * @return {@link List} of sourcecode of constructors
     * @throws ImplerException if there isn't non-private constructors
     */
    private static List<String> constructors(Class<?> token) throws ImplerException {
        if (token.isInterface()) {
            return Collections.emptyList();
        }
        List<String> result = Arrays.stream(token.getDeclaredConstructors())
                .filter(x -> !Modifier.isPrivate(x.getModifiers()))
                .map(CodeGen::genConstructor).toList();
        if (result.isEmpty()) {
            throw new ImplerException("No non-private constructors");
        }
        return result;
    }

    /**
     * Generate a constructor sourcecode for given {@link Constructor}
     * @param constructor the given constructor
     * @return generated constructor sourcecode
     */
    private static String genConstructor(final Constructor<?> constructor) {
        return String.join(
                LINE_SEPARATOR,
                TAB + genExec(constructor, plusImpl(constructor.getDeclaringClass())),
                TAB.repeat(2) + SUPER + OPEN_BRACKET + genArgs(constructor, false) + CLOSE_BRACKET + SEMICOLON,
                TAB + CURLY_CLOSE_BRACKET
        );
    }

    /**
     * Generates open line for the given executable
     * @param exec the given {@link Executable}
     * @param name exutable name
     * @return open line for the given executable
     */
    private static String genExec(Executable exec, String name) {
        return String.join(
                SPACE,
                PUBLIC,
                name + OPEN_BRACKET + genArgs(exec, true) + CLOSE_BRACKET,
                genExceptions(exec), CURLY_OPEN_BRACKET
        );
    }

    /**
     * Generates sourcecode for arguments
     *
     * @param exec the given {@link Executable}
     * @param declaration a boolean flag for declaration
     * @return sourcecode for arguments
     */
    private static String genArgs(Executable exec, boolean declaration) {
        return Arrays.stream(exec.getParameters()).map(
                x -> (declaration ? x.getType().getCanonicalName() + SPACE : NOTHING) + x.getName()
        ).collect(Collectors.joining(ARGS_DELIMITER));
    }

    /**
     * Generates thrown exceptions
     *
     * @param exec the given {@link Executable}
     * @return thrown exceptions
     */
    private static String genExceptions(Executable exec) {
        Class<?>[] except = exec.getExceptionTypes();
        if (except.length == 0) {
            return NOTHING;
        }
        return String.join(
                SPACE, THROWS,
                Arrays.stream(except).map(Class::getCanonicalName).collect(Collectors.joining(ARGS_DELIMITER))
        );
    }

    /**
     * Returns {@link List} of methods sourcecode
     *
     * @param token the given token
     * @return {@link List} of generated methods
     */
    private static List<String> methods(Class<?> token) {
        return superMethods(token).stream().map(MethodWrapper::method).filter(x -> Modifier.isAbstract(x.getModifiers()))
                .map(CodeGen::genMethod).toList();
    }

    /**
     * Returns all not private parents methods for given {@link Class} token
     *
     * @param token the given token
     * @return {@link Set} of {@link MethodWrapper} for methods
     */
    private static Set<MethodWrapper> superMethods(Class<?> token) {
        if (token == null) {
            return Collections.emptySet();
        }
        Set<MethodWrapper> result = wrap(token.getMethods());
        result.addAll(wrap(token.getDeclaredMethods()));
        result.addAll(superMethods(token.getSuperclass()));
        return result;
    }

    /**
     * Wrappes methods
     *
     * @param methods array of methods
     * @return {@link Set} of {@link MethodWrapper}
     */
    private static Set<MethodWrapper> wrap(Method[] methods) {
        return Arrays.stream(methods).map(MethodWrapper::new).collect(Collectors.toSet());
    }

    /**
     * Generates a sourcecode for method
     *
     * @param method the given {@link Method}
     * @return sourcecode for method
     */
    private static String genMethod(Method method) {
        Class<?> returnType = method.getReturnType();
        return String.join(
                LINE_SEPARATOR,
                TAB + genExec(method, String.join(SPACE, method.getReturnType().getCanonicalName(), method.getName())),
                String.join(
                        SPACE, TAB.repeat(2) + RETURN,
                        returnType.isPrimitive() ? (
                                returnType.equals(void.class) ? NOTHING : (
                                        returnType.equals(boolean.class) ? "true" : "0"
                                )) : null,
                        SEMICOLON
                ),
                TAB + CURLY_CLOSE_BRACKET
        );
    }

    /**
     * Wrapper for method with equals and hashCode
     */
    private record MethodWrapper(Method method) {

        /**
         * Calculate {@link Object#hashCode()} by name, parameter types and return type
         * @return hashcode
         */
        @Override
        public int hashCode() {
            return Objects.hash(method.getName(), Arrays.hashCode(method.getParameterTypes()), method.getReturnType());
        }

        /**
         * {@link Object#equals(Object)}
         *
         * @param obj another object
         * @return true if given object equals that
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            MethodWrapper o = (MethodWrapper) obj;
            return o.method.getName().equals(method.getName()) && Arrays.equals(o.method.getParameterTypes(), method.getParameterTypes()) &&
                    Objects.equals(o.method.getReturnType(), method.getReturnType());
        }
    }

    /**
     * Returns name for implemented class
     *
     * @param token the given token
     * @return name for given class token
     */
    public static String plusImpl(Class<?> token) {
        return token.getSimpleName() + IMPL;
    }

    /**
     * Suffix for name of implemented class
     */
    public static final String IMPL = "Impl";
    /**
     * Separator
     */
    public static final char SEPARATOR_CHAR = File.separatorChar;
    /**
     * Separator
     */
    public static final String PATH_SEPARATOR = File.pathSeparator;
    /**
     * Separator
     */
    private static final String LINE_SEPARATOR = System.lineSeparator();
    /**
     * Keyword
     */
    private static final String PACKAGE = "package";
    /**
     * Keyword
     */
    private static final String CLASS = "class";
    /**
     * Keyword
     */
    private static final String PUBLIC = "public";
    /**
     * Keyword
     */
    private static final String SUPER = "super";
    /**
     * Keyword
     */
    private static final String IMPLEMENTS = "implements";
    /**
     * Keyword
     */
    private static final String EXTENDS = "extends";
    /**
     * Keyword
     */
    private static final String THROWS = "throws";
    /**
     * Keyword
     */
    private static final String RETURN = "return";
    /**
     * Syntactic unit
     */
    private static final String OPEN_BRACKET = "(";
    /**
     * Syntactic unit
     */
    private static final String CLOSE_BRACKET = ")";
    /**
     * Syntactic unit
     */
    private static final String CURLY_OPEN_BRACKET = "{";
    /**
     * Syntactic unit
     */
    private static final String CURLY_CLOSE_BRACKET = "}";
    /**
     * Syntactic unit
     */
    private static final String SEMICOLON = ";";
    /**
     * Tabulation
     */
    private static final String TAB = "\t";
    /**
     * delimiter for arguments
     */
    private static final String ARGS_DELIMITER = ", ";
    /**
     * Empty string
     */
    private static final String NOTHING = "";
    /**
     * Space
     */
    private static final String SPACE = " ";
}
