/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venky.poi;

import java.lang.reflect.Method;
import java.util.Date;

/**
 *
 * @author venky
 */
public class BeanIntrospector<B> {
    private Class<B> beanClass;
    protected Class<B> getBeanClass(){
    	return beanClass;
    }
    protected BeanIntrospector(Class<B> beanClass) {
        this.beanClass = beanClass;
    }
    private static Class<?>[] primitiveNumericClasses = new Class[] { int.class, short.class, long.class, double.class, float.class }; 
    protected static boolean isNumeric(Class<?> headingClass){
    
        if (Number.class.isAssignableFrom(headingClass)){
            return true;
        }
        if (headingClass.isPrimitive()){
            for (Class<?> pc : primitiveNumericClasses){
                if (pc.equals(headingClass)){
                    return true;
                }
            }
        }
        return false;
    }
    
    protected static boolean isDate(Class<?> headClass){
        return Date.class.isAssignableFrom(headClass);
    }
    
    protected static boolean isBoolean(Class<?> headClass){
        return boolean.class.equals(headClass) || Boolean.class.isAssignableFrom(headClass) ;
    }
    
    protected Method getMethod(String methodName,Class<?> ... parameterTypes) { 
        try {
            return beanClass.getMethod(methodName, parameterTypes);
        } catch (Exception ex) {
            return null;
        }
    }
    protected B createInstance(){ 
        try { 
            return beanClass.newInstance();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    
    protected Method getGetter(String heading) {
        return getMethod("get"+heading,new Class[]{});
    }
}
