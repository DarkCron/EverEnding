package com.ever.ending.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ever.ending.gameobject.GameSprite;
import com.ever.ending.interfaces.IMovable;
import com.ever.ending.interfaces.IRenderable;
import com.ever.ending.interfaces.ITypeable;
import com.ever.ending.management.DeltaTime;

import java.util.ArrayList;

public class UICollection extends UIElement implements ITypeable, IRenderable {


    private ArrayList<UIElement> collection;
    private FrameBuffer collectionRender;
    private SpriteBatch renderBatch;
    private OrthographicCamera transformMatrix;

    public UICollection(){

    }

    public UICollection(Rectangle location, UIScene parentScene){
        super(location,new GameSprite("Tests/UI/panel.png"),parentScene);
        this.collection = new ArrayList<>();
        collectionRender = new FrameBuffer(Pixmap.Format.RGBA8888,(int)location.width,(int)location.height,false);
        renderBatch = new SpriteBatch();
        this.transformMatrix = new OrthographicCamera(collectionRender.getWidth(),collectionRender.getHeight()){
            {
                this.translate(this.viewportWidth/2,this.viewportHeight/2);
                this.translate(location.x,location.y);
                this.update();
            }
        };
        renderBatch.setProjectionMatrix(this.transformMatrix.combined);
    }

    @Override
    public void drag(Vector2 mouseLoc) {
        for (UIElement uiElement : collection) {
            Vector2 calc = new Vector2(mouseLoc).sub(this.getPosition());
            if(uiElement.canEdit()){
                uiElement.drag(calc);
                return;
            }
        }

        super.drag(mouseLoc);
    }

    @Override
    public void move(Vector2 deltaMovement) {
        super.move(deltaMovement);
        this.transformMatrix.translate(deltaMovement);
        this.transformMatrix.update();
        renderBatch.setProjectionMatrix(this.transformMatrix.combined);
    }

    @Override
    public void setPosition(Vector2 location) {
        Vector2 temp = new Vector2(this.getLocation().x,this.getLocation().y);
        super.setPosition(location);
        location.sub(temp);
        this.transformMatrix.translate(location);
        this.transformMatrix.update();
        renderBatch.setProjectionMatrix(this.transformMatrix.combined);
    }

    @Override
    public IMovable select() {
        return super.select();
    }

    @Override
    public void unSelect() {
        for (UIElement uiElement : collection) {
            uiElement.unSelect();
        }
        super.unSelect();
    }

    @Override
    public boolean containsMouse(Vector2 mousePos) {
        for (UIElement uiElement : collection) {
            Vector2 calc = new Vector2(mousePos).sub(this.getPosition());
            if(uiElement.containsMouse(calc)){
                uiElement.select();
            }else if(uiElement.isCanBeEdited()){
                uiElement.unSelect();
            }
        }
        return super.containsMouse(mousePos);
    }

    @Override
    public void clicked(Vector2 mousePos) {
        super.clicked(mousePos);
        for (UIElement uiElement : collection) {
            Vector2 calc = new Vector2(mousePos).sub(this.getPosition());
            if(uiElement.containsMouse(calc)){
                uiElement.clicked(calc);
                uiElement.getRelativeClickLocation().set(uiElement.relativeClickLocation(calc).add(uiElement.getPosition()).sub(this.getPosition()));
            }
        }
    }

    public void addElement(UIElement element){
        this.collection.add(element);
        element.setParent(this);
        element.setParentScene(this.getParentScene());
    }


    public void removeElement(UIElement element){
        this.collection.remove(element);
    }

    public ArrayList<UIElement> getCollection() {
        return collection;
    }

    @Override
    public void update(DeltaTime delta) {
        super.update(delta);
        for (UIElement uiElement : collection) {
            uiElement.update(delta);
        }
    }

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        batch.draw(new Sprite(collectionRender.getColorBufferTexture()){
            {
                this.flip(false,true);
            }
        },this.getLocation().x,this.getLocation().y,this.getLocation().width,this.getLocation().height);
        if(this.isCanBeEdited() && this.getEditableController() != null){
            this.getEditableController().draw(delta,null,batch);
        }
    }

    @Override
    public void resize(Vector2 mod) {
        super.resize(mod);
        collectionRender.dispose();
        collectionRender = new FrameBuffer(Pixmap.Format.RGBA8888,(int)this.getLocation().width,(int)this.getLocation().height,false);
        this.transformMatrix = new OrthographicCamera(collectionRender.getWidth(),collectionRender.getHeight()){
            {
                this.translate(this.viewportWidth/2,this.viewportHeight/2);
                this.translate(getLocation().x,getLocation().y);
                this.update();
            }
        };
        this.setPosition(this.getPosition());
    }

    @Override
    public void dispose() {
        for (UIElement uiElement : collection) {
            uiElement.dispose();
        }
        collection.clear();
        if(collectionRender!=null){
            collectionRender.dispose();
        }
    }

    @Override
    public void setInput(String input) {
        for (UIElement uiElement : collection) {
            if(uiElement.isCanBeEdited() && uiElement instanceof ITypeable){
                ((ITypeable) uiElement).setInput(input);
            }
        }
    }

    @Override
    public String getInput() {
        for (UIElement uiElement : collection) {
            if(uiElement.isCanBeEdited() && uiElement instanceof ITypeable){
                return ((ITypeable) uiElement).getInput();
            }
        }
        return "";
    }

    @Override
    public void generateRender(DeltaTime delta) {
        for (UIElement uiElement : this.collection) {
            if(uiElement instanceof IRenderable){
                ((IRenderable) uiElement).generateRender(delta);
            }
        }

        renderBatch.begin();

        collectionRender.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //renderBatch.draw(GameConstants.DEBUG_TEX,this.getLocation().x,this.getLocation().y,400,400);
        super.draw(delta, null, renderBatch);
        for (UIElement uiElement : collection) {
            uiElement.draw(delta,null,renderBatch);
        }
        renderBatch.flush(); //flushed alpha channels
        collectionRender.end();
        renderBatch.end();
    }

    @Override
    public FrameBuffer getRenderBuffer() {
        return this.collectionRender;
    }
}
