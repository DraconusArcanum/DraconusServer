package com.draconusarcanum.wurm.mods.actions;

import java.util.List;
import java.util.Collections;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.MethodsReligion;

import com.wurmonline.server.players.Player;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;

import com.wurmonline.server.items.Item;

import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;

import com.draconusarcanum.wurm.mods.allinone.WurmConst;
import com.draconusarcanum.wurm.mods.allinone.DracoItems;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.draconusarcanum.wurm.mods.utils.GoTo;
import com.draconusarcanum.wurm.mods.allinone.DracoItems;

public class PortalTeleport implements ModAction, BehaviourProvider, ActionPerformer {

    public static final Logger logger = Logger.getLogger("PortalTeleport");

    public final short actionId;
    public final ActionEntry actionEntry;

    public PortalTeleport() {

        actionId = (short) ModActions.getNextActionId();
        int[] types = {
            WurmConst.ACTION_TYPE_FATIGUE,
            WurmConst.ACTION_TYPE_MISSION,
            WurmConst.ACTION_TYPE_SHOW_ON_SELECT_BAR,
        };

        actionEntry = ActionEntry.createEntry( actionId, "Use Portal", "Teleporting", types);
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

        if ( ! DracoItems.isPortalItem(target) ) {
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

    @Override
    public boolean action(Action action, Creature actor, Item source, Item target, short num, float counter) {

        Communicator comm = actor.getCommunicator();

        if ( ! ( actor instanceof Player ) ) {
            return true;
        }

        Player player = (Player) actor;

        if ( DracoItems.isHomePortalItem(target) ) {

            String name = player.getVillageName();
            if ( name.length() == 0 ) {
                comm.sendNormalServerMessage("You are not a member of a deed.  You have no home to go to...");
                return true;
            }

            GoTo.sendToVillage(actor,name);
            return true;
        }

        int x = target.getData1();
        int y = target.getData2();

        int floor = 0;
        int layer = target.getAuxData();
        //int floor = target.getTemperature();

        if ( layer > 0 ) {
            floor = layer;
            layer = 1;
        }

        if ( ! DracoItems.isPortalItem(target) ) {
            return true;
        }

        GoTo.sendToXy(actor,x,y,layer,floor);
        return true;
    }

}
