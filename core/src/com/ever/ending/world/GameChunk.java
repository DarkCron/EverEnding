package com.ever.ending.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.gameobject.GameObject;
import com.ever.ending.interfaces.resources.IReloadable;
import com.ever.ending.management.resources.GCDB;
import com.ever.ending.tile.GameTile;

import java.util.ArrayList;

public class GameChunk implements Json.Serializable, IReloadable {
    static final Vector2 defaultChunkSize = new Vector2(128*20,128*20);

    private Rectangle region;
    private int xID;
    private int yID;
    private ArrayList<GameObject> gameObjectsInChunk = new ArrayList<>();
    private GameTile[] chunkTiles;
    private boolean isLoaded = false;

    public GameChunk(){}

    public GameChunk(Vector2 position) {
        this.xID = (int)position.x;
        this.yID = (int)position.y;
        Vector2 regionLocation = new Vector2(position);
        regionLocation.scl(defaultChunkSize);
        region = new Rectangle(regionLocation.x,regionLocation.y,defaultChunkSize.x,defaultChunkSize.y);
        chunkTiles = new GameTile[(int)(defaultChunkSize.x/128) * (int)(defaultChunkSize.y/128)];
    }

    public int getyID() {
        return yID;
    }

    public int getxID() {
        return xID;
    }

    public Rectangle getRegion() {
        return region;
    }

    public void addGameObject(GameObject go){
        gameObjectsInChunk.add(go);
    }

    @Override
    public void write(Json json) {
        json.setTypeName(this.getClass().toString());
        json.writeObjectStart("Chunk");
        json.writeValue("x",xID);
        json.writeValue("y",yID);
        json.writeObjectEnd();

        json.writeArrayStart("Tiles");
        for (GameTile tile : chunkTiles) {
            json.writeValue(tile);
        }
        json.writeArrayEnd();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void read(Json json, JsonValue jsonData) {
        xID = jsonData.get("Chunk").get("x").asInt();
        yID = jsonData.get("Chunk").get("y").asInt();
        region = new Rectangle(xID*defaultChunkSize.x,yID*defaultChunkSize.y,defaultChunkSize.x,defaultChunkSize.y);

        chunkTiles = json.readValue(GameTile[].class,jsonData.get("Tiles"));
    }

    public void removeGameObject(GameObject gameObject) {
        gameObjectsInChunk.remove(gameObject);
    }

    public ArrayList<GameObject> getGameObjectsInChunk() {
        return gameObjectsInChunk;
    }

    @Override
    public void Reload(GCDB gcdb) {
        if(!isLoaded){
            isLoaded = true;
            for (GameObject gameObject : gameObjectsInChunk) {
                gameObject.Reload(gcdb);
            }
        }
    }
}
