package com.ever.ending.management;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ever.ending.management.input.Controller;

public abstract class DrawableScene{
    private final SpriteBatch sceneBatch;
    private final OrthographicCamera sceneCamera;

    public Controller getSceneController() {
        return sceneController;
    }

    public void setSceneController(Controller sceneController) {
        this.sceneController = sceneController;
    }

    private Controller sceneController;

    public DrawableScene(){
        sceneBatch = new SpriteBatch();
        sceneCamera = new OrthographicCamera(GameConstants.SCREEN_WIDTH,GameConstants.SCREEN_HEIGHT);
        sceneCamera.update();
        sceneController = new Controller();
        Gdx.input.setInputProcessor(sceneController);
        this.loadResources();
    }

    public abstract void loadResources();

    public OrthographicCamera getSceneCamera(){
        return this.sceneCamera;
    }

    public void update(DeltaTime delta) {
        sceneController.update(delta);
    }

    public void draw(DeltaTime delta, SpriteBatch batch) {
        update(delta);
        sceneCamera.update();
    }

    public void dispose() {

    }

    public SpriteBatch getSceneBatch() {
        return sceneBatch;
    }

}

