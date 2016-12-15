
package com.draconusarcanum.wurm.mods.utils;

import java.util.Set;
import java.util.List;
import java.util.Arrays;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Collections;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.wurmonline.server.villages.Villages;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Creatures;
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

    public static HashMap<String,Integer> getCreatureHisto( boolean killable ) {
        HashMap<String,Integer> histo = new HashMap<String,Integer>();
        Integer v = 0;
        for ( Creature targ : Creatures.getInstance().getCreatures() ) {

            if ( killable && ! isOkToDestroy(targ) ) {
                continue;
            }

            CreatureTemplate tplat = targ.getTemplate();
            String name = tplat.getName();

            v = histo.get( name );
            if ( v == null ) {
                histo.put(name,1);
            } else {
                histo.put(name, v+1 );
            }
        }
        return histo;
    }

    public static boolean isOnDeed(Creature target) {
        return Villages.getVillage( target.getTileX(), target.getTileY(), true) != null;
    }

    public static boolean isOkToDestroy(Creature target) {
        if (isOnDeed(target)) {
            logger.log(Level.INFO,"onDeed:" + target.getName());
            return false;
        }
        if ( target.isBranded() ) {
            return false;
        }
        if ( target.isDominated() ) {
            return false;
        }
        if ( target.isUndead() ) {
            return false;
        }
        if ( target.isReborn() ) {
            return false;
        }
        if ( target.getHitched() != null ) {
            return false;
        }
        if ( target.isRidden() ) {
            return false;
        }
        return true;
    }

    public static void cullByName(Integer count, String name) {
        LinkedList<Creature> list = new LinkedList<Creature>();

        for ( Creature targ : getCreaturesByName(name) ) {
            logger.log(Level.INFO,"by name: " + targ.getName());
            if (! isOkToDestroy(targ) ) {
                continue;
            }
            logger.log(Level.INFO,"to kill!");
            list.add(targ);
        }

        Collections.shuffle(list);

        //Set<Creature> randSet = new HashSet<Creature>(list.subList(0, count));

        for ( Creature targ : list.subList(0, count) ) {
            targ.destroy();
        }
    }

    public static Creature[] getCreaturesByName(String name) {
        HashSet<Creature> ret = new HashSet<Creature>();
        for ( Creature targ : Creatures.getInstance().getCreatures() ) {
            if ( targ.getTemplate().getName().equals(name) ) {
                ret.add(targ);
            }
        }
        return ret.toArray(new Creature[ret.size()]);
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
