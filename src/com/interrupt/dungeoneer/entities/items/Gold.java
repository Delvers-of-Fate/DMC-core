package com.interrupt.dungeoneer.entities.items;

import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.managers.StringManager;

import java.text.MessageFormat;

public class Gold extends Item {
    @EditorProperty
    public int goldAmount;
    public boolean autoPickup;
    public boolean playedDropSound;

    public Gold() {
        this.goldAmount = 1;
        this.autoPickup = false;
        this.playedDropSound = false;
        this.tex = 88;
        this.artType = ArtType.item;
        this.name = StringManager.get("items.Gold.defaultNameText");
        this.collidesWith = CollidesWith.staticOnly;
        this.dropSound = "drops/drop_gold.mp3";
        this.collision.x = 0.1F;
        this.collision.y = 0.1F;
    }

    public Gold(float x, float y) {
        super(x, y, 0, ItemType.gold, StringManager.get("items.Gold.defaultNameText"));
        this.goldAmount = 1;
        this.autoPickup = false;
        this.playedDropSound = false;
    }

    public Gold(int amount) {
        this();

        this.goldAmount = amount;
        this.name = StringManager.get("items.Gold.defaultNameText") + " x" + Integer.toString(amount);
        if(this.goldAmount <= 0) {
            this.goldAmount = 1;
        }

        if(this.goldAmount > 5) {
            this.tex = 89;
        }

        this.pickupSound = "pu_gold.mp3";
    }

    public String GetItemText() {
        return MessageFormat.format(StringManager.get("items.Gold.goldItemText"), this.goldAmount);
    }

    public void tick(Level level, float delta) {
        super.tick(level, delta);
        if(this.isActive && this.autoPickup) {
            Player p = Game.instance.player;
            if(Math.abs(p.x + 0.5F - this.x) < 0.3F && Math.abs(p.y + 0.5F - this.y) < 0.3F) {
                ++p.gold;
                this.isActive = false;
            }
        }

    }

    protected void pickup(Player player) {
        if(this.isActive) {
            player.gold += this.goldAmount;
            this.isActive = false;
            Audio.playSound(this.pickupSound, 0.3F, 1.0F);
        }

    }
}
