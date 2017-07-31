package com.interrupt.dungeoneer.game;

import com.badlogic.gdx.Application.ApplicationType;
import com.interrupt.dungeoneer.input.Actions.Action;

public class Options {
    public static transient Options instance = new Options();
    public boolean mouseInvert = false;
    public boolean enableMusic = true;
    public float mouseXSensitivity = 1.0F;
    public float mouseYSensitivity = 1.0F;
    public Action mouseButton1Action;
    public Action mouseButton2Action;
    public Action mouseButton3Action;
    public float musicVolume;
    public float uiSize;
    public float sfxVolume;
    public float gfxQuality;
    public boolean fullScreen;
    public boolean useMouseScroller;
    public boolean hideUI;
    public boolean headBobEnabled;
    public boolean shadowsEnabled;
    public int key_use;
    public int key_attack;
    public int key_forward;
    public int key_backward;
    public int key_strafe_left;
    public int key_strafe_right;
    public int key_map;
    public int key_inventory;
    public int key_next_item;
    public int key_previous_item;
    /** @deprecated */
    @Deprecated
    public int key_cursor;
    public int key_drop;
    public int key_look_up;
    public int key_look_down;
    public int key_turn_left;
    public int key_turn_right;
    public float fieldOfView;
    public int graphicsDetailLevel;

    public Options() {
        this.mouseButton1Action = Action.ATTACK;
        this.mouseButton2Action = Action.USE;
        this.mouseButton3Action = Action.INVENTORY;
        this.musicVolume = 0.5F;
        this.uiSize = 1.0F;
        this.sfxVolume = 1.0F;
        this.gfxQuality = 1.0F;
        this.fullScreen = false;
        this.useMouseScroller = true;
        this.hideUI = false;
        this.headBobEnabled = true;
        this.shadowsEnabled = true;
        this.key_use = 33;
        // this.key_attack = 62; Replaced by jumping
        this.key_forward = 51;
        this.key_backward = 47;
        this.key_strafe_left = 29;
        this.key_strafe_right = 32;
        this.key_map = 41;
        this.key_inventory = 37;
        this.key_next_item = 72;
        this.key_previous_item = 71;
        this.key_cursor = 54;
        this.key_drop = 45;
        this.key_look_up = 19;
        this.key_look_down = 20;
        this.key_turn_left = 21;
        this.key_turn_right = 22;
        this.fieldOfView = 80.0F;
        this.graphicsDetailLevel = 3;
    }

    public Options(ApplicationType applicationType) {
        this.mouseButton1Action = Action.ATTACK;
        this.mouseButton2Action = Action.USE;
        this.mouseButton3Action = Action.INVENTORY;
        this.musicVolume = 0.5F;
        this.uiSize = 1.0F;
        this.sfxVolume = 1.0F;
        this.gfxQuality = 1.0F;
        this.fullScreen = false;
        this.useMouseScroller = true;
        this.hideUI = false;
        this.headBobEnabled = true;
        this.shadowsEnabled = true;
        this.key_use = 33;
        this.key_attack = 62;
        this.key_forward = 51;
        this.key_backward = 47;
        this.key_strafe_left = 29;
        this.key_strafe_right = 32;
        this.key_map = 41;
        this.key_inventory = 37;
        this.key_next_item = 72;
        this.key_previous_item = 71;
        this.key_cursor = 54;
        this.key_drop = 45;
        this.key_look_up = 19;
        this.key_look_down = 20;
        this.key_turn_left = 21;
        this.key_turn_right = 22;
        this.fieldOfView = 80.0F;
        this.graphicsDetailLevel = 3;
        if (applicationType == ApplicationType.Android || applicationType == ApplicationType.iOS) {
            this.graphicsDetailLevel = 2;
        }

        if (applicationType == ApplicationType.Android || applicationType == ApplicationType.iOS) {
            this.shadowsEnabled = false;
        }

    }
}
