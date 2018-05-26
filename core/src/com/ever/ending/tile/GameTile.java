package com.ever.ending.tile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.interfaces.resources.IReloadable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.management.resources.GCDB;

public class GameTile implements IDrawable, Json.Serializable, IReloadable {
    private Vector2 tileLoc;
    private IDrawable tileTex;
    private int resourceID;

    @Override
    public void update(DeltaTime delta) {

    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public IDrawable clone() throws CloneNotSupportedException {
        return null;
    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }

    @Override
    public void Reload(GCDB gcdb) {

    }
}
