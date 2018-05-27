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
import com.ever.ending.interfaces.manipulation.IMovable;
import com.ever.ending.interfaces.IRenderable;
import com.ever.ending.interfaces.manipulation.ITypeable;
import com.ever.ending.management.DeltaTime;

import java.util.ArrayList;

public class UICollection extends UIElement implements ITypeable, IRenderable {


    private ArrayList<UIElement> collection;
    private FrameBuffer collectionRender;
    private SpriteBatch renderBatch;

    public OrthographicCamera getTransformMatrix() {
        return transformMatrix;
    }

    private OrthographicCamera transformMatrix;

    public UICollection(){

    }

    public UICollection(Rectangle location, UIScene parentScene){
        super(location,new GameSprite("Tests/manipulation/panel.png"),parentScene);
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
            }else if(uiElement instanceof UICollection){
                uiElement.drag(calc);
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

        for (UIElement uiElement : collection) {
            if(uiElement instanceof UICollection){
                updateTransformCollection((UICollection) uiElement);
            }
        }
    }

    @Override
    public void setPosition(Vector2 location) {
        Vector2 temp = new Vector2(this.getLocation().x,this.getLocation().y);
        super.setPosition(location);
        location.sub(temp);
        this.transformMatrix.translate(location);
        this.transformMatrix.update();
        renderBatch.setProjectionMatrix(this.transformMatrix.combined);

        for (UIElement uiElement : collection) {
            if(uiElement instanceof UICollection){
                updateTransformCollection((UICollection) uiElement);
            }
        }
    }

    private void updateTransformCollection(UICollection uiElement){
        OrthographicCamera newTransform =  new OrthographicCamera(((UICollection) uiElement).getRenderBuffer().getWidth(),((UICollection) uiElement).getRenderBuffer().getHeight());
        newTransform.translate(newTransform.viewportWidth/2,newTransform.viewportHeight/2);
        newTransform.translate(uiElement.getLocation().x,uiElement.getLocation().y);
        Vector2 stuff = new Vector2(uiElement.getParentLoc());
        newTransform.translate(new Vector2(stuff));
        newTransform.update();
        ((UICollection) uiElement).setTransformMatrix(newTransform);

        for (UIElement element : uiElement.getCollection()) {
            if(element instanceof UICollection){
                updateTransformCollection((UICollection) element);
            }
        }
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
                uiElement.getRelativeClickLocation().set(uiElement.relativeClickLocation(calc).add(uiElement.getPosition()).sub(new Vector2(this.getPosition()).add(this.getParentLoc())));
            }
        }
    }

    public void addElement(UIElement element){
        this.collection.add(element);
        element.setParent(this);
        element.setParentScene(this.getParentScene());

        if(element instanceof UICollection){
            ((UICollection) element).updateTransformCollection((UICollection) element);
        }
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
        this.getTransformMatrix().update();
        renderBatch.setProjectionMatrix(this.getTransformMatrix().combined);

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
        },this.getLocation().x + this.getParentLoc().x,this.getLocation().y + this.getParentLoc().y,this.getLocation().width,this.getLocation().height);
        if(this.isCanBeEdited() && this.getEditableController() != null){
            this.getEditableController().draw(delta,null,batch);
        }
    }

    @Override
    public void resize(Vector2 mod) {
        for (UIElement uiElement : collection) {
            if(uiElement.canEdit()){
                uiElement.resize(mod);
                return;
            }
        }

        super.resize(mod);
        collectionRender.dispose();
        collectionRender = new FrameBuffer(Pixmap.Format.RGBA8888,(int)this.getLocation().width,(int)this.getLocation().height,false);
        updateTransformCollection(this);
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
        Gdx.gl.glClearColor(1, 0, 0, 1);
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

    public void setTransformMatrix(OrthographicCamera transformMatrix) {
        this.transformMatrix = transformMatrix;
    }
}
