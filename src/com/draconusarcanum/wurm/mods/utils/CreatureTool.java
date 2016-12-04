
package com.draconusarcanum.wurm.mods.utils;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.wurmonline.server.creatures.CreatureTypes;
import com.wurmonline.server.creatures.CreatureTemplate;
import com.wurmonline.server.creatures.CreatureTemplateIds;
import com.wurmonline.server.creatures.CreatureTemplateFactory;

import com.draconusarcanum.wurm.mods.utils.TweakApiPerms;

public class CreatureTool {

    public static final Logger logger = Logger.getLogger("CreatureTool");

    public static CreatureTemplate getTemplate(String name) {
        CreatureTemplateFactory fact = CreatureTemplateFactory.getInstance();
        HashMap<String,CreatureTemplate> tmap = (HashMap<String,CreatureTemplate>) TweakApiPerms.getItemField(fact,"templatesByName");
        if ( tmap == null ) {
            return null;
        }
        return tmap.get(name);
    }

    public static boolean setTemplateField(String creatureName, String fieldName, Object fieldValue) {
        CreatureTemplate tmpl = getTemplate(creatureName);
        if ( tmpl == null ) {
            return false;
        }
        return TweakApiPerms.setClassField("com.wurmonline.server.creatures.CreatureTemplate", fieldName, tmpl, fieldValue);
    }

    public static boolean makeLikeHorse(String creatureName) {
            CreatureTemplate tmpl = getTemplate(creatureName);
            if ( tmpl == null ) {
                return false;
            }

            addTemplateTypes("Unicorn",42,14,12,43); // HORSE, LEADABLE, SWIM, DOMESTIC
            setTemplateField("Unicorn","isHorse",true);
            return true;
    }

    public static boolean addTemplateTypes(String creatureName, int ... types ) {
        Method meth = null;

        CreatureTemplate tmpl = getTemplate(creatureName);
        if ( tmpl == null ) {
            return false;
        }

        for ( Method derp : tmpl.getClass().getDeclaredMethods() ) {
            logger.log(Level.SEVERE,"templ Meth: " + derp.getName());
        }

        meth = TweakApiPerms.getClassMeth("com.wurmonline.server.creatures.CreatureTemplate","assignTypes","int[]");
        if ( meth == null ) {
            return false;
        }

        try {
            meth.invoke( tmpl, types );
            return true;
        } catch ( Throwable e ) {
            logger.log(Level.SEVERE,"addTemplateTypes: " + e.toString());
        }
        return false;
    }

}
