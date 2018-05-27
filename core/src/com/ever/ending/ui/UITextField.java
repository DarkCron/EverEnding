package com.ever.ending.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ever.ending.gameobject.GameSprite;
import com.ever.ending.interfaces.manipulation.ITypeable;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.GameFont;

public class UITextField extends UIElement implements ITypeable {

    private static final String DEFAULT_TF_PATH = "Tests/UI/textField_panel.png";
    private static final Vector2 DEFAULT_OFFSET = new Vector2(5,5);
    private UIPanel typePanel;
    private String fieldString = "";
    private BitmapFont font = new BitmapFont();
    private Vector2 typePanelOffset;
    private Vector2 textLoc;
    private GlyphLayout textLayout;

    @Override
    public void dispose() {
        textLayout.reset();
        textLayout = null;
        this.typePanel.dispose();
        this.typePanel = null;
        this.font.dispose();
        this.font = null;
    }

    public UITextField(Rectangle location, IDrawable bg, UIScene parentScene){
        super(location,bg,parentScene);
        typePanel = new UIPanel(new Rectangle(),new GameSprite(DEFAULT_TF_PATH), parentScene);
        this.typePanelOffset = DEFAULT_OFFSET;
        this.setTypePanelSize(this.typePanelOffset);

        font = new GameFont(GameFont.DEFAULT_FONT, 32).getFont();
        font.setColor(Color.BLACK);
        this.setInput("Test");
        typePanel.setParent(this);
    }

    public UITextField(String text, int fontSize, Vector2 loc, IDrawable bg, UIScene parentScene){
        super(new Rectangle(),bg,parentScene);

        font = new GameFont(GameFont.DEFAULT_FONT, fontSize).getFont();
        font.setColor(Color.BLACK);
        this.fieldString = text;
        this.textLayout = new GlyphLayout(this.font,this.fieldString);
        this.typePanelOffset = DEFAULT_OFFSET;
        this.setLocation(this.optimalPanelSize(this.textLayout));
        this.getLocation().setX(this.getLocation().x + loc.x);
        this.getLocation().setY(this.getLocation().y + loc.y);

        typePanel = new UIPanel(new Rectangle(),new GameSprite(DEFAULT_TF_PATH,3,3,1,1), parentScene);
        this.setTypePanelSize(this.typePanelOffset);
        this.generateTextLoc();



        typePanel.setParent(this);
    }

    private void setTypePanelSize(Vector2 offset) {
        Rectangle tf_bounds = new Rectangle(offset.x,offset.y,this.getLocation().width-2*offset.x,this.getLocation().height-2*offset.y);
        this.typePanel.setLocation(tf_bounds);
    }

    private Rectangle optimalPanelSize(GlyphLayout layout) {
        Rectangle tf_bounds = new Rectangle(0,0,
                layout.width+3*this.typePanelOffset.x, layout.height*2+2*this.typePanelOffset.y);
        return tf_bounds;
    }

    private void generateTextLoc() {
        this.textLoc = new Vector2(this.getLocation().x + (typePanel.getLocation().x)/2 + typePanelOffset.x,
                this.getLocation().y + typePanel.getLocation().y  + (typePanel.getLocation().height ) - (typePanel.getLocation().height - textLayout.height)/3 );
    }

    @Override
    public void resize(Vector2 mod) {
        super.resize(mod);
        this.setTypePanelSize(this.typePanelOffset);
        this.generateTextLoc();
    }

    @Override
    public void setSize(Vector2 size) {
        super.setSize(size);
        this.setTypePanelSize(this.typePanelOffset);
        this.generateTextLoc();
    }

    @Override
    public void setInput(String input) {
        this.fieldString = input;
        if(this.textLayout == null){
            this.textLayout = new GlyphLayout(this.font,this.fieldString);
        }else{
            this.textLayout.setText(font,this.fieldString);
        }

        this.generateTextLoc();
        while(this.textLayout.width + 10 > this.typePanel.getLocation().width){
            this.setSize(new Vector2(this.getLocation().width+10,this.getLocation().height));
        }
        while(this.textLayout.height + 10 > this.typePanel.getLocation().height){
            this.setSize(new Vector2(this.getLocation().width,this.getLocation().height+10));
        }
    }

    @Override
    public String getInput() {
        return this.fieldString;
    }

    @Override
    public void move(Vector2 deltaMovement) {
        super.move(deltaMovement);
        this.generateTextLoc();
    }

    @Override
    public void setPosition(Vector2 location) {
        super.setPosition(location);
        this.generateTextLoc();
    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        super.draw(delta, bounds, batch);
        typePanel.draw(delta,bounds,batch);
        font.draw(batch,this.getInput(),this.getParentLoc().x + this.textLoc.x,this.getParentLoc().y + this.textLoc.y);
    }
}
