package com.ever.ending.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.ever.ending.interfaces.IRenderable;
import com.ever.ending.interfaces.manipulation.IEditable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.DrawableScene;

import java.util.ArrayList;

public abstract class UIScene extends DrawableScene implements IEditable {

    private ArrayList<UIElement> elements = new ArrayList<>();
    private boolean isEditable = false;

    public UIScene(FrameBuffer mainCanvas){
        super(mainCanvas);
        this.getSceneCamera().translate(this.getSceneCamera().viewportWidth/2,this.getSceneCamera().viewportHeight/2);
    }

    public ArrayList<UIElement> getElements() {
        return elements;
    }

    @Override
    public void update(DeltaTime delta) {
        super.update(delta);
        for (UIElement element : elements) {
            element.update(delta);
        }
    }

    @Override
    public void draw(DeltaTime delta, SpriteBatch batch) {
        super.draw(delta, batch);
        for (UIElement element : elements) {
            if(element instanceof IRenderable){
                ((IRenderable) element).generateRender(delta);
            }
        }


        Gdx.gl.glClearColor(0, 0, 0, 1);
        this.getSceneBatch().setProjectionMatrix(this.getSceneCamera().combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.getSceneBatch().begin();
        this.getMainCanvas().begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        for (UIElement element : elements) {
            element.draw(delta,null,this.getSceneBatch());
        }
        this.getSceneBatch().flush();
        this.getMainCanvas().end();
        this.getSceneBatch().end();

    }

    @Override
    public void dispose() {
        super.dispose();
        for (UIElement element : elements) {
            element.dispose();
        }
    }

    @Override
    public boolean canEdit() {
        return this.isEditable;
    }

    @Override
    public void setEditable(boolean editable) {
        this.isEditable = editable;
    }
}
