package com.draconusarcanum.wurm.mods.utils;

import java.lang.String;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;

import com.draconusarcanum.wurm.contrib.ArgumentTokenizer;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import com.draconusarcanum.wurm.mods.utils.WurmCmd;

public class CmdTool {

    public static final Logger logger = Logger.getLogger("CmdTool");

    public HashMap<String,WurmCmd> cmdmap = null;

    public CmdTool() {
        cmdmap = new HashMap<String,WurmCmd>();
    }

    public void addWurmCmd( WurmCmd cmd ) {
        logger.log(Level.INFO,String.format("addWurmCmd: ->%s<-", cmd.cmdName));
        cmdmap.put( cmd.cmdName, cmd );
    }

    public boolean runWurmCmd( Player player, String[] argv) {
        return runWurmCmd( (Creature)player, argv );
    }

    public boolean runWurmCmd( Creature player, String[] argv) {

        if (argv.length == 0) {
            return false;
        }

        String cmdName = argv[0];

        WurmCmd cmd = cmdmap.get( cmdName );
        if ( cmd == null ) {
            logger.log(Level.INFO, String.format("cmdName not found: ->%s<-", cmdName));
            return false;
        }

        Communicator comm = player.getCommunicator();

        if ( player.getPower() < cmd.minPower ) {
            comm.sendNormalServerMessage("Newp No Perms: " + cmdName);
            return true;
        }

        try {

            cmd.runWurmCmd(player, argv);
            return true;

        } catch (Throwable e) {

            String mesg = String.format("WurmCmd Error: %s %s", cmdName, e.toString());
            comm.sendNormalServerMessage(mesg);
            return true;

        }
    }

    public void addCmdHook() {

        HookManager hooks = HookManager.getInstance();

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

                if ( runWurmCmd( (Creature)player, argv ) ) {
                    return null;
                }


            } catch (Throwable e) {
                comm.sendNormalServerMessage( String.format("Cmd Err (%s) %s", message, e.toString()) );
                return null;
            }

            return method.invoke(proxy,args);

        });
    }

}
