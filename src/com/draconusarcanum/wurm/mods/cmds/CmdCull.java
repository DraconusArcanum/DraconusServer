package com.draconusarcanum.wurm.mods.cmds;

import java.lang.Long;
import java.lang.String;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.SortedSet;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;

import com.draconusarcanum.wurm.mods.utils.WurmCmd;
import com.draconusarcanum.wurm.mods.utils.CreatureTool;

public class CmdCull extends WurmCmd {

    public CmdCull() {
        super("#cull",5);
    }

    @Override
    public boolean runWurmCmd(Creature actor, String[] argv) {
        Communicator comm = actor.getCommunicator();

        HashMap<String,Integer> histo = CreatureTool.getCreatureHisto(true);
        SortedSet<String> names = new TreeSet<String>(histo.keySet());

        if ( argv.length == 3 ) {
            Integer count = Integer.valueOf( argv[1] );
            String name = argv[2];
            comm.sendNormalServerMessage( String.format("Culling %d of %ss", count, name) );
            CreatureTool.cullByName(count,name);
        }

        for ( String name : names ) {
            String line = String.format("%s: %d", name, histo.get(name) );
            comm.sendNormalServerMessage(line);
        }

        comm.sendNormalServerMessage("usage: cull [<count> <name>]");

        return true;
    }

}
