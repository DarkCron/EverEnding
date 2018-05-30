package com.ever.ending.gameobject;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.interfaces.drawable.IDrawable;

public class GameSprite implements IDrawable {

    public Sprite getBaseSprite() {
        return baseSprite;
    }

    private Sprite baseSprite;
    private String texPath;

    public GameSprite(){
    }

    public GameSprite(String texPath){
        this(texPath,new Texture(texPath));
    }

    public GameSprite(String texPath , int srcWidth, int srcHeight) {
        this(texPath,new Texture(texPath),srcWidth,srcHeight);
    }

    public GameSprite(String texPath , int srcX, int srcY, int srcWidth, int srcHeight) {
        this(texPath,new Texture(texPath),srcX,srcY,srcWidth,srcHeight);
    }

    public GameSprite(String texPath , Texture texture) {
        baseSprite = new Sprite(texture);
        this.texPath = texPath;
    }

    public GameSprite(String texPath , Texture texture, int srcWidth, int srcHeight) {
        baseSprite = new Sprite(texture, srcWidth, srcHeight);
        this.texPath = texPath;
    }

    public GameSprite(String texPath , Texture texture, int srcX, int srcY, int srcWidth, int srcHeight) {
        baseSprite = new Sprite(texture, srcX, srcY, srcWidth, srcHeight);
        this.texPath = texPath;
    }

    public GameSprite(String texPath , TextureRegion region) {
        baseSprite = new Sprite(region);
        this.texPath = texPath;
    }


    public GameSprite(String texPath , TextureRegion region, int srcX, int srcY, int srcWidth, int srcHeight) {
        baseSprite = new Sprite(region, srcX, srcY, srcWidth, srcHeight);
        this.texPath = texPath;
    }

    @Override
    public void update(DeltaTime delta) {

    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        baseSprite.setPosition(bounds.x,bounds.y);
        baseSprite.setSize(bounds.width,bounds.height);
        baseSprite.draw(batch);
    }

    @Override
    public void dispose() {
    }

    @Override
    public IDrawable clone() throws CloneNotSupportedException {
        return (GameSprite)super.clone();
    }

    @Override
    public Rectangle getScreenPos() {
        return new Rectangle(this.baseSprite.getX(),this.baseSprite.getY(),this.baseSprite.getWidth(),this.baseSprite.getWidth());
    }

    @Override
    public void write(Json json) {
        json.writeObjectStart("Type");
        json.writeValue("c",this.getClass().getName());
        json.writeObjectEnd();

        json.writeObjectStart("Tex");
        json.writeValue("texF",this.texPath);
        json.writeObjectEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        texPath = jsonData.get("Tex").get("texF").asString();
        baseSprite = new Sprite(new Texture(texPath));
    }
}
