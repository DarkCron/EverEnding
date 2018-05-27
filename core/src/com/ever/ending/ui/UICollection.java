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
import com.ever.ending.interfaces.IRenderable;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.interfaces.manipulation.IMovable;
import com.ever.ending.interfaces.manipulation.IOpenClose;
import com.ever.ending.interfaces.manipulation.ITypeable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.input.Controller;

import java.util.ArrayList;
import java.util.Collection;

public class UICollection extends UIElement implements ITypeable, IRenderable, IOpenClose {


    private ArrayList<UIElement> collection;
    private FrameBuffer collectionRender;
    private SpriteBatch renderBatch;

    public OrthographicCamera getTransformMatrix() {
        return transformMatrix;
    }

    private OrthographicCamera transformMatrix;
    private MenuBar menuBar = null;

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
    public void mouseMove(Vector2 mousePos, Controller controller) {
        super.mouseMove(mousePos, controller);
    }

    @Override
    public void clicked(Vector2 mousePos) {
        super.clicked(mousePos);
        if(menuBar != null){
            for (UIButton uiButton : menuBar.getButtons()) {
                if(uiButton.containsMouse(new Vector2(mousePos).sub(this.getPosition()))){
                    uiButton.clicked(mousePos);
                }
            }
        }
        for (int i = 0; i < collection.size(); i++) {
            UIElement uiElement = collection.get(i);
            Vector2 calc = new Vector2(mousePos).sub(this.getPosition());
            if(uiElement.containsMouse(calc)){
                uiElement.clicked(calc);
                uiElement.getRelativeClickLocation().set(uiElement.relativeClickLocation(calc).add(uiElement.getPosition()).sub(new Vector2(this.getPosition()).add(this.getParentLoc())));
            }
        }
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public void setMenuBar(MenuBar menuBar) {
        this.menuBar = menuBar;
    }

    public void addElement(UIElement element){
        this.collection.add(element);
        element.setParent(this);
        element.setParentScene(this.getParentScene());

        if(element instanceof UICollection){
            ((UICollection) element).updateTransformCollection((UICollection) element);
            ((UICollection) element).setMenuBar(new MenuBar((UICollection) element));
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

        if(menuBar != null){
            menuBar.update(delta);
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

        if(menuBar != null){
            menuBar.generateButtonSizes();
        }
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
        if(menuBar!=null){
            menuBar.draw(delta,this.renderBatch);
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


    @Override
    public boolean isOpen() {
        if(this.getParent() instanceof UICollection){
            return ((UICollection) this.getParent()).getCollection().contains(this);
        }
        return false;
    }

    @Override
    public void close() {
        if(this.isOpen() && this.getParent() instanceof UICollection){
            ((UICollection) this.getParent()).getCollection().remove(this);
        }
        this.unSelect();
    }

    @Override
    public void open() {
        if(!this.isOpen() && this.getParent() instanceof UICollection){
            ((UICollection) this.getParent()).getCollection().add(this);
        }
    }

    @Override
    public void setClosed(boolean closed) {
        if(closed){
            this.close();
        }else{
            this.open();
        }
    }
}

class MenuBar{

    private static final IDrawable CLOSE_BUTTON = new GameSprite("Tests/UI/ui_menupanel.png",0,0,64,64);
    private static final IDrawable MINIMIZE_BUTTON = new GameSprite("Tests/UI/ui_menupanel.png",64,0,64,64);
    private static final IDrawable MAXIMIZE_BUTTON = new GameSprite("Tests/UI/ui_menupanel.png",128,0,64,64);

    private static Rectangle BASE_SIZE = new Rectangle(0,0,32,32);
    private static Vector2 BASE_OFFSET = new Vector2(10,10);

    private UIButton close_button;
    private UIButton minimize_button;
    private UIButton maximize_button;

    private UICollection parent;

    public MenuBar(UICollection parent){
        this.parent = parent;
        Vector2 basePos = new Vector2(parent.getLocation().width - BASE_SIZE.width,
                parent.getLocation().height  - BASE_SIZE.height);
        close_button = new UIButton(new Rectangle(basePos.x,basePos.y,BASE_SIZE.width, BASE_SIZE.height),parent.getParentScene());
        close_button.setStates(CLOSE_BUTTON,CLOSE_BUTTON,CLOSE_BUTTON);
        close_button.setParent(parent);
        close_button.setClick_function((v)->{
            parent.close();
        });

        maximize_button = new UIButton(new Rectangle(basePos.x - BASE_OFFSET.x - BASE_SIZE.width,basePos.y,BASE_SIZE.width, BASE_SIZE.height),parent.getParentScene());
        maximize_button.setStates(MAXIMIZE_BUTTON,MAXIMIZE_BUTTON,MAXIMIZE_BUTTON);
        maximize_button.setParent(parent);

        minimize_button = new UIButton(new Rectangle(basePos.x - 2*BASE_OFFSET.x - 2*BASE_SIZE.width,basePos.y,BASE_SIZE.width, BASE_SIZE.height),parent.getParentScene());
        minimize_button.setStates(MINIMIZE_BUTTON,MINIMIZE_BUTTON,MINIMIZE_BUTTON);
        minimize_button.setParent(parent);
    }

    public void generateButtonSizes(){
        Vector2 basePos = new Vector2(parent.getLocation().width - BASE_SIZE.width,
                parent.getLocation().height  - BASE_SIZE.height);
        close_button.setLocation(new Rectangle(basePos.x,basePos.y,BASE_SIZE.width, BASE_SIZE.height));
        maximize_button.setLocation(new Rectangle(basePos.x - BASE_OFFSET.x - BASE_SIZE.width,basePos.y,BASE_SIZE.width, BASE_SIZE.height));
        minimize_button.setLocation(new Rectangle(basePos.x - 2*BASE_OFFSET.x - 2*BASE_SIZE.width,basePos.y,BASE_SIZE.width, BASE_SIZE.height));
    }

    public void update(DeltaTime time){
        if(close_button != null){
            close_button.update(time);
        }
        if(minimize_button != null){
            minimize_button.update(time);
        }
        if(maximize_button != null){
            maximize_button.update(time);
        }
    }

    public void draw(DeltaTime time, SpriteBatch batch){
        if(close_button != null){
            close_button.draw(time,null,batch);
        }
        if(minimize_button != null){
            minimize_button.draw(time,null,batch);
        }
        if(maximize_button != null){
            maximize_button.draw(time,null,batch);
        }
    }

    public Collection<UIButton> getButtons(){
        return new ArrayList<UIButton>(){
            {
                this.add(close_button);
                this.add(maximize_button);
                this.add(minimize_button);
            }
        };
    }
}