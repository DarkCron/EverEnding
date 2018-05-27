package com.ever.ending.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.gameobject.GameObject;
import com.ever.ending.gameobject.GameObjectSaveData;
import com.ever.ending.interfaces.manipulation.IMovable;
import com.ever.ending.interfaces.resources.IReloadable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.management.resources.GCDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class GameWorld implements Json.Serializable, IDrawable, IReloadable {

    private ArrayList<ArrayList<GameChunk>> worldChunks = new ArrayList<>();
    private int worldChunkZeroID = 0; //ZeroID is where worldChunks[id].getxID() == 0
    private ArrayList<Integer> worldChunkZeroRows = new ArrayList<Integer>() {
        {
            this.add(0);
        }
    };
    private ArrayList<GameChunk> handledChunks = new ArrayList<>();
    private ArrayList<GameObject> chunkUniqueObjects = new ArrayList<>();
    private GCDB usedDatabase;

    public GameWorld(){
        worldChunks.add(new ArrayList<>());
        worldChunks.get(worldChunks.size()-1).add(new GameChunk(Vector2.Zero));
    }

    public void addChunk(Vector2 gameChunk) {
        System.out.println("Adding: "+gameChunk);
        int listIndex = (int)gameChunk.x + worldChunkZeroID;
        if(listIndex<0){
            int step = -listIndex-1;
            while (step>=0){
                worldChunks.add(0, new ArrayList<>());
                worldChunks.get(0).add(0,new GameChunk(new Vector2(listIndex+step-worldChunkZeroID,0)));
                worldChunkZeroRows.add(0,0);
                //worldChunkZeroID+=1;
                step-=1;
            }
            worldChunkZeroID+=-listIndex;
        }
        if(listIndex > (worldChunks.size())-1){
            while (listIndex > (worldChunks.size())-1){
                worldChunks.add(new ArrayList<>());
                worldChunkZeroRows.add(0);
                worldChunks.get(worldChunks.size()-1).add(new GameChunk(new Vector2(worldChunks.size()-1-worldChunkZeroID,0)));
            }
        }
        listIndex = (int)gameChunk.x + worldChunkZeroID;
        ArrayList<GameChunk> collumn = worldChunks.get(listIndex);
        int zeroYid = worldChunkZeroRows.get(listIndex);
        int listYIndex = (int)gameChunk.y + zeroYid;
        if(listYIndex<0){
            int step = -listYIndex-1;
            while (step>=0){
                //worldChunks.add(0,new ArrayList<GameChunk>());
                collumn.add(0,new GameChunk(new Vector2(collumn.get(zeroYid).getxID(),listYIndex+step-zeroYid)));
                worldChunkZeroRows.set(listIndex,worldChunkZeroRows.get(listIndex)+1);
                step-=1;
            }
        }
        if(listYIndex > (worldChunks.get(listIndex).size())-1){
            while (listYIndex > (worldChunks.get(listIndex).size())-1){
                collumn.add(new GameChunk(new Vector2(collumn.get(zeroYid).getxID(),worldChunks.get(listIndex).size()-zeroYid)));
            }
        }

    }

    public ArrayList<GameChunk> getGameChunkRegion(Rectangle region){
        ArrayList<GameChunk> gameChunkRegion = new ArrayList<>();
        int x1 = (int)Math.floor(region.x /GameChunk.defaultChunkSize.x);
        int x2 = (int)Math.ceil((region.x+region.width) /GameChunk.defaultChunkSize.x);
        int y1 = (int)Math.floor(region.y /GameChunk.defaultChunkSize.y);
        int y2 = (int)Math.ceil((region.y+region.height) /GameChunk.defaultChunkSize.y);

        for (int x = x1; x<=x2 ;x++){
            int row = x + worldChunkZeroID;
            while(row<=-1){
                addChunk(new Vector2(x, 0));
                row = x + worldChunkZeroID;
            }
            while(row>=worldChunks.size()){
                addChunk(new Vector2(x, 0));
                row = x + worldChunkZeroID;
            }
            for (int y = y1; y <= y2; y++) {
                int collumn = y + worldChunkZeroRows.get(row);
                while(collumn<=-1){
                    addChunk(new Vector2(x, y));
                    collumn = y + worldChunkZeroRows.get(row);
                }
                while(collumn>=worldChunks.get(row).size()){
                    addChunk(new Vector2(x, y));
                    collumn = y + worldChunkZeroRows.get(row);
                }
                try {
                    gameChunkRegion.add(worldChunks.get(row).get(collumn));
                } catch (IndexOutOfBoundsException e) {
                    addChunk(new Vector2(row, collumn));
                    collumn = y + worldChunkZeroRows.get(row);
                    gameChunkRegion.add(worldChunks.get(row).get(collumn));
                }
            }
        }
        return gameChunkRegion;
    }

    public ArrayList<GameChunk> getCameraGameChunkRegion(Rectangle region){
        ArrayList<GameChunk> gameChunkRegion = new ArrayList<>();
        int x1 = (int)Math.floor(region.x /GameChunk.defaultChunkSize.x);
        int x2 = (int)Math.ceil((region.x+region.width) /GameChunk.defaultChunkSize.x);
        int y1 = (int)Math.floor(region.y /GameChunk.defaultChunkSize.y);
        int y2 = (int)Math.ceil((region.y+region.height) /GameChunk.defaultChunkSize.y);

        for (int x = (x1 + worldChunkZeroID) >= 0 ? x1 : 0; x<=x2 && x + worldChunkZeroID<worldChunks.size();x++){
            int row = x + worldChunkZeroID;

            for (int y = (y1 + worldChunkZeroRows.get(row)) >= 0 ? y1 : 0; y <= y2 && y + worldChunkZeroRows.get(row) < worldChunks.get(row).size(); y++) {
                int collumn = y + worldChunkZeroRows.get(row);
                gameChunkRegion.add(worldChunks.get(row).get(collumn));
                gameChunkRegion.get(gameChunkRegion.size()-1).Reload(this.usedDatabase);
            }
        }
        return gameChunkRegion;
    }

    public void addGameObject(GameObject gameObject){
        ArrayList<GameChunk> chunkList = this.getGameChunkRegion(new Rectangle(gameObject.getX(),gameObject.getY(),gameObject.getWidth(),gameObject.getHeight()));

        if(chunkList.size() == 0){
            throw new RuntimeException();
        }



        for (GameChunk chunk :
                chunkList) {
            chunk.addGameObject(gameObject);
        }
    }

    @Override
    public void write(Json json) {
        json.setTypeName(this.getClass().toString());
        json.writeObjectStart("ChunkList");
        json.writeValue("items",worldChunks.stream().flatMap(ArrayList::stream).collect(Collectors.toList()), ArrayList.class,GameChunk.class);
        json.writeObjectEnd();

        HashMap<GameObject,ArrayList<GridPoint2>> gameObjects = new HashMap<>();
        for (ArrayList<GameChunk> worldChunk : worldChunks) {
            for (GameChunk gameChunk : worldChunk) {
                for (GameObject gameObject : gameChunk.getGameObjectsInChunk()) {
                    if(!gameObjects.containsKey(gameObject)){
                        gameObjects.put(gameObject,new ArrayList<GridPoint2>(){
                            {
                                this.add(new GridPoint2(gameChunk.getxID(),gameChunk.getyID()));
                            }
                        });
                    }else{
                        gameObjects.get(gameObject).add(new GridPoint2(gameChunk.getxID(),gameChunk.getyID()));
                    }
                }
            }
        }
        json.writeArrayStart("SaveData");
        for (GameObject gameObject : gameObjects.keySet()) {
            json.writeValue(new GameObjectSaveData(gameObject,gameObjects.get(gameObject)));
        }
        json.writeArrayEnd();

        json.writeObjectStart("GCDB");
        json.writeValue("db",usedDatabase, GCDB.class);
        json.writeObjectEnd();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void read(Json json, JsonValue jsonData) {
        {
            /* Reading CHUNKS */
            ArrayList<JsonValue> chunkValues = new ArrayList<>();
            try {
                if (json.readValue(ArrayList.class, jsonData.get("ChunkList").get("items")) != null) {
                    // if(((ArrayList<?>)chunkValues).get(0) instanceof JsonValue)
                    {
                        chunkValues = json.readValue(ArrayList.class, jsonData.get("ChunkList").get("items"));
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            ArrayList<GameChunk> chunks = new ArrayList<>();
            for (JsonValue val : chunkValues) {
                chunks.add(json.fromJson(GameChunk.class, val.toString()));
            }
            int collumn = chunks.get(0).getxID();
            int index = 0;
            int collumnIndex = 0;
            worldChunks = new ArrayList<>();
            worldChunks.add(new ArrayList<>());
            worldChunkZeroRows = new ArrayList<>();
            worldChunkZeroRows.add(0);
            while (true) {
                int counter = 0;
                while (index < chunks.size() && collumn == chunks.get(index).getxID()) {
                    if (chunks.get(index).getyID() == 0) {
                        worldChunkZeroRows.set(collumnIndex, counter);
                    }
                    worldChunks.get(collumnIndex).add(chunks.get(index));
                    index++;
                    counter++;
                }
                if (index >= chunks.size()) {
                    break;
                }

                collumnIndex++;
                worldChunks.add(new ArrayList<>());
                worldChunkZeroRows.add(0);
                collumn = chunks.get(index).getxID();
                if (chunks.get(index).getxID() == 0) {
                    this.worldChunkZeroID = collumnIndex;
                }
            }
        }
        /* Reading CHUNKS DONE */

        setUsedDatabase(json.readValue(GCDB.class,jsonData.get("GCDB").get("db")));

        /* Reading OBJECTS*/
        GameObjectSaveData[] data = json.readValue(GameObjectSaveData[].class,jsonData.get("SaveData"));
        for (GameObjectSaveData objData : data) {
            objData.Reload(this.getUsedDatabase());
            objData.getRef().setInWorld(this);
            for (GridPoint2 gridPoint2 : objData.getChunkLocs()) {
                worldChunks.get(gridPoint2.x + worldChunkZeroID).get(gridPoint2.y+this.worldChunkZeroRows.get(gridPoint2.x + worldChunkZeroID)).addGameObject(objData.getRef());
            }
        }
        /* READING OBJECTS DONE*/
    }


    public void update(DeltaTime delta, Rectangle camera) {
        handledChunks = getCameraGameChunkRegion(camera);
        this.generateObjectList(handledChunks);
        update(delta);
    }

    private void generateObjectList(ArrayList<GameChunk> handledChunks) {
        HashSet<GameObject> uniques = new HashSet<>();
        for (GameChunk chunk: handledChunks) {
            uniques.addAll(chunk.getGameObjectsInChunk());
        }
        this.chunkUniqueObjects = new ArrayList<>(uniques);
    }

    @Override
    public void update(DeltaTime delta) {
        for (GameObject obj : this.chunkUniqueObjects) {
            obj.update(delta);
        }
    }

    @Override
    public void draw(DeltaTime delta,Rectangle bounds, SpriteBatch batch) {
        for (GameObject obj : this.chunkUniqueObjects) {
            obj.draw(delta, obj.getGameBounds(),batch);
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public IDrawable clone() throws CloneNotSupportedException {
        return null;
    }

    public void updateSpritePosition(Vector2 oldPosition,Vector2 newPosition,GameObject gameObject) {
        ArrayList<GameChunk> preChunks = this.getGameChunkRegion(new Rectangle(oldPosition.x,oldPosition.y,gameObject.getWidth(),gameObject.getHeight()));
        ArrayList<GameChunk> postChunks = this.getGameChunkRegion(new Rectangle(newPosition.x,newPosition.y,gameObject.getWidth(),gameObject.getHeight()));
        if(!preChunks.equals(postChunks)){
            for(GameChunk chunk: preChunks){
                chunk.removeGameObject(gameObject);
            }
            for(GameChunk chunk: postChunks){
                if(!chunk.getGameObjectsInChunk().contains(gameObject)){
                    chunk.addGameObject(gameObject);
                }
            }
        }
    }

    public GCDB getUsedDatabase() {
        return usedDatabase;
    }

    public void setUsedDatabase(GCDB usedDatabase) {
        this.usedDatabase = usedDatabase;
    }

    @Override
    public void Reload(GCDB gcdb) {

    }

    public IMovable selectRandomSprite() {
        for (ArrayList<GameChunk> worldChunk : worldChunks) {
            for (GameChunk gameChunk : worldChunk) {
                for (GameObject gameObject : gameChunk.getGameObjectsInChunk()) {
                    return gameObject;
                }
            }
        }
        return null;
    }
}
