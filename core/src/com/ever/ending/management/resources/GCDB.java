package com.ever.ending.management.resources;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.interfaces.resources.IResource;
import com.ever.ending.interfaces.drawable.IDrawable;

import java.util.ArrayList;
import java.util.Arrays;

public class GCDB implements Json.Serializable{

    ArrayList<IResource> gameResources = new ArrayList<>();
    private long resourceID;

    public GCDB(){}

    public void addGameTileSource(IResource source){
        gameResources.add(source);
        source.setID(resourceID);
        resourceID++;
    }

    @Override
    public void write(Json json) {
        json.writeArrayStart("GameTileSources");
        for (IResource source : gameResources){
            json.writeValue(source);
        }
        json.writeObjectEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        gameResources.addAll(Arrays.asList(json.readValue(GameResource[].class,jsonData.get("GameTileSources"))));
    }

    public IDrawable reloadResource(int resourceID) throws CloneNotSupportedException {
        //TODO Log N access
        for (IResource gameResource : gameResources) {
            if (gameResource instanceof GameResource) {
                if(gameResource.getID() == resourceID){
                    return ((GameResource) gameResource).getResouceClone();
                }
            }
        }
        throw new RuntimeException("Resource not found ID:" +resourceID);
    }
}
