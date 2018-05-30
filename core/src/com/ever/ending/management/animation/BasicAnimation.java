package com.ever.ending.management.animation;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.interfaces.drawable.IAnim;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.interfaces.drawable.IDrawable;

import java.util.Arrays;

public class BasicAnimation implements IAnim, Json.Serializable {
    private TextureRegion[] frames;
    private int[] frameTimes;
    private String texPath;

    private int currentFrame = -1;
    private boolean bIsPlaying = false;
    private boolean bIsRepeating = false;
    private int timePassed = 0;

    public BasicAnimation(){}

    public BasicAnimation(String texName, TextureRegion[] frames, int[] frameTimes){
        this.frames =  Arrays.copyOf(frames,frames.length);
        this.frameTimes =  Arrays.copyOf(frameTimes,frames.length);
        this.currentFrame = 0;
        this.setFrameTime(frameTimes);
        this.texPath = texName;
    }

    public BasicAnimation(String texName, TextureRegion[] frames, int frameTime){
        this.frames = Arrays.copyOf(frames,frames.length);
        this.frameTimes = new int[frames.length];
        for(int i = 0; i < frameTimes.length; i++){
            this.frameTimes[i] = frameTime;
        }
        this.currentFrame = 0;
        this.setFrameTime(frameTime);
        this.texPath = texName;
    }

    public BasicAnimation(String texName, Rectangle[] frames, int frameTime){
        this.frames = new TextureRegion[frames.length];
        for(int i = 0; i < frames.length; i++){
            this.frames[i] = new TextureRegion(new Texture(texName),(int)frames[i].x,(int)frames[i].y,(int)frames[i].width,(int)frames[i].height);
        }
        this.frameTimes = new int[frames.length];
        for(int i = 0; i < frameTimes.length; i++){
            this.frameTimes[i] = frameTime;
        }
        this.currentFrame = 0;
        this.setFrameTime(frameTime);
        this.texPath = texName;
    }

    protected void Reload(String texName, TextureRegion[] frames, int[] frameTimes){
        this.frames = frames;
        this.frameTimes = frameTimes;
        this.currentFrame = 0;
        this.setFrameTime(frameTimes);
        this.texPath = texName;
    }

    @Override
    public void play() {
        this.bIsPlaying = true;
    }

    @Override
    public void play(int frame) {
        this.bIsPlaying = true;
        this.currentFrame = frame;
    }

    @Override
    public void stop() {
        this.reset();
    }

    @Override
    public void pause() {
        this.bIsPlaying = false;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void repeat(boolean bRepeat) {
        this.bIsRepeating = bRepeat;
    }

    @Override
    public boolean isRepeating() {
        return this.bIsRepeating;
    }

    /**
     * Turns of repeating
     */
    @Override
    public void reset() {
        this.currentFrame = 0;
        this.bIsPlaying = false;
        this.bIsRepeating = false;
        this.timePassed = 0;
    }

    @Override
    public void reset(int frame) {
        this.currentFrame = frame;
        this.bIsPlaying = false;
        this.bIsRepeating = false;
    }

    @Override
    public void setFrameTime(int deltaT) {
        for (int i =0; i<frameTimes.length;i++) {
            frameTimes[i] = deltaT;
        }
    }

    @Override
    public void setFrameTime(int[] deltaT) {
        for(int i = 0; i<deltaT.length; i++){
            if(i == frameTimes.length-1){
                break; //failsafe
            }
            this.frameTimes[i] = deltaT[i];
        }
    }

    @Override
    public int getFrameTime(int frame) {
        if(frame >= frameTimes.length-1){
            return frameTimes[0];
        }
        return this.frameTimes[frame];
    }

    @Override
    public void update(DeltaTime delta) {
        if(bIsPlaying){
            timePassed+=delta.getMillis();
        }
        if(timePassed>=frameTimes[currentFrame]){
            timePassed=0;
            currentFrame++;
            if(currentFrame>=frameTimes.length){
                if(bIsRepeating){
                    currentFrame = 0;
                }else{
                    stop();
                }
            }
        }
    }

    @Override
    public void dispose() {

    }

    @Override
    public IDrawable clone() throws CloneNotSupportedException {
        return new BasicAnimation(this.texPath,this.frames,this.frameTimes);
    }

    @Override
    public Rectangle getScreenPos() {
        return null;
    }

    @Override
    public void draw(DeltaTime delta, Rectangle box, SpriteBatch batch) {
        batch.draw(frames[currentFrame],box.x,box.y,box.width,box.height);
    }

    @Override
    public void display(int frame, Rectangle box, SpriteBatch batch) {
        batch.draw(frames[frame],box.x,box.y,box.width,box.height);
    }

    @Override
    public void write(Json json) {
        json.writeObjectStart("Anim");
        json.writeValue("path",this.texPath);
        json.writeObjectEnd();

        json.writeArrayStart("Frames");
        for(TextureRegion region : frames){
           // json.writeValue("frame",new Rectangle(region.getRegionX(),region.getRegionY(),region.getRegionWidth(),region.getRegionHeight()),Rectangle.class);
            json.writeValue(new Rectangle(region.getRegionX(),region.getRegionY(),region.getRegionWidth(),region.getRegionHeight()));
        }

        json.writeArrayEnd();

        json.writeArrayStart("Frame Times");
        for(int time : frameTimes){
            json.writeValue(time);
        }
        json.writeArrayEnd();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        Rectangle[] frames = json.readValue(Rectangle[].class, jsonData.get("Frames"));
        this.frameTimes = json.readValue(int[].class, jsonData.get("Frame Times"));
        this.texPath = jsonData.get("Anim").get("path").asString();
        Texture tex = new Texture(this.texPath);
        this.frames = new TextureRegion[frames.length];
        for(int i = 0; i<frames.length;i++){
            this.frames[i] = new TextureRegion(tex,frames[i].x,frames[i].y,frames[i].width,frames[i].height);
        }
    }
}
