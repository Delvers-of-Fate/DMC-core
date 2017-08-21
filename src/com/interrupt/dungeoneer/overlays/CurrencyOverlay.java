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
import com.badlogic.gdx.utils.Align;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.entities.Player;
import com.interrupt.dungeoneer.gfx.TextureAtlas;

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

        int tex = 0;
        if(player.platinum < 5) { tex = 90; } else { tex = 91; }
        Image platinumImage = new Image(new TextureRegionDrawable(TextureAtlas.cachedAtlases.get("item").sprite_regions[tex]));

        if(player.gold < 5) { tex = 88; } else { tex = 89; }
        Image goldImage = new Image(new TextureRegionDrawable(TextureAtlas.cachedAtlases.get("item").sprite_regions[tex]));

        if(player.silver < 5) { tex = 94; } else { tex = 95; }
        Image silverImage = new Image(new TextureRegionDrawable(TextureAtlas.cachedAtlases.get("item").sprite_regions[tex]));

        if(player.copper < 5) { tex = 92; } else { tex = 93; }
        Image copperImage = new Image(new TextureRegionDrawable(TextureAtlas.cachedAtlases.get("item").sprite_regions[tex]));

        contentTable.add(platinumImage).width(40.0F).height(40.0F).align(Align.left);
        contentTable.add(platinumAmountLabel).padLeft(2.0F).width(45.0F);
        contentTable.row();
        contentTable.add(goldImage).width(40.0F).height(40.0F).align(Align.left);
        contentTable.add(goldAmountLabel).width(45.0F);
        contentTable.row();
        contentTable.add(silverImage).width(40.0F).height(40.0F).align(Align.left);
        contentTable.add(silverAmountLabel).padLeft(2.0F).width(45.0F);
        contentTable.row();
        contentTable.add(copperImage).width(40.0F).height(40.0F).align(Align.left);
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
