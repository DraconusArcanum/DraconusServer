package com.draconusarcanum.wurm.mods.cmds;

import java.lang.String;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;

import com.draconusarcanum.wurm.mods.utils.WurmCmd;

public class CmdWoot extends WurmCmd {

    public CmdWoot () {
        super("#woot",5);
    }

    @Override
    public boolean runWurmCmd(Creature actor, String[] argv) {
        Communicator comm = actor.getCommunicator();
        comm.sendNormalServerMessage("woot to you: " + actor.getName());
        return true;
    }

}
