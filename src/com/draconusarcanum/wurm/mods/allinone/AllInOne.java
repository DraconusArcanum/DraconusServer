package com.draconusarcanum.wurm.mods.allinone;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.String;
import java.lang.reflect.Method;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import javassist.ClassPool;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.creatures.CreatureStatus;

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
    public static boolean allowTentsOnDeed = true;
    public static boolean loadFullContainers = true;
    public static boolean hidePlayerGodInscriptions = true;

    public static String noCooldownSpells = "";

    public CmdTool cmdtool = null;

    public static final Logger logger = Logger.getLogger("DraconusArcanum");

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
            noCooldownSpells = props.getProperty("noCooldownSpells", "");

            stfuNpcs = Boolean.valueOf( props.getProperty("stfuNpcs","true") );
            loadFullContainers = Boolean.valueOf( props.getProperty("loadFullContainers","true") );
            noMineDrift = Boolean.valueOf( props.getProperty("noMineDrift","true") );
            allowTentsOnDeed = Boolean.valueOf( props.getProperty("allowTentsOnDeed","true") );
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

            for ( String name : noCooldownSpells.split(",") ) {
                SpellTool.noSpellCooldown(name);
            }


        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Error in onServerStarted()", e);
        }
    }

}


