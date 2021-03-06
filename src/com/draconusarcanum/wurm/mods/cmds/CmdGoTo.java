
package com.draconusarcanum.wurm.mods.cmds;

import java.lang.String;
import java.lang.Integer;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;

import com.draconusarcanum.wurm.mods.utils.GoTo;
import com.draconusarcanum.wurm.mods.utils.WurmCmd;

public class CmdGoTo extends WurmCmd {

    public CmdGoTo() {
        super("#goto",5);
    }

    @Override
    public boolean runWurmCmd(Creature actor, String[] argv) {
        Communicator comm = actor.getCommunicator();

        if ( argv.length != 2 ) {
            comm.sendNormalServerMessage("usage: #goto <deed|player>");
            return true;
        }

        if ( argv[1].matches("^[0-9]+,[0-9]+$") ) {
            String[] coords = argv[1].split(",");
            GoTo.sendToXy(actor, Integer.valueOf(coords[0]), Integer.valueOf(coords[1]), 0, 0);
            return true;
        }

        if ( GoTo.sendToPlayer(actor, argv[1]) ) {
            return true;
        }

        if ( GoTo.sendToVillage(actor, argv[1]) ) {
            return true;
        }

        return true;
    }

}
