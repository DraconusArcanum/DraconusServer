package com.draconusarcanum.wurm.mods.allinone;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.String;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.ClassPool;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.CannotCompileException;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.CreatureStatus;

import com.wurmonline.server.behaviours.Actions;

import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.spells.Spells;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;

import com.wurmonline.server.villages.Village;
import com.wurmonline.server.villages.Villages;

import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;

import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmServerMod;
import org.gotti.wurmunlimited.modloader.interfaces.ServerStartedListener;
import org.gotti.wurmunlimited.modloader.interfaces.ItemTemplatesCreatedListener;

import com.wurmonline.server.creatures.ai.ChatManager;

import com.draconusarcanum.wurm.mods.utils.SpellTool;
import com.draconusarcanum.wurm.mods.utils.ItemHelper;
import com.draconusarcanum.wurm.mods.utils.CreatureTool;

import com.draconusarcanum.wurm.mods.actions.GmProtect;
import com.draconusarcanum.wurm.mods.actions.CorpseBounty;

import com.draconusarcanum.wurm.mods.allinone.DracoItems;

import com.draconusarcanum.wurm.contrib.ArgumentTokenizer;

import com.draconusarcanum.wurm.mods.utils.CmdTool;

import com.draconusarcanum.wurm.mods.cmds.CmdCull;
import com.draconusarcanum.wurm.mods.cmds.CmdGoTo;
import com.draconusarcanum.wurm.mods.cmds.CmdWoot;
import com.draconusarcanum.wurm.mods.cmds.CmdAddAff;
import com.draconusarcanum.wurm.mods.cmds.CmdCoffers;

public class AllInOne implements WurmServerMod, Configurable, PreInitable,
                                 Initable, ServerStartedListener, BehaviourProvider,
                                 ItemTemplatesCreatedListener {
    
    public static boolean addGmProtect = true;

    public static boolean gmFullFavor = true;
    public static boolean gmFullStamina = true;

    public static boolean itemHolyBook = true;
    public static boolean itemNymphPortal = true;
    public static boolean itemDemonPortal = true;

    public static boolean setUnicornIsHorse = true;

    public static boolean stfuNpcs = true;
    public static boolean noMineDrift = true;
    public static boolean allSurfaceMine = true;
    public static boolean lampsAutoLight = true;
    public static boolean allowTentsOnDeed = true;
    public static boolean loadFullContainers = true;
    public static boolean hidePlayerGodInscriptions = true;

    public static String makeNonAgro = "";
    public static String noCooldownSpells = "";

    public CmdTool cmdtool = null;

    public static final Logger logger = Logger.getLogger("DraconusArcanum");

    public static String[] createItemDescs = {
        "(IFBBJLjava/lang/String;)Lcom/wurmonline/server/items/Item;",
        "(IFFFFZBBJLjava/lang/String;B)Lcom/wurmonline/server/items/Item;",
    };

    @Override
    public void preInit() {
        logger.log(Level.INFO,"preInit()");
        ModActions.init();
    }

    @Override
    public void init() {

        logger.log(Level.INFO,"init()");

        HookManager hooks = HookManager.getInstance();

        ClassPool pool = hooks.getClassPool();

        try {

            if ( stfuNpcs ) {

                hooks.registerHook("com.wurmonline.server.creatures.ai.ChatManager",
                                   "answerLocalChat",
                                   "(Lcom/wurmonline/server/Message;Ljava/lang/String;)V",
                                    () -> (proxy, method, args) -> {
                    return null;
                });

                hooks.registerHook("com.wurmonline.server.creatures.ai.ChatManager",
                                   "getSayToCreature",
                                   "(Lcom/wurmonline/server/creatures/Creature;)Ljava/lang/String;",
                                    () -> (proxy, method, args) -> {
                    return null;
                });
            }


            if ( hidePlayerGodInscriptions ) {

                hooks.registerHook("com.wurmonline.server.deities.Deities",
                                   "getRandomNonHateDeity",
                                   "()Lcom/wurmonline/server/deities/Deity;",
                                   () -> (proxy, method, args) -> {
                    return null;
                });
            }

            if ( gmFullFavor ) {

                hooks.registerHook("com.wurmonline.server.players.Player",
                                   "depleteFavor",
                                   "(FZ)V",
                                   () -> (proxy, method, args) -> {

                    if (proxy instanceof Player) {
                        Player player = (Player) proxy;
                        if ( player.getPower() >= 5 ) {
                            return null;
                        }
                    }

                    return method.invoke(proxy,args);

                });

            }

            hooks.registerHook("com.wurmonline.server.players.Player",
                               "increaseAffinity",
                               "(II)V",
                               () -> (proxy, method, args) -> {


                logger.log(Level.SEVERE, String.format("incAff: %d %d", args[0], args[1]));
                return method.invoke(proxy,args);

            });

            hooks.registerHook("com.wurmonline.server.creatures.Communicator",
                               "reallyHandle_CMD_MESSAGE",
                               "(Ljava/nio/ByteBuffer;)V",
                                () -> (proxy, method, args) -> {

                ByteBuffer byteBuffer = ((ByteBuffer) args[0]).duplicate();

                Communicator comm = (Communicator) proxy;

                Player player = comm.player;

                // yuck...
                byte[] tempStringArr = new byte[byteBuffer.get() & 255];
                byteBuffer.get(tempStringArr);
                String message = new String(tempStringArr, "UTF-8");
                tempStringArr = new byte[byteBuffer.get() & 255];
                byteBuffer.get(tempStringArr);
                String title = new String(tempStringArr, "UTF-8");


                logger.log(Level.INFO, String.format("User Cmd: %s %s %s", comm.player.getName(), title, message));

                String[] argv = ArgumentTokenizer.tokenize( message ).toArray(new String[0]);

                try { 

                    if ( cmdtool.runWurmCmd( player, argv ) ) {
                        return null;
                    }


                } catch (Throwable e) {
                    comm.sendNormalServerMessage( String.format("Cmd Err (%s) %s", message, e.toString()) );
                    return null;
                }

                return method.invoke(proxy,args);

            });

            if ( loadFullContainers ) {

                hooks.registerHook("com.wurmonline.server.behaviours.CargoTransportationMethods",
                                   "targetIsNotEmptyContainerCheck",
                                   "(Lcom/wurmonline/server/items/Item;Lcom/wurmonline/server/creatures/Creature;Lcom/wurmonline/server/items/Item;Z)Z",
                                   () -> (proxy, method, args) -> {

                    return false;
                });
            }

            if ( noMineDrift ) {

                hooks.registerHook("com.wurmonline.server.behaviours.TileRockBehaviour",
                                   "getFloorAndCeiling",
                                   "(IIIIZZLcom/wurmonline/server/creatures/Creature;)[I",
                                   () -> (proxy, method, args) -> {


                    int baseFloor = (int)args[2] + (int)args[3];

                    int[] ret = (int[]) method.invoke(proxy,args);

                    logger.log(Level.INFO, String.format("getFloorAndCeiling: %d %d", baseFloor, (int)ret[0]));

                    if (ret[0] > baseFloor && ret[0] - baseFloor <= 3 ) {
                        ret[0] = baseFloor;
                    }

                    if (ret[0] < baseFloor && baseFloor - ret[0] <= 3 ) {
                        ret[0] = baseFloor;
                    }

                    return ret;

                });
            }

            if ( allowTentsOnDeed ) {

                hooks.registerHook("com.wurmonline.server.behaviours.MethodsItems",
                                   "mayDropTentOnTile",
                                   "(Lcom/wurmonline/server/creatures/Creature;)Z",
                                   () -> (proxy, method, args) -> {
                    return true;
                });
            }

            if ( allSurfaceMine ) {
                hookSurfaceMine(pool);
            }



            /* Hook Item Creation */

            for ( String desc : createItemDescs ) {

                //logger.log(Level.INFO,"createItem: " + desc );

                hooks.registerHook("com.wurmonline.server.items.ItemFactory", "createItem", desc, 
                                   () -> (proxy, method, args ) -> {

                    Object retn = method.invoke(proxy,args);
                    //logger.log(Level.INFO, "createItem: " + retn.toString() );

                    if ( retn != null ) {
                        Item item = (Item) retn;
                        onItemCreated( (Item) retn );
                    }

                    return retn;
                });
            }

            if ( lampsAutoLight ) {

                hooks.registerHook("com.wurmonline.server.items.Item", "refuelLampFromClosestVillage", "()V",
                                   () -> (proxy, method, args) -> {

                    Item lamp = (Item) proxy;
                    lamp.setAuxData((byte)100);
                    return null;
                });
            }


            hooks.registerHook("com.wurmonline.server.structures.Structure",
                               "isEnemyAllowed",
                               "(Lcom/wurmonline/server/creatures/Creature;S)Z",
                               () -> (proxy, method, args) -> {

                short act = (short)args[1];
                //logger.log(Level.INFO, String.format("isEnemyAllowed: %d", act) );

                if ( act >= Actions.actionEntrys.length ) {
                    return false;
                }

                return method.invoke(proxy,args);
            });

            /*
            hooks.registerHook("com.wurmonline.server.structures.Structure",
                               "isEnemy",
                               "(Lcom/wurmonline/server/creatures/Creature;)Z",
                               () -> (proxy, method, args) -> {

                Object ret = method.invoke(proxy,args);
                logger.log(Level.INFO, String.format("isEnemy: %s", ret.toString()) );
                return ret;
            });
            */


            /* Fix for a bug introduced by NPCs being near papyrus with missions on examine */
            /*
            hooks.registerHook("com.wurmonline.server.questions.Questions",
                               "addQuestion",
                               "(Lcom/wurmonline/server/questions/Question;)V",
                               () -> (proxy, method, args) -> {

                if ( !( args[0] instanceof Player ) ) {
                    return false;
                }

                return method.invoke(proxy,args);
            });
            */

            /*
            if ( gmFullStamina ) {
                hooks.registerHook("com.wurmonline.server.creatures.CreatureStatus",
                                   "modifyStamina2",
                                   "(F)V",
                                   () -> (proxy, method, args) -> {

                    CreatureStatus status = (CreatureStatus) proxy;

                    if ( status.statusHolder.getPower() >= 5 ) {
                        args[0] = 100.0f;
                    }

                    return method.invoke(proxy,args);

                });
            }
            */

        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Error in init()", e);
        }

    }

    public void onItemCreated(Item item) {
        if ( lampsAutoLight ) {
            if ( item.isStreetLamp() ) {
                //logger.log(Level.INFO, "createItem() isStreetLamp() == true");
                item.setAuxData((byte)100);
                item.setIsAutoLit(true);
                item.setHasNoDecay(true);
            }
        }
    }

    public void hookSurfaceMine( ClassPool pool ) {
        try {
            CtClass tileRockBehav = pool.get("com.wurmonline.server.behaviours.TileRockBehaviour");
            CtMethod cm = tileRockBehav.getMethod("action", "(Lcom/wurmonline/server/behaviours/Action;Lcom/wurmonline/server/creatures/Creature;Lcom/wurmonline/server/items/Item;IIZIISF)Z");

            cm.instrument(
                new ExprEditor() {
                    public void edit(MethodCall m)
                                  throws CannotCompileException
                    {
                        //logger.log(Level.INFO, String.format("MethodCall: %s %s", m.getClassName(), m.getMethodName()));
                        if ( ! m.getClassName().equals("java.util.Random") ) {
                            return;
                        }
                        if ( ! m.getMethodName().equals("nextInt") ) {
                            return;
                        }
                        // A bit ghetto but...  right now, the nextInt() call we want
                        // is the only one in the method that calls with nextInt(5)...
                        m.replace("{ if ( $1 == 5 ) { $_ = 0; } else { $_ = $proceed($$); } }");
                    }
                });

        } catch (Throwable e) {
            logger.log(Level.INFO, "hookSurfaceMine()", e);
        }

    }

    @Override 
    public void configure(Properties props){
        try {

            CorpseBounty.cashPerCorpse = Long.valueOf( props.getProperty("cashPerCorpse","0") );

            addGmProtect = Boolean.valueOf( props.getProperty("addGmProtect","true") );

            gmFullFavor = Boolean.valueOf( props.getProperty("gmFullFavor","true") );
            gmFullStamina = Boolean.valueOf( props.getProperty("gmFullStamina","true") );

            itemHolyBook = Boolean.valueOf( props.getProperty("itemHolyBook", "true") );
            itemNymphPortal = Boolean.valueOf( props.getProperty("itemNymphPortal", "true") );
            itemDemonPortal = Boolean.valueOf( props.getProperty("itemDemonPortal", "true") );

            setUnicornIsHorse = Boolean.valueOf( props.getProperty("setUnicornIsHorse","true") );
            makeNonAgro = props.getProperty("makeNonAgro", "");
            noCooldownSpells = props.getProperty("noCooldownSpells", "");

            stfuNpcs = Boolean.valueOf( props.getProperty("stfuNpcs","true") );
            loadFullContainers = Boolean.valueOf( props.getProperty("loadFullContainers","true") );
            noMineDrift = Boolean.valueOf( props.getProperty("noMineDrift","true") );
            lampsAutoLight = Boolean.valueOf( props.getProperty("lampsAutoLight", "true"));
            allowTentsOnDeed = Boolean.valueOf( props.getProperty("allowTentsOnDeed","true") );
            allSurfaceMine = Boolean.valueOf( props.getProperty("allSurfaceMine","true") );
            hidePlayerGodInscriptions = Boolean.valueOf( props.getProperty("hidePlayerGodInscriptions","true") );

        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Error in configure()", e);
        }
    }

    @Override
    public void onItemTemplatesCreated() {

        if (itemHolyBook) DracoItems.addHolyBook();
        if (itemNymphPortal) DracoItems.addNymphPortal();
        if (itemDemonPortal) DracoItems.addDemonPortal();

        /* allow gifting coins as mission rewards */
        ItemHelper.makeMissionItem( ItemList.coinIron );
        ItemHelper.makeMissionItem( ItemList.coinSilver );
        ItemHelper.makeMissionItem( ItemList.coinGold );
        ItemHelper.makeMissionItem( ItemList.coinCopperFive );
        ItemHelper.makeMissionItem( ItemList.coinIronFive );
        ItemHelper.makeMissionItem( ItemList.coinSilverFive );
        ItemHelper.makeMissionItem( ItemList.coinGoldFive );
        ItemHelper.makeMissionItem( ItemList.coinCopperTwenty );
        ItemHelper.makeMissionItem( ItemList.coinIronTwenty );
        ItemHelper.makeMissionItem( ItemList.coinSilverTwenty );
        ItemHelper.makeMissionItem( ItemList.coinGoldTwenty );

        ItemHelper.makeMissionItem( ItemList.eggSmall );

    }
    
    @Override
    public void onServerStarted() {
        try {

            if (addGmProtect) ModActions.registerAction(new GmProtect());
            if (CorpseBounty.cashPerCorpse > 0) ModActions.registerAction(new CorpseBounty());

            /* Make unicorns "horse like" */

            // #fillup
            // #sendhome <player>
            // #sendto <player> <player|deed>

/*
            CmdTool cmdtool = new CmdTool();
            cmdtool.addCmdHook();
            cmdtool.addWurmCmd( new CmdWoot() );
*/
            cmdtool = new CmdTool();
            //cmdtool.addCmdHook();
            cmdtool.addWurmCmd( new CmdCull() );
            cmdtool.addWurmCmd( new CmdGoTo() );
            cmdtool.addWurmCmd( new CmdWoot() );
            cmdtool.addWurmCmd( new CmdAddAff() );
            cmdtool.addWurmCmd( new CmdCoffers() );

            CreatureTool.makeLikeHorse("Unicorn");

            for ( String name : makeNonAgro.split(",") ) {
                CreatureTool.makeAlignZero(name);
                CreatureTool.makeNoAggHuman(name);
            }

            for ( String name : noCooldownSpells.split(",") ) {
                SpellTool.noSpellCooldown(name);
            }


        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Error in onServerStarted()", e);
        }
    }

}


