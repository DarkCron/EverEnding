package com.ever.ending.interfaces.manipulation;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ever.ending.interfaces.control.IController;
import com.ever.ending.interfaces.manipulation.IControllable;

public interface IMovable extends IControllable {
    public void move(Vector2 deltaMovement);
    public void setPosition(Vector2 location);
    public Vector2 getPosition();
    public Rectangle getScreenPos();
    public void drag(Vector2 mouseLoc, IController.KnownMouseButtons button);
}
