package com.ever.ending.management;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class GameConstants {
    public final static int TARGET_SCREEN_WIDTH = 1920;
    public final static int TARGET_SCREEN_HEIGHT = 1080;
    public final static int SCREEN_WIDTH = (int)(1366);
    public final static int SCREEN_HEIGHT = (int)(768);
    public final static Texture DEBUG_TEX = new Texture("Tests/UI/panel.png");
    public static Vector2 mouseMod = new Vector2(1,1);

    public static void generateMouseMod(){
        mouseMod = new Vector2((float) TARGET_SCREEN_WIDTH / (float)SCREEN_WIDTH, (float)TARGET_SCREEN_HEIGHT / (float)SCREEN_HEIGHT);
    }
}
