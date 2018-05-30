package com.ever.ending.gameobject;


import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.interfaces.control.IController;
import com.ever.ending.interfaces.manipulation.IMovable;
import com.ever.ending.interfaces.resources.IReloadable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.management.resources.GCDB;
import com.ever.ending.management.resources.GameResource;
import com.ever.ending.world.GameWorld;

public class GameObject implements IMovable, IReloadable, IDrawable {

    private IDrawable resource;
    private int resourceID;
    private Rectangle gameBounds = new Rectangle(0,0,128,128);
    private GameWorld inWorld = null;

    public IDrawable getResource() {
        return resource;
    }

    public int getResourceID() {
        return resourceID;
    }

    public void setInWorld(GameWorld world) {
        this.inWorld = world;
    }

    public GameWorld getInWorld() {
        return inWorld;
    }

    public GameObject () {
        super();
    }

    public GameObject (GameResource source,Rectangle gameBounds, GameWorld gw) {
        try {
            this.resource = source.getResouceClone();
        }catch (Exception e){
            this.resource = null;
            e.printStackTrace();
        }
        this.setWorld(gw);
        this.gameBounds = gameBounds;
    }

    private void setWorld(GameWorld gw) {
        this.inWorld = gw;
        gw.addGameObject(this);
    }

    @Override
    public void move(Vector2 deltaMovement) {
        this.updateWorld(new Vector2(this.getX(),this.getY()), new Vector2(this.getX()+deltaMovement.x,this.getY()+deltaMovement.y));
        this.setPosition(this.getX()+deltaMovement.x,this.getY()+deltaMovement.y);
    }

    public void updateWorld(Vector2 oldPosition, Vector2 newPosition){
        if(this.inWorld!=null){
            this.inWorld.updateSpritePosition(oldPosition,newPosition,this);
        }
    }

    @Override
    public void drag(Vector2 mouseLoc, IController.KnownMouseButtons button) {
        //TODO
    }

    public void setPosition(float x, float y) {
        this.setPosition(new Vector2(x,y));
    }

    @Override
    public void setPosition(Vector2 location) {
        this.updateWorld(new Vector2(this.getX(),this.getY()),location);
        this.gameBounds.setPosition(location);
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(this.gameBounds.x,this.gameBounds.y);
    }


    @Override
    public void update(DeltaTime delta) {
        resource.update(delta);
    }

    @Override
    public void draw(DeltaTime delta,Rectangle bounds, SpriteBatch batch) {
        resource.draw(delta, bounds, batch);
    }

    @Override
    public void dispose() {

    }

    @Override
    public IDrawable clone() throws CloneNotSupportedException {
        return null;
    }

    public float getX() {
        return this.gameBounds.x;
    }

    public float getY() {
        return this.gameBounds.y;
    }

    public float getWidth() {
        return this.gameBounds.width;
    }

    public float getHeight() {
        return this.gameBounds.height;
    }

    @Override
    public void write(Json json) {
        json.writeObjectStart("Type");
        json.writeValue("c",this.getClass().getName());
        json.writeValue("id",this.resourceID);
        json.writeObjectEnd();

        json.writeObjectStart("WorldLoc");
        json.writeValue("bounds",gameBounds,Rectangle.class);
        json.writeObjectEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.resourceID = jsonData.get("Type").get("id").asInt();
        this.gameBounds = json.readValue(Rectangle.class,jsonData.get("WorldLoc").get("bounds"));
    }

    @Override
    public void Reload(GCDB gcdb) {
        try {
            this.resource = gcdb.reloadResource(this.resourceID);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public Rectangle getGameBounds() {
        return this.gameBounds;
    }

    @Override
    public Rectangle getScreenPos() {
        return this.getGameBounds();
    }
}
