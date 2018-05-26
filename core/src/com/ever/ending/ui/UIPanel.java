package com.ever.ending.ui;

import com.badlogic.gdx.math.Rectangle;
import com.ever.ending.interfaces.drawable.IDrawable;

public class UIPanel extends UIElement {

    public UIPanel(){
    }

    public UIPanel(Rectangle location, IDrawable drawable, UIScene parentScene){
        super(location,drawable,parentScene);
    }

}
