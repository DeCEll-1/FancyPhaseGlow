package DeCell.CPG.Reflection;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.Objects;


public class Reflections {

    //region reflection
    public static final MethodHandles.Lookup lookup = MethodHandles.lookup();
    public static final Class<?> fieldClass;
    public static final Class<?> fieldArrayClass;
    public static final Class<?> methodClass;
    public static final Class<?> typeClass;
    public static final Class<?> typeArrayClass;
    public static final Class<?> parameterizedTypeClass;
    public static final Class<?> constructorClass;
    public static final Class<?> constructorArrayClass;
    public static final MethodHandle getFieldTypeHandle;
    public static final MethodHandle setFieldHandle;
    public static final MethodHandle getFieldHandle;
    public static final MethodHandle getFieldNameHandle;
    public static final MethodHandle setFieldAccessibleHandle;
    public static final MethodHandle getParameterCount;
    public static final MethodHandle getMethodNameHandle;
    public static final MethodHandle getMethodHandle;
    public static final MethodHandle invokeMethodHandle;
    public static final MethodHandle setMethodAccessable;
    public static final MethodHandle getModifiersHandle;
    public static final MethodHandle getParameterTypesHandle;
    public static final MethodHandle getReturnTypeHandle;
    public static final MethodHandle getDeclaringClass;
    public static final MethodHandle getGenericTypeHandle;
    public static final MethodHandle getTypeNameHandle;
    public static final MethodHandle getActualTypeArgumentsHandle;
    public static final MethodHandle setConstructorAccessibleHandle;
    public static final MethodHandle getDeclaredConstructorsHandle;
    public static final MethodHandle getDeclaredFieldsHandle;
    public static final MethodHandle getConstructorParameterTypesHandle;
    public static final MethodHandle constructorGetDeclaringClass;
    public static final MethodHandle constructorNewInstanceHandle;
    public static final Class<?> modifierClass;
    public static final MethodHandle modifierIsPublic;
    public static final MethodHandle modifierIsStatic;
    public static final MethodHandle methodIsSynthetic;

    public static final MethodHandle methodIsBridge;

    public static final Class<?> filesClass;
    public static final Class<?> pathClass;
    public static final MethodHandle filesReadStringHandle;
    public static final Class<?> uriClass;
    public static final MethodHandle pathOfUriHandle;

    public static final MethodHandle uriCreateHandle;
    public static final MethodHandle filesDeleteHandle;
    public static final MethodHandle filesDeleteIfExistsHandle;

    static {
        try {
            fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
            fieldArrayClass = Class.forName("[Ljava.lang.reflect.Field;", false, Class.class.getClassLoader());
            methodClass = Class.forName("java.lang.reflect.Method", false, Class.class.getClassLoader());
            typeClass = Class.forName("java.lang.reflect.Type", false, Class.class.getClassLoader());
            typeArrayClass = Class.forName("[Ljava.lang.reflect.Type;", false, Class.class.getClassLoader());
            parameterizedTypeClass = Class.forName("java.lang.reflect.ParameterizedType", false, Class.class.getClassLoader());
            constructorClass = Class.forName("java.lang.reflect.Constructor", false, Class.class.getClassLoader());
            constructorArrayClass = Class.forName("[Ljava.lang.reflect.Constructor;", false, Class.class.getClassLoader());

            setFieldHandle = lookup.findVirtual(fieldClass, "set", MethodType.methodType(void.class, Object.class, Object.class));
            getFieldHandle = lookup.findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
            getFieldNameHandle = lookup.findVirtual(fieldClass, "getName", MethodType.methodType(String.class));
            getFieldTypeHandle = lookup.findVirtual(fieldClass, "getType", MethodType.methodType(Class.class));
            setFieldAccessibleHandle = lookup.findVirtual(fieldClass, "setAccessible", MethodType.methodType(void.class, boolean.class));


            getMethodNameHandle = lookup.findVirtual(methodClass, "getName", MethodType.methodType(String.class));
            getMethodHandle = lookup.findVirtual(Class.class, "getMethod", MethodType.methodType(methodClass, String.class, Class[].class));

            invokeMethodHandle = lookup.findVirtual(methodClass, "invoke", MethodType.methodType(Object.class, Object.class, Object[].class));
            setMethodAccessable = lookup.findVirtual(methodClass, "setAccessible", MethodType.methodType(void.class, boolean.class));
            getModifiersHandle = lookup.findVirtual(methodClass, "getModifiers", MethodType.methodType(int.class));

            getParameterTypesHandle = lookup.findVirtual(methodClass, "getParameterTypes", MethodType.methodType(Class[].class));
            getReturnTypeHandle = lookup.findVirtual(methodClass, "getReturnType", MethodType.methodType(Class.class));
            getParameterCount = lookup.findVirtual(methodClass, "getParameterCount", MethodType.methodType(int.class));

            getDeclaringClass = lookup.findVirtual(methodClass, "getDeclaringClass", MethodType.methodType(Class.class));

            getGenericTypeHandle = lookup.findVirtual(fieldClass, "getGenericType", MethodType.methodType(typeClass));
            getTypeNameHandle = lookup.findVirtual(typeClass, "getTypeName", MethodType.methodType(String.class));
            getActualTypeArgumentsHandle = lookup.findVirtual(parameterizedTypeClass, "getActualTypeArguments", MethodType.methodType(typeArrayClass));

            setConstructorAccessibleHandle = lookup.findVirtual(constructorClass, "setAccessible", MethodType.methodType(void.class, boolean.class));
            getConstructorParameterTypesHandle = lookup.findVirtual(constructorClass, "getParameterTypes", MethodType.methodType(Class[].class));
            constructorNewInstanceHandle = lookup.findVirtual(constructorClass, "newInstance", MethodType.methodType(Object.class, Object[].class));

            constructorGetDeclaringClass = lookup.findVirtual(constructorClass, "getDeclaringClass", MethodType.methodType(Class.class));

            getDeclaredConstructorsHandle = lookup.findVirtual(Class.class, "getDeclaredConstructors", MethodType.methodType(constructorArrayClass));
            getDeclaredFieldsHandle = lookup.findVirtual(Class.class, "getDeclaredFields", MethodType.methodType(fieldArrayClass));

            modifierClass = Class.forName("java.lang.reflect.Modifier", false, Class.class.getClassLoader());

            modifierIsPublic = lookup.findStatic(modifierClass, "isPublic", MethodType.methodType(boolean.class, int.class));
            modifierIsStatic = lookup.findStatic(modifierClass, "isStatic", MethodType.methodType(boolean.class, int.class));

            methodIsSynthetic = lookup.findVirtual(methodClass, "isSynthetic", MethodType.methodType(boolean.class));
            methodIsBridge = lookup.findVirtual(methodClass, "isBridge", MethodType.methodType(boolean.class));

            filesClass = Class.forName("java.nio.file.Files", false, Class.class.getClassLoader());
            pathClass = Class.forName("java.nio.file.Path", false, Class.class.getClassLoader());
            uriClass = Class.forName("java.net.URI", false, Class.class.getClassLoader());

            pathOfUriHandle = lookup.findStatic(pathClass, "of", MethodType.methodType(pathClass, uriClass));

            // Files.readString(Path) -> String
            filesReadStringHandle = lookup.findStatic(filesClass, "readString", MethodType.methodType(String.class, pathClass));

            uriCreateHandle = lookup.findStatic(
                    uriClass,
                    "create",
                    MethodType.methodType(uriClass, String.class)
            );

            filesDeleteHandle = lookup.findStatic(
                    filesClass,
                    "delete",
                    MethodType.methodType(void.class, pathClass)
            );

            // Files.deleteIfExists(Path) -> boolean
            filesDeleteIfExistsHandle = lookup.findStatic(
                    filesClass,
                    "deleteIfExists",
                    MethodType.methodType(boolean.class, pathClass)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
//endregion

    public static Object getFieldFromName(Class<?> c, String name) throws Throwable {
        for (Object field : c.getDeclaredFields())
            if (Objects.equals(name, (String) Reflections.getFieldNameHandle.invoke(field)))
                return field;
        return null;
    }

    public static VarHandle getVarHandle(Class<?> targetClass, String fieldName, Class<?> fieldType) throws Throwable {
        // Create a private lookup with full access to the target class
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(targetClass, MethodHandles.lookup());

        // Find and return the VarHandle
        return privateLookup.findVarHandle(targetClass, fieldName, fieldType);
    }


    public static Object invokeMethod(String methodName, Object target) {
        if (target == null || methodName == null || methodName.isEmpty()) {
            throw new IllegalArgumentException("Target object and method name cannot be null or empty");
        }

        try {
            Class<?> clazz = target.getClass();

            // Get the Method object using getMethod (no parameters)
            Object method = getMethodHandle.invoke(clazz, methodName, new Class<?>[0]);

            // Make it accessible in case it's private/protected
            setMethodAccessable.invoke(method, true);

            // Invoke the method on the target
            return invokeMethodHandle.invoke(method, target, new Object[0]);

        } catch (Throwable t) {
            throw new RuntimeException("Failed to invoke method '" + methodName +
                    "' on " + target.getClass().getName(), t);
        }
    }

    public static Object invokeMethod(String methodName, Object targetOrClass, Class<?>[] parameterTypes, Object... args) {
        if (methodName == null || methodName.isEmpty()) {
            throw new IllegalArgumentException("Method name cannot be null or empty");
        }
        if (targetOrClass == null) {
            throw new IllegalArgumentException("Target object or target Class cannot be null");
        }

        Class<?>[] paramTypes = (parameterTypes != null) ? parameterTypes : new Class<?>[0];
        Object[] methodArgs = (args != null) ? args : new Object[0];

        try {
            Class<?> clazz;
            Object instance;

            if (targetOrClass instanceof Class<?>) {
                clazz = (Class<?>) targetOrClass;
                instance = null;
            } else {
                clazz = targetOrClass.getClass();
                instance = targetOrClass;
            }

            Object method = getMethodHandle.invoke(clazz, methodName, paramTypes);

            setMethodAccessable.invoke(method, true);

            return invokeMethodHandle.invoke(method, instance, methodArgs);

        } catch (Throwable t) {
            String className = (targetOrClass instanceof Class<?>)
                    ? ((Class<?>) targetOrClass).getName()
                    : targetOrClass.getClass().getName();

            throw new RuntimeException("Failed to invoke method '" + methodName +
                    "' on " + className + " with the specified parameters.", t);
        }
    }

    public static Object createInstanceWithArgs(Class<?> clazz, Class<?>[] parameterTypes, Object... args) {
        if (parameterTypes == null) parameterTypes = new Class<?>[0];
        if (args == null) args = new Object[0];

        try {
            Object[] constructors = (Object[]) getDeclaredConstructorsHandle.invoke(clazz);

            for (Object constructor : constructors) {
                Class<?>[] paramTypes = (Class<?>[]) getConstructorParameterTypesHandle.invoke(constructor);

                if (paramTypes.length != parameterTypes.length) {
                    continue;
                }

                boolean match = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    if (paramTypes[i] != parameterTypes[i]) {
                        match = false;
                        break;
                    }
                }

                if (match) {
                    setConstructorAccessibleHandle.invoke(constructor, true);

                    Object[] newArray = Arrays.copyOf(args, args.length + 1);
                    newArray[0] = constructor;
                    System.arraycopy(args, 0, newArray, 1, args.length);
                    return constructorNewInstanceHandle.invokeWithArguments(newArray);
                }
            }

            throw new NoSuchMethodException("No constructor found for " + clazz.getName() +
                    " with specified parameter types.");

        } catch (Throwable t) {
            throw new RuntimeException("Failed to instantiate " + clazz.getName() + " with arguments via MethodHandles", t);
        }
    }
}