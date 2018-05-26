package com.ever.ending.interfaces.drawable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.ever.ending.management.DeltaTime;

public interface IAnim extends IDrawable {
    public void play();
    public void play(int frame);
    public void pause();
    public void stop();
    public boolean isPlaying();
    public void repeat(boolean bRepeat);
    public boolean isRepeating();
    public void reset();
    public void reset(int frame);

    public void setFrameTime(int deltaT);
    public void setFrameTime(int...deltaT);
    public int getFrameTime(int frame);
    public void update(DeltaTime delta);
    public void draw(DeltaTime delta, Rectangle box, SpriteBatch batch);
    public void display(int frame,Rectangle box, SpriteBatch batch);
}
