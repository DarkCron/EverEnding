package com.ever.ending.interfaces.manipulation;

import com.badlogic.gdx.math.Vector2;
import com.ever.ending.interfaces.control.IController;
import com.ever.ending.management.input.Controller;

public interface ISelectable extends IControllable{
    public boolean containsMouse(Vector2 mousePos);
    public IMovable select();
    public void unSelect();
    public void mouseMove(Vector2 mousePos, Controller controller);
    public void clicked(Vector2 mousePos, IController.KnownMouseButtons button);
    public Vector2 relativeClickLocation(Vector2 mousePos);
    public Vector2 getRelativeClickLocation();
}
