package com.ever.ending.interfaces;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.ever.ending.management.DeltaTime;

public interface IRenderable {
    public void generateRender(DeltaTime delta);
    public FrameBuffer getRenderBuffer();
}
