package com.interrupt.dungeoneer.entities.triggers;

import com.badlogic.gdx.math.Vector3;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.managers.ItemManager;

public class TriggeredItem extends Trigger {

    @EditorProperty(group = "Accepted Item")
    public String itemName;

    public TriggeredItem() {
        this.triggerType = TriggerType.PLAYER_TOUCHED;
        this.itemName = null;
    }

    @Override
    public void doTriggerEvent(String value) {
        if (this.itemName != null) {
            ItemManager manager = Game.instance.itemManager;
            String holdingItem = Game.instance.player.GetHeldItem().GetName();
            if (holdingItem != null && itemName.equals(holdingItem)) {
                Audio.playPositionedSound(this.triggerSound, new Vector3(this.x, this.y, this.z), 0.8F, 11.0F);
                Game.instance.level.trigger(this, this.triggersId, this.triggerValue);
            }

        }
        super.doTriggerEvent(value);
    }
}
