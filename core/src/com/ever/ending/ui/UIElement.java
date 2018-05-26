package com.ever.ending.ui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.interfaces.IEditable;
import com.ever.ending.interfaces.IMovable;
import com.ever.ending.interfaces.IResizable;
import com.ever.ending.interfaces.ISelectable;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.GameFont;

public abstract class UIElement implements ISelectable, IMovable, IDrawable, IResizable, IEditable {

    private UIScene parentScene;
    private IDrawable drawable;
    private Rectangle location;
    private UIElement parent;

    private Vector2 relativeClickLocation;

    private boolean canBeEdited = false;
    private boolean beingEdited = false;
    private UIElement editableController = null;




    public UIElement(){

    }

    public UIElement(Rectangle location, IDrawable drawable, UIScene parentScene){
        this.drawable = drawable;
        this.location = location;
        this.parentScene = parentScene;
    }

    public UIScene getParentScene() {
        return parentScene;
    }

    public void setParentScene(UIScene parentScene) {
        this.parentScene = parentScene;
    }

    @Override
    public IDrawable clone() throws CloneNotSupportedException {
        return (IDrawable) super.clone();
    }

    public IDrawable getDrawable() {
        return drawable;
    }

    public Rectangle getLocation() {
        return location;
    }

    @Override
    public Rectangle getScreenPos() {
        return this.getLocation();
    }

    @Override
    public void move(Vector2 deltaMovement) {
        this.getLocation().setX(this.getLocation().x + deltaMovement.x);
        this.getLocation().setY(this.getLocation().y + deltaMovement.y);
    }

    @Override
    public void drag(Vector2 mouseLoc) {
        if(!this.canEdit()){
            return;
        }
        this.setPosition(mouseLoc.add(this.getRelativeClickLocation()));
    }

    @Override
    public void setPosition(Vector2 location) {
        this.getLocation().setPosition(location);
    }

    @Override
    public Vector2 getPosition() {
        return new Vector2(this.getLocation().x,this.getLocation().y);
    }

    @Override
    public void resize(Vector2 mod) {
        this.getLocation().setSize(this.getSize().x * mod.x,this.getSize().y * mod.y);
        this.generateEditController();
    }

    @Override
    public void setSize(Vector2 size) {
        this.getLocation().setSize(size.x,size.y);
        this.generateEditController();
    }

    public void setDrawable(IDrawable drawable) {
        this.drawable = drawable;
    }

    @Override

    public Vector2 getSize() {
        return new Vector2(this.getLocation().getWidth(),this.getLocation().getHeight());
    }

    @Override
    public IMovable select() {
        if(this.parentScene.canEdit()){
           this.canBeEdited = true;
           this.generateEditController();
        }
        return this;
    }

    private void generateEditController() {
        if(this.editableController==null){
            this.editableController = new UICheckBox(new Rectangle(0,this.getLocation().height + 10,32,32),this.beingEdited,this.getParentScene(),"Edit?");
        }else{ //LEAK PREVENTION
            this.editableController.setPosition(new Vector2(0,this.getLocation().height + 10));
        }
        this.editableController.setParent(this);
    }

    @Override
    public void unSelect() {
        if(this.canBeEdited){
            this.canBeEdited = false;
            this.beingEdited = false;
            if(this.editableController != null && this.editableController instanceof UICheckBox){
                ((UICheckBox) this.editableController).setChecked(false);
            }
        }
    }


    @Override
    public void clicked(Vector2 mousePos) {
        Vector2 calc = new Vector2(mousePos.x - this.getPosition().x, mousePos.y - this.getPosition().y);
        this.relativeClickLocation = relativeClickLocation(calc);
        if(this.canBeEdited && this.editableController != null && this.editableController.getLocation().contains(calc)){
            this.editableController.clicked(mousePos);
            if(this.editableController instanceof UICheckBox){
                this.setEditable(((UICheckBox) this.editableController).isChecked());
            }
        }
    }

    @Override
    public Vector2 relativeClickLocation(Vector2 mousePos) {
        return new Vector2(this.getParentLoc()).sub(mousePos);
    }

    @Override
    public Vector2 getRelativeClickLocation() {
        return relativeClickLocation;
    }

    @Override
    public boolean containsMouse(Vector2 mousePos) {
        boolean contains = false;
        contains = this.getLocation().contains(mousePos);
        if(!contains && this.canBeEdited && this.editableController != null){
            Vector2 calc = new Vector2(mousePos.x - this.getPosition().x, mousePos.y - this.getPosition().y);
            contains = this.editableController.getLocation().contains(calc);
        }
        return contains;
    }

    @Override
    public void update(DeltaTime delta) {
        if(this.getDrawable()!=null){
            this.getDrawable().update(delta);
        }

        if(this.canBeEdited && this.editableController != null){
            this.editableController.update(delta);
        }
    }

    BitmapFont font = new GameFont("Fonts/SourceSansPro-Regular.otf",16).getFont();

    @Override
    public void draw(DeltaTime delta, Rectangle bounds, SpriteBatch batch) {
        if(this.getDrawable() != null){
            if(this.getParent() == null){
                this.getDrawable().draw(delta,this.getLocation(),batch);
                font.draw(batch,this.getClass().getName(),this.getLocation().x,this.getLocation().y  );
            }else{
                Rectangle actualLoc = new Rectangle(this.getLocation());
                actualLoc.setPosition(this.getParentLoc().x + actualLoc.x, this.getParentLoc().y + actualLoc.y);
                // actualLoc.setPosition(this.getParentLoc().x, this.getParentLoc().y);
                this.getDrawable().draw(delta,actualLoc,batch);
                font.draw(batch,this.getClass().getName(),actualLoc.x,actualLoc.y  );
            }
        }

        if(this.canBeEdited && this.editableController != null){
            this.editableController.draw(delta,null,batch);
        }
    }

    @Override
    public void dispose() {
        if(this.getDrawable() != null){
            this.getDrawable().dispose();
        }

        this.font.dispose();

        if(this.editableController!=null){
            this.editableController.dispose();
        }

        this.drawable = null;
        this.font = null;
        this.editableController = null;
    }


    @Override
    public void write(Json json) {
        throw new RuntimeException();
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        throw new RuntimeException();
    }

    public UIElement getParent() {
        return parent;
    }

    public void setParent(UIElement parent) {
        this.parent = parent;
    }

    protected void setLocation(Rectangle tf_bounds) {
        this.location = tf_bounds;
    }

    @Override
    public boolean canEdit() {
        return this.beingEdited;
    }

    @Override
    public void setEditable(boolean editable) {
        this.beingEdited = editable;
    }

    public boolean isCanBeEdited() {
        return canBeEdited;
    }

    public void setCanBeEdited(boolean canBeEdited) {
        this.canBeEdited = canBeEdited;
    }

    public UIElement getEditableController() {
        return editableController;
    }

    public void setEditableController(UIElement editableController) {
        this.editableController = editableController;
    }

    public Vector2 getParentLoc(){
        if(this.parent == null){
            return Vector2.Zero;
        }else{
            Vector2 temp = this.parent.getParentLoc();
            return new Vector2(temp.x + this.parent.getLocation().x, temp.y + this.parent.getLocation().y);
        }
    }
}
