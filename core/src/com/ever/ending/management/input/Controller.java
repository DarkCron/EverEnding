package com.ever.ending.management.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.ever.ending.interfaces.manipulation.IMovable;
import com.ever.ending.interfaces.control.IController;
import com.ever.ending.interfaces.control.IControllerCamera;
import com.ever.ending.interfaces.control.IControllerMoveable;
import com.ever.ending.interfaces.control.IControllerSimpleActions;
import com.ever.ending.management.DeltaTime;

import java.util.*;
import java.util.stream.Collectors;


public class Controller implements InputProcessor,IControllerCamera,IControllerMoveable,IControllerSimpleActions {

    private static PriorityQueue<InputKey> knownKeys = new PriorityQueue<InputKey>(Comparator.comparingInt(InputKey::getAssignedKey)){
        {
            this.add(new InputKey(Input.Keys.A,GameActions.MOVE_LEFT));
            this.add(new InputKey(Input.Keys.D,GameActions.MOVE_RIGHT));
            this.add(new InputKey(Input.Keys.W,GameActions.MOVE_UP));
            this.add(new InputKey(Input.Keys.S,GameActions.MOVE_DOWN));
            this.add(new InputKey(Input.Keys.Q,GameActions.ZOOM_CAMERA_IN));
            this.add(new InputKey(Input.Keys.E,GameActions.ZOOM_CAMERA_OUT));

            this.add(new InputKey(Input.Keys.LEFT,GameActions.MOVE_CAMERA_LEFT));
            this.add(new InputKey(Input.Keys.RIGHT,GameActions.MOVE_CAMERA_RIGHT));
            this.add(new InputKey(Input.Keys.UP,GameActions.MOVE_CAMERA_UP));
            this.add(new InputKey(Input.Keys.DOWN,GameActions.MOVE_CAMERA_DOWN));

            this.add(new InputKey(Input.Keys.ENTER,GameActions.CONFIRM));
            this.add(new InputKey(Input.Keys.BACKSPACE,GameActions.CANCEL));
            this.add(new InputKey(Input.Keys.ESCAPE,GameActions.ESC));
            this.add(new InputKey(Input.Keys.SPACE,GameActions.MENU));
            this.add(new InputKey(Input.Keys.TAB,GameActions.TAB));
        }
    };
    private static final HashSet<InputKey> holdKeys = new HashSet<>();
    private static final HashSet<KnownMouseButtons> holdButtons = new HashSet<>();
    private static KnownMouseButtons lastPressedButton = null;

    public void update(DeltaTime delta){
        for (InputKey key: holdKeys) {
            holdHandler.handle(key.getAction());
        }
    }

    public IMovable getMovableObj() {
        return movableObj;
    }

    public void setMovableObj(IMovable movableObj) {
        this.movableObj = movableObj;
    }


    /**
     * Use this to create your own key event handler.
     */
    @FunctionalInterface
    public interface KeyCommander{
        void handle(IController.GameActions action);
    }

    public KeyCommander getHoldHandler() {
        return holdHandler;
    }

    public void setHoldHandler(KeyCommander holdHandler) {
        this.holdHandler = holdHandler;
    }

    public KeyCommander getPressHandler() {
        return pressHandler;
    }

    public void setPressHandler(KeyCommander pressHandler) {
        this.pressHandler = pressHandler;
    }

    private KeyCommander holdHandler = this::handleKeyHold;
    private KeyCommander pressHandler = this::handleKeyPress;

    private IMovable movableObj;

    private void handleKeyHold(IController.GameActions action){
        switch (action) {
            case MOVE_UP:
                this.moveKeyUp();
                break;
            case MOVE_DOWN:
                this.moveKeyDown();
                break;
            case MOVE_LEFT:
                this.moveKeyLeft();
                break;
            case MOVE_RIGHT:
                this.moveKeyRight();
                break;
            case MOVE_CAMERA_UP:
                this.moveCameraKeyUp();
                break;
            case MOVE_CAMERA_DOWN:
                this.moveCameraKeyDown();
                break;
            case MOVE_CAMERA_LEFT:
                this.moveCameraKeyLeft();
                break;
            case MOVE_CAMERA_RIGHT:
                this.moveCameraKeyRight();
                break;
        }
    }

    private void handleKeyPress(IController.GameActions action){
        switch (action) {
            case CONFIRM:
                this.actionKeyConfirm();
                break;
            case CANCEL:
                this.actionKeyCancel();
                break;
            case ESC:
                this.actionKeyEsc();
                break;
            case MENU:
                this.actionKeyMenu();
                break;
        }
    }



    @Override
    public void moveCameraKeyUp() {
        
    }

    @Override
    public void moveCameraKeyDown() {
        
    }

    @Override
    public void moveCameraKeyLeft() {
        
    }

    @Override
    public void moveCameraKeyRight() {
        
    }

    @Override
    public void zoomCameraIn() {

    }

    @Override
    public void zoomCameraOut() {

    }

    @Override
    public void moveKeyUp() {
        
    }

    @Override
    public void moveKeyDown() {
        
    }

    @Override
    public void moveKeyLeft() {
        
    }

    @Override
    public void moveKeyRight() {
        
    }

    @Override
    public void actionKeyConfirm() {
        
    }

    @Override
    public void actionKeyCancel() {
        
    }

    @Override
    public void actionKeyEsc() {
        
    }

    @Override
    public void actionKeyMenu() {
        
    }

    @Override
    public void actionKeyTab() {

    }

    @Override
    public boolean keyDown(int keycode) {
        List<InputKey> find = knownKeys.stream().filter(inputKey -> inputKey.getAssignedKey() == keycode).collect(Collectors.toList());
        holdKeys.addAll(find);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        List<InputKey> find = knownKeys.stream().filter(inputKey -> inputKey.getAssignedKey() == keycode).collect(Collectors.toList());
        for (InputKey key: find) {
            pressHandler.handle(key.getAction());
        }
        holdKeys.removeAll(find);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    public boolean isButtonDown(KnownMouseButtons button){
        return holdButtons.contains(button);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        KnownMouseButtons knownMouseButton = null;
        if(button == KnownMouseButtons.LEFT.getButton()){
            knownMouseButton = KnownMouseButtons.LEFT;
        }else
        if(button == KnownMouseButtons.RIGHT.getButton()){
            knownMouseButton = KnownMouseButtons.RIGHT;
        }else
        if(button == KnownMouseButtons.MIDDLE.getButton()){
            knownMouseButton = KnownMouseButtons.MIDDLE;
        }
        holdButtons.add(knownMouseButton);
        lastPressedButton = knownMouseButton;
        return false;
    }

    public KnownMouseButtons getLastPressedButton(){
        return lastPressedButton;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        KnownMouseButtons knownMouseButton = null;
        if(button == KnownMouseButtons.LEFT.getButton()){
            knownMouseButton = KnownMouseButtons.LEFT;
        }else
        if(button == KnownMouseButtons.RIGHT.getButton()){
            knownMouseButton = KnownMouseButtons.RIGHT;
        }else
        if(button == KnownMouseButtons.MIDDLE.getButton()){
            knownMouseButton = KnownMouseButtons.MIDDLE;
        }
        holdButtons.remove(knownMouseButton);
        lastPressedButton = null;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
