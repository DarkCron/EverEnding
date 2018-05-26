package com.ever.ending.gameobject;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.interfaces.resources.IReloadable;
import com.ever.ending.management.resources.GCDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GameObjectSaveData implements Json.Serializable,IReloadable {
    private GameObject ref;
    private ArrayList<GridPoint2> chunkLocs = new ArrayList<>();
    static private HashMap<String,Class<? extends GameObject>> gameObjectClasses = new HashMap<>();

    public ArrayList<GridPoint2> getChunkLocs() {
        return chunkLocs;
    }

    public GameObjectSaveData(){

    }

    public GameObjectSaveData(GameObject ref, GridPoint2 loc){
        this.ref = ref;
        this.chunkLocs.add(loc);
    }

    public GameObjectSaveData(GameObject ref, ArrayList<GridPoint2> chunkLocs){
        this.ref = ref;
        this.chunkLocs = chunkLocs;
    }

    public void addLoc(GridPoint2 loc){
        if(!this.chunkLocs.contains(loc)){
            this.chunkLocs.add(loc);
        }
    }

    @Override
    public void write(Json json) {
        json.writeObjectStart("Ref");
        json.writeValue("r",ref);
        json.writeObjectEnd();

        json.writeArrayStart("Locs");
        for (GridPoint2 chunkLoc : chunkLocs) {
            json.writeValue(chunkLoc);
        }
        json.writeArrayEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        JsonValue val2 = jsonData.get("Ref").get("r").get("Type").get("c");
        Class c = GameObject.class;
        String s = val2.asString();
        if(!gameObjectClasses.containsKey(s)){
            try {
                c = Class.forName(s);
            }catch (Exception e){
                System.out.println("Error reading Type: "+s);
                c = GameObject.class;
            }
            gameObjectClasses.put(s,c);
        }else{
            c = gameObjectClasses.get(s);
        }

        this.ref = json.readValue((Class<? extends GameObject>) c,jsonData.get("Ref").get("r"));
        this.chunkLocs.addAll(Arrays.asList(json.readValue(GridPoint2[].class,jsonData.get("Locs"))));


    }

    public GameObject getRef() {
        return ref;
    }

    @Override
    public void Reload(GCDB gcdb) {
        ref.Reload(gcdb);
    }
}
