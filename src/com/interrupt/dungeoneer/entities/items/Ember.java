//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.interrupt.dungeoneer.entities.items;

import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.entities.Entity.ArtType;
import com.interrupt.dungeoneer.entities.Entity.CollidesWith;
import com.interrupt.dungeoneer.entities.Item.ItemType;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.managers.StringManager;
import java.text.MessageFormat;

public class Ember extends Item {
    @EditorProperty
    public int emberAmount;
    public boolean autoPickup;
    public boolean playedDropSound;

    public Ember() {
        this.emberAmount = 1;
        this.autoPickup = false;
        this.playedDropSound = false;
        this.tex = 100;
        this.artType = ArtType.item;
        this.name = StringManager.get("items.Ember.defaultNameText");
        this.collidesWith = CollidesWith.staticOnly;
        this.dropSound = "drops/drop_gold.mp3";
        this.collision.x = 0.1F;
        this.collision.y = 0.1F;
    }

    public Gold(float x, float y) {
        super(x, y, 0, ItemType.ember, StringManager.get("items.Ember.defaultNameText"));
        this.emberAmount = 1;
        this.autoPickup = false;
        this.playedDropSound = false;
    }

    public Ember(int amount) {
        this();
        this.emberAmount = amount;
        this.name = StringManager.get("items.Ember.defaultNameText");
        if (this.emberAmount <= 0) {
            this.emberAmount = 1;
        }

        if (this.emberAmount > 5) {
            this.tex = 101;
        }

        this.pickupSound = "pu_gold.mp3";
    }

    public String GetItemText() {
        return MessageFormat.format(StringManager.get("items.Ember.goldItemText"), this.emberAmount);
    }

    public void tick(Level level, float delta) {
        super.tick(level, delta);
        if (this.isActive && this.autoPickup) {
            Player p = Game.instance.player;
            if (Math.abs(p.x + 0.5F - this.x) < 0.3F && Math.abs(p.y + 0.5F - this.y) < 0.3F) {
                ++p.ember;
                this.isActive = false;
            }
        }

    }

    protected void pickup(Player player) {
        if (this.isActive) {
            player.ember += this.emberAmount;
            this.isActive = false;
            Audio.playSound(this.pickupSound, 0.3F, 1.0F);
            this.makeItemPickupAnimation(player);
        }

    }
}
