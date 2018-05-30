package com.ever.ending.game.hero.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.gameobject.GameSprite;
import com.ever.ending.interfaces.control.IController;
import com.ever.ending.interfaces.control.IControllerMouse;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.interfaces.manipulation.IEditable;
import com.ever.ending.interfaces.manipulation.IResizable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.GameConstants;
import com.ever.ending.management.GameFont;
import com.ever.ending.management.input.Controller;
import com.ever.ending.ui.*;

import java.util.*;

public class HeroGameScene extends UIScene {

    private static final HeroPowerBarInfo[] powerTypes = new HeroPowerBarInfo[3];
    private static final HeroPowerBarLayout[] powerBarLayouts = new HeroPowerBarLayout[3];
    private RightSkillBar rightSkillBar;
    private EnemyGroup enemies;
   // private ModNumTextIcon test;

    public HeroGameScene(FrameBuffer mainCanvas) {
        super(mainCanvas);
        this.setEditable(false);
        Controller sceneController = new HeroGameController(this);
        this.setSceneController(sceneController);
        Gdx.input.setInputProcessor(sceneController);

        this.getElements().clear();
        this.getElements().add(rightSkillBar);
        this.getElements().add(enemies);
        for (HeroPowerBarLayout heroPowerBarLayout : powerBarLayouts) {
            this.getElements().add(heroPowerBarLayout);
        }
        //this.getElements().add(test);
    }

    @Override
    public void loadResources() {
        String iconTex = "Hero_game/layout/layout_powerbar_icons.png";
        int fontSizePower = 40;
        powerTypes[0] = new HeroPowerBarInfo(HeroPowerBarInfo.PowerType.TENACITY,GameFont.DEFAULT_FONT,fontSizePower,
                new UIPanel(new Rectangle(),new GameSprite(iconTex,0,0,32,32),this));
        powerTypes[0].setPowerSize(1);
        powerTypes[1] = new HeroPowerBarInfo(HeroPowerBarInfo.PowerType.OFFSENSE,GameFont.DEFAULT_FONT,fontSizePower,
                new UIPanel(new Rectangle(),new GameSprite(iconTex,32,0,32,32),this));
        powerTypes[1].setPowerSize(-30);
        powerTypes[2] = new HeroPowerBarInfo(HeroPowerBarInfo.PowerType.SPECIAL,GameFont.DEFAULT_FONT,fontSizePower,
                new UIPanel(new Rectangle(),new GameSprite(iconTex,64,0,32,32),this));
        powerTypes[2].setPowerSize(60);

        String barTex = "Hero_game/layout/layout_powerbar.png";
        powerBarLayouts[0] = new HeroPowerBarLayout(300,new GameSprite("Tests/UI/panel.png"),
                new UIPanel(new Rectangle(0,0,3,3),new GameSprite(barTex,0,0,3,3),this),
                this,powerTypes[0]);
        powerBarLayouts[1] = new HeroPowerBarLayout(200,new GameSprite("Tests/UI/panel.png"),
                new UIPanel(new Rectangle(0,0,3,3),new GameSprite(barTex,6,0,3,3),this),
                this,powerTypes[1]);
        powerBarLayouts[2] = new HeroPowerBarLayout(100,new GameSprite("Tests/UI/panel.png"),
                new UIPanel(new Rectangle(0,0,3,3),new GameSprite(barTex,3,0,3,3),this),
                this,powerTypes[2]);


        GameSprite tempSprite = new GameSprite("Hero_game/layout/daemarbora.png",0,0,128,128);

//        enemies = new EnemyGroup(new BigEnemyLocation(this,tempSprite));

        try {
            enemies = new EnemyGroup(this,new SmallEnemyLocation(this,tempSprite.clone(),tempSprite.clone(),tempSprite.clone()));
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        rightSkillBar = new RightSkillBar(this);

       // test = new ModNumTextIcon(new GameSprite(iconTex,0,0,32,32),this,"+5");
    }

    float getHighestPowerValue(){
        float highestValue = 30;
        for (HeroPowerBarInfo powerType : powerTypes) {
            if(Math.abs(powerType.getPowerSize()) > highestValue){
                highestValue = Math.abs(powerType.getPowerSize());
            }
        }
        return highestValue;
    }

    HeroPowerBarInfo getPowerInfo(HeroPowerBarInfo.PowerType type){
        switch (type) {
            case TENACITY:
                return powerTypes[0];
            case OFFSENSE:
                return powerTypes[1];
            case SPECIAL:
                return powerTypes[2];
        }
        throw new RuntimeException();
    }

    @Override
    public void update(DeltaTime delta) {
        super.update(delta);
    }

    @Override
    public void draw(DeltaTime delta, SpriteBatch batch) {
        super.draw(delta, batch);
    }
}

@FunctionalInterface
interface AdjustPowerValue{
    public void adjust(HeroPowerBarInfo powerBarInfo, HeroGameScene parentScene );
}

class HeroPowerBarLayout extends UIElement {
    private final static int DEFAULT_BAR_WIDTH = 512;
    private final static int DEFAULT_BAR_HEIGHT = DEFAULT_BAR_WIDTH/2/3;
    private final static Rectangle DEFAULT_BAR_LAYOUT = new Rectangle(GameConstants.TARGET_SCREEN_WIDTH/2 - DEFAULT_BAR_WIDTH/2,0,DEFAULT_BAR_WIDTH,DEFAULT_BAR_HEIGHT);

    private final static Vector2 FILL_BAR_OFFSET = new Vector2(1,1);
    private final static Vector2 ICON_SIZE = new Vector2(64,64);
    private final static float ICON_OFFSET = 5;
    private final static float ICON_TEXT_DISTANCE = 10;

    private HeroPowerBarInfo powerBar;
    private IDrawable innerbar;
    private String lastKnownPower = "";

    public HeroPowerBarLayout() {
        super();
    }

    public HeroPowerBarLayout(float y, IDrawable frame, UIPanel fill , HeroGameScene parentScene, HeroPowerBarInfo powerBar) {
        super(new Rectangle(DEFAULT_BAR_LAYOUT.x,y,DEFAULT_BAR_WIDTH,DEFAULT_BAR_HEIGHT), frame, parentScene);
        this.powerBar = powerBar;
        Rectangle fillBarSize = new Rectangle(DEFAULT_BAR_LAYOUT.x + FILL_BAR_OFFSET.x + DEFAULT_BAR_WIDTH/2,
                y + FILL_BAR_OFFSET.y,
                0,
                DEFAULT_BAR_HEIGHT - 2*FILL_BAR_OFFSET.y);
        innerbar = fill;
        fill.getLocation().set(fillBarSize.x,fillBarSize.y,fillBarSize.width,fillBarSize.height);
    }

    public void generateFillbarSize(float percentage){
        float fillBarSize = (DEFAULT_BAR_WIDTH/2 - 2* FILL_BAR_OFFSET.x)*percentage;
        if(innerbar instanceof IResizable){
            ((IResizable) innerbar).setSize(new Vector2(fillBarSize,((IResizable) innerbar).getSize().y));
        }
    }

    @Override
    public void update(DeltaTime delta) {
        super.update(delta);
        this.powerBar.update(delta);
        if(this.getParentScene() instanceof HeroGameScene){
            this.generateFillbarSize(this.powerBar.getPowerSize() / ((HeroGameScene) this.getParentScene()).getHighestPowerValue());
            this.generateIconLocationSize();
        }
    }

    private void generateIconLocationSize() {
        String powerString = String.valueOf((int)this.powerBar.getPowerSize());
        if(!powerString.equals(lastKnownPower)){
            GlyphLayout temp = new GlyphLayout(this.powerBar.getNumFont().getFont(),powerString);
            float y = this.getLocation().y + ICON_OFFSET + ICON_SIZE.y - (this.getLocation().height )/2 - ICON_SIZE.y / 10;
            float x = this.getPosition().x + this.getLocation().width /2 ;
            float textHeight = temp.height;
            float text_y = (ICON_SIZE.y - textHeight + ICON_SIZE.y*2/3)/2 + y;
            this.powerBar.setTextLoc(new Vector2(x- Math.round(temp.width/10)*10 -ICON_TEXT_DISTANCE/2,text_y));
            powerBar.getPowerIcon().getScreenPos().setSize(ICON_SIZE.x,ICON_SIZE.y);
            powerBar.getPowerIcon().getScreenPos().setPosition(new Vector2(x + ICON_TEXT_DISTANCE/2,y));
            lastKnownPower = powerString;
            temp.reset();
        }
    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        super.draw(delta, bounds, batch);
        innerbar.draw(delta,bounds,batch);
        powerBar.getPowerIcon().draw(delta,powerBar.getPowerIcon().getScreenPos(),batch);
        powerBar.getNumFont().getFont().draw(batch,lastKnownPower,powerBar.getTextLoc().x,powerBar.getTextLoc().y);
        batch.flush();
    }
}

class HeroPowerBarInfo {
    public enum PowerType{
        TENACITY, OFFSENSE, SPECIAL
    }

    private PowerType powerType = PowerType.TENACITY;
    private float powerSize = 0;
    private float targetSize = 0;
    private IDrawable powerIcon = null;
    private GameFont numFont;
    private Vector2 textLoc = Vector2.Zero;

    public Vector2 getTextLoc() {
        return textLoc;
    }

    public void setTextLoc(Vector2 textLoc) {
        this.textLoc = textLoc;
    }

    public HeroPowerBarInfo(PowerType type , String fontLoc, int fontSize, IDrawable icon){
        FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameters.borderColor = Color.WHITE;
        parameters.color = Color.BLACK;
        parameters.shadowOffsetX = 2;
        parameters.shadowOffsetY = 2;
        parameters.shadowColor = Color.LIGHT_GRAY;
        numFont = new GameFont(fontLoc,fontSize,parameters);
        this.powerIcon = icon;
        this.powerType = type;
    }

    public void update(DeltaTime delta){
        if(this.targetSize != powerSize){
            float diff = (this.targetSize - this.powerSize);
            if(Math.abs(diff) <= 0.5){
                this.powerSize = this.targetSize;
            }else if(Math.abs(diff) <= 10){
                float mod = diff > 0 ? 0.3f : -0.3f;
                this.powerSize += mod;
            }else{
                this.powerSize += diff*.02f;
            }
        }
    }

    public PowerType getPowerType() {
        return powerType;
    }

    public float getPowerSize() {
        return powerSize;
    }

    public IDrawable getPowerIcon() {
        return powerIcon;
    }

    public GameFont getNumFont() {
        return numFont;
    }

    public float getTargetSize() {
        return targetSize;
    }

    public void setPowerSize(float powerSize) {
        this.targetSize = powerSize;
    }
}

class EnemyGroup extends UIElement{
    private ArrayList<UIElement> enemyInfos = new ArrayList<>();

    public EnemyGroup(HeroGameScene parent, UIElement ... enemies){
        super(new Rectangle(),null,parent);
        enemyInfos.clear();
        enemyInfos.addAll(Arrays.asList(enemies));
    }


    @Override
    public void update(DeltaTime delta) {
        for (UIElement enemyInfo : enemyInfos) {
            enemyInfo.update(delta);
        }
    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        for (UIElement enemyInfo : enemyInfos) {
            enemyInfo.draw(delta,enemyInfo.getLocation(),batch);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public IDrawable clone() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public Rectangle getScreenPos() {
        return null;
    }
}

class BigEnemyLocation extends UIElement{
    private static final Vector2 SIZE = new Vector2(1000,600);
    private static final float VERTICAL_OFFSET = 50;
    private static final Rectangle LOCATION = new Rectangle((GameConstants.TARGET_SCREEN_WIDTH - SIZE.x)/2,
            GameConstants.TARGET_SCREEN_HEIGHT - VERTICAL_OFFSET - SIZE.y,
            SIZE.x,
            SIZE.y);

    public BigEnemyLocation(){

    }

    public BigEnemyLocation( HeroGameScene parentScene,IDrawable drawable) {
        this(new UIPanel(new Rectangle(),drawable, parentScene),parentScene);
    }

    public BigEnemyLocation(UIPanel drawable, UIScene parentScene) {
        super( new Rectangle(LOCATION), drawable, parentScene);
        drawable.getLocation().set(LOCATION.x,LOCATION.y,LOCATION.width,LOCATION.height);
    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        super.draw(delta, null, batch);
    }
}

class SmallEnemyLocation extends UIElement{

    private static final Vector2 SIZE = new Vector2(500,500);
    private static final float VERTICAL_OFFSET = 50;
    private static final float DISTANCE_BETWEEN = (GameConstants.TARGET_SCREEN_WIDTH - SIZE.x*3) / 4f;
    private static final Rectangle LOCATION1 = new Rectangle(DISTANCE_BETWEEN,
            GameConstants.TARGET_SCREEN_HEIGHT - VERTICAL_OFFSET - SIZE.y,
            SIZE.x,
            SIZE.y);
    private static final Rectangle LOCATION2 = new Rectangle(DISTANCE_BETWEEN*2 + SIZE.x,
            GameConstants.TARGET_SCREEN_HEIGHT - VERTICAL_OFFSET - SIZE.y,
            SIZE.x,
            SIZE.y);
    private static final Rectangle LOCATION3 = new Rectangle(DISTANCE_BETWEEN*3 + SIZE.x *2,
            GameConstants.TARGET_SCREEN_HEIGHT - VERTICAL_OFFSET - SIZE.y,
            SIZE.x,
            SIZE.y);
    private static final Rectangle FRAME = new Rectangle(DISTANCE_BETWEEN,VERTICAL_OFFSET,DISTANCE_BETWEEN*2 + SIZE.x*3,SIZE.y);

    private UIPanel enemy1_panel;
    private UIPanel enemy2_panel;
    private UIPanel enemy3_panel;

    public SmallEnemyLocation(UIScene parentScene, IDrawable tex1) {
        this(parentScene, new UIPanel[] {new UIPanel(new Rectangle(),tex1,parentScene)});
    }

    public SmallEnemyLocation(UIScene parentScene, IDrawable tex1, IDrawable tex2) {
        this(parentScene,  new UIPanel[] {new UIPanel(new Rectangle(),tex2,parentScene),
                new UIPanel(new Rectangle(),tex2,parentScene)});
    }

    public SmallEnemyLocation(UIScene parentScene, IDrawable tex1, IDrawable tex2, IDrawable tex3) {
        this(parentScene,  new UIPanel[] {new UIPanel(new Rectangle(),tex2,parentScene),
                new UIPanel(new Rectangle(),tex2,parentScene),
                new UIPanel(new Rectangle(),tex3,parentScene)});
    }

    public SmallEnemyLocation(UIScene parentScene, UIPanel ... enemyPanels) {
        super(FRAME,null , parentScene);
        if(enemyPanels.length == 3){
            enemy1_panel = enemyPanels[0];
            enemy2_panel = enemyPanels[1];
            enemy3_panel = enemyPanels[2];
        }else         if(enemyPanels.length == 2){
            enemy1_panel = enemyPanels[0];
            enemy3_panel = enemyPanels[1];
        }else         if(enemyPanels.length == 1){
            enemy2_panel = enemyPanels[0];
        }
        if(enemy1_panel != null) {
            enemy1_panel.getLocation().set(LOCATION1.x, LOCATION1.y, LOCATION1.width, LOCATION1.height);
        }
        if(enemy2_panel != null) {
            enemy2_panel.getLocation().set(LOCATION2.x, LOCATION2.y, LOCATION2.width, LOCATION2.height);
        }
        if(enemy3_panel != null) {
            enemy3_panel.getLocation().set(LOCATION3.x, LOCATION3.y, LOCATION3.width, LOCATION3.height);
        }
    }

    @Override
    public void update(DeltaTime delta) {
        super.update(delta);
        if(enemy1_panel != null){
            enemy1_panel.update(delta);
        }
        if(enemy2_panel != null){
            enemy2_panel.update(delta);
        }
        if(enemy3_panel != null){
            enemy3_panel.update(delta);
        }
    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        super.draw(delta, bounds, batch);
        if(enemy1_panel != null){
            enemy1_panel.draw(delta,bounds,batch);
        }
        if(enemy2_panel != null){
            enemy2_panel.draw(delta,bounds,batch);
        }
        if(enemy3_panel != null){
            enemy3_panel.draw(delta,bounds,batch);
        }

    }
}

class RightSkillBar extends UICollection {
    private static final float WIDTH = 512 + 128;
    private static final float HEIGHT = WIDTH * 2 / 3;
    private static final Vector2 OFFSET = new Vector2(30,20);
    private static final Rectangle LOCATION = new Rectangle(
            GameConstants.TARGET_SCREEN_WIDTH - WIDTH - OFFSET.x,
            OFFSET.y,
            WIDTH,
            HEIGHT
    );

    private static final Vector2 BUTTON_PANEL_OFFSET = new Vector2(20,20);
    private static final Vector2 BUTTON_PANEL_SIZE = new Vector2(
            (WIDTH - 4 * BUTTON_PANEL_OFFSET.x)/3,
            (HEIGHT - 3 * BUTTON_PANEL_OFFSET.y)/2);
    private static final Rectangle BUTTON_PANEL1 = new Rectangle(
            BUTTON_PANEL_OFFSET.x, BUTTON_PANEL_OFFSET.y,
            BUTTON_PANEL_SIZE.x, BUTTON_PANEL_SIZE.y
    );
    private static final Rectangle BUTTON_PANEL2 = new Rectangle(
            BUTTON_PANEL_OFFSET.x*2 + BUTTON_PANEL_SIZE.x, BUTTON_PANEL_OFFSET.y,
            BUTTON_PANEL_SIZE.x, BUTTON_PANEL_SIZE.y
    );
    private static final Rectangle BUTTON_PANEL3 = new Rectangle(
            BUTTON_PANEL_OFFSET.x*3 + BUTTON_PANEL_SIZE.x*2, BUTTON_PANEL_OFFSET.y,
            BUTTON_PANEL_SIZE.x, BUTTON_PANEL_SIZE.y
    );
    private static final Rectangle BUTTON_PANEL4 = new Rectangle(
            BUTTON_PANEL_OFFSET.x, BUTTON_PANEL_OFFSET.y*2 + BUTTON_PANEL_SIZE.y,
            BUTTON_PANEL_SIZE.x, BUTTON_PANEL_SIZE.y
    );
    private static final Rectangle BUTTON_PANEL5 = new Rectangle(
            BUTTON_PANEL_OFFSET.x*2 + BUTTON_PANEL_SIZE.x, BUTTON_PANEL_OFFSET.y*2 + BUTTON_PANEL_SIZE.y,
            BUTTON_PANEL_SIZE.x, BUTTON_PANEL_SIZE.y
    );
    private static final Rectangle BUTTON_PANEL6 = new Rectangle(
            BUTTON_PANEL_OFFSET.x*3 + BUTTON_PANEL_SIZE.x*2, BUTTON_PANEL_OFFSET.y*2 + BUTTON_PANEL_SIZE.y,
            BUTTON_PANEL_SIZE.x, BUTTON_PANEL_SIZE.y
    );

    private static final AdjustPowerValue plusThree = (powerBarInfo,heroGameScene) -> {
        powerBarInfo.setPowerSize(powerBarInfo.getTargetSize()+3);
        switch (powerBarInfo.getPowerType()) {
            case TENACITY:
                HeroPowerBarInfo temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.OFFSENSE);
                temp.setPowerSize(temp.getTargetSize()-3);
                break;
            case OFFSENSE:
                temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.SPECIAL);
                temp.setPowerSize(temp.getTargetSize()-3);
                break;
            case SPECIAL:
                temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.TENACITY);
                temp.setPowerSize(temp.getTargetSize()-3);
                break;
        }
    };
    private static final AdjustPowerValue switchVal = (powerBarInfo,heroGameScene) -> {
        powerBarInfo.setPowerSize(powerBarInfo.getTargetSize()*-1);
        switch (powerBarInfo.getPowerType()) {
            case TENACITY:
                HeroPowerBarInfo temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.OFFSENSE);
                temp.setPowerSize(temp.getTargetSize()+2);
                temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.SPECIAL);
                temp.setPowerSize(temp.getTargetSize()+2);
                break;
            case OFFSENSE:
                temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.TENACITY);
                temp.setPowerSize(temp.getTargetSize()+2);
                temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.SPECIAL);
                temp.setPowerSize(temp.getTargetSize()+2);
                break;
            case SPECIAL:
                temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.OFFSENSE);
                temp.setPowerSize(temp.getTargetSize()+2);
                temp =  heroGameScene.getPowerInfo(HeroPowerBarInfo.PowerType.TENACITY);
                temp.setPowerSize(temp.getTargetSize()+2);
                break;
        }
    };

    private static final HeroGameAbilityInfo plusThreeAbility = new HeroGameAbilityInfo(plusThree){
        {
            this.setInfo(HeroPowerBarInfo.PowerType.TENACITY,"+3","-3","");
            this.setInfo(HeroPowerBarInfo.PowerType.OFFSENSE,"","+3","-3");
            this.setInfo(HeroPowerBarInfo.PowerType.SPECIAL,"-3","","+3");
        }
    };

    private static final HeroGameAbilityInfo switchValAbility = new HeroGameAbilityInfo(switchVal){
        {
            this.setInfo(HeroPowerBarInfo.PowerType.TENACITY,"*-1","+2","+2");
            this.setInfo(HeroPowerBarInfo.PowerType.OFFSENSE,"+2","*-1","+2");
            this.setInfo(HeroPowerBarInfo.PowerType.SPECIAL,"+2","+2","*-1");
        }
    };

    private HeroGameAbilityInfo[] abilities = new HeroGameAbilityInfo[]{plusThreeAbility,plusThreeAbility,plusThreeAbility,switchValAbility,switchValAbility,switchValAbility};

    public RightSkillBar(HeroGameScene parentScene) {
        super(new Rectangle(LOCATION), parentScene);
        this.setDrawable(null);
        this.addElement(new UIButton(BUTTON_PANEL1,new GameSprite("Tests/UI/panel.png"),parentScene));
        if(this.getCollection().get(this.getCollection().size()-1) instanceof UIButton){
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setStates(
                    new GameSprite("Tests/UI/panel.png"){
                        {
                            this.getBaseSprite().setColor(Color.GRAY);
                        }
                    },
                    new GameSprite("Tests/UI/panel.png"){
                        {
                            this.getBaseSprite().setColor(Color.WHITE);
                        }
                    },
                    new GameSprite("Tests/UI/panel.png"){
                        {
                            this.getBaseSprite().setColor(Color.LIGHT_GRAY);
                        }
                    }
            );
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setClick_function(v->{
                abilities[0].getAdjustPowerValue().adjust(parentScene.getPowerInfo(HeroPowerBarInfo.PowerType.TENACITY),parentScene);
            });
        }

        this.addElement(new UIButton(BUTTON_PANEL2,new GameSprite("Tests/UI/panel.png"),parentScene));
        if(this.getCollection().get(this.getCollection().size()-1) instanceof UIButton && this.getCollection().get(0) instanceof UIButton){
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setStates((UIButton) this.getCollection().get(0));
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setClick_function(v->{
                abilities[1].getAdjustPowerValue().adjust(parentScene.getPowerInfo(HeroPowerBarInfo.PowerType.OFFSENSE),parentScene);
            });
        }
        this.addElement(new UIButton(BUTTON_PANEL3,new GameSprite("Tests/UI/panel.png"),parentScene));
        if(this.getCollection().get(this.getCollection().size()-1) instanceof UIButton && this.getCollection().get(0) instanceof UIButton){
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setStates((UIButton) this.getCollection().get(0));
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setClick_function(v->{
                abilities[2].getAdjustPowerValue().adjust(parentScene.getPowerInfo(HeroPowerBarInfo.PowerType.SPECIAL),parentScene);
            });
        }
        this.addElement(new UIButton(BUTTON_PANEL4,new GameSprite("Tests/UI/panel.png"),parentScene));
        if(this.getCollection().get(this.getCollection().size()-1) instanceof UIButton && this.getCollection().get(0) instanceof UIButton){
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setStates((UIButton) this.getCollection().get(0));
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setClick_function(v->{
                abilities[3].getAdjustPowerValue().adjust(parentScene.getPowerInfo(HeroPowerBarInfo.PowerType.TENACITY),parentScene);
            });
        }
        this.addElement(new UIButton(BUTTON_PANEL5,new GameSprite("Tests/UI/panel.png"),parentScene));
        if(this.getCollection().get(this.getCollection().size()-1) instanceof UIButton && this.getCollection().get(0) instanceof UIButton){
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setStates((UIButton) this.getCollection().get(0));
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setClick_function(v->{
                abilities[4].getAdjustPowerValue().adjust(parentScene.getPowerInfo(HeroPowerBarInfo.PowerType.OFFSENSE),parentScene);
            });
        }
        this.addElement(new UIButton(BUTTON_PANEL6,new GameSprite("Tests/UI/panel.png"),parentScene));
        if(this.getCollection().get(this.getCollection().size()-1) instanceof UIButton && this.getCollection().get(0) instanceof UIButton){
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setStates((UIButton) this.getCollection().get(0));
            ((UIButton) this.getCollection().get(this.getCollection().size()-1)).setClick_function(v->{
                abilities[5].getAdjustPowerValue().adjust(parentScene.getPowerInfo(HeroPowerBarInfo.PowerType.SPECIAL),parentScene);
            });
        }



        HeroPowerBarInfo.PowerType currentPower = HeroPowerBarInfo.PowerType.TENACITY;
        int index = 0;
        for (Map.Entry<HeroPowerBarInfo.PowerType,String> powerTypeStringEntry : abilities[0].getEffects(currentPower).entrySet()) {
            GameSprite iconSprite = null;
            if(parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon() instanceof UIPanel){
                if(((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable() instanceof GameSprite){
                    iconSprite = (GameSprite) ((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable();
                }
            }
            ModNumTextIcon DEFAULT_PANEL1_NUMTEXT = null;
            try {
                DEFAULT_PANEL1_NUMTEXT = new ModNumTextIcon(
                        iconSprite.clone(),
                        parentScene,
                        powerTypeStringEntry.getValue());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            DEFAULT_PANEL1_NUMTEXT.positionInBox(BUTTON_PANEL1,20,index++ , abilities[0].getEffects(currentPower).size(),50);
            this.addElement(DEFAULT_PANEL1_NUMTEXT);
        }

        currentPower = HeroPowerBarInfo.PowerType.OFFSENSE;
        index = 0;
        for (Map.Entry<HeroPowerBarInfo.PowerType,String> powerTypeStringEntry : abilities[1].getEffects(currentPower).entrySet()) {
            GameSprite iconSprite = null;
            if(parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon() instanceof UIPanel){
                if(((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable() instanceof GameSprite){
                    iconSprite = (GameSprite) ((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable();
                }
            }
            ModNumTextIcon DEFAULT_PANEL2_NUMTEXT = null;
            try {
                DEFAULT_PANEL2_NUMTEXT = new ModNumTextIcon(
                        iconSprite.clone(),
                        parentScene,
                        powerTypeStringEntry.getValue());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            DEFAULT_PANEL2_NUMTEXT.positionInBox(BUTTON_PANEL2,20,index++ , abilities[1].getEffects(currentPower).size(),50);
            this.addElement(DEFAULT_PANEL2_NUMTEXT);
        }

        currentPower = HeroPowerBarInfo.PowerType.SPECIAL;
        index = 0;
        for (Map.Entry<HeroPowerBarInfo.PowerType,String> powerTypeStringEntry : abilities[2].getEffects(currentPower).entrySet()) {
            GameSprite iconSprite = null;
            if(parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon() instanceof UIPanel){
                if(((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable() instanceof GameSprite){
                    iconSprite = (GameSprite) ((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable();
                }
            }
            ModNumTextIcon DEFAULT_PANEL3_NUMTEXT = null;
            try {
                DEFAULT_PANEL3_NUMTEXT = new ModNumTextIcon(
                        iconSprite.clone(),
                        parentScene,
                        powerTypeStringEntry.getValue());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            DEFAULT_PANEL3_NUMTEXT.positionInBox(BUTTON_PANEL3,20,index++ , abilities[2].getEffects(currentPower).size(),50);
            this.addElement(DEFAULT_PANEL3_NUMTEXT);
        }

        currentPower = HeroPowerBarInfo.PowerType.TENACITY;
        index = 0;
        for (Map.Entry<HeroPowerBarInfo.PowerType,String> powerTypeStringEntry : abilities[3].getEffects(currentPower).entrySet()) {
            GameSprite iconSprite = null;
            if(parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon() instanceof UIPanel){
                if(((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable() instanceof GameSprite){
                    iconSprite = (GameSprite) ((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable();
                }
            }
            ModNumTextIcon DEFAULT_PANEL4_NUMTEXT = null;
            try {
                DEFAULT_PANEL4_NUMTEXT = new ModNumTextIcon(
                        iconSprite.clone(),
                        parentScene,
                        powerTypeStringEntry.getValue());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            DEFAULT_PANEL4_NUMTEXT.positionInBox(BUTTON_PANEL4,20,index++ , abilities[3].getEffects(currentPower).size(),50);
            this.addElement(DEFAULT_PANEL4_NUMTEXT);
        }

        currentPower = HeroPowerBarInfo.PowerType.OFFSENSE;
        index = 0;
        for (Map.Entry<HeroPowerBarInfo.PowerType,String> powerTypeStringEntry : abilities[4].getEffects(currentPower).entrySet()) {
            GameSprite iconSprite = null;
            if(parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon() instanceof UIPanel){
                if(((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable() instanceof GameSprite){
                    iconSprite = (GameSprite) ((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable();
                }
            }
            ModNumTextIcon DEFAULT_PANEL5_NUMTEXT = null;
            try {
                DEFAULT_PANEL5_NUMTEXT = new ModNumTextIcon(
                        iconSprite.clone(),
                        parentScene,
                        powerTypeStringEntry.getValue());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            DEFAULT_PANEL5_NUMTEXT.positionInBox(BUTTON_PANEL5,20,index++ , abilities[4].getEffects(currentPower).size(),50);
            this.addElement(DEFAULT_PANEL5_NUMTEXT);
        }

        currentPower = HeroPowerBarInfo.PowerType.SPECIAL;
        index = 0;
        for (Map.Entry<HeroPowerBarInfo.PowerType,String> powerTypeStringEntry : abilities[5].getEffects(currentPower).entrySet()) {
            GameSprite iconSprite = null;
            if(parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon() instanceof UIPanel){
                if(((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable() instanceof GameSprite){
                    iconSprite = (GameSprite) ((UIPanel) parentScene.getPowerInfo(powerTypeStringEntry.getKey()).getPowerIcon()).getDrawable();
                }
            }
            ModNumTextIcon DEFAULT_PANEL6_NUMTEXT = null;
            try {
                DEFAULT_PANEL6_NUMTEXT = new ModNumTextIcon(
                        iconSprite.clone(),
                        parentScene,
                        powerTypeStringEntry.getValue());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            DEFAULT_PANEL6_NUMTEXT.positionInBox(BUTTON_PANEL6,20,index++ , abilities[5].getEffects(currentPower).size(),50);
            this.addElement(DEFAULT_PANEL6_NUMTEXT);
        }
    }
}

class HeroGameAbilityInfo{
    private AdjustPowerValue adjustPowerValue = null;
    private HashMap<HeroPowerBarInfo.PowerType,LinkedHashMap<HeroPowerBarInfo.PowerType,String>> completeInfo = new HashMap<>();

    public HeroGameAbilityInfo(AdjustPowerValue adjustPowerValue){
        this.adjustPowerValue = adjustPowerValue;
        completeInfo.put(HeroPowerBarInfo.PowerType.OFFSENSE, new LinkedHashMap<>());

        completeInfo.put(HeroPowerBarInfo.PowerType.TENACITY, new LinkedHashMap<>());

        completeInfo.put(HeroPowerBarInfo.PowerType.SPECIAL, new LinkedHashMap<>());
    }

    public void setInfo(HeroPowerBarInfo.PowerType type,String effectOnTenacity, String effectOnOffense, String effectOnSpecial){
        if(!effectOnTenacity.equalsIgnoreCase("")){
            completeInfo.get(type).put(HeroPowerBarInfo.PowerType.TENACITY, effectOnTenacity);
        }
        if(!effectOnOffense.equalsIgnoreCase("")){
            completeInfo.get(type).put(HeroPowerBarInfo.PowerType.OFFSENSE, effectOnOffense);
        }
        if(!effectOnSpecial.equalsIgnoreCase("")){
            completeInfo.get(type).put(HeroPowerBarInfo.PowerType.SPECIAL, effectOnSpecial);
        }
    }

    public LinkedHashMap<HeroPowerBarInfo.PowerType,String> getEffects(HeroPowerBarInfo.PowerType type){
        return completeInfo.get(type);
    }

    public AdjustPowerValue getAdjustPowerValue() {
        return adjustPowerValue;
    }

    public void setAdjustPowerValue(AdjustPowerValue adjustPowerValue) {
        this.adjustPowerValue = adjustPowerValue;
    }
}

class ModNumTextIcon extends UIPanel{
    private static final GameFont DEFAULT_FONT = new GameFont(GameFont.DEFAULT_FONT,32){
        {
            this.getFont().setColor(Color.BLUE);
        }
    };
    private static final Vector2 ICON_SIZE = new Vector2(54,54);
    private static final float ICON_TEXT_DISTANCE = 16;


    private static GameFont textFont = DEFAULT_FONT;

    private UIStylizedText textPanel;
    private Rectangle total_location = new Rectangle();
    private GlyphLayout text_layout;

    public ModNumTextIcon(IDrawable drawable, UIScene parentScene, String text) {
        super(new Rectangle(0,0,ICON_SIZE.x,ICON_SIZE.y), drawable, parentScene);
        text_layout = new GlyphLayout(textFont.getFont(),text);

        total_location.setSize(ICON_SIZE.x + text_layout.width*2 + ICON_TEXT_DISTANCE,
                ICON_SIZE.y);
        this.getLocation().setPosition(text_layout.width*2 + ICON_TEXT_DISTANCE, 0);

        textPanel = new UIStylizedText(text,textFont,new Rectangle(0,0,text_layout.width*2,ICON_SIZE.y),null,parentScene);
        this.setPosition(new Vector2(200,200));
    }

    public void positionInBox(Rectangle box, float icon_offset, int index,int amount_of_elements, float vertical_offset){
        if(amount_of_elements == 1){
            float x = box.x + box.width - icon_offset - total_location.width;
            float y = box.y + (box.height + total_location.height)/2;
            this.setPosition(new Vector2(x,y));
            return;
        }else if(amount_of_elements == 2){
            float x = box.x + box.width - icon_offset - total_location.width;
            float y = vertical_offset;
            if(index == 1){
                y = box.height - vertical_offset;
            }
            this.setPosition(new Vector2(x,y));
            return;
        }
        else if(amount_of_elements == 3){
            float x = box.x + box.width - icon_offset - total_location.width;
            float y = vertical_offset - ICON_SIZE.y;
            if(index == 1){
                y = (box.height  - ICON_SIZE.y)/2;
            }else if(index == 2){
                y = box.height - vertical_offset;
            }
            y += box.y;
            this.setPosition(new Vector2(x,y));
            return;
        }

    }

    @Override
    public void setPosition(Vector2 location) {
        //super.setPosition(location);
        this.getLocation().setPosition(text_layout.width*2 + ICON_TEXT_DISTANCE + location.x, location.y);
        textPanel.setPosition(location);
        this.total_location.setPosition(location);
    }

    @Override
    public void setParent(UIElement parent) {
        super.setParent(parent);
        this.textPanel.setParent(parent);
    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        super.draw(delta, bounds, batch);
        this.textPanel.draw(delta,null,batch);
    }
}

class HeroGameController extends Controller implements IControllerMouse {

    private HeroGameScene controllingScene;

    HeroGameController(HeroGameScene scene){
        this.controllingScene = scene;
    }

    @Override
    public void update(DeltaTime delta) {
        super.update(delta);
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        this.mouseMove(new Vector2(screenX,GameConstants.SCREEN_HEIGHT -screenY));
        return super.mouseMoved(screenX, screenY);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        KnownMouseButtons knownMouseButton = null;
        if(button == KnownMouseButtons.LEFT.getButton()){
            knownMouseButton = KnownMouseButtons.LEFT;
        }else
        if(button == KnownMouseButtons.RIGHT.getButton()){
            knownMouseButton = KnownMouseButtons.RIGHT;
        }else
        if(button == KnownMouseButtons.MIDDLE.getButton()){
            knownMouseButton = KnownMouseButtons.MIDDLE;
        }
        click(new Vector2(screenX,GameConstants.SCREEN_HEIGHT -screenY),knownMouseButton);
        return super.touchDown(screenX, screenY, pointer, button);
    }

    @Override
    public boolean scrolled(int amount) {
        this.scroll(amount);
        return super.scrolled(amount);
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        this.drag(new Vector2(screenX,GameConstants.SCREEN_HEIGHT -screenY),this.getLastPressedButton());
        return super.touchDragged(screenX, screenY, pointer);
    }

    @Override
    public void drag(Vector2 mouseLoc, KnownMouseButtons button) {
        mouseLoc.scl(GameConstants.mouseMod);
    }

    @Override
    public void mouseMove(Vector2 mouseLoc) {
        mouseLoc.scl(GameConstants.mouseMod);
        for (int i = controllingScene.getElements().size()-1; i >= 0; i--) {
            controllingScene.getElements().get(i).mouseMove(mouseLoc, this);
        }
    }

    @Override
    public void click(Vector2 mouseLoc, IController.KnownMouseButtons button) {
        mouseLoc.scl(GameConstants.mouseMod);
        for (int i = controllingScene.getElements().size()-1; i >= 0; i--) {
            if(controllingScene.getElements().get(i).containsMouse(mouseLoc)){
                controllingScene.getElements().get(i).clicked(mouseLoc,button);
                return;
            }
        }
    }

    @Override
    public void scroll(int amount) {
        if(this.getMovableObj() instanceof IEditable){
            if(!((IEditable) this.getMovableObj()).canEdit()){
                return;
            }
        }else{
            return;
        }

        if(this.getMovableObj()!=null){
            if(this.getMovableObj() instanceof IResizable){
                float mod = amount < 0 ? 1.1f : 0.9f;
                ((IResizable) this.getMovableObj()).resize(new Vector2(mod,mod));
            }
        }
    }

    String allowedCharacters = ".,?!(){}[];'\"&|%*+-/\n\r";
    String newLineChars = "\n\r";


    @Override
    public boolean keyTyped(char character) {
        return super.keyTyped(character);
    }
}