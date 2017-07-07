package com.interrupt.dungeoneer.entities.items;

import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Entity;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.managers.StringManager;
import java.text.MessageFormat;

public class Copper extends Item
{
    public Copper()
    {
        this.tex = 92;
        this.artType = Entity.ArtType.item;
        this.name = StringManager.get("items.Copper.defaultNameText");
        this.collidesWith = Entity.CollidesWith.staticOnly;
        this.dropSound = "drops/drop_gold.mp3";
        this.collision.x = 0.1F;
        this.collision.y = 0.1F;
    }

    @EditorProperty
    public int copperAmount = 1;
    public boolean autoPickup = false;
    public boolean playedDropSound = false;

    public Copper(float x, float y)
    {
        super(x, y, 0, Item.ItemType.gold, StringManager.get("items.Copper.defaultNameText"));
    }

    public Copper(int amount)
    {
        this();

        this.copperAmount = amount;
        this.name = StringManager.get("items.Copper.defaultNameText") + " x" + Integer.toString(amount);
        if (this.copperAmount <= 0) {
            this.copperAmount = 1;
        }
        if (this.copperAmount > 5) {
            this.tex = 93;
        }
        this.pickupSound = "pu_gold.mp3";
    }

    public String GetItemText()
    {
        return MessageFormat.format(StringManager.get("items.Copper.copperItemText"), this.copperAmount);
    }

    public void tick(Level level, float delta)
    {
        super.tick(level, delta);
        if ((this.isActive) && (this.autoPickup))
        {
            Player p = Game.instance.player;
            if ((Math.abs(p.x + 0.5F - this.x) < 0.3F) && (Math.abs(p.y + 0.5F - this.y) < 0.3F))
            {
                p.copper += 1;
                this.isActive = false;
            }
        }
    }

    protected void pickup(Player player)
    {
        if (this.isActive)
        {
            player.copper += this.copperAmount;
            this.isActive = false;
            Audio.playSound(this.pickupSound, 0.3F, 1.0F);
        }
    }
}
