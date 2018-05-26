package com.ever.ending.interfaces.drawable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.ever.ending.management.DeltaTime;


public interface IDrawable extends Json.Serializable,Cloneable{
    public void update(DeltaTime delta);
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch);
    public void dispose();
    public IDrawable clone() throws CloneNotSupportedException;
}
