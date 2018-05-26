package com.ever.ending.interfaces;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public interface IMovable extends IControllable{
    public void move(Vector2 deltaMovement);
    public void setPosition(Vector2 location);
    public Vector2 getPosition();
    public Rectangle getScreenPos();
    public void drag(Vector2 mouseLoc);
}
