package com.interrupt.dungeoneer.entities.triggers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.game.Game;

public class TriggeredItem extends Trigger {

    @EditorProperty(group = "Accepted Item")
    public String itemName;

    public TriggeredItem() {
        this.triggerType = TriggerType.PLAYER_TOUCHED;
        this.itemName = null;
    }

    @Override
    public void doTriggerEvent(String value) {
        String holdingItem;

        try {
            holdingItem = Game.instance.player.GetHeldItem().GetName();
        } catch (Exception ex) {
            holdingItem = null;
            Gdx.app.log("Delver", "Player is holding null?");
        }

        if (holdingItem != null && holdingItem.equals(itemName)) {
            Audio.playPositionedSound(this.triggerSound, new Vector3(this.x, this.y, this.z), 0.8F, 11.0F);
            Game.instance.level.trigger(this, this.triggersId, this.triggerValue);
        }
    }
}
