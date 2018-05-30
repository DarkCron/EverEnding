package com.ever.ending;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.ever.ending.game.hero.scenes.HeroGameScene;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.DrawableScene;
import com.ever.ending.management.GameConstants;
import com.ever.ending.scenes.UITestScene;
import com.ever.ending.scenes.testScene;

public class EverEnding extends ApplicationAdapter{
	private SpriteBatch batch;
	private Texture img;
	private DrawableScene drawScene;
	private FrameBuffer target_render;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		target_render = new FrameBuffer(Pixmap.Format.RGBA8888, GameConstants.TARGET_SCREEN_WIDTH, GameConstants.TARGET_SCREEN_HEIGHT,false);
		//drawScene = new UITestScene(target_render);
		drawScene = new HeroGameScene(target_render);
		GameConstants.generateMouseMod();

	}

	private boolean nani = false;

	@Override
	public void render () {


//		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
//			Gdx.app.exit();
//		}

		drawScene.draw(new DeltaTime((int)(Gdx.graphics.getDeltaTime()*1000)),null);

		batch.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.draw(new Sprite(target_render.getColorBufferTexture()){
			{
				this.flip(false,true);
			}
		},0,0,GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);
		batch.end();
		batch.flush();
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
