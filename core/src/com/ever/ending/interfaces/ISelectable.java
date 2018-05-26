package com.ever.ending.interfaces;

import com.badlogic.gdx.math.Vector2;

public interface ISelectable extends IControllable{
    public boolean containsMouse(Vector2 mousePos);
    public IMovable select();
    public void unSelect();
    public void clicked(Vector2 mousePos);
    public Vector2 relativeClickLocation(Vector2 mousePos);
    public Vector2 getRelativeClickLocation();
}
