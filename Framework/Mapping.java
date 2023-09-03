package etu2070.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Mapping {

    String className;
    String method;
    Method declaredMethod;

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public Mapping(String className, String method) {
        setClassName(className);
        setMethod(method);
    }

    public Method getDeclaredMethod() throws NoSuchMethodException, ClassNotFoundException {
        if (declaredMethod != null) return declaredMethod;
        Class<?> cls = Class.forName(getClassName());
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(getMethod())) {
                declaredMethod = method;
                return method;
            }
        }
        throw new NoSuchMethodException("Method not found");
    }
    
}
