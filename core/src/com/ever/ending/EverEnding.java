package com.ever.ending;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.DrawableScene;
import com.ever.ending.scenes.UITestScene;
import com.ever.ending.scenes.testScene;

public class EverEnding extends ApplicationAdapter{
	private SpriteBatch batch;
	private Texture img;
	private DrawableScene drawScene;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		drawScene = new UITestScene();
	}

	private boolean nani = false;

	@Override
	public void render () {


//		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
//			Gdx.app.exit();
//		}
		drawScene.draw(new DeltaTime((int)(Gdx.graphics.getDeltaTime()*1000)),null);
//		Gdx.gl.glClearColor(1, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		drawScene.dispose();
	}
}
