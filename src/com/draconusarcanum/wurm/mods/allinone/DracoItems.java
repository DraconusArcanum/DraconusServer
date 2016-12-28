package com.draconusarcanum.wurm.mods.allinone;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.server.MiscConstants;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemTypes;
import com.wurmonline.server.items.ItemTemplate;
import com.wurmonline.server.items.ItemTemplateCreator;

import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import com.draconusarcanum.wurm.mods.actions.HolyBookPray;
import com.draconusarcanum.wurm.mods.actions.PortalTeleport;

public class DracoItems {

    public static final int HOLY_BOOK = 56001;
    public static final int NYMPH_PORTAL = 56002;
    public static final int DEMON_PORTAL = 56003;
    public static final int NYMPH_HOME_PORTAL = 56004;
    public static final int DEMON_HOME_PORTAL = 56005;

    public static final Logger logger = Logger.getLogger("DraconusArcanum");

    public static boolean actPortalDone = false;

    public static int[] portalItems = { NYMPH_PORTAL, DEMON_PORTAL, NYMPH_HOME_PORTAL, DEMON_HOME_PORTAL };
    public static int[] homePortalItems = { NYMPH_HOME_PORTAL, DEMON_HOME_PORTAL };

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

    public static void addNymphPortal() {

        try { 

            ItemTemplateCreator.createItemTemplate(
                NYMPH_PORTAL,
                "nymph portal", "portals",
                "almost full", "somewhat occupied", "half-full", "emptyish",
                "A portal statue in the shape of a nymph",
                new short[]{
                    ItemTypes.ITEM_TYPE_NAMED, //108,
                    ItemTypes.ITEM_TYPE_NOTAKE, //31,
                    ItemTypes.ITEM_TYPE_OWNER_DESTROYABLE, //135,
                    ItemTypes.ITEM_TYPE_STONE, //25,
                    ItemTypes.ITEM_TYPE_TURNABLE, //51,
                    ItemTypes.ITEM_TYPE_DECORATION, //52,
                    ItemTypes.ITEM_TYPE_REPAIRABLE, //44,
                    ItemTypes.ITEM_TYPE_DESTROYABLE, //86,
                    ItemTypes.ITEM_TYPE_COLORABLE, //92,
                    ItemTypes.ITEM_TYPE_TRANSPORTABLE, //176,
                    ItemTypes.ITEM_TYPE_NEVER_SHOW_CREATION_WINDOW_OPTION, //178

                    ItemTypes.ITEM_TYPE_HASDATA,
                },
                (short)60,
                (short)1,
                0,
                12096000,
                20, 30, 160,
                -10,
                MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.decoration.statue.nymph.",
                15.0f,
                70000,
                (byte)62); // marble

            ItemTemplateCreator.createItemTemplate(
                NYMPH_HOME_PORTAL,
                "nymph home portal", "portals",
                "almost full", "somewhat occupied", "half-full", "emptyish",
                "A portal statue in the shape of a nymph",
                new short[]{
                    ItemTypes.ITEM_TYPE_NAMED, //108,
                    ItemTypes.ITEM_TYPE_NOTAKE, //31,
                    ItemTypes.ITEM_TYPE_OWNER_DESTROYABLE, //135,
                    ItemTypes.ITEM_TYPE_STONE, //25,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_TURNABLE, //51,
                    ItemTypes.ITEM_TYPE_DECORATION, //52,
                    ItemTypes.ITEM_TYPE_REPAIRABLE, //44,
                    ItemTypes.ITEM_TYPE_DESTROYABLE, //86,
                    ItemTypes.ITEM_TYPE_COLORABLE, //92,
                    ItemTypes.ITEM_TYPE_TRANSPORTABLE, //176,
                    ItemTypes.ITEM_TYPE_NEVER_SHOW_CREATION_WINDOW_OPTION, //178

                    ItemTypes.ITEM_TYPE_HASDATA,
                },
                (short)60,
                (short)1,
                0,
                12096000,
                20, 30, 160,
                -10,
                MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.decoration.statue.nymph.",
                15.0f,
                70000,
                (byte)62); // marble

                addActPortal();

        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Uncaught Exception in addNymphPortal", e);
        }
    }

    public static void addDemonPortal() {
        try { 

            ItemTemplateCreator.createItemTemplate(
                DEMON_PORTAL,
                "demon portal", "portals",
                "almost full", "somewhat occupied", "half-full", "emptyish",
                "A portal statue in the shape of a demon",
                new short[]{
                    ItemTypes.ITEM_TYPE_NAMED, //108,
                    ItemTypes.ITEM_TYPE_NOTAKE, //31,
                    ItemTypes.ITEM_TYPE_OWNER_DESTROYABLE, //135,
                    ItemTypes.ITEM_TYPE_STONE, //25,
                    ItemTypes.ITEM_TYPE_TURNABLE, //51,
                    ItemTypes.ITEM_TYPE_DECORATION, //52,
                    ItemTypes.ITEM_TYPE_REPAIRABLE, //44,
                    ItemTypes.ITEM_TYPE_DESTROYABLE, //86,
                    ItemTypes.ITEM_TYPE_COLORABLE, //92,
                    ItemTypes.ITEM_TYPE_TRANSPORTABLE, //176,
                    ItemTypes.ITEM_TYPE_NEVER_SHOW_CREATION_WINDOW_OPTION, //178

                    ItemTypes.ITEM_TYPE_HASDATA,
                },
                (short)60,
                (short)1,
                0,
                12096000,
                20, 30, 160,
                -10,
                MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.decoration.statue.demon.",
                15.0f,
                100000,
                (byte)15);

            ItemTemplateCreator.createItemTemplate(
                DEMON_HOME_PORTAL,
                "demon home portal", "portals",
                "almost full", "somewhat occupied", "half-full", "emptyish",
                "A portal statue in the shape of a demon",
                new short[]{
                    ItemTypes.ITEM_TYPE_NAMED, //108,
                    ItemTypes.ITEM_TYPE_NOTAKE, //31,
                    ItemTypes.ITEM_TYPE_OWNER_DESTROYABLE, //135,
                    ItemTypes.ITEM_TYPE_STONE, //25,
                    ItemTypes.ITEM_TYPE_MISSION,
                    ItemTypes.ITEM_TYPE_TURNABLE, //51,
                    ItemTypes.ITEM_TYPE_DECORATION, //52,
                    ItemTypes.ITEM_TYPE_REPAIRABLE, //44,
                    ItemTypes.ITEM_TYPE_DESTROYABLE, //86,
                    ItemTypes.ITEM_TYPE_COLORABLE, //92,
                    ItemTypes.ITEM_TYPE_TRANSPORTABLE, //176,
                    ItemTypes.ITEM_TYPE_NEVER_SHOW_CREATION_WINDOW_OPTION, //178

                    ItemTypes.ITEM_TYPE_HASDATA,
                },
                (short)60,
                (short)1,
                0,
                12096000,
                20, 30, 160,
                -10,
                MiscConstants.EMPTY_BYTE_PRIMITIVE_ARRAY,
                "model.decoration.statue.demon.",
                15.0f,
                100000,
                (byte)15);

                addActPortal();

        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Uncaught Exception in addNymphPortal", e);
        }
    }

    public static void addActPortal() {
        if ( ! actPortalDone ) {
            logger.log(Level.INFO,"Adding PortalTeleport Action");
            ModActions.registerAction( new PortalTeleport() );
            actPortalDone = true;
        }
    }

    public static boolean isPortalItem(Item item) {
        int id = item.getTemplateId();
        for ( int pid : portalItems ) {
            if ( id == pid ) {
                return true;
            }
        }
        return false;
    }
    public static boolean isHomePortalItem(Item item) {
        int id = item.getTemplateId();
        for ( int pid : homePortalItems ) {
            if ( id == pid ) {
                return true;
            }
        }
        return false;
    }
}
