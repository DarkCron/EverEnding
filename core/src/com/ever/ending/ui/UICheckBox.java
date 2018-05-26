package com.ever.ending.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ever.ending.gameobject.GameSprite;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.management.DeltaTime;

public class UICheckBox extends UIElement {
    private static final IDrawable CHECKED_BOX = new GameSprite("Tests/UI/ui_checkbox.png",0,0,32,32);
    private static final IDrawable UNCHECKED_BOX = new GameSprite("Tests/UI/ui_checkbox.png",32,0,32,32);

    private boolean isChecked = false;
    private UITextField label = null;

    public  UICheckBox(Rectangle location, boolean bChecked, UIScene parentScene){
        super(location,null,parentScene);

        if(bChecked){
            this.setDrawable(CHECKED_BOX);
        }else{
            this.setDrawable(UNCHECKED_BOX);
        }
    }

    public  UICheckBox(Rectangle location, boolean bChecked, UIScene parentScene, String label){
        this(location,bChecked,parentScene);
        this.label = new UITextField(label,25, new Vector2(30,-10), new GameSprite("Tests/UI/panelAnim.png",3,3,1,1),this.getParentScene());
        this.label.setParent(this);
    }

    @Override
    public void clicked(Vector2 mousePos) {
        super.clicked(mousePos);
        this.setChecked(!this.isChecked);
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;

        if(this.isChecked){
            this.setDrawable(CHECKED_BOX);
        }else{
            this.setDrawable(UNCHECKED_BOX);
        }
    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        super.draw(delta, bounds, batch);

        if(label!=null){
            label.draw(delta,null,batch);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        label.dispose();
        this.label = null;
    }
}
