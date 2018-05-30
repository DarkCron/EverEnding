package com.ever.ending.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.management.GameFont;

public class UIStylizedText extends UITextField {
    public enum Styles{
        CENTER,LEFT,RIGHT
    }

    private Styles currentStyle = Styles.CENTER;

    public UIStylizedText(Rectangle location, IDrawable bg, UIScene parentScene) {
        super(location, bg, parentScene);
        generateTextLoc();
    }

    public UIStylizedText(String text, int fontSize, Vector2 loc, IDrawable bg, UIScene parentScene) {
        super(text, fontSize, loc, bg, parentScene);
        generateTextLoc();
    }

    public UIStylizedText(String text, GameFont font, Vector2 loc, IDrawable bg, UIScene parentScene) {
        super(text, font, loc, bg, parentScene);
        generateTextLoc();
    }

    public UIStylizedText(String text, GameFont font, Rectangle loc, IDrawable bg, UIScene parentScene) {
        super(text, font, loc, bg, parentScene);
        generateTextLoc();
    }

    @Override
    protected void generateTextLoc() {
        super.generateTextLoc();
        if(currentStyle == null){
            return;
        }
        switch (currentStyle){
            case CENTER:
                this.getTextLoc().x += this.getTypePanel().getLocation().width/2 - this.getTextLayout().width /2;
                break;
            case LEFT:
                break;
            case RIGHT:
                this.getTextLoc().x += this.getTypePanel().getLocation().width - this.getTextLayout().width;
                break;
        }
    }
}
