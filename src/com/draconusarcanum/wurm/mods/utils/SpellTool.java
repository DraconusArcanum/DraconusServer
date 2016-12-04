package com.draconusarcanum.wurm.mods.utils;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.spells.Spells;

import com.draconusarcanum.wurm.mods.utils.TweakApiPerms;

public class SpellTool {

    public static final Logger logger = Logger.getLogger("SpellTool");

    static public boolean noSpellCooldown(String name) {
        Spell spell = getSpellByName(name);
        if ( spell == null ) {
            return false;
        }

        logger.log(Level.INFO, "noSpellCooldown: " + spell.name);
        return TweakApiPerms.setClassField("com.wurmonline.server.spells.Spell", "cooldown", spell, 0);
    }

    public static Spell getSpellByName(String name) {
        for ( Spell spell : Spells.getAllSpells() ) {
            //logger.log(Level.INFO, String.format("spell ->%s<-", spell.name));
            if ( spell.name.equals(name) ) {
                return spell;
            }
        }
        logger.log(Level.INFO, "getSpellByName failed: " + name);
        return null;
    }
}
