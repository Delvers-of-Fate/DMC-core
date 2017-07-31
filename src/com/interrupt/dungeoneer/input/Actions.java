package com.interrupt.dungeoneer.input;

import com.badlogic.gdx.utils.Array;
import java.util.Hashtable;

public class Actions {
    public static final Hashtable<Actions.Action, Integer> keyBindings = new Hashtable();
    public static final Hashtable<Actions.Action, Integer> gamepadBindings = new Hashtable();
    public static final Array<Actions.Action> keyOrder = new Array();

    public Actions() {
    }

    static {
        keyBindings.put(Actions.Action.USE, 33);
        keyBindings.put(Actions.Action.DROP, 45);
        keyBindings.put(Actions.Action.INVENTORY, 37);
        keyBindings.put(Actions.Action.ITEM_NEXT, 72);
        keyBindings.put(Actions.Action.ITEM_PREVIOUS, 71);
        keyBindings.put(Actions.Action.MAP, 41);
        keyBindings.put(Actions.Action.FORWARD, 51);
        keyBindings.put(Actions.Action.BACKWARD, 47);
        keyBindings.put(Actions.Action.STRAFE_LEFT, 29);
        keyBindings.put(Actions.Action.STRAFE_RIGHT, 32);
        keyBindings.put(Actions.Action.TURN_LEFT, 21);
        keyBindings.put(Actions.Action.TURN_RIGHT, 22);
        keyBindings.put(Actions.Action.LOOK_UP, 19);
        keyBindings.put(Actions.Action.LOOK_DOWN, 20);
        keyOrder.add(Actions.Action.USE);
        keyOrder.add(Actions.Action.DROP);
        keyOrder.add(Actions.Action.INVENTORY);
        keyOrder.add(Actions.Action.ITEM_NEXT);
        keyOrder.add(Actions.Action.ITEM_PREVIOUS);
        keyOrder.add(Actions.Action.MAP);
        keyOrder.add(Actions.Action.FORWARD);
        keyOrder.add(Actions.Action.BACKWARD);
        keyOrder.add(Actions.Action.STRAFE_LEFT);
        keyOrder.add(Actions.Action.STRAFE_RIGHT);
        keyOrder.add(Actions.Action.TURN_LEFT);
        keyOrder.add(Actions.Action.TURN_RIGHT);
        keyOrder.add(Actions.Action.LOOK_UP);
        keyOrder.add(Actions.Action.LOOK_DOWN);
    }

    public static enum Action {
        USE,
        DROP,
        ATTACK,
        INVENTORY,
        ITEM_NEXT,
        ITEM_PREVIOUS,
        MAP,
        FORWARD,
        BACKWARD,
        STRAFE_LEFT,
        STRAFE_RIGHT,
        TURN_LEFT,
        TURN_RIGHT,
        LOOK_UP,
        LOOK_DOWN;

        private Action() {
        }
    }
}
