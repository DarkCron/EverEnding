package com.ever.ending.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface IResizable extends IControllable{
    public void setSize(Vector2 size);
    public void resize(Vector2 mod);
    public Vector2 getSize();
}
