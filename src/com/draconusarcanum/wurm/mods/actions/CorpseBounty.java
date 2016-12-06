package com.draconusarcanum.wurm.mods.actions;

import java.lang.Long;
import java.util.List;
import java.util.Collections;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.MethodsItems;
import com.wurmonline.server.behaviours.MethodsReligion;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.Items;

import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;

//import com.draconusarcanum.wurm.mods.allinone.WurmConst;
//import com.draconusarcanum.wurm.mods.allinone.DracoItems;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

//import com.draconusarcanum.wurm.mods.utils.GoTo;
//import com.draconusarcanum.wurm.mods.allinone.DracoItems;

public class CorpseBounty implements ModAction, BehaviourProvider, ActionPerformer {

    public static final Logger logger = Logger.getLogger("CorpseBounty");

    public static Long cashPerCorpse = 0L;

    public final short actionId;
    public final ActionEntry actionEntry;

    public CorpseBounty() {

        actionId = (short) ModActions.getNextActionId();

        int[] types = {};
        actionEntry = ActionEntry.createEntry( actionId, "Redeem Bounty", "Redeeming", types);
        ModActions.registerAction(actionEntry);
    }

    @Override
    public short getActionId() {
        return actionId;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature actor, Item source, Item target) {

        if ( target == null ) {
            return null;
        }

        if ( cashPerCorpse == 0 ) {
            return null;
        }

        if ( ! isRedeemCorpse(actor,target) ) {
            return null;
        }

        return Collections.singletonList(actionEntry);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature actor, Item target) {
        return getBehavioursFor(actor, null, target);
    }

    @Override
    public boolean action(Action action, Creature actor, Item target, short num, float counter) {
        return action(action, actor, null, target, num, counter);
    }

    public boolean isRedeemCorpse(Creature actor, Item item) {
        if ( ! item.isCorpse() ) {
            return false;
        }
        if ( ! MethodsItems.isLootableBy(actor,item) ) {
            return false;
        }
        return true;
    }

    @Override
    public boolean action(Action action, Creature actor, Item source, Item target, short num, float counter) {

        Communicator comm = actor.getCommunicator();

        if ( ! isRedeemCorpse(actor,target) ) {
            comm.sendNormalServerMessage("Newp: not a redeemable corpse");
            return true;
        }

        Items.destroyItem( target.getWurmId() );

        try {
            actor.addMoney( cashPerCorpse );
            comm.sendNormalServerMessage( String.format("Bounty Awarded: %d iron" , cashPerCorpse ));
        } catch (IOException e) {
            comm.sendNormalServerMessage( "CorpseBounty: " + e.toString() );
        }
        return true;
    }
}
