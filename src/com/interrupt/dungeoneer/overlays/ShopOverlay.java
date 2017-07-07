//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.interrupt.dungeoneer.overlays;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.annotations.EditorProperty;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.gfx.TextureAtlas;
import com.interrupt.dungeoneer.rpg.Stats;
import com.interrupt.dungeoneer.ui.UiSkin;
import com.interrupt.helpers.ShopItem;
import com.interrupt.managers.ItemManager;
import com.interrupt.managers.StringManager;
import java.util.Iterator;

public class ShopOverlay extends WindowOverlay {
    private final Player player;
    private String titleText = StringManager.get("overlays.ShopPause.titleText");
    private String descriptionText = StringManager.get("overlays.ShopPause.descriptionText");
    private String itemPrefix = "";
    private Array<ShopItem> items;
    private Array<ShopItem> selected = new Array();
    private Label lblTotalAmount;
    private Label lblGoldAmount;
    float rotOffset = 0.0F;

    public boolean platinumAllowed;
    public boolean goldAllowed;
    public boolean silverAllowed;
    public boolean copperAllowed;

    public ShopOverlay(Player player) {
        this.player = player;
    }

    public ShopOverlay(Player player, String prefix, String title, String description, Array<ShopItem> items, Boolean platinumAllowed, Boolean goldAllowed, Boolean silverAllowed, Boolean copperAllowed) {
        this.player = player;
        this.titleText = title;
        this.itemPrefix = prefix;
        this.descriptionText = description;
        this.items = items;

        this.platinumAllowed = false;
        this.goldAllowed = false;
        this.silverAllowed = false;
        this.copperAllowed = true;

    }

    public ShopOverlay(Player player, String title, String description, Array<ShopItem> items, Boolean platinumAllowed, Boolean goldAllowed, Boolean silverAllowed, Boolean copperAllowed) {
        this.player = player;
        this.titleText = title;
        this.descriptionText = description;
        this.items = items;

        this.platinumAllowed = false;
        this.goldAllowed = false;
        this.silverAllowed = false;
        this.copperAllowed = true;
    }

    public void onShow() {
        super.onShow();
        InputListener listener = new InputListener() {
            public boolean keyDown(InputEvent event, int keycode) {
                if(keycode == 131 || keycode == 4) {
                    OverlayManager.instance.remove(ShopOverlay.this);
                }

                return false;
            }
        };
        this.ui.addListener(listener);
        Audio.playSound("/ui/ui_dialogue_open.mp3", 0.35F);
    }

    public void onHide() {
        Audio.playSound("/ui/ui_dialogue_close.mp3", 0.35F);
    }

    protected Table makeContent() {
        int windowWidth = 200;
        this.buttonOrder.clear();
        Table contentTable = new Table();
        contentTable.columnDefaults(1).align(16);
        Label lblTotalLabel;
        if(this.titleText != null) {
            lblTotalLabel = new Label(this.titleText, (LabelStyle)this.skin.get(LabelStyle.class));
            lblTotalLabel.setColor(0.5F, 0.75F, 1.0F, 1.0F);
            lblTotalLabel.setWrap(true);
            lblTotalLabel.setAlignment(1);
            lblTotalLabel.setFontScale(0.75F);
            contentTable.add(lblTotalLabel).minWidth((float)windowWidth).colspan(2).padBottom(5.0F).center();
            contentTable.row();
        }

        if(this.descriptionText != null) {
            lblTotalLabel = new Label(this.descriptionText, (LabelStyle)this.skin.get(LabelStyle.class));
            lblTotalLabel.setFontScale(0.75F);
            lblTotalLabel.setWrap(true);
            lblTotalLabel.setAlignment(1);
            contentTable.add(lblTotalLabel).minWidth((float)windowWidth).colspan(2).padBottom(5.0F).center();
            contentTable.row();
        }

        Iterator var9 = this.items.iterator();

        while(var9.hasNext()) {
            ShopItem item = (ShopItem)var9.next();
            this.addShopItem(contentTable, this.itemPrefix, item);
        }

        lblTotalLabel = new Label(StringManager.get("overlays.ShopPause.totalText"), (LabelStyle)this.skin.get(LabelStyle.class));
        lblTotalLabel.setAlignment(12);
        lblTotalLabel.setFontScale(0.75F);
        this.lblTotalAmount = new Label(String.format("%d", new Object[]{Integer.valueOf(this.getPurchaseCost())}), (LabelStyle)this.skin.get(LabelStyle.class));
        this.lblTotalAmount.setAlignment(16);
        this.lblTotalAmount.setFontScale(0.75F);
        contentTable.add(lblTotalLabel).align(8).padBottom(5.0F).padTop(3.0F);
        contentTable.add(this.lblTotalAmount).align(16).padBottom(5.0F).padTop(3.0F).padLeft(10.0F);
        contentTable.row();
        Label lblGoldLabel = new Label(StringManager.get("overlays.ShopPause.yourGoldText"), (LabelStyle)this.skin.get(LabelStyle.class));
        lblGoldLabel.setAlignment(12);
        lblGoldLabel.setFontScale(0.75F);
        this.lblGoldAmount = new Label(String.format("%d", new Object[]{Integer.valueOf(this.player.gold - this.getPurchaseCost())}), (LabelStyle)this.skin.get(LabelStyle.class));
        this.lblGoldAmount.setAlignment(16);
        this.lblGoldAmount.setFontScale(0.75F);
        Image goldIcon = new Image(new TextureRegionDrawable(((TextureAtlas)TextureAtlas.cachedAtlases.get("item")).sprite_regions[89]));
        goldIcon.setWidth(3.0F);
        goldIcon.setHeight(3.0F);
        goldIcon.setAlign(8);
        Table goldTable = new Table();
        goldTable.add(goldIcon).width(20.0F).height(20.0F);
        goldTable.add(this.lblGoldAmount);
        goldTable.pack();
        TextButton doneBtn = new TextButton(StringManager.get("overlays.ShopPause.doneButton"), (TextButtonStyle)this.skin.get(TextButtonStyle.class));
        contentTable.add(doneBtn).align(8);
        contentTable.add(goldTable).align(16);
        doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Iterator var4 = ShopOverlay.this.selected.iterator();

                while(var4.hasNext()) {
                    ShopItem item = (ShopItem)var4.next();
                    Player var10000;
                    if(item.item != null) {
                        Item copy = ItemManager.Copy(item.item.getClass(), item.item);
                        boolean hadSpace = ShopOverlay.this.player.addToInventory(copy);
                        if(!hadSpace) {
                            ShopOverlay.this.dropItem(copy);
                        }

                        var10000 = ShopOverlay.this.player;
                        var10000.gold -= item.cost.intValue();
                    } else if(item.upgrade != null) {
                        Stats var8 = ShopOverlay.this.player.stats;
                        var8.END += item.upgrade.stats.END;
                        var8 = ShopOverlay.this.player.stats;
                        var8.ATK += item.upgrade.stats.ATK;
                        var8 = ShopOverlay.this.player.stats;
                        var8.DEF += item.upgrade.stats.DEF;
                        var8 = ShopOverlay.this.player.stats;
                        var8.DEX += item.upgrade.stats.DEX;
                        var8 = ShopOverlay.this.player.stats;
                        var8.MAG += item.upgrade.stats.MAG;
                        var8 = ShopOverlay.this.player.stats;
                        var8.SPD += item.upgrade.stats.SPD;
                        var10000 = ShopOverlay.this.player;
                        var10000.gold -= item.cost.intValue();
                    }
                }

                if(ShopOverlay.this.selected.size > 0) {
                    Audio.playSound("ui/ui_buy.mp3", 0.6F);
                }

                OverlayManager.instance.remove(ShopOverlay.this);
            }
        });
        this.buttonOrder.add(doneBtn);
        contentTable.pack();
        return contentTable;
    }

    protected void dropItem(Item itm) {
        float rot = this.player.rot + this.rotOffset;
        float throwPower = 0.01F;
        ++this.rotOffset;
        float projx = (0.0F * (float)Math.cos((double)rot) + 1.0F * (float)Math.sin((double)rot)) * 1.0F;
        float projy = (1.0F * (float)Math.cos((double)rot) - 0.0F * (float)Math.sin((double)rot)) * 1.0F;
        itm.isActive = true;
        itm.isDynamic = true;
        itm.x = this.player.x + 0.5F + projx * 0.2F;
        itm.y = this.player.y + 0.5F + projy * 0.2F;
        itm.z = this.player.z + 0.4F;
        itm.xa = projx * throwPower * 0.3F;
        itm.ya = projy * throwPower * 0.3F;
        itm.za = throwPower * 0.05F;
        itm.ignorePlayerCollision = true;
        Game.instance.level.SpawnEntity(itm);
        itm.x = this.player.x + 0.5F + projx * 0.5F;
        itm.y = this.player.y + 0.5F + projy * 0.5F;
    }

    protected void addShopItem(Table table, String prefix, final ShopItem item) {
        final Label itemName = new Label(item.getName().replace(prefix, ""), (LabelStyle)this.skin.get(LabelStyle.class));
        Integer cost = item.cost;
        String costText = this.selected.contains(item, true)?StringManager.get("overlays.ShopPause.buyText"):cost + "";
        String iconAtlas = item.item.spriteAtlas != null?item.item.spriteAtlas:"item";
        Image itemIcon = new Image(new TextureRegionDrawable(((TextureAtlas)TextureAtlas.cachedAtlases.get(iconAtlas)).sprite_regions[item.item.tex]));
        itemIcon.setWidth(6.0F);
        itemIcon.setHeight(6.0F);
        itemName.setWrap(true);
        itemName.setAlignment(12);
        itemName.setFontScale(0.75F);
        itemName.setColor(1.0F, 1.0F, 1.0F, 0.6F);
        final Label value = new Label(costText, (LabelStyle)this.skin.get(LabelStyle.class));
        value.setAlignment(16);
        value.setFontScale(0.75F);
        if(this.selected.contains(item, true)) {
            value.setColor(1.0F, 1.0F, 1.0F, 0.9F);
        } else if(this.player.gold - this.getPurchaseCost() >= item.cost.intValue()) {
            value.setColor(0.6F, 1.0F, 0.6F, 0.6F);
        } else {
            value.setColor(1.0F, 0.6F, 0.6F, 0.6F);
        }

        itemName.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if(ShopOverlay.this.selected.contains(item, true)) {
                    ShopOverlay.this.selected.removeValue(item, true);
                } else if(item.cost.intValue() <= ShopOverlay.this.player.gold - ShopOverlay.this.getPurchaseCost()) {
                    ShopOverlay.this.selected.add(item);
                }

                ShopOverlay.this.makeLayout();
                Audio.playSound("/ui/ui_button_click.mp3", 0.35F);
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                itemName.setColor(((LabelStyle)UiSkin.getSkin().get("inputover", LabelStyle.class)).fontColor);
                value.setColor(value.getColor().r, value.getColor().g, value.getColor().b, 1.0F);
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                if(ShopOverlay.this.selected.contains(item, true)) {
                    itemName.setColor(((LabelStyle)UiSkin.getSkin().get("input", LabelStyle.class)).fontColor);
                    value.setColor(value.getColor().r, value.getColor().g, value.getColor().b, 0.9F);
                } else {
                    itemName.setColor(((LabelStyle)UiSkin.getSkin().get("input", LabelStyle.class)).fontColor);
                    value.setColor(value.getColor().r, value.getColor().g, value.getColor().b, 0.6F);
                }

            }
        });
        Table itemTable = new Table();
        itemTable.add(itemIcon).width(12.0F).height(12.0F).padRight(4.0F).align(1);
        itemTable.add(itemName).width(180.0F).align(8);
        itemTable.pack();
        table.add(itemTable).align(8);
        table.add(value).align(16).padLeft(1.0F);
        table.row();
        this.buttonOrder.add(itemName);
    }

    public int getPurchaseCost() {
        int cost = 0;

        ShopItem item;
        for(Iterator var2 = this.selected.iterator(); var2.hasNext(); cost += item.cost.intValue()) {
            item = (ShopItem)var2.next();
        }

        return cost;
    }
}
