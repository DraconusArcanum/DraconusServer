package com.draconusarcanum.wurm.mods.actions;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.wurmonline.server.Items;
import com.wurmonline.server.Server;

import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.behaviours.MethodsReligion;
//import com.wurmonline.server.behaviours.ActionTypes;

import com.wurmonline.server.deities.Deity;
import com.wurmonline.server.players.Player;

import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.creatures.Communicator;

import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import com.wurmonline.server.items.ItemSettings;

import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.skills.NoSuchSkillException;

import org.gotti.wurmunlimited.modsupport.actions.ModAction;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;

import com.draconusarcanum.wurm.mods.allinone.WurmConst;
import com.draconusarcanum.wurm.mods.allinone.DracoItems;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.draconusarcanum.wurm.mods.utils.TweakApiPerms;

public class HolyBookPray implements ModAction, BehaviourProvider, ActionPerformer {

    public static final Logger logger = Logger.getLogger("HolyBookPray");

    public final short actionId;
    public final Method prayResult;
    public final ActionEntry actionEntry;


    public HolyBookPray() {

        actionId = (short) ModActions.getNextActionId();
        prayResult = TweakApiPerms.getClassMeth(
                        "com.wurmonline.server.behaviours.MethodsReligion",
                        "prayResult",
                        "com.wurmonline.server.creatures.Creature",
                        "float",
                        "com.wurmonline.server.deities.Deity",
                        "int");

        int[] types = {
            WurmConst.ACTION_TYPE_FATIGUE,
            WurmConst.ACTION_TYPE_MISSION,
            WurmConst.ACTION_TYPE_SHOW_ON_SELECT_BAR,
        };

        actionEntry = ActionEntry.createEntry( actionId, "Pray", "Praying", types);
        ModActions.registerAction(actionEntry);
    }

    @Override
    public short getActionId() {
        return actionId;
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {

        if ( target == null ) {
            return null;
        }

        if ( target.getTemplateId() != DracoItems.HOLY_BOOK ) {
            return null;
        }

        if ( performer.getDeity() == null ) {
            return null;
        }

        return Collections.singletonList(actionEntry);
    }

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {
        return getBehavioursFor(performer, null, target);
    }

    @Override
    public boolean action(Action action, Creature performer, Item target, short num, float counter) {
        return action(action, performer, null, target, num, counter);
    }

    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {

        Deity deity = null;
        Skill prayer = null;

        int time = action.getTimeLeft();
        float faith = performer.getFaith();

        Communicator comm = performer.getCommunicator();

        deity = performer.getDeity();
        if ( deity == null ) {
            comm.sendNormalServerMessage("You cannot pray without being a priest");
            return true;
        }

        try {
            prayer = performer.getSkills().getSkill( SkillList.PRAYER );
        } catch (NoSuchSkillException nss) {
            return true;
        }

        if ( counter == 1.0f ) {
            int remain = 300 - (int)prayer.knowledge;
            performer.sendActionControl("praying", true,remain);
            action.setTimeLeft(remain);
            comm.sendNormalServerMessage("You begin to pray to " + deity.name);
            return false;
        }

        if ( counter * 10.0f >= (float)time ) {

            float res = (float) prayer.skillCheck( prayer.getKnowledge(0.0) - (double)(30.0f + Server.rand.nextFloat() * 60.0f), faith, false, counter / 3.0f );
            int rare = action.getRarity();

            if ( res > 0.0 ) {
                try {
                    prayResult.invoke( null, performer, res, deity, rare );
                } catch ( IllegalAccessException e ) {
                    logger.log(Level.SEVERE,"IllegalAccess: prayResult");
                } catch ( InvocationTargetException e ) {
                    logger.log(Level.SEVERE,"InvokeExc: prayResult");
                } catch ( Throwable e ) {
                    logger.log(Level.SEVERE,"InvokeExc: prayResult ", e);
                }
            }

            deity.increaseFavor();
            performer.checkPrayerFaith();
            comm.sendNormalServerMessage("You finish your prayer to" + deity.name);
            return true;
        }

        return false;
    }

}
