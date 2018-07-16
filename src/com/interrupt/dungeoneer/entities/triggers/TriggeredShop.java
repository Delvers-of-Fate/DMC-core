package com.interrupt.dungeoneer.entities.triggers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Item.ItemCondition;
import com.interrupt.dungeoneer.entities.items.Elixer;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.overlays.MessageOverlay;
import com.interrupt.dungeoneer.overlays.Overlay;
import com.interrupt.dungeoneer.overlays.OverlayManager;
import com.interrupt.dungeoneer.overlays.ShopOverlay;
import com.interrupt.helpers.ShopItem;
import com.interrupt.managers.StringManager;
import java.util.Iterator;

public class TriggeredShop extends Trigger {
    @EditorProperty
    public String messageFile = null;
    @EditorProperty
    public TriggeredShop.ShopType shopType;
    @EditorProperty
    public String title;
    @EditorProperty
    public String description;
    @EditorProperty
    public boolean pausesGame;
    public Array<ShopItem> items;

    @EditorProperty(group = "Currency")
    public TriggeredShop.AcceptedCurrency acceptedCurrency;

    public TriggeredShop() {
        this.shopType = TriggeredShop.ShopType.upgrades;
        this.title = StringManager.get("triggers.TriggeredShop.titleText");
        this.description = StringManager.get("triggers.TriggeredShop.descriptionText");
        this.pausesGame = false;
        this.items = null;

        this.acceptedCurrency = AcceptedCurrency.GOLD;

        this.hidden = true;
        this.spriteAtlas = "editor";
        this.tex = 16;
        this.isSolid = true;
    }

    public void doTriggerEvent(String value) {
        if(this.messageFile != null && !this.messageFile.equals("")) {
            final MessageOverlay message = new MessageOverlay(this.messageFile, Game.instance.player, (NinePatchDrawable)null, (Color)null);
            message.pausesGame = this.pausesGame;
            message.afterAction = new Action() {
                public boolean act(float delta) {
                    TriggeredShop.this.showShopOverlay(message);
                    return true;
                }
            };
            OverlayManager.instance.push(message);
        } else {
            this.showShopOverlay((Overlay)null);
        }

        super.doTriggerEvent(value);
    }

    public void showShopOverlay(Overlay previousOverlay) {
        if(this.items == null) {
            this.items = new Array();
            if(this.shopType == TriggeredShop.ShopType.upgrades) {
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomRangedWeapon(Integer.valueOf(7), ItemCondition.normal)));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomRangedWeapon(Integer.valueOf(7), ItemCondition.normal)));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomWeapon(Integer.valueOf(7), ItemCondition.normal)));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomArmor(Integer.valueOf(7), ItemCondition.normal)));
                this.items.add(new ShopItem(new Elixer()));
            } else if(this.shopType == TriggeredShop.ShopType.scrolls) {
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomScroll()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomScroll()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomScroll()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomScroll()));
            } else if(this.shopType == TriggeredShop.ShopType.potions) {
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomPotion()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomPotion()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomPotion()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomPotion()));
            } else if(this.shopType == TriggeredShop.ShopType.wands) {
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomWand()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomWand()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomWand()));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomWand()));
            } else if(this.shopType == TriggeredShop.ShopType.weapons) {
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomWeapon(Integer.valueOf(7))));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomWeapon(Integer.valueOf(7))));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomRangedWeapon(Integer.valueOf(7))));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomRangedWeapon(Integer.valueOf(7))));
            } else if(this.shopType == TriggeredShop.ShopType.armor) {
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomArmor(Integer.valueOf(7))));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomArmor(Integer.valueOf(7))));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomArmor(Integer.valueOf(7))));
                this.items.add(new ShopItem(Game.instance.itemManager.GetRandomArmor(Integer.valueOf(7))));
            }
        }

        Array<ShopItem> toRemove = new Array();
        Iterator var3 = this.items.iterator();

        while(var3.hasNext()) {
            ShopItem item = (ShopItem)var3.next();
            if(item.item == null && item.upgrade == null) {
                toRemove.add(item);
            }
        }

        this.items.removeAll(toRemove, true);
        ShopOverlay shopOverlay;
        if(previousOverlay != null) {
            shopOverlay = new ShopOverlay(Game.instance.player, (String)null, (String)null, this.items, this.acceptedCurrency);
            shopOverlay.pausesGame = this.pausesGame;
            shopOverlay.timer = 1000.0F;
            OverlayManager.instance.replace(previousOverlay, shopOverlay);
        } else {
            shopOverlay = new ShopOverlay(Game.instance.player, this.title, this.description, this.items, this.acceptedCurrency);
            shopOverlay.pausesGame = this.pausesGame;
            OverlayManager.instance.push(shopOverlay);
        }

    }

    public static enum ShopType {
        upgrades,
        scrolls,
        potions,
        weapons,
        wands,
        armor;

        private ShopType() {
        }
    }

    public static enum AcceptedCurrency {
        EMBER,
        GOLD,

        private AcceptedCurrency() {
        }
    }
}
