package com.ever.ending.management.resources;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.interfaces.resources.IResource;
import com.ever.ending.interfaces.drawable.IDrawable;

public class GameResource implements Json.Serializable, IResource {

    IDrawable resource;
    long resourceID = 0;

    public GameResource(){

    }

    public GameResource(IDrawable resource){
        this.resource = resource;
    }

    public IDrawable getResouceClone() throws CloneNotSupportedException{
        return resource.clone();
    }

    @Override
    public void write(Json json) {
        resource.write(json);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        JsonValue val = jsonData.get("Type").get("c");
        Class c = IDrawable.class;
        String s = val.asString();
        try {
            c = Class.forName(s);
        }catch (Exception e){
            System.out.println("Error reading Type: "+s);
            c = IDrawable.class;
        }
        resource = json.readValue((Class<? extends IDrawable>)c ,jsonData);
    }

    @Override
    public void setID(long id) {
        resourceID = id;
    }

    @Override
    public long getID() {
        return this.resourceID;
    }
}
