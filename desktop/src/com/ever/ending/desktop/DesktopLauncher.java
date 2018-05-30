package com.ever.ending.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ever.ending.EverEnding;
import com.ever.ending.management.GameConstants;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = (int)(GameConstants.SCREEN_WIDTH);
		config.height = (int)(GameConstants.SCREEN_HEIGHT);
		config.fullscreen = false;
		new LwjglApplication(new EverEnding(), config);
	}
}
