package com.draconusarcanum.wurm.mods.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.draconusarcanum.wurm.mods.utils.ClassMap;

public class TweakApiPerms {

    public static final Logger logger = Logger.getLogger("TweakApiPerms");
    public static final ClassMap classmap = new ClassMap();

    public static Class getClass(String name) {
        return classmap.getClass(name);
    }

    public static boolean setItemField( Object item, String fieldName, Object valu ) {

        Field field = null;
        Class clas = item.getClass();

        try {
            field = clas.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.log(Level.SEVERE,String.format("setItemField(): No Such Field: %s on %s", fieldName, clas.getName()) );
            for ( Field oops : clas.getDeclaredFields() ) {
                String butHas = String.format("has field: %s", oops.getName() );
                logger.log(Level.INFO,butHas);
            }
            return false;
        }

        field.setAccessible(true);

        try {
            field.set(item,valu);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE,"setItemField Illegal: " + fieldName);
            return false;
        }
        return true;

    }

    public static Object getItemField( Object item, String fieldName ) {

        Field field = null;
        Class clas = item.getClass();

        try {
            field = clas.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.log(Level.SEVERE,String.format("getItemField(): No Such Field: %s on %s", fieldName, clas.getName()) );
            return null;
        }

        field.setAccessible(true);

        try {
            return field.get(item);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE,"getItemField Illegal: " + fieldName);
            return null;
        }
    }

    public static boolean setClassField(String className, String fieldName, Object item, Object valu ) {

        Field field = null;

        Class clas = classmap.getClass(className);
        if ( clas == null ) {
            return false;
        }

        try {
            field = clas.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.log(Level.SEVERE,String.format("setItemField(): No Such Field: %s on %s", fieldName, clas.getName()) );
            for ( Field oops : clas.getDeclaredFields() ) {
                String butHas = String.format("has field: %s", oops.getName() );
                logger.log(Level.INFO,butHas);
            }
            return false;
        }

        field.setAccessible(true);

        try {
            field.set(item,valu);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE,"setItemField Illegal: " + fieldName);
            return false;
        }
        return true;

    }

    public static Object getClassField( String className, String fieldName, Object item ) {

        Field field = null;

        Class clas = classmap.getClass(className);
        if ( clas == null ) {
            return false;
        }

        try {
            field = clas.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            logger.log(Level.SEVERE,String.format("getItemField(): No Such Field: %s on %s", fieldName, clas.getName()) );
            return null;
        }

        field.setAccessible(true);

        try {
            return field.get(item);
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE,"getItemField Illegal: " + fieldName);
            return null;
        }
    }


    public static Method getClassMeth(String cname, String mname, String ... params) {

        Method meth = null;

        Class [] ptypes = classmap.getClassArray(params);
        if ( ptypes == null ) {
            return null;
        }

        Class clas = classmap.getClass(cname);
        if ( clas == null ) {
            return null;
        }

        try {
            meth = clas.getDeclaredMethod(mname, ptypes);
        } catch (NoSuchMethodException e) {
            logger.log(Level.SEVERE,"Method Not Found: " + mname);
            return null;
        }

        meth.setAccessible(true);
        return meth;
    }
}
