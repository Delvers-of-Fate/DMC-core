package com.interrupt.dungeoneer.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.interrupt.dungeoneer.Audio;
import com.interrupt.dungeoneer.GameInput;
import com.interrupt.dungeoneer.GameManager;
import com.interrupt.dungeoneer.collision.Collidor;
import com.interrupt.dungeoneer.collision.Collision;
import com.interrupt.dungeoneer.entities.items.*;
import com.interrupt.dungeoneer.entities.items.Potion.PotionType;
import com.interrupt.dungeoneer.entities.items.Weapon.DamageType;
import com.interrupt.dungeoneer.entities.triggers.ButtonModel;
import com.interrupt.dungeoneer.entities.triggers.Trigger;
import com.interrupt.dungeoneer.entities.triggers.Trigger.TriggerType;
import com.interrupt.dungeoneer.game.CachePools;
import com.interrupt.dungeoneer.game.Colors;
import com.interrupt.dungeoneer.game.Game;
import com.interrupt.dungeoneer.game.Level;
import com.interrupt.dungeoneer.game.Options;
import com.interrupt.dungeoneer.gfx.DynamicLight;
import com.interrupt.dungeoneer.gfx.GlRenderer;
import com.interrupt.dungeoneer.gfx.animation.lerp3d.LerpFrame;
import com.interrupt.dungeoneer.gfx.animation.lerp3d.LerpedAnimation;
import com.interrupt.dungeoneer.input.Actions;
import com.interrupt.dungeoneer.input.ControllerState;
import com.interrupt.dungeoneer.input.ReadableKeys;
import com.interrupt.dungeoneer.input.Actions.Action;
import com.interrupt.dungeoneer.input.ControllerState.Buttons;
import com.interrupt.dungeoneer.overlays.*;
import com.interrupt.dungeoneer.rpg.Stats;
import com.interrupt.dungeoneer.screens.GameScreen;
import com.interrupt.dungeoneer.statuseffects.BurningEffect;
import com.interrupt.dungeoneer.statuseffects.PoisonEffect;
import com.interrupt.dungeoneer.statuseffects.SlowEffect;
import com.interrupt.dungeoneer.statuseffects.StatusEffect;
import com.interrupt.dungeoneer.tiles.ExitTile;
import com.interrupt.dungeoneer.tiles.Tile;
import com.interrupt.dungeoneer.ui.Hotbar;
import com.interrupt.helpers.PlayerHistory;
import com.interrupt.managers.StringManager;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class Player extends Actor {
    public int platinum = 0;
    public int gold = 0;
    public int silver = 0;
    public int copper = 0;
    public float rot = 0.0F;
    public float yrot = 0.0F;
    public float rota = 0.0F;
    public float rotya = 0.0F;
    public float rot2 = 0.0F;
    public boolean hasAttacked;
    private float attackSpeed = 1.0F;
    private float attackChargeSpeed = 1.0F;
    public float handAnimateTimer = 0.0F;
    private float tickcount = 0.0F;
    public boolean ignoreStairs = false;
    public float spawnX;
    public float spawnY;
    public int keys = 0;
    public float attackChargeTime = 40.0F;
    public float attackCharge = 0.0F;
    /** @deprecated */
    @Deprecated
    public float attackDelay = 0.0F;
    public float headbob = 0.0F;
    public transient Float stepUpLerp = null;
    public transient Float stepUpTimer = null;
    public boolean doingHeldItemTransition = false;
    public float heldItemTransitionEnd = 0.0F;
    public float heldItemTransition = 0.0F;
    private boolean wasOnFloorLast = false;
    private float lastSplashTime = 0.0F;
    private float stepHeight = 0.35F;
    private float lastZ;
    public boolean isHoldingOrb = false;
    public int levelNum = 0;
    public Array<Item> inventory = new Array();
    public Integer selectedBarItem = null;
    public Integer heldItem = null;
    public Array<Entity> startingInventory = new Array();
    public HashMap<String, Item> equippedItems = new HashMap();
    private Long torchSoundInstance = null;
    private Long stepsSoundInstance = null;
    private Integer tapLength = null;
    public final int inventorySize = 24;
    public Item hovering = null;
    private boolean attackButtonWasPressed = false;
    float walkVel = 0.05F;
    float walkSpeed = 0.15F;
    float minWalkSpeed = 0.01F;
    float rotSpeed = 0.009F;
    float maxRot = 0.06F;
    private transient float xm = 0.0F;
    private transient float zm = 0.0F;
    private transient float deltaX = 0.0F;
    private transient float deltaY = 0.0F;
    public transient float friction = 1.0F;
    public float randomSeed = 1.0F;
    private transient Vector2 walkVelVector = new Vector2();
    private transient Vector2 lastDelta = new Vector2();
    private transient Collision hitLoc = new Collision();
    private transient float nextx;
    private transient float nexty;
    private transient boolean touchingItem = false;
    Vector3 tempVec1 = new Vector3();
    Vector3 tempVec2 = new Vector3();
    Vector3 tempVec3 = new Vector3();
    Vector3 tempVec4 = new Vector3();
    public Color torchColor = new Color(1.0F, 0.8F, 0.4F, 1.0F);
    public boolean inEditor = false;
    public static transient ControllerState controllerState = new ControllerState();
    private transient Array<Entity> pickList = new Array();
    private HashMap<String, Float> messageViews = new HashMap();
    public transient LerpedAnimation handAnimation = null;
    public PlayerHistory history = new PlayerHistory();
    public transient boolean wasGamepadDragging = false;
    public float screenshakeAmount = 0.0F;
    public transient Vector2 screenshake = new Vector2();
    public Array<Potion> shuffledPotions = new Array();
    public Array<PotionType> discoveredPotions = new Array();
    private transient float footstepsTimer = 30.0F;
    private transient boolean holdingTwoHanded = false;
    public HashMap<String, String> seenMessages = new HashMap();
    public Stats calculatedStats = new Stats();
    public transient boolean isOnLadder = false;
    public LerpedAnimation dyingAnimation = null;
    public transient boolean isDead = false;
    public transient float strafeCameraAngleMod = 0.0F;
    private boolean canLevelUp = true;
    private static Vector3 pickEntityTemp1 = new Vector3();
    private static Vector3 pickEntityTemp2 = new Vector3();

    private float jumpModifier = 1.1F;
    private boolean isSprinting = false;
    private float sprintModifier = 0.15F;

    public Player() {
        this.isSolid = true;
        this.collision.set(0.2F, 0.2F, 0.65F);
        this.dropSound = "drops/drop_soft.mp3";
        this.hidden = true;
        this.mass = 2.0F;
    }

    public Player(Game game) {
        this.z = 0.0F;
        this.rot = -1.5707964F;
        this.maxHp = 8;
        this.hp = this.maxHp;
        this.collision.set(0.2F, 0.2F, 0.65F);
        this.isSolid = true;
        this.dropSound = "drops/drop_soft.mp3";
        this.mass = 2.0F;
        game.player = this;
    }

    public void makeStartingInventory() {
        if(this.inventory.size < 24) {
            for(int i = 0; i < 24; ++i) {
                this.inventory.add((Item) null);
            }
        }

        if(this.startingInventory != null && this.startingInventory.size > 0) {
            boolean equippedWeapon = false;
            Iterator var2 = this.startingInventory.iterator();

            while(var2.hasNext()) {
                Entity e = (Entity)var2.next();
                Entity toAdd = null;
                if(e instanceof Item) {
                    toAdd = e;
                } else if(e instanceof ItemSpawner) {
                    toAdd = ((ItemSpawner)e).getItem();
                }

                if(toAdd != null) {
                    if(toAdd instanceof Weapon) {
                        this.addToInventory((Item)toAdd, false);
                        if(!equippedWeapon) {
                            this.equip((Item)toAdd, false);
                        }

                        equippedWeapon = true;
                    } else if(toAdd instanceof Armor) {
                        this.equipArmor((Item)toAdd);
                    } else if(toAdd instanceof Item) {
                        this.addToInventory((Item)toAdd);
                    }
                }
            }

            this.startingInventory.clear();
        }

    }

    public void init() {
        this.makeStartingInventory();
        this.setupController();
    }

    public void checkAngles(Level level, float delta) {
        if(level.collidesWithAngles(this.x + this.xa * delta, this.y, this.collision, this)) {
            this.xa = 0.0F;
        }

        if(level.collidesWithAngles(this.x, this.y + this.ya * delta, this.collision, this)) {
            this.ya = 0.0F;
        }

    }

    public void tick(Level level, float delta) {
        this.setMusicVolume();
        this.stepUpTick(delta);
        this.calculatedStats.Recalculate(this);
        if(this.hp > this.getMaxHp()) {
            this.hp = this.getMaxHp();
        }

        isSprinting = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        this.nextx = this.x + this.xa * delta;
        this.nexty = this.y + this.ya * delta;
        if(!this.inEditor) {
            Entity item = level.checkItemCollision(this.nextx, this.y, this.collision.x);
            if(item == null) {
                item = level.checkItemCollision(this.x, this.nexty, this.collision.x);
            }

            if(item != null) {
                item.encroached(this);
            }

        }

        Vector3 floorSlope = level.getSlope(this.x, this.y, this.z, this.collision.x);
        float slopeXMod = floorSlope.x * Math.abs(floorSlope.x) * 0.01F;
        float slopeYMod = floorSlope.y * Math.abs(floorSlope.y) * 0.01F;
        if(Math.abs(slopeXMod) < 0.003F) {
            slopeXMod = 0.0F;
        }

        if(Math.abs(slopeYMod) < 0.003F) {
            slopeYMod = 0.0F;
        }

        if(this.isOnFloor) {
            this.xa += slopeXMod * delta;
            this.ya += slopeYMod * delta;
        }

        Entity encroaching;
        if(this.isSolid && !level.isFree(this.nextx, this.y, this.z, this.collision, this.stepHeight, false, this.hitLoc)) {
            this.xa = 0.0F;
        } else {
            encroaching = null;
            if(this.isSolid) {
                encroaching = level.getHighestEntityCollision(this.nextx, this.y, this.z, this.collision, this);
                if(encroaching == null) {
                    encroaching = level.checkStandingRoomWithEntities(this.nextx, this.y, this.z, this.collision, this);
                }
            }

            if(encroaching != null && this.z <= encroaching.z + encroaching.collision.z - this.stepHeight) {
                this.xa = 0.0F;
            } else if(encroaching != null) {
                if(this.z > encroaching.z + encroaching.collision.z - this.stepHeight && level.collidesWorldOrEntities(this.nextx, this.y, encroaching.z + encroaching.collision.z, this.collision, this)) {
                    this.x += this.xa * delta;
                    this.stepUp(encroaching.z + encroaching.collision.z - this.z);
                    this.z = encroaching.z + encroaching.collision.z;
                } else {
                    this.xa = 0.0F;
                    if(!this.inEditor) {
                        encroaching.encroached(this);
                    }
                }
            } else {
                this.x += this.xa * delta;
            }
        }

        /// Jumping
        ///
        /// via DMC
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (this.isOnFloor && this.statusEffects == null && !isSprinting) {
                this.stepUp(jumpModifier);
                this.z += jumpModifier;
            }
        }

        if(this.isSolid && !level.isFree(this.x, this.nexty, this.z, this.collision, this.stepHeight, false, this.hitLoc)) {
            this.ya = 0.0F;
        } else {
            encroaching = null;
            if(this.isSolid) {
                encroaching = level.getHighestEntityCollision(this.x, this.nexty, this.z, this.collision, this);
                if(encroaching == null) {
                    encroaching = level.checkStandingRoomWithEntities(this.x, this.nexty, this.z, this.collision, this);
                }
            }

            if(encroaching != null && this.z <= encroaching.z + encroaching.collision.z - this.stepHeight) {
                this.ya = 0.0F;
            } else if(encroaching != null) {
                if(this.z > encroaching.z + encroaching.collision.z - this.stepHeight && level.collidesWorldOrEntities(this.x, this.nexty, encroaching.z + encroaching.collision.z, this.collision, this)) {
                    this.y += this.ya * delta;
                    this.stepUp(encroaching.z + encroaching.collision.z - this.z);
                    this.z = encroaching.z + encroaching.collision.z;
                } else {
                    this.ya = 0.0F;
                    if(!this.inEditor) {
                        encroaching.encroached(this);
                    }
                }
            } else {
                this.y += this.ya * delta;
            }
        }

        boolean onEntity = false;
        Array<Entity> allStandingOn = level.getEntitiesColliding(this.x, this.y, this.z + this.za * delta - 2.0E-4F, this);
        Entity standingOn = null;
        Iterator var9 = allStandingOn.iterator();

        while(true) {
            do {
                do {
                    Entity on;
                    do {
                        if(!var9.hasNext()) {
                            if(this.isSolid && this.za > 0.0F && !level.isFree(this.x, this.y, this.z + this.za, this.collision, this.stepHeight, false, (Collision)null)) {
                                this.za = 0.0F;
                            }

                            if(standingOn == null) {
                                this.z += this.za * delta;
                            } else if(this.za <= 0.0F) {
                                onEntity = true;
                            }

                            boolean wasOnFloor = this.isOnFloor;
                            float lastZa = this.za;
                            float floorHeight = level.maxFloorHeight(this.x, this.y, this.z, this.collision.x);
                            float floorClampMod = 0.035F;
                            if(this.floating) {
                                floorClampMod = 0.0F;
                            }

                            this.isOnFloor = this.z <= floorHeight + 0.5F + floorClampMod;
                            if(!this.isOnFloor && !onEntity) {
                                if(!this.isOnLadder && !this.floating) {
                                    this.za -= 0.0035F * delta;
                                }
                            } else if(!this.isOnLadder) {
                                float stepUpToHeight = floorHeight + 0.5F;
                                if(standingOn != null && standingOn.z + standingOn.collision.z - this.z < this.stepHeight && standingOn.z + standingOn.collision.z > stepUpToHeight) {
                                    stepUpToHeight = standingOn.z + standingOn.collision.z;
                                }

                                if(this.isSolid && level.collidesWorldOrEntities(this.x, this.y, stepUpToHeight, this.collision, this)) {
                                    if(stepUpToHeight > this.z) {
                                        this.stepUp(stepUpToHeight - this.z);
                                        this.z = stepUpToHeight;
                                    } else if(this.isOnFloor && !this.isOnEntity) {
                                        this.z = floorHeight + 0.5F;
                                    } else if(this.isOnEntity) {
                                        this.z = stepUpToHeight;
                                    }
                                }

                                if(!this.floating) {
                                    this.za = 0.0F;
                                }
                            }

                            this.headbob = (float)Math.sin((double)(this.tickcount * 0.319F)) * Math.min(Math.abs(this.xa) + Math.abs(this.ya), 0.15F) * 0.3F;
                            if(this.floating) {
                                this.headbob = 0.0F;
                            }

                            this.inwater = false;
                            Tile waterTile = level.findWaterTile(this.x + 0.5F, this.y + 0.5F, this.z, this.collision);
                            float ladderMul;
                            if(waterTile != null) {
                                if(this.lastZ >= waterTile.floorHeight + 0.5F) {
                                    this.splash(level, waterTile.floorHeight + 0.5F, waterTile);
                                    if(waterTile.data.hurts > 0) {
                                        this.lavaHurtTimer = 0.0F;
                                    }
                                }

                                if(waterTile.data.hurts > 0) {
                                    this.lavaHurtTimer -= delta;
                                    if(this.lavaHurtTimer <= 0.0F) {
                                        this.lavaHurtTimer = 30.0F;
                                        this.hit(0.0F, 0.0F, waterTile.data.hurts, 0.0F, waterTile.data.damageType);
                                    }
                                }

                                if(this.isOnFloor && waterTile.data.walkSound != null) {
                                    this.footstepsTimer -= delta;
                                    if(this.footstepsTimer < 0.0F) {
                                        this.footstepsTimer = 25.0F;
                                        ladderMul = Math.max(Math.abs(this.xa), Math.abs(this.ya));
                                        ladderMul = Math.min(ladderMul, 1.0F);
                                        Audio.playSound(waterTile.data.walkSound, ladderMul * 4.0F, 0.9F + Game.rand.nextFloat() * 0.2F);
                                    }
                                }

                                this.friction = 0.08F;
                                this.xa -= (this.xa - this.xa * 0.5F) * this.friction * delta;
                                this.ya -= (this.ya - this.ya * 0.5F) * this.friction * delta;
                                this.stepHeight = 0.3499F + (waterTile.floorHeight + 0.5F - this.z);
                                this.headbob = (float)Math.sin((double)(this.tickcount / 24.0F)) * 0.02F;
                                this.inwater = true;
                            } else {
                                this.stepHeight = 0.35F;
                                Tile current = level.getTileOrNull((int)(this.x + 0.5F), (int)(this.y + 0.5F));
                                if(current == null) {
                                    current = Tile.solidWall;
                                }

                                if(current != null && current.data.hurts > 0 && this.z <= current.getMaxFloorHeight() + 0.5F && this.isOnFloor) {
                                    this.lavaHurtTimer -= delta;
                                    if(this.lavaHurtTimer <= 0.0F) {
                                        this.lavaHurtTimer = 30.0F;
                                        this.hit(0.0F, 0.0F, current.data.hurts, 0.0F, current.data.damageType);
                                    }
                                }

                                if(this.isOnFloor && current.data.walkSound != null) {
                                    this.footstepsTimer -= delta;
                                    if(this.footstepsTimer < 0.0F) {
                                        this.footstepsTimer = 20.0F;
                                        float stepvol = Math.max(Math.abs(this.xa), Math.abs(this.ya));
                                        stepvol = Math.min(stepvol, 1.0F);
                                        Audio.playSound(current.data.walkSound, stepvol * 2.0F, 0.9F + Game.rand.nextFloat() * 0.2F);
                                    }
                                }

                                if(this.isOnFloor) {
                                    this.friction = current.data.friction;
                                } else if(!onEntity && !this.isOnLadder) {
                                    this.friction = 0.1F;
                                } else {
                                    this.friction = 1.0F;
                                }

                                this.xa -= (this.xa - this.xa * 0.8F) * this.friction * delta;
                                this.ya -= (this.ya - this.ya * 0.8F) * this.friction * delta;
                            }

                            if(!wasOnFloor && this.isOnFloor && !this.inwater && Math.abs(lastZa) > 0.03F) {
                                Audio.playSound(this.dropSound, Math.abs(lastZa) * 1.0F, 1.0F);
                                if(Math.abs(lastZa) > 0.06F) {
                                    this.makeFallingDustEffect();
                                }
                            }

                            this.screenshakeAmount = Math.max(this.screenshakeAmount -= delta * 0.1F, 0.0F);
                            if(this.screenshakeAmount > 0.0F && Game.instance != null) {
                                this.screenshake.x = (float)Math.sin((double)(Game.instance.time * 0.88F)) * this.screenshakeAmount * 0.36F;
                                this.screenshake.y = (float)Math.cos((double)(Game.instance.time * 0.9F)) * this.screenshakeAmount * 0.36F;
                            }

                            if(this.isOnLadder) {
                                if(this.walkVelVector.y > 0.0F) {
                                    ladderMul = this.yrot;
                                    if(ladderMul > 0.2F) {
                                        ladderMul = 0.2F;
                                    } else if(ladderMul < -0.2F) {
                                        ladderMul = -0.2F;
                                    }

                                    this.za = ladderMul * 0.1F * delta;
                                } else {
                                    this.za = 0.0F;
                                }

                                if(this.isOnFloor && this.za < 0.0F) {
                                    this.za = 0.0F;
                                }

                                this.isOnLadder = false;
                            }

                            return;
                        }

                        on = (Entity)var9.next();
                    } while(!this.isSolid);

                    if(standingOn == null || on.z + on.collision.z > standingOn.z + standingOn.collision.z) {
                        standingOn = on;
                    }
                } while(standingOn == null);

                standingOn.encroached(this);
            } while(!(standingOn instanceof Actor) && !(standingOn instanceof Trigger));

            this.za = 0.02F;
            this.xa += (Game.rand.nextFloat() * 0.01F - 0.005F) * delta;
            this.ya += (Game.rand.nextFloat() * 0.01F - 0.005F) * delta;
        }
    }

    private void setMusicVolume() {
        float musicLerp = Math.max(this.hp == 0?0.0F:(float)this.hp / (float)this.maxHp, 0.0F);
        if(this.isDead) {
            musicLerp = 0.0F;
        }

        Audio.setMusicTargetVolume(musicLerp);
    }

    public void die() {
        Gdx.app.log("DelverGame", "Oh noes :( Player is dying!");
        Array<LerpFrame> deathFrames = new Array();
        this.floating = false;
        if(!this.inwater) {
            deathFrames.add(new LerpFrame(new Vector3(), new Vector3(), 275.0F));
            deathFrames.add(new LerpFrame(new Vector3(0.0F, -0.3F, 0.1F), new Vector3(0.0F, 0.0F, 70.0F), 1000.0F));
            deathFrames.add(new LerpFrame(new Vector3(0.0F, -0.3F, 0.1F), new Vector3(0.0F, 0.0F, 70.0F), 10.0F));
        } else {
            deathFrames.add(new LerpFrame(new Vector3(), new Vector3(), 275.0F));
            deathFrames.add(new LerpFrame(new Vector3(0.0F, 0.0F, 0.1F), new Vector3(70.0F, 0.0F, 0.0F), 1000.0F));
            deathFrames.add(new LerpFrame(new Vector3(0.0F, 0.0F, 0.1F), new Vector3(70.0F, 0.0F, 0.0F), 10.0F));
        }

        this.dyingAnimation = new LerpedAnimation(deathFrames);
        this.dyingAnimation.play(6.0F, Interpolation.pow4In);
        Audio.playSound("sfx_death.mp3", 1.0F);
        this.dropItem(this.selectedBarItem, Game.instance.level, 0.075F);
        this.isDead = true;
    }

    public void editorTick(Level level, float delta) {
        this.walkVel = 0.05F;
        this.walkSpeed = 0.15F;
        this.rotSpeed = 0.009F;
        this.maxRot = 0.06F;
        this.isSolid = true;
        this.tick(level, delta);
    }

    public void tick(Level level, float delta, GameInput input) {
        boolean isInOverlay = OverlayManager.instance.current() != null;
        if(this.isDead) {
            this.dyingAnimation.animate(delta);
        } else {
            this.tickStatusEffects(delta);
        }

        this.walkVel = 0.05F;
        this.walkSpeed = this.getWalkSpeed();
        this.rotSpeed = 0.009F;
        this.maxRot = 0.06F;
        if((double)Math.abs(this.rot) > 6.28318531D) {
            if(this.rot > 0.0F) {
                this.rot %= 6.2831855F;
            } else if(this.rot < 0.0F) {
                this.rot %= 6.2831855F;
            }
        }

        boolean up = false;
        boolean down = false;
        boolean left = false;
        boolean right = false;
        boolean turnLeft = false;
        boolean turnRight = false;
        boolean turnUp = false;
        boolean turnDown = false;
        boolean attack = false;
        if(!this.isDead && !isInOverlay) {
            up = input.isMoveForwardPressed();
            down = input.isMoveBackwardsPressed();
            left = input.isStrafeLeftPressed();
            right = input.isStrafeRightPressed();
            turnLeft = input.isTurnLeftPressed();
            turnRight = input.isTurnRightPressed();
            turnUp = input.isLookUpPressed();
            turnDown = input.isLookDownPressed();
            attack = input.isAttackPressed() || controllerState.attack;
        }

        if(this.heldItem != null) {
            if(this.handAnimation != null && this.handAnimation.playing) {
                this.handAnimation.animate(delta);
            }
        } else if(this.heldItem == null) {
            this.handAnimation = null;
        }

        if(Game.isMobile) {
            attack = input.isAttackPressed() || Game.hud.isAttackPressed() || controllerState.attack;
            if(!attack && input.isLeftTouched()) {
                if(this.tapLength == null) {
                    this.tapLength = Integer.valueOf(0);
                } else {
                    Integer var14 = this.tapLength;
                    Integer var15 = this.tapLength = Integer.valueOf(this.tapLength.intValue() + 1);
                }
            } else if(this.tapLength != null) {
                if(this.tapLength.intValue() < 10) {
                    this.Use(level, Gdx.input.getX(), Gdx.input.getY());
                }

                this.tapLength = null;
            }
        }

        this.lastZ = this.z;
        float controllerXMod;
        if(this.ignoreStairs) {
            controllerXMod = Math.max(Math.abs(this.x - this.spawnX), Math.abs(this.y - this.spawnY));
            if(controllerXMod > 1.0F) {
                this.ignoreStairs = false;
            }
        }

        if(this.attackCharge > 0.0F) {
            controllerXMod = this.attackCharge / this.attackChargeTime;
            controllerXMod = Math.min(controllerXMod, 1.0F);
            this.walkVel = (float)((double)this.walkVel * (1.0D - (double)(0.5F * controllerXMod) * (1.2D - (double)((float)this.stats.DEX * 0.06F))));
            if(this.GetHeldItem() == null) {
                this.attackCharge = 0.0F;
            }
        }

        if(this.GetHeldItem() instanceof Weapon) {
            this.attackChargeSpeed = ((Weapon)this.GetHeldItem()).getChargeSpeed();
        } else {
            this.attackChargeSpeed = 1.0F;
        }

        this.xm = 0.0F;
        this.zm = 0.0F;
        this.walkVelVector.set(0.0F, 0.0F);
        if(up) {
            ++this.walkVelVector.y;
        }

        if(down) {
            --this.walkVelVector.y;
        }

        if(left) {
            ++this.walkVelVector.x;
        }

        if(right) {
            --this.walkVelVector.x;
        }

        this.walkVelVector = this.walkVelVector.nor();
        if(this.walkVelVector.y < 0.0F) {
            this.walkVelVector.x *= 0.8F;
            this.walkVelVector.y *= 0.5F;
        }

        this.zm += this.walkVelVector.y * this.walkVel;
        this.xm += this.walkVelVector.x * this.walkVel;
        this.strafeCameraAngleMod *= 0.825F;
        this.strafeCameraAngleMod += this.xm * delta;
        float xMod;
        if(input.usingGamepad && !this.isDead) {
            if(input.isCursorCatched()) {
                this.zm += -controllerState.controllerMove.y * this.walkVel;
                this.xm += -controllerState.controllerMove.x * this.walkVel;
            } else if(input.showingGamepadCursor) {
                input.gamepadCursorPosition.add(controllerState.controllerMove.x * 8.0F, -controllerState.controllerMove.y * 8.0F);
                input.gamepadCursorPosition.x = Math.min(input.gamepadCursorPosition.x, (float)Gdx.graphics.getWidth());
                input.gamepadCursorPosition.y = Math.min(input.gamepadCursorPosition.y, (float)Gdx.graphics.getHeight());
                input.gamepadCursorPosition.x = Math.max(input.gamepadCursorPosition.x, 0.0F);
                input.gamepadCursorPosition.y = Math.max(input.gamepadCursorPosition.y, 0.0F);
                Game.ui.mouseMoved((int)input.gamepadCursorPosition.x, Gdx.graphics.getHeight() - (int)input.gamepadCursorPosition.y);
                int var10001;
                int var10002;
                if(controllerState.use) {
                    this.wasGamepadDragging = true;
                    var10001 = (int)input.gamepadCursorPosition.x;
                    var10002 = Gdx.graphics.getHeight() - (int)input.gamepadCursorPosition.y;
                    input.getClass();
                    input.touchDown(var10001, var10002, 0, 0);
                } else if(this.wasGamepadDragging) {
                    this.wasGamepadDragging = false;
                    var10001 = (int)input.gamepadCursorPosition.x;
                    var10002 = Gdx.graphics.getHeight() - (int)input.gamepadCursorPosition.y;
                    input.getClass();
                    input.touchUp(var10001, var10002, 0, 0);
                }
            }

            controllerXMod = 0.02F * Options.instance.mouseXSensitivity * 0.5F;
            xMod = (!Options.instance.mouseInvert?0.02F:-0.02F) * Options.instance.mouseYSensitivity * 0.5F;
            this.rota += controllerState.controllerLook.x * controllerXMod * delta;
            this.rotya += controllerState.controllerLook.y * xMod * delta;
        }

        this.touchingItem = false;
        boolean inOverlay = OverlayManager.instance.current() != null;
        if(!inOverlay && !this.isDead && (Game.isMobile || !input.isCursorCatched()) && Gdx.input.isButtonPressed(0)) {
            if(Game.hud.dragging != null) {
                this.touchingItem = true;
            } else if(Gdx.input.justTouched() && !attack && input.uiTouchPointer == null && input.lastTouchedPointer != null) {
                Entity touching = this.pickEntity(level, Gdx.input.getX(input.lastTouchedPointer.intValue()), Gdx.input.getY(input.lastTouchedPointer.intValue()), 0.9F);
                if(touching != null) {
                    input.uiTouchPointer = input.lastTouchedPointer;
                }

                if(touching != null && touching instanceof Item) {
                    this.touchingItem = true;
                    if(touching.isActive) {
                        if(!(touching instanceof Key) && !(touching instanceof Gold)) {
                            touching.isActive = false;
                            Game.hud.dragging = (Item) touching;
                            Game.dragging = Game.hud.dragging;
                            Game.hud.refresh();
                        } else {
                            touching.use(this, 0.0F, 0.0F);
                        }
                    }
                } else if(touching != null && !(touching instanceof Stairs) && !(touching instanceof Door)) {
                    touching.use(this, Game.camera.direction.x, Game.camera.direction.z);
                }
            }
        }

        this.hovering = null;
        if(!this.touchingItem && !Game.isMobile && !input.isCursorCatched()) {
            this.hovering = this.pickItem(level, input.getPointerX(), input.getPointerY(), 0.9F);
        }

        float flightFriction;
        if(!this.isDead && (!Game.isMobile || input.isCursorCatched()) && !OverlayManager.instance.shouldPauseGame()) {
            String useText = (String)ReadableKeys.keyNames.get(Actions.keyBindings.get(Action.USE));
            if(Game.isMobile) {
                useText = StringManager.get("entities.Player.mobileUseText");
            }

            Entity centered = this.pickEntity(level, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0.7F);
            if(centered != null) {
                if(centered instanceof Item && Math.abs(centered.xa) < 0.01F && Math.abs(centered.ya) < 0.01F && Math.abs(centered.za) < 0.01F) {
                    Game.ShowUseMessage(MessageFormat.format(StringManager.get("entities.Player.getItemText"), new Object[]{useText, ((Item)((Item)centered)).GetName() + "\n" + ((Item)((Item)centered)).GetInfoText()}), ((Item)((Item)centered)).GetTextColor());
                } else if(centered instanceof Stairs) {
                    Game.ShowUseMessage(MessageFormat.format(StringManager.get("entities.Player.useText"), new Object[]{useText, ((Stairs)((Stairs)centered)).getUseText()}));
                } else if(centered instanceof Door) {
                    Game.ShowUseMessage(MessageFormat.format(StringManager.get("entities.Player.useText"), new Object[]{useText, ((Door)centered).getUseText()}));
                } else if(centered instanceof Trigger) {
                    Game.ShowUseMessage(MessageFormat.format(StringManager.get("entities.Player.useText"), new Object[]{useText, ((Trigger)centered).getUseVerb()}));
                } else if(centered instanceof ButtonModel) {
                    Game.ShowUseMessage(MessageFormat.format(StringManager.get("entities.Player.useText"), new Object[]{useText, ((ButtonModel)centered).useVerb}));
                }
            } else {
                Ray ray = Game.camera.getPickRay((float)(Gdx.graphics.getWidth() / 2), (float)(Gdx.graphics.getHeight() / 2));
                flightFriction = ray.direction.x * 0.7F;
                float projy = ray.direction.z * 0.7F;
                int checkx = (int)Math.floor((double)(ray.origin.x + flightFriction));
                int checky = (int)Math.floor((double)(ray.origin.z + projy));
                Tile hit = level.getTile(checkx, checky);
                if(hit instanceof ExitTile) {
                    Game.ShowUseMessage(MessageFormat.format(StringManager.get("entities.Player.exitDungeonText"), new Object[]{useText}));
                }
            }
        }

        if(turnLeft) {
            this.rota += this.rotSpeed * delta;
            if(this.rota > this.maxRot) {
                this.rota = this.maxRot;
            }
        } else if(turnRight) {
            this.rota -= this.rotSpeed * delta;
            if(this.rota < -this.maxRot) {
                this.rota = -this.maxRot;
            }
        }

        this.rot += this.rota;
        this.rota = (float)((double)this.rota * 0.8D);
        if(turnUp) {
            this.rotya += this.rotSpeed * 0.6F * delta;
            if(this.rotya > this.maxRot) {
                this.rotya = this.maxRot;
            }
        } else if(turnDown) {
            this.rotya -= this.rotSpeed * 0.6F * delta;
            if(this.rotya < -this.maxRot) {
                this.rotya = -this.maxRot;
            }
        }

        this.yrot += this.rotya;
        this.rotya = (float)((double)this.rotya * 0.8D);
        if(Game.isMobile && !this.isDead && !isInOverlay) {
            xMod = 60.0F;
            if(input.isLeftTouched() && (!this.touchingItem || input.uiTouchPointer != input.leftPointer)) {
                this.deltaX = input.getLeftTouchPosition().x - (float)Gdx.input.getX(input.leftPointer.intValue());
                this.deltaY = input.getLeftTouchPosition().y - (float)Gdx.input.getY(input.leftPointer.intValue());
                this.deltaX *= Math.abs(this.deltaX);
                this.deltaY *= Math.abs(this.deltaY);
                this.deltaX *= GameScreen.cDelta;
                this.deltaY *= GameScreen.cDelta;
                if(this.deltaY > xMod) {
                    this.deltaY = xMod;
                } else if(this.deltaY < -xMod) {
                    this.deltaY = -xMod;
                }

                if(this.deltaX > xMod) {
                    this.deltaX = xMod;
                } else if(this.deltaX < -xMod) {
                    this.deltaX = -xMod;
                }

                this.deltaY /= xMod;
                this.deltaX /= xMod;
                if(Math.abs(this.deltaX) < 0.1F) {
                    this.deltaX = 0.0F;
                }

                if(Math.abs(this.deltaY) < 0.1F) {
                    this.deltaY = 0.0F;
                }

                if(!Game.ignoreTouch) {
                    this.zm += this.deltaY * this.walkVel;
                    this.xm += this.deltaX * this.walkVel;
                }
            }

            if(Game.hud.isAttackPressed() && (!this.touchingItem || input.uiTouchPointer != input.rightPointer)) {
                this.deltaX = 0.0F;
                this.deltaY = 0.0F;
                Integer thisX = Integer.valueOf(0);
                Integer thisY = Integer.valueOf(0);
                if(input.isRightTouched()) {
                    thisX = Integer.valueOf(Gdx.input.getX(input.rightPointer.intValue()));
                    thisY = Integer.valueOf(Gdx.input.getY(input.rightPointer.intValue()));
                } else if(Game.hud.isAttackPressed() && input.uiTouchPointer != null) {
                    thisX = Integer.valueOf(Gdx.input.getX(input.uiTouchPointer.intValue()));
                    thisY = Integer.valueOf(Gdx.input.getY(input.uiTouchPointer.intValue()));
                }

                if(this.lastDelta == null) {
                    this.lastDelta = new Vector2((float)thisX.intValue(), (float)thisY.intValue());
                }

                this.deltaX = (float)((int)this.lastDelta.x - thisX.intValue());
                this.deltaY = (float)((int)this.lastDelta.y - thisY.intValue());
                if(Options.instance != null && Options.instance.mouseInvert) {
                    this.deltaY *= -1.0F;
                }

                this.deltaX *= Options.instance.mouseXSensitivity;
                this.deltaY *= Options.instance.mouseYSensitivity;
                if(!Game.ignoreTouch) {
                    this.rotya += this.deltaY / 800.0F * Game.GetUiSize() / 85.0F;
                    this.rota += this.deltaX / 400.0F * Game.GetUiSize() / 85.0F;
                }

                this.lastDelta.set((float)thisX.intValue(), (float)thisY.intValue());
            }

            if(!Game.hud.isAttackPressed()) {
                this.lastDelta = null;
            }
        }

        if(Game.ignoreTouch) {
            Game.ignoreTouch = false;
        }

        this.tickcount += delta;
        if(this.hasAttacked && !attack) {
            this.hasAttacked = false;
        }

        if(this.doingHeldItemTransition) {
            this.heldItemTransition += delta;
        }

        if((double)this.yrot > 1.3D) {
            this.yrot = 1.3F;
        }

        if((double)this.yrot < -1.3D) {
            this.yrot = -1.3F;
        }

        xMod = (float)((double)this.xm * Math.cos((double)this.rot) + (double)this.zm * Math.sin((double)this.rot)) * this.walkSpeed * delta;
        float yMod = (float)((double)this.zm * Math.cos((double)this.rot) - (double)this.xm * Math.sin((double)this.rot)) * this.walkSpeed * delta;
        if(this.floating) {
            float flySpeed = 0.6F;
            if(!this.isOnFloor && !this.isOnEntity) {
                xMod = GameManager.renderer.camera.direction.x * this.zm * flySpeed;
                yMod = GameManager.renderer.camera.direction.z * this.zm * flySpeed;
                xMod += (float)((double)this.xm * Math.cos((double)this.rot)) * flySpeed * delta;
                yMod += (float)((double)(-this.xm) * Math.sin((double)this.rot)) * flySpeed * delta;
            }

            this.za += GameManager.renderer.camera.direction.y * 0.008F * this.walkVelVector.y * flySpeed;
            flightFriction = 0.4F;
            this.za -= (this.za - this.za * 0.8F) * flightFriction * delta;
            this.xa -= (this.xa - this.xa * 0.8F) * flightFriction * delta;
            this.ya -= (this.ya - this.ya * 0.8F) * flightFriction * delta;
        }

        if(this.isOnLadder) {
            xMod *= 0.5F;
            yMod *= 0.5F;
        }

        this.xa += xMod * Math.min(this.friction * 1.4F, 1.0F);
        this.ya += yMod * Math.min(this.friction * 1.4F, 1.0F);
        this.tick(level, delta);
        Item held;
        if(this.handAnimateTimer > 0.0F) {
            this.handAnimateTimer -= delta;
            held = this.GetHeldItem();
            if(held != null && held instanceof Weapon) {
                ((Weapon)held).tickAttack(this, level, delta);
            }
        } else {
            held = this.GetHeldItem();
            if(this.handAnimation == null) {
                this.playIdleAnimation(held);
            }

            if(!(held instanceof Weapon) && !(held instanceof Decoration) && !(held instanceof Potion)) {
                if(held instanceof Food) {
                    if(attack) {
                        ((Food)held).Eat(this);
                        this.removeFromInventory(held);
                    }
                } else if(held instanceof Armor && attack) {
                    Armor a = (Armor)held;
                    this.ChangeHeldItem((Integer)null, true);
                    this.equip(a);
                }
            } else {
                boolean attackOnRelease = true;
                if(held instanceof Wand && ((Wand)held).autoFire) {
                    attackOnRelease = false;
                } else if(held instanceof Gun) {
                    attackOnRelease = false;
                    if(!attack) {
                        ((Gun)held).resetTrigger();
                    }
                }

                if(!attack && this.attackCharge > 0.0F && !this.hasAttacked) {
                    if(attackOnRelease) {
                        this.Attack(level);
                    } else {
                        this.hasAttacked = true;
                        this.attackCharge = 0.0F;
                        if(held instanceof Weapon && !(held instanceof Gun)) {
                            this.playAttackAnimation((Weapon)held, 1.0F);
                        }
                    }
                } else if(attack && this.attackCharge < this.attackChargeTime) {
                    if(held instanceof Gun) {
                        Gun g = (Gun)held;
                        this.attackCharge = 0.001F;
                        if(g.canFire()) {
                            g.doAttack(this, level, 1.0F);
                        }
                    } else if(held instanceof Wand) {
                        Wand w = (Wand)held;
                        if(w.autoFire) {
                            if(this.handAnimation != null) {
                                this.handAnimation.stop();
                            }

                            this.Attack(level);
                        } else if(this.attackCharge <= 0.0F) {
                            this.playChargeAnimation(this.attackChargeSpeed);
                        }
                    } else if(this.attackCharge <= 0.0F) {
                        this.playChargeAnimation(this.attackChargeSpeed);
                    }

                    this.attackCharge += this.attackChargeSpeed * delta;
                }
            }
        }

        if(!this.isDead && !isInOverlay) {
            if(input.doUseAction() || controllerState.buttonEvents.contains(Buttons.USE, true)) {
                this.Use(level);
            }

            if(input.keyEvents.contains(8)) {
                this.DoHotbarAction(1);
            }

            if(input.keyEvents.contains(9)) {
                this.DoHotbarAction(2);
            }

            if(input.keyEvents.contains(10)) {
                this.DoHotbarAction(3);
            }

            if(input.keyEvents.contains(11)) {
                this.DoHotbarAction(4);
            }

            if(input.keyEvents.contains(12)) {
                this.DoHotbarAction(5);
            }

            if(input.keyEvents.contains(Input.Keys.U)) {
                OverlayManager.instance.push(new CurrencyOverlay(this));
            }

            if(input.keyEvents.contains(13)) {
                this.DoHotbarAction(6);
            }

            if(input.doDropAction()) {
                Audio.playSound("inventory/drop_item.mp3", 0.7F, Game.rand.nextFloat() * 0.1F + 0.95F);
                this.dropItem(this.selectedBarItem, level, 0.2F);
            }

            if(Game.hotbar.gamepadPosition != null && controllerState.buttonEvents.contains(Buttons.HOTBAR_USE, true)) {
                this.DoHotbarAction(Game.hotbar.gamepadPosition.intValue() + 1);
                Game.hotbar.gamepadPosition = null;
            }

            if(controllerState.buttonEvents.contains(Buttons.DROP, true)) {
                if(Game.hotbar.gamepadPosition != null) {
                    Audio.playSound("inventory/drop_item.mp3", 0.7F, Game.rand.nextFloat() * 0.1F + 0.95F);
                    this.dropItem(Game.hotbar.gamepadPosition, level, 0.2F);
                    Game.hotbar.gamepadPosition = null;
                } else {
                    Audio.playSound("inventory/drop_item.mp3", 0.7F, Game.rand.nextFloat() * 0.1F + 0.95F);
                    this.dropItem(this.selectedBarItem, level, 0.2F);
                }
            }

            if(Game.isDebugMode) {
                if(input.keyEvents.contains(Input.Keys.K)) {
                    OverlayManager.instance.push(new DebugOverlay(this));
                } else if(input.keyEvents.contains(Input.Keys.NUMPAD_4)) {
                    Game.instance.level.up.changeLevel(level);
                } else if(input.keyEvents.contains(Input.Keys.NUMPAD_6)) {
                    Game.instance.level.down.changeLevel(level);
                }
            }

            if(controllerState.buttonEvents.contains(Buttons.HOTBAR_RIGHT, true)) {
                if(Game.hotbar.gamepadPosition == null) {
                    Game.hotbar.gamepadPosition = Integer.valueOf(0);
                } else {
                    Game.hotbar.gamepadPosition = Integer.valueOf((Game.hotbar.gamepadPosition.intValue() + 1) % 6);
                }
            } else if(controllerState.buttonEvents.contains(Buttons.HOTBAR_LEFT, true)) {
                if(Game.hotbar.gamepadPosition == null) {
                    Game.hotbar.gamepadPosition = Integer.valueOf(5);
                } else {
                    Hotbar var37 = Game.hotbar;
                    var37.gamepadPosition = Integer.valueOf(var37.gamepadPosition.intValue() - 1);
                    if(Game.hotbar.gamepadPosition.intValue() < 0) {
                        Game.hotbar.gamepadPosition = Integer.valueOf(6 - Game.hotbar.gamepadPosition.intValue() - 2);
                    }
                }
            }

            if(input.doInventoryAction()) {
                Audio.playSound("inventory/open_inventory.mp3", 0.5F, 1.0F);
                Game.instance.toggleInventory();
                if(Game.instance.getShowingInventory()) {
                    GameManager.renderer.showMap = false;
                }
            }

            if(input.doNextItemAction()) {
                this.wieldNextHotbarItem();
            }

            if(input.doPreviousItemAction()) {
                this.wieldPreviousHotbarItem();
            }

            if(input.doMapAction()) {
                if(OverlayManager.instance.current() == null) {
                    OverlayManager.instance.push(new MapOverlay());
                } else {
                    OverlayManager.instance.clear();
                }

                if(GameManager.renderer.showMap && Game.instance.getShowingInventory()) {
                    Game.instance.toggleInventory();
                }

                if(GameManager.renderer.showMap) {
                    Audio.playSound("/ui/ui_map_open.mp3", 0.3F);
                } else {
                    Audio.playSound("/ui/ui_map_close.mp3", 0.3F);
                }
            }

            if(input.doBackAction()) {
                if(Game.instance.getShowingInventory()) {
                    Game.instance.toggleInventory();
                } else if(Game.instance.getInteractMode()) {
                    Game.instance.toggleInteractMode();
                }
            }
        }

        this.updatePlayerLight(level, delta);
        this.tickAttached(level, delta);
    }

    private void updatePlayerLight(Level level, float delta) {
        this.torchColor.set(1.0F, 0.8F, 0.4F, 1.0F);
        Item primaryHeld = this.GetHeldItem();
        if(primaryHeld != null) {
            primaryHeld.tickEquipped(this, level, delta, "PRIMARY");
        }

        Item offhandItem = (Item)this.equippedItems.get("OFFHAND");
        if(offhandItem != null && !this.isHoldingTwoHanded()) {
            offhandItem.tickEquipped(this, level, delta, "OFFHAND");
        }

        DynamicLight light = GlRenderer.getLight();
        if(light != null) {
            light.color.set(this.torchColor.r, this.torchColor.g, this.torchColor.b);
            light.position.set(this.x + 0.5F, this.z + this.getStepUpValue(), this.y + 0.5F);
            GlRenderer.playerLightColor.set(this.torchColor.r, this.torchColor.g, this.torchColor.b);
        }

        this.holdingTwoHanded = primaryHeld != null && primaryHeld instanceof Weapon && ((Weapon)primaryHeld).twoHanded;
    }

    private void playIdleAnimation(Item held) {
        if(this.handAnimation == null && held instanceof Weapon) {
            Weapon w = (Weapon)held;
            this.handAnimation = Game.animationManager.getAnimation(w.chargeAnimation);
            if(this.handAnimation == null) {
                this.handAnimation = Game.animationManager.getAnimation(w.attackAnimation);
            }

            if(this.handAnimation != null) {
                this.handAnimation.play(0.0F);
                this.handAnimation.stop();
            }
        }

    }

    private void playChargeAnimation(float animationSpeed) {
        LerpedAnimation previousAnimation = this.handAnimation;
        Item w = this.GetHeldItem();
        if(w instanceof Weapon) {
            Weapon weapon = (Weapon)w;
            this.handAnimation = Game.animationManager.getAnimation(weapon.chargeAnimation);
            if(this.handAnimation != null) {
                this.handAnimation.play(animationSpeed * 0.03F, previousAnimation);
            }
        } else if(w instanceof Decoration || w instanceof Potion) {
            this.handAnimation = Game.animationManager.decorationCharge;
            if(this.handAnimation != null) {
                this.handAnimation.play(animationSpeed * 0.03F, previousAnimation);
            }
        }

        if(this.handAnimation == null) {
            this.playIdleAnimation(w);
        }

    }

    private Item pickItem(Level level, int pickX, int pickY, float maxDistance) {
        Entity picked = this.pickEntity(level, pickX, pickY, maxDistance);
        return picked != null && picked instanceof Item?(Item)picked:null;
    }

    private Entity pickEntity(Level level, int pickX, int pickY, float maxDistance) {
        if(Game.camera == null) {
            return null;
        } else {
            Vector3 levelIntersection = this.tempVec1.set(Vector3.Zero);
            Vector3 intersection = this.tempVec2.set(Vector3.Zero);
            Vector3 testPos = this.tempVec3.set(Vector3.Zero);
            Ray ray = Game.camera.getPickRay((float)pickX, (float)pickY);
            boolean hitLevel = Collidor.intersectRayTriangles(ray, GameManager.renderer.GetCollisionTrianglesAlong(ray, maxDistance), levelIntersection, (Vector3)null);
            float worldHitDistance = this.tempVec4.set(ray.origin).sub(levelIntersection).len();
            Array<Entity> toCheck = level.spatialhash.getEntitiesAt(this.x, this.y, maxDistance);

            for(int i = 0; i < toCheck.size; ++i) {
                Entity e = (Entity)toCheck.get(i);
                if(!(e instanceof Sprite) && !(e instanceof Light) && (double)Math.abs(e.x - this.x - 0.5F) < (double)maxDistance * 1.5D && (double)Math.abs(e.y - this.y - 0.5F) < (double)maxDistance * 1.5D) {
                    testPos.x = e.x;
                    testPos.z = e.y;
                    testPos.y = e.z;
                    float colSizeMod = 0.0F;
                    if(e instanceof Item) {
                        testPos.y = e.z - 0.34F;
                        colSizeMod = 0.2F;
                    } else if(e instanceof Stairs) {
                        colSizeMod = 0.6F;
                    }

                    Vector3 end;
                    if(!(e instanceof Item) && !(e instanceof Stairs)) {
                        BoundingBox b = CachePools.getAABB(e);
                        if(Intersector.intersectRayBounds(ray, b, intersection)) {
                            end = pickEntityTemp1.set(ray.origin);
                            end = pickEntityTemp2.set(intersection);
                            float distance = end.sub(end).len();
                            if(distance < maxDistance + colSizeMod && (!hitLevel || distance < worldHitDistance * 1.1F)) {
                                this.pickList.add(e);
                            }
                        }
                    } else if(Intersector.intersectRaySphere(ray, testPos, e.collision.x / 1.5F + colSizeMod, intersection)) {
                        Vector3 start = pickEntityTemp1.set(ray.origin);
                        end = pickEntityTemp2.set(intersection);
                        float distance = start.sub(end).len();
                        if(distance < maxDistance && (!hitLevel || distance < worldHitDistance)) {
                            this.pickList.add(e);
                        }
                    }
                }
            }

            Entity found = null;

            for(int i = 0; i < this.pickList.size; ++i) {
                Entity e = (Entity)this.pickList.get(i);
                if(found == null || e instanceof Item) {
                    found = e;
                }
            }

            this.pickList.clear();
            return found;
        }
    }

    public void Use(Level level) {
        if(Game.isMobile) {
            Array<Entity> entities = Game.instance.level.entities;

            for(int i = 0; i < entities.size; ++i) {
                Entity e = (Entity)entities.get(i);
                if(!(e instanceof Item) && !(e instanceof Stairs)) {
                    if(e instanceof Door) {
                        if(Math.abs(this.x + 0.5F - e.x) < 1.0F && Math.abs(this.y + 0.5F - e.y) < 1.0F && Math.abs(this.z - e.z) < 1.0F) {
                            e.use(this, 0.0F, 0.0F);
                            Game.ShowMessage("", 1.0F);
                            return;
                        }
                    } else if(e instanceof Trigger) {
                        Trigger trigger = (Trigger)e;
                        if(trigger.triggerType == TriggerType.USE && Math.abs(this.x + 0.5F - e.x) < 0.8F && Math.abs(this.y + 0.5F - e.y) < 0.8F) {
                            e.use(this, 0.0F, 0.0F);
                            Game.ShowMessage("", 1.0F);
                            return;
                        }
                    } else if(e instanceof ButtonModel) {
                        ButtonModel trigger = (ButtonModel)e;
                        if(Math.abs(this.x + 0.5F - e.x) < 0.8F && Math.abs(this.y + 0.5F - e.y) < 0.8F) {
                            e.use(this, 0.0F, 0.0F);
                            Game.ShowMessage("", 1.0F);
                            return;
                        }
                    }
                } else if(Math.abs(this.x + 0.5F - e.x) < 0.5F && Math.abs(this.y + 0.5F - e.y) < 0.5F && Math.abs(this.z - e.z) < 1.0F) {
                    e.use(this, 0.0F, 0.0F);
                    Game.ShowMessage("", 1.0F);
                    return;
                }
            }
        }

        float usedist = 0.95F;
        Entity near = null;
        Tile hit = null;
        Entity centered = this.pickEntity(level, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0.7F);
        float dstep;
        if(centered != null) {
            float projx = (0.0F * (float)Math.cos((double)this.rot) + (float)Math.sin((double)this.rot)) * 1.0F;
            dstep = ((float)Math.cos((double)this.rot) - 0.0F * (float)Math.sin((double)this.rot)) * 1.0F;
            centered.use(this, projx, dstep);
        } else {
            for(int i = 1; i < 10 && near == null && hit == null; ++i) {
                dstep = (float)i / 6.0F * usedist;
                float projx = Game.camera.direction.x * dstep;
                float projy = Game.camera.direction.z * dstep;
                int checkx = (int)Math.floor((double)(this.x + 0.5F + projx));
                int checky = (int)Math.floor((double)(this.y + 0.5F + projy));
                hit = level.getTile(checkx, checky);
                if(hit != null && !hit.blockMotion) {
                    hit = null;
                }
            }

            if(hit != null) {
                hit.use();
            }
        }
    }

    private void Use(Level level, int touchX, int touchY) {
        float usedist = 0.95F;
        Entity near = null;
        Tile hit = null;
        Entity centered = this.pickEntity(level, touchX, touchY, 0.9F);
        if(centered != null && !(centered instanceof Stairs)) {
            float projx = (0.0F * (float)Math.cos((double)this.rot) + (float)Math.sin((double)this.rot)) * 1.0F;
            float projy = ((float)Math.cos((double)this.rot) - 0.0F * (float)Math.sin((double)this.rot)) * 1.0F;
            centered.use(this, projx, projy);
        } else {
            Ray ray = Game.camera.getPickRay((float)touchX, (float)touchY);

            for(int i = 1; i < 10 && near == null && hit == null; ++i) {
                float dstep = (float)i / 6.0F * usedist;
                float projx = ray.direction.x * dstep;
                float projy = ray.direction.z * dstep;
                int checkx = (int)Math.floor((double)(ray.origin.x + projx));
                int checky = (int)Math.floor((double)(ray.origin.z + projy));
                hit = level.getTile(checkx, checky);
                if(hit != null && !hit.blockMotion) {
                    hit = null;
                }
            }

            if(hit != null) {
                hit.use();
            }

        }
    }

    public void playAttackAnimation(Weapon w, float attackPower) {
        this.playAttackAnimation(w, attackPower, w.getSpeed() * 0.25F + (float)(this.stats.DEX - 4) * 0.015F);
    }

    public void playAttackAnimation(Weapon w, float attackPower, float speed) {
        LerpedAnimation previousAnimation = null;
        if(this.handAnimation != null) {
            previousAnimation = this.handAnimation;
        }

        if(attackPower >= 0.5F && w.attackStrongAnimation != null) {
            this.handAnimation = Game.animationManager.getAnimation(w.attackStrongAnimation);
        } else {
            this.handAnimation = Game.animationManager.getAnimation(w.attackAnimation);
        }

        if(this.handAnimation != null && (previousAnimation != this.handAnimation || !this.handAnimation.playing)) {
            if(previousAnimation != null) {
                previousAnimation.stop();
            }

            this.handAnimation.play(speed, previousAnimation);
        }

    }

    private void Attack(Level lvl) {
        this.hasAttacked = true;
        float attackPower = this.attackCharge / this.attackChargeTime;
        this.attackCharge = 0.0F;
        Item held = this.GetHeldItem();
        if(held != null) {
            if(held instanceof Weapon) {
                Weapon w = (Weapon)held;
                this.playAttackAnimation(w, attackPower);
                w.doAttack(this, lvl, attackPower);
            } else {
                this.dropItem(held, lvl, attackPower);
                this.heldItem = null;
                held.xa = Game.camera.direction.x * attackPower * 0.3F;
                held.ya = Game.camera.direction.z * attackPower * 0.3F;
                held.za = Game.camera.direction.y * attackPower * 0.3F;
                held.za = (float)((double)held.za + (double)attackPower * 0.05D);
                if(held instanceof Potion) {
                    ((Potion)held).activateExplosion(false);
                }
            }

        }
    }

    public Item dropItem(Integer invLocation, Level level, float throwPower) {
        if(invLocation != null && invLocation.intValue() >= 0 && invLocation.intValue() < this.inventory.size) {
            Item itm = (Item)this.inventory.get(invLocation.intValue());
            this.dropItem(itm, level, throwPower);
            if(invLocation == this.selectedBarItem) {
                this.selectedBarItem = null;
            }

            return itm;
        } else {
            return null;
        }
    }

    public void throwItem(Item itm, Level level, float throwPower, float xOffset) {
        float projx = (0.0F * (float)Math.cos((double)this.rot) + 1.0F * (float)Math.sin((double)this.rot)) * 1.0F;
        float projy = (1.0F * (float)Math.cos((double)this.rot) - 0.0F * (float)Math.sin((double)this.rot)) * 1.0F;
        itm.isActive = true;
        itm.isDynamic = true;
        itm.x = this.x + 0.5F + projx * 0.1F;
        itm.y = this.y + 0.5F + projy * 0.1F;
        itm.z = this.z + 0.5F;
        itm.xa = projx * throwPower * 0.3F;
        itm.ya = projy * throwPower * 0.3F;
        itm.za = throwPower * 0.05F;
        itm.ignorePlayerCollision = true;
        level.SpawnEntity(itm);
        itm.x = this.x + 0.5F + projx * 0.25F;
        itm.y = this.y + 0.5F + projy * 0.25F;
        float x_projx = (0.0F * (float)Math.cos((double)(this.rot + 1.8F)) + 1.0F * (float)Math.sin((double)(this.rot + 1.8F))) * 1.0F;
        float y_projy = (1.0F * (float)Math.cos((double)(this.rot + 1.8F)) - 0.0F * (float)Math.sin((double)(this.rot + 1.8F))) * 1.0F;
        itm.xa -= x_projx * xOffset;
        itm.ya -= y_projy * xOffset;
    }

    public void dropItemFromInv(Integer invLocation, Level level, float throwPower, float xOffset) {
        Item itm = this.dropItem(invLocation, level, throwPower);
        if(itm != null) {
            float projx = (0.0F * (float)Math.cos((double)this.rot) + 1.0F * (float)Math.sin((double)this.rot)) * 1.0F;
            float projy = (1.0F * (float)Math.cos((double)this.rot) - 0.0F * (float)Math.sin((double)this.rot)) * 1.0F;
            itm.x = this.x + 0.5F + projx * 0.0F;
            itm.y = this.y + 0.5F + projy * 0.0F;
            float x_projx = (0.0F * (float)Math.cos((double)this.rot + 1.8D) + 1.0F * (float)Math.sin((double)this.rot + 1.8D)) * 1.0F;
            float y_projy = (1.0F * (float)Math.cos((double)this.rot + 1.8D) - 0.0F * (float)Math.sin((double)this.rot + 1.8D)) * 1.0F;
            itm.xa -= x_projx * xOffset;
            itm.ya -= y_projy * xOffset;
            itm.ignorePlayerCollision = true;
        }
    }

    public void dropItem(Item itm, Level level, float throwPower) {
        if(itm != null) {
            float projx = (0.0F * (float)Math.cos((double)this.rot) + 1.0F * (float)Math.sin((double)this.rot)) * 1.0F;
            float projy = (1.0F * (float)Math.cos((double)this.rot) - 0.0F * (float)Math.sin((double)this.rot)) * 1.0F;
            itm.isActive = true;
            itm.x = this.x + 0.5F + projx * 0.0F;
            itm.y = this.y + 0.5F + projy * 0.0F;
            itm.z = this.z + 0.36F;
            itm.xa = projx * throwPower * 0.3F;
            itm.ya = projy * throwPower * 0.3F;
            itm.za = throwPower * 0.05F;
            itm.ignorePlayerCollision = true;
            level.SpawnEntity(itm);
            itm.isDynamic = true;
            this.removeFromInventory(itm);
        }
    }

    private void splash(Level level, float splashZ, Tile tile) {
        if(this.tickcount - this.lastSplashTime >= 30.0F) {
            this.lastSplashTime = this.tickcount;
            Random r = new Random();
            int particleCount = 19;
            particleCount = (int)((float)particleCount * Options.instance.gfxQuality);

            for(int i = 0; i < particleCount; ++i) {
                Particle p = CachePools.getParticle(this.x + 0.5F, this.y + 0.5F, splashZ, r.nextFloat() * 0.04F - 0.02F, r.nextFloat() * 0.04F - 0.02F, r.nextFloat() * 0.01F + 0.015F, tile.data.particleTex, tile.data.particleColor, tile.data.particleFullbrite);
                p.endScale = 0.1F;
                level.SpawnNonCollidingEntity(p);
            }

            int amt = 6;
            float rotAmount = 6.28318F / (float)amt;
            float rot = Game.rand.nextFloat();

            for(int i = 0; i < amt; ++i) {
                Particle p = CachePools.getParticle(this.x + 0.5F, this.y + 0.5F, splashZ, 0.0F, 0.0F, 0.0F, 18, tile.data.particleColor, tile.data.particleFullbrite);
                p.x += 0.125F * Game.rand.nextFloat() - 0.0625F;
                p.y += 0.125F * Game.rand.nextFloat() - 0.0625F;
                p.checkCollision = false;
                p.floating = true;
                p.lifetime = (float)((int)(24.0F * Game.rand.nextFloat()) + 40);
                p.playAnimation(49, 51, (float)(20 + r.nextInt(10)));
                p.scale = 1.0F;
                p.xa = (0.0F * (float)Math.cos((double)rot) + 1.0F * (float)Math.sin((double)rot)) * 0.075F;
                p.ya = (1.0F * (float)Math.cos((double)rot) - 0.0F * (float)Math.sin((double)rot)) * 0.075F;
                p.xa += this.xa;
                p.ya += this.ya;
                p.x += p.xa * 0.2F;
                p.y += p.ya * 0.2F;
                p.maxVelocity = 1.0E-4F;
                p.dampenAmount = 0.85F;
                level.SpawnNonCollidingEntity(p);
                rot += rotAmount;
            }

            Audio.playSound("splash2.mp3", 0.35F);
        }
    }

    public void ChangeHeldItem(Integer invPos, boolean doTransition) {
        if(this.handAnimation == null || !this.handAnimation.playing) {
            this.selectedBarItem = invPos;
            if(doTransition) {
                this.doingHeldItemTransition = true;
                this.heldItemTransitionEnd = 16.0F;
                this.heldItemTransition = 0.0F;
            } else {
                this.doingHeldItemTransition = false;
            }

            this.handAnimation = null;
            this.heldItem = invPos;
            Item held = this.GetHeldItem();
            if(doTransition && held != null && held.equipSound != null) {
                Audio.playSound(held.equipSound, 0.1F, Game.rand.nextFloat() * 0.1F + 0.95F);
            }
        }

    }

    public Boolean isEquipped(Item item) {
        return item == this.GetHeldItem()?Boolean.valueOf(true):(this.equippedItems.containsValue(item)?Boolean.valueOf(true):Boolean.valueOf(false));
    }

    public Boolean isHeld(Item item) {
        Item held = this.GetHeldItem();
        return Boolean.valueOf(item == held);
    }

    public boolean addToInventory(Item item) {
        return this.addToInventory(item, true);
    }

    public boolean addToInventory(Item item, boolean autoEquip) {
        if(item instanceof Key) {
            ++this.keys;
            return true;
        } else if(this.inventory.contains(item, true)) {
            return false;
        } else {
            if(this.inventory.size < 24) {
                this.inventory.add(item);
            } else {
                boolean foundSpot = false;

                for(int i = 0; i < 24 && !foundSpot; ++i) {
                    if(this.inventory.get(i) == null) {
                        this.inventory.set(i, item);
                        foundSpot = true;
                    }
                }

                if(!foundSpot) {
                    return false;
                }
            }

            if((item instanceof Weapon || item instanceof Decoration) && this.heldItem == null && autoEquip) {
                this.equip(item);
            } else if(item instanceof QuestItem) {
                ((QuestItem)item).doQuestThing();
            }

            Game.RefreshUI();
            return true;
        }
    }

    public void removeFromInventory(Item item) {
        if(this.inventory.contains(item, true)) {
            this.dequip(item);
            Item held = this.GetHeldItem();
            int pos = this.inventory.indexOf(item, true);
            this.inventory.set(pos, (Item) null);
            if(held != null) {
                this.heldItem = Integer.valueOf(this.inventory.indexOf(held, true));
            }

            Game.RefreshUI();
        }
    }

    public void equip(Item item) {
        this.equip(item, true);
    }

    public void equip(Item item, boolean doTransition) {
        int itempos = this.inventory.indexOf(item, true);
        if(this.selectedBarItem != null && this.selectedBarItem.intValue() == itempos) {
            this.selectedBarItem = null;
        }

        if(!(item instanceof Weapon) && !(item instanceof Decoration) && !(item instanceof Potion) && !(item instanceof Food)) {
            if(item instanceof Armor) {
                this.equipArmor(item, true);
            }
        } else {
            this.ChangeHeldItem(Integer.valueOf(itempos), doTransition);
        }

    }

    public void equipArmor(Item item, boolean playSound) {
        this.equipArmor(item);
        if(playSound) {
            Audio.playSound("ui/ui_equip_armor.mp3", 0.4F);
        }

    }

    public void equipArmor(Item item) {
        int swappos;
        if(this.equippedItems.containsKey(item.equipLoc)) {
            swappos = this.inventory.indexOf(item, true);
            if(swappos >= 0) {
                this.inventory.set(swappos, this.equippedItems.get(item.equipLoc));
            }
        }

        this.equippedItems.put(item.equipLoc, item);
        swappos = this.inventory.indexOf(item, true);
        if(swappos >= 0) {
            this.inventory.set(this.inventory.indexOf(item, true), (Item) null);
        }

        Game.hotbar.refresh();
        Game.bag.refresh();
        Game.hud.refreshEquipLocations();
    }

    public void wieldNextHotbarItem() {
        int i = this.selectedBarItem == null?0:(this.selectedBarItem.intValue() + 1) % 6;
        this.ChangeHeldItem(Integer.valueOf(i), true);
    }

    public void wieldPreviousHotbarItem() {
        int i = this.selectedBarItem == null?5:(this.selectedBarItem.intValue() + 5) % 6;
        this.ChangeHeldItem(Integer.valueOf(i), true);
    }

    public void dequip(Item item) {
        if(this.equipped(item)) {
            int itempos = this.inventory.indexOf(item, true);
            if(this.selectedBarItem != null && this.selectedBarItem.intValue() == itempos) {
                this.selectedBarItem = null;
            }

            if(item instanceof Weapon || item instanceof Decoration || item instanceof Potion || item instanceof Food) {
                this.heldItem = null;
            }

        }
    }

    public boolean equipped(Item item) {
        return (item instanceof Weapon || item instanceof Decoration || item instanceof Potion || item instanceof Food) && this.GetHeldItem() == item?true:this.equippedItems.containsValue(item);
    }

    public Item GetHeldItem() {
        return this.heldItem != null && this.heldItem.intValue() >= 0 && this.heldItem.intValue() < this.inventory.size?(Item)this.inventory.get(this.heldItem.intValue()):null;
    }

    public Item GetHeldOffhandItem() {
        return (Item)this.equippedItems.get("OFFHAND");
    }

    public Armor GetEquippedArmor() {
        Item itm = (Item)this.equippedItems.get("ARMOR");
        return itm instanceof Armor?(Armor)itm:null;
    }

    public Item GetEquippedRing() {
        return (Item)this.equippedItems.get("RING");
    }

    public Item GetEquippedAmulet() {
        return (Item)this.equippedItems.get("AMULET");
    }

    public void DoHotbarAction(int location) {
        --location;
        if(location >= 0 && location < this.inventory.size) {
            Item itm = (Item)this.inventory.get(location);
            if(itm != null) {
                if(this.selectedBarItem != null && this.selectedBarItem.intValue() == location) {
                    if(this.equipped(itm) && (this.handAnimation == null || !this.handAnimation.playing)) {
                        this.dequip(itm);
                    } else {
                        this.ChangeHeldItem((Integer)null, true);
                    }
                } else {
                    if(this.equipped(itm)) {
                        this.dequip(itm);
                        return;
                    }

                    if(itm.inventoryUse(this)) {
                        return;
                    }

                    this.ChangeHeldItem(Integer.valueOf(location), true);
                }
            }

        }
    }

    public String GetAttackText() {
        Item held = this.GetHeldItem();
        if(held != null) {
            if(held instanceof Weapon) {
                Weapon w = (Weapon)held;
                int randDamage = w.getRandDamage();
                int baseDamage = w.getBaseDamage() + w.getElementalDamage() + this.getDamageStatBoost();
                if(randDamage == 0) {
                    return Integer.toString(baseDamage);
                }

                return MessageFormat.format(StringManager.get("entities.Player.weaponAttackText"), new Object[]{Integer.valueOf(baseDamage), Integer.valueOf(randDamage + baseDamage)});
            }

            if(held instanceof Potion) {
                return String.format("%.0f", new Object[]{Float.valueOf(((Potion)held).getExplosionDamageAmount())});
            }

            if(held instanceof Decoration) {
                return "1";
            }
        }

        return "0";
    }

    public void addExperience(int e) {
        this.exp += e;
        if(!this.isDead && this.exp >= this.getNextLevel() && this.canLevelUp) {
            ++this.level;
            this.hp = this.getMaxHp();
            OverlayManager.instance.push(new LevelUpOverlay(this));
        }

    }

    public void hitEffect(Level level, DamageType inDamageType) {
        Random r = new Random();
        int particleCount = 8;
        particleCount = (int)((float)particleCount * Options.instance.gfxQuality);

        for(int i = 0; i < particleCount; ++i) {
            level.SpawnNonCollidingEntity(CachePools.getParticle(this.x + 0.5F, this.y + 0.5F, this.z + 0.6F, r.nextFloat() * 0.02F - 0.01F, r.nextFloat() * 0.02F - 0.01F, r.nextFloat() * 0.02F - 0.01F, (float)(220 + r.nextInt(500)), 1.0F, 0.0F, Actor.getBloodTexture(this.bloodType), Actor.getBloodColor(this.bloodType), false));
        }

        this.shake(2.0F);
    }

    public void attackButtonTouched() {
        this.attackButtonWasPressed = true;
    }

    public void hit(float projx, float projy, int damage, float knockback, DamageType damageType) {
        this.takeDamage(damage, damageType);
        if(damageType == DamageType.POISON) {
            this.addStatusEffect(new PoisonEffect(1000, 160, 1, false));
            this.history.poisoned();
        } else if(damageType == DamageType.FIRE) {
            if(Game.rand.nextBoolean()) {
                this.addStatusEffect(new BurningEffect(1000, 160, 1));
            }
        } else if(damageType == DamageType.ICE) {
            this.addStatusEffect(new SlowEffect(0.5F, 200));
        } else if(damageType == DamageType.PARALYZE) {
            this.addStatusEffect(new SlowEffect(0.1F, 200));
        }

        this.shake((float)damage / (float)this.getMaxHp() * 5.0F);
    }

    public void takeDamage(int damage, DamageType damageType) {
        if(!this.isDead) {
            if(this.statusEffects != null && this.statusEffects.size > 0) {
                Iterator var3 = this.statusEffects.iterator();

                while(var3.hasNext()) {
                    StatusEffect s = (StatusEffect)var3.next();
                    if(s.active) {
                        if(damageType == DamageType.PHYSICAL) {
                            damage = (int)((float)damage * s.damageMod);
                        } else {
                            damage = (int)((float)damage * s.magicDamageMod);
                        }
                    }
                }
            }

            if(damageType == DamageType.HEALING) {
                damage = (int)((float)damage * -1.0F);
            }

            if(damage < 0) {
                Game.flash(Colors.HEAL_FLASH, 20);
            } else {
                Game.flash(Colors.HURT_FLASH, 20);
            }

            this.hp -= damage;
            if(this.hp > this.getMaxHp()) {
                this.hp = this.getMaxHp();
            }

            this.history.tookDamage(damage);
        }
    }

    public float getAttackSpeed() {
        return this.attackSpeed + (float)this.stats.DEX * 0.016F;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public void updateMouseInput(GameInput input) {
        if(!this.touchingItem && !Game.isMobile && input.caughtCursor) {
            this.rot = (float)((double)this.rot - (double)((float)Gdx.input.getDeltaX()) / 230.0D * (double)Options.instance.mouseXSensitivity);
            float deltaY = (float)Gdx.input.getDeltaY();
            if(Options.instance.mouseInvert) {
                deltaY *= -1.0F;
            }

            this.yrot = (float)((double)this.yrot - (double)deltaY / 230.0D * (double)Options.instance.mouseYSensitivity);
            if((double)this.yrot > 1.3D) {
                this.yrot = 1.3F;
            }

            if((double)this.yrot < -1.3D) {
                this.yrot = -1.3F;
            }
        }

    }

    public float getWalkSpeed() {
        float baseSpeed = 0.1F + (float)this.stats.SPD * 0.015F;

        /// Sprinting
        ///
        /// via DMC
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            baseSpeed = sprintModifier + (float)this.stats.SPD * 0.015F;
        }

        if(this.statusEffects != null && this.statusEffects.size > 0) {
            Iterator var2 = this.statusEffects.iterator();

            while(var2.hasNext()) {
                StatusEffect s = (StatusEffect)var2.next();
                if(s.active) {
                    baseSpeed *= s.speedMod;
                }
            }

            return baseSpeed * this.GetEquippedSpeedMod();
        } else {
            return baseSpeed * this.GetEquippedSpeedMod();
        }
    }

    public void setupController() {
        try {
            Game.instance.input.setGamepadManager(Game.gamepadManager);
            controllerState = Game.gamepadManager.controllerState;
        } catch (Exception var2) {
            Gdx.app.log("Delver", var2.getMessage());
        }

    }

    public void setMessageViews(String message, int views) {
        this.messageViews.put(message, Float.valueOf((float)views));
    }

    public int getMessageViews(String message) {
        Float views = (Float)this.messageViews.get(message);
        return views == null?0:Math.round(views.floatValue());
    }

    public int getDamageStatBoost() {
        return Math.max(0, this.stats.ATK - 4 + this.calculatedStats.ATK);
    }

    public int getMagicStatBoost() {
        return Math.max(0, this.stats.MAG - 4 + this.calculatedStats.MAG);
    }

    public int getDefenseStatBoost() {
        return Math.max(0, this.stats.DEF - 4);
    }

    public void shake(float amount) {
        if(!this.isDead) {
            amount = Math.min(20.0F, amount);
            this.screenshakeAmount = Math.max(this.screenshakeAmount, amount);
        }
    }

    public void shake(float amount, float range, Vector3 position) {
        if(!this.isDead) {
            float distance = this.tempVec1.set(this.x, this.y, this.z).sub(position).len();
            float mod = 1.0F - Math.min(distance / range, 1.0F);
            float finalAmount = Math.min(20.0F, amount * mod);
            this.screenshakeAmount = Math.max(this.screenshakeAmount, finalAmount);
        }
    }

    public boolean isHoldingTwoHanded() {
        return this.holdingTwoHanded;
    }

    public int getMaxHp() {
        return this.maxHp + this.calculatedStats.HP;
    }

    public int GetArmorClass() {
        return this.calculatedStats.DEF + this.getDefenseStatBoost();
    }

    public float GetEquippedSpeedMod() {
        float armorInfluence = 0.16F;
        float speedToWeightRatio = ((float)this.stats.SPD + (float)this.calculatedStats.SPD * armorInfluence) / (float)this.stats.SPD;
        return Math.max(speedToWeightRatio, this.minWalkSpeed);
    }

    public void makeFallingDustEffect() {
        for(int i = 0; i < 6; ++i) {
            Particle p = CachePools.getParticle(this.x + this.xa + 0.5F, this.y + this.ya + 0.5F, this.z - this.za - 0.4F, 0.0F, 0.0F, 0.0F, 18, Color.WHITE, false);
            float offset = 0.25F;
            p.x += offset * Game.rand.nextFloat() - offset * 0.5F;
            p.y += offset * Game.rand.nextFloat() - offset * 0.5F;
            p.checkCollision = false;
            p.floating = true;
            p.lifetime = (float)((int)(15.0F * Game.rand.nextFloat()) + 22);
            p.playAnimation(18, 23, p.lifetime);
            p.startScale = 1.0F + (0.5F * Game.rand.nextFloat() - 0.25F);
            p.endScale = 1.0F + (0.5F * Game.rand.nextFloat() - 0.25F);
            p.scale = 0.5F;
            offset = 0.01F;
            p.xa = offset * Game.rand.nextFloat() - offset * 0.5F + this.xa * 0.7F;
            p.ya = offset * Game.rand.nextFloat() - offset * 0.5F + this.ya * 0.7F;
            p.za = 0.00125F * Game.rand.nextFloat() + 0.0025F;
            Game.GetLevel().SpawnNonCollidingEntity(p);
        }

    }

    public void attach(Entity toAttach) {
        toAttach.x += 0.5F;
        toAttach.y += 0.5F;
        super.attach(toAttach);
    }

    protected void UseArmor() {
        Iterator var1 = this.equippedItems.values().iterator();

        while(var1.hasNext()) {
            Item e = (Item)var1.next();
            if(e instanceof Armor && ((Armor)e).armor > 0) {
                ((Armor)e).wasUsed();
            }
        }

    }
}
