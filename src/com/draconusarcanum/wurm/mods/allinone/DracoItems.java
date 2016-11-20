package com.draconusarcanum.wurm.mods.allinone;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.server.MiscConstants;

import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateCreator;

import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.draconusarcanum.wurm.mods.actions.HolyBookPray;

public class DracoItems {

    public static final int HOLY_BOOK = 56001;

    public static final Logger logger = Logger.getLogger("DraconusArcanum");

    public static void addHolyBook() {
        try {

                ItemTemplate itmp = ItemTemplateCreator.createItemTemplate(
                    HOLY_BOOK,
                    "holy book", "holy books", "superb", "normal", "worn", "tattered",
                    "A leather bound holy book of prayers", 
                    new short[] {
                        ItemTypes.ITEM_TYPE_NAMED,
                        //ItemTypes.ITEM_TYPE_NODROP,
                        ItemTypes.ITEM_TYPE_LEATHER,
                        ItemTypes.ITEM_TYPE_MISSION,
                        ItemTypes.ITEM_TYPE_FULLPRICE,
                        ItemTypes.ITEM_TYPE_REPAIRABLE,
                    },
                    (short) 325,                                // imageNumber ? ( copied from tome )
                    (short) 1,                                  // behaviorType
                    0,                                          // combatDamage ( option to fight with tome? )
                    9072000,                                    // decayTime ( coppied from bow... )
                    3, 30, 30,                                  // x,y,z in cm
                    -10,                                        // primary skill
                    MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,   // bodySpaces?
                    "model.artifact.tomemagic.white",           // modelName
                    20.0f,                                      // difficulty (to create? to use?)
                    1000,                                       // weightInGrams
                    (byte) 33,                                  // material
                    500,                                        // value
                    true
                );

                ModActions.registerAction( new HolyBookPray() );

        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Uncaught Exception in onServerStarted", e);
        }
    }
}
