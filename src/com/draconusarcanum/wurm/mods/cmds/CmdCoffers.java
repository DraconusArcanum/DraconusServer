package com.draconusarcanum.wurm.mods.cmds;

import java.lang.Long;
import java.lang.String;

import com.wurmonline.server.economy.Shop;
import com.wurmonline.server.economy.Economy;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;

import com.draconusarcanum.wurm.mods.utils.GoTo;
import com.draconusarcanum.wurm.mods.utils.WurmCmd;

public class CmdCoffers extends WurmCmd {

    public CmdCoffers() {
        super("#coffers",5);
    }

    @Override
    public boolean runWurmCmd(Creature actor, String[] argv) {
        Communicator comm = actor.getCommunicator();

        Shop kingsShop = Economy.getEconomy().getKingsShop();

        if ( argv.length == 2 ) {
            Long cash = Long.valueOf( argv[1] );
            kingsShop.setMoney( cash );
        }

        Long coff = kingsShop.getMoney();

        comm.sendNormalServerMessage( String.format("kings coffers: %d", coff) );

        return true;
    }

}
