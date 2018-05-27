package com.ever.ending.ui;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ever.ending.gameobject.GameSprite;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.interfaces.manipulation.IMovable;
import com.ever.ending.interfaces.manipulation.func_interfaces.ClickLambda;

import java.util.IdentityHashMap;

public class UIButton extends UIElement{
    private final static IDrawable UNPRESSED_STATE = new GameSprite("Tests/UI/ui_button.png",0,0,64,64);
    private final static IDrawable SELECT_STATE = new GameSprite("Tests/UI/ui_button.png",64,0,64,64);
    private final static IDrawable PRESSED_STATE = new GameSprite("Tests/UI/ui_button.png",64*2,0,64,64);

    private IDrawable select_state = SELECT_STATE;
    private IDrawable unpressed_state = UNPRESSED_STATE;
    private IDrawable pressed_state = PRESSED_STATE;

    private ClickLambda click_function = null;

    public UIButton(){

    }

    public UIButton(Rectangle location, UIScene parentScene) {
        super(location, UNPRESSED_STATE, parentScene);
    }

    public UIButton(Rectangle location, UIScene parentScene, ClickLambda click_function) {
        this(location, parentScene);
        setClick_function(click_function);
    }

    public void setClick_function(ClickLambda click_function) {
        this.click_function = click_function;
    }

    public void setStates(IDrawable select_state, IDrawable unpressed_state, IDrawable pressed_state){
        this.select_state = select_state;
        this.unpressed_state = unpressed_state;
        this.pressed_state = pressed_state;
        this.setDrawable(unpressed_state);
    }

    @Override
    public void clicked(Vector2 mousePos) {
        super.clicked(mousePos);
        if(this.click_function != null){
            this.click_function.click(mousePos);
        }
    }

    @Override
    public void unSelect() {
        super.unSelect();
        this.setDrawable(unpressed_state);
    }

    @Override
    public IMovable select() {
        this.setDrawable(pressed_state);
        return super.select();
    }
}
