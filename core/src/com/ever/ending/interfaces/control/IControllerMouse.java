package com.ever.ending.interfaces.control;

import com.badlogic.gdx.math.Vector2;

public interface IControllerMouse extends IController {
    public void mouseMove(Vector2 mouseLoc);
    public void click(Vector2 mouseLoc, IController.KnownMouseButtons button);
    public void scroll(int amount);
    public void drag(Vector2 mouseLoc, KnownMouseButtons button);
}
