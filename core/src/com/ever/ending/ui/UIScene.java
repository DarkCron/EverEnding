package com.ever.ending.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.ever.ending.interfaces.IEditable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.DrawableScene;

import java.util.ArrayList;

public abstract class UIScene extends DrawableScene implements IEditable {

    private ArrayList<UIElement> elements = new ArrayList<>();
    private boolean isEditable = false;

    public UIScene(){
        super();
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        this.getSceneBatch().setProjectionMatrix(this.getSceneCamera().combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.getSceneBatch().begin();
        for (UIElement element : elements) {
            element.draw(delta,null,this.getSceneBatch());
        }
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
