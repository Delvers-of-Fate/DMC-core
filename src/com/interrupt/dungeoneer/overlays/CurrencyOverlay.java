package com.interrupt.dungeoneer.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.entities.Player;

public class CurrencyOverlay extends WindowOverlay {

    private final Player player;
    protected TextButton doneBtn;

    private Label platinumAmountLabel;
    private Label goldAmountLabel;
    private Label silverAmountLabel;
    private Label copperAmountLabel;

    private boolean escapePressed = false;


    public CurrencyOverlay(Player player) {
        this.player = player;
    }


    public void onShow() {
        super.onShow();

        Audio.playSound("/ui/ui_dialogue_open.mp3", 0.35F);
    }

    public void onHide() {
        Audio.playSound("/ui/ui_dialogue_close.mp3", 0.35F);
    }

    protected Table makeContent() {
        Table contentTable = new Table();

        this.doneBtn = new TextButton("DONE", (TextButton.TextButtonStyle)this.skin.get(TextButton.TextButtonStyle.class));
        this.doneBtn.setWidth(200.0F);
        this.doneBtn.setHeight(50.0F);
        this.doneBtn.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                OverlayManager.instance.remove(CurrencyOverlay.this);
            }
        });

        this.platinumAmountLabel = new Label(Integer.toString(player.platinum), this.skin);
        this.goldAmountLabel = new Label(Integer.toString(player.gold), this.skin);
        this.silverAmountLabel = new Label(Integer.toString(player.silver), this.skin);
        this.copperAmountLabel = new Label(Integer.toString(player.copper), this.skin);

        int amount;
        if(player.platinum < 5) { amount = 90; } else { amount = 91; }
        Image platinumImage = new Image(new TextureRegionDrawable(com.interrupt.dungeoneer.gfx.TextureAtlas.cachedAtlases.get("item").sprite_regions[amount]));

        if(player.gold < 5) { amount = 88; } else { amount = 89; }
        Image goldImage = new Image(new TextureRegionDrawable(com.interrupt.dungeoneer.gfx.TextureAtlas.cachedAtlases.get("item").sprite_regions[amount]));

        if(player.silver < 5) { amount = 92; } else { amount = 93; }
        Image silverImage = new Image(new TextureRegionDrawable(com.interrupt.dungeoneer.gfx.TextureAtlas.cachedAtlases.get("item").sprite_regions[amount]));

        if(player.copper < 5) { amount = 94; } else { amount = 95; }
        Image copperImage = new Image(new TextureRegionDrawable(com.interrupt.dungeoneer.gfx.TextureAtlas.cachedAtlases.get("item").sprite_regions[amount]));

        contentTable.add(platinumImage).width(40.0F).height(40.0F).align(8);
        contentTable.add(platinumAmountLabel).padLeft(2.0F).width(45.0F);
        contentTable.row();
        contentTable.add(goldImage).width(40.0F).height(40.0F).align(8);
        contentTable.add(goldAmountLabel).width(45.0F);
        contentTable.row();
        contentTable.add(silverImage).width(40.0F).height(40.0F).align(8);
        contentTable.add(silverAmountLabel).padLeft(2.0F).width(45.0F);
        contentTable.row();
        contentTable.add(copperImage).width(40.0F).height(40.0F).align(8);
        contentTable.add(copperAmountLabel).padLeft(2.0F).width(45.0F);
        contentTable.row();
        contentTable.add(this.doneBtn);

        contentTable.pack();
        return contentTable;
    }

    public void tick(float delta) {
        if (this.running) {
            this.timer += delta;
        }

        // https://libgdx.badlogicgames.com/nightlies/docs/api/constant-values.html
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {

            OverlayManager.instance.remove(CurrencyOverlay.this);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            OverlayManager.instance.remove(CurrencyOverlay.this);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {

            // fix for instant close
            if(escapePressed) {
                OverlayManager.instance.remove(CurrencyOverlay.this);
            } else {
                escapePressed = true;
            }

        }

    }
}
