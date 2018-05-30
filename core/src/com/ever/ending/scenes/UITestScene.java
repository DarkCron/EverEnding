package com.ever.ending.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ever.ending.gameobject.GameSprite;
import com.ever.ending.interfaces.IRenderable;
import com.ever.ending.interfaces.control.IController;
import com.ever.ending.interfaces.control.IControllerMouse;
import com.ever.ending.interfaces.drawable.IDrawable;
import com.ever.ending.interfaces.manipulation.*;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.DrawableScene;
import com.ever.ending.management.GameConstants;
import com.ever.ending.management.animation.BasicAnimation;
import com.ever.ending.management.input.Controller;
import com.ever.ending.ui.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class UITestScene extends DrawableScene {
    private UITest uiScene;

    public UITestScene(FrameBuffer mainCanvas){
        super(mainCanvas);
        this.uiScene = new UITest(mainCanvas);
    }

    @Override
    public void loadResources() {
    }

    @Override
    public void update(DeltaTime delta) {
        super.update(delta);
        this.uiScene.update(delta);
    }

    @Override
    public void draw(DeltaTime delta, SpriteBatch batch) {
        super.draw(delta, batch);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        getSceneBatch().setProjectionMatrix(this.getSceneCamera().combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //getSceneBatch().begin();
        this.uiScene.draw(delta,null);
        //getSceneBatch().end();
    }

    class UITest extends UIScene{

        public UITest(FrameBuffer mainCanvas){
            super(mainCanvas);
            this.setEditable(true);
            this.setSceneController(new UIController(){});
            Gdx.input.setInputProcessor(this.getSceneController());

            UICollection collection = new UICollection(new Rectangle(300,50,500,500),this);
            UICollection collection1 = new UICollection(new Rectangle(20,20,300,300),this);
            UICollection collection2 = new UICollection(new Rectangle(00,00,200,200),this);
            this.getElements().add(collection);
            UIPanel basicPanel = new UIPanel(new Rectangle(50,0,400,600),new GameSprite("Tests/UI/panel.png"),this);
            //this.getElements().add(basicPanel);
            //collection.addElement(basicPanel);

            Rectangle[] frames = new Rectangle[40];
            for(int i = 0; i<frames.length/2; i++){
                frames[i] = new Rectangle((i)%10,i/10,1,1);
            }
            frames[frames.length/2] = frames[(frames.length/2)-1];
            for(int i = (frames.length/2)-1; i>0; i--){
                frames[(frames.length/2-i)+frames.length/2] = new Rectangle((i)%10,(i)/10,1,1);
            }

            BasicAnimation anim = new BasicAnimation("Tests/UI/panelAnim.png",frames,100);
            UIPanel animatedPanel = new UIPanel(new Rectangle(500,0,400,600),anim,this);
            anim.play();
            anim.repeat(true);
            //this.getElements().add(animatedPanel);
            collection.addElement(animatedPanel);
            try {
                collection.addElement(new UIPanel(new Rectangle(250,200,400,600),anim.clone(),this){
                    {
                        if(this.getDrawable() instanceof BasicAnimation){
                            ((BasicAnimation) this.getDrawable()).play();
                            ((BasicAnimation) this.getDrawable()).repeat(true);
                            ((BasicAnimation) this.getDrawable()).setFrameTime(50);
                        }
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                UITextField textField = new UITextField(new Rectangle(0,0,200,80),anim.clone(),this);
                if(textField.getDrawable() instanceof BasicAnimation){
                    ((BasicAnimation) textField.getDrawable()).play();
                    ((BasicAnimation) textField.getDrawable()).repeat(true);
                    ((BasicAnimation) textField.getDrawable()).setFrameTime(70);
                }
                //this.getElements().add(textField);
                collection.addElement(textField);
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                UITextField textField = new UITextField(new Rectangle(800,200,200,80),anim.clone(),this);
                if(textField.getDrawable() instanceof BasicAnimation){
                    ((BasicAnimation) textField.getDrawable()).play();
                    ((BasicAnimation) textField.getDrawable()).repeat(true);
                    ((BasicAnimation) textField.getDrawable()).setFrameTime(70);
                }
                this.getElements().add(textField);
                //collection.addElement(textField);
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                UITextField textField = new UITextField(new Rectangle(0,0,500,80),anim.clone(),this);
                if(textField.getDrawable() instanceof BasicAnimation){
                    ((BasicAnimation) textField.getDrawable()).play();
                    ((BasicAnimation) textField.getDrawable()).repeat(true);
                    ((BasicAnimation) textField.getDrawable()).setFrameTime(70);
                }
                collection1.addElement(textField);
                //collection.addElement(textField);
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                UITextField textField = new UITextField(new Rectangle(0,0,500,80),anim.clone(),this);
                if(textField.getDrawable() instanceof BasicAnimation){
                    ((BasicAnimation) textField.getDrawable()).play();
                    ((BasicAnimation) textField.getDrawable()).repeat(true);
                    ((BasicAnimation) textField.getDrawable()).setFrameTime(70);
                }
                collection2.addElement(textField);
                //collection.addElement(textField);
            }catch (Exception e){
                e.printStackTrace();
            }

            try {
                UIStylizedText textField = new UIStylizedText(new Rectangle(0,0,100,100),anim.clone(),this);
                if(textField.getDrawable() instanceof BasicAnimation){
                    ((BasicAnimation) textField.getDrawable()).play();
                    ((BasicAnimation) textField.getDrawable()).repeat(true);
                    ((BasicAnimation) textField.getDrawable()).setFrameTime(70);
                }
                textField.setInput("Stylized.");
                this.getElements().add(textField);
                //collection.addElement(textField);
            }catch (Exception e){
                e.printStackTrace();
            }

            collection1.addElement(collection2);
            //this.getElements().add(collection1);
            collection.addElement(collection1);
            
            collection.addElement(new UIButton(new Rectangle(500,400,50,50),this){
                {
                    this.setClick_function((v)->{
                        if(this.getParent() instanceof UICollection){
                            for (UIElement uiElement : ((UICollection) this.getParent()).getCollection()) {
                                if(uiElement instanceof UITextField){
                                    ((UITextField) uiElement).setInput("Clicked form: "+v);
                                }
                            }
                        }
                    });
                }
            });

            collection.addElement(new UIButton(new Rectangle(50,400,50,50),this){
                {
                    this.setClick_function((v)->{
                        collection1.open();
                    });
                }
            });

            collection.addElement(new UIButton(new Rectangle(110,400,50,50),this){
                {
                    this.setClick_function((v)->{
                        collection1.close();
                    });
                }
            });

            this.getElements().add(new UIButton(new Rectangle(610,400,50,50),this){
                {
                    this.setClick_function((v)->{
                        try {
                            ScriptEngineManager factory = new ScriptEngineManager();
                            ScriptEngine engine = factory.getEngineByName("nashorn");
                            engine.eval("" +
                                    "var Rectangle = Java.type('com.badlogic.gdx.math.Rectangle');" +
                                    "var rect = new Rectangle(5,5,20,20);" +
                                    "print(rect);");
                            TextArea area = new TextArea();
                            area.setName("Script");
                            JFrame frame = new JFrame();
                            frame.add(area);
                            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            frame.addWindowListener(new WindowAdapter() {
                                @Override
                                public void windowClosed(WindowEvent e) {
                                    System.out.println(area.getText());
                                }
                            });
                            frame.setVisible(true);
                        }catch (Exception e){
                            System.out.println(e.getMessage());
                        }

                    });
                }
            });
        }

        @Override
        public void loadResources() {

        }

        @Override
        public void draw(DeltaTime delta, SpriteBatch batch) {
            for (UIElement uiElement : this.getElements()) {
                if(uiElement instanceof IRenderable){
                    ((IRenderable) uiElement).generateRender(delta);
                }
            }
            super.draw(delta, batch);
        }
    }

    class UIController extends Controller implements IControllerMouse {
        {
            this.setHoldHandler((a)->{
                switch (a) {
                    case MOVE_LEFT:
                        this.moveKeyLeft();
                        break;
                    case MOVE_RIGHT:
                        this.moveKeyRight();
                        break;
                    case MOVE_CAMERA_LEFT:
                        this.moveCameraKeyLeft();
                        break;
                    case MOVE_CAMERA_RIGHT:
                        this.moveCameraKeyRight();
                        break;
                }
            });
        }

        @Override
        public void moveCameraKeyRight() {
            if(this.getMovableObj() instanceof IResizable){
                ((IResizable) this.getMovableObj()).resize(new Vector2(1.05f,1.0f));
            }
        }

        @Override
        public void moveCameraKeyLeft() {
            if(this.getMovableObj() instanceof IResizable){
                ((IResizable) this.getMovableObj()).resize(new Vector2(0.95f,1.0f));
            }
        }

        @Override
        public void moveKeyLeft() {
            if(this.getMovableObj()!=null){
                if(this.getMovableObj() instanceof IDrawable){
                    if(this.getMovableObj() instanceof  UIElement){
                        if(((UIElement) this.getMovableObj()).getDrawable()instanceof BasicAnimation){
                            ((BasicAnimation) ((UIElement) this.getMovableObj()).getDrawable()).setFrameTime(((BasicAnimation) ((UIElement) this.getMovableObj()).getDrawable()).getFrameTime(0)-05);
                        }
                    }
                }
            }
        }

        @Override
        public void moveKeyRight() {
            if(this.getMovableObj()!=null){
                if(this.getMovableObj() instanceof IDrawable){
                    if(this.getMovableObj() instanceof  UIElement){
                        if(((UIElement) this.getMovableObj()).getDrawable()instanceof BasicAnimation){
                            ((BasicAnimation) ((UIElement) this.getMovableObj()).getDrawable()).setFrameTime(((BasicAnimation) ((UIElement) this.getMovableObj()).getDrawable()).getFrameTime(0)+5);
                        }
                    }
                }
            }
        }

        @Override
        public void update(DeltaTime delta) {
            super.update(delta);
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            this.mouseMove(new Vector2(screenX,GameConstants.SCREEN_HEIGHT -screenY));
            return super.mouseMoved(screenX, screenY);
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
            click(new Vector2(screenX,GameConstants.SCREEN_HEIGHT -screenY),knownMouseButton);
            return super.touchDown(screenX, screenY, pointer, button);
        }

        @Override
        public boolean scrolled(int amount) {
            this.scroll(amount);
            return super.scrolled(amount);
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            this.drag(new Vector2(screenX,GameConstants.SCREEN_HEIGHT -screenY),this.getLastPressedButton());
            return super.touchDragged(screenX, screenY, pointer);
        }

        @Override
        public void drag(Vector2 mouseLoc, KnownMouseButtons button) {
//            if(this.getMovableObj()!=null){
//                this.getMovableObj().drag(new Vector2(mouseLoc.x, mouseLoc.y),button);
//            }
            mouseLoc.scl(GameConstants.mouseMod);
            for (int i = uiScene.getElements().size()-1; i >= 0; i--) {
                if(uiScene.getElements().get(i).containsMouse(mouseLoc)){
                    uiScene.getElements().get(i).drag(mouseLoc,button);
                    return;
                }
            }
        }

        @Override
        public void mouseMove(Vector2 mouseLoc) {
            mouseLoc.scl(GameConstants.mouseMod);
            for (int i = uiScene.getElements().size()-1; i >= 0; i--) {
                uiScene.getElements().get(i).mouseMove(mouseLoc, this);
            }
        }

        @Override
        public void click(Vector2 mouseLoc, IController.KnownMouseButtons button) {
            mouseLoc.scl(GameConstants.mouseMod);
            for (int i = uiScene.getElements().size()-1; i >= 0; i--) {
                if(uiScene.getElements().get(i).containsMouse(mouseLoc)){
                    uiScene.getElements().get(i).clicked(mouseLoc,button);
                    return;
                }
            }
//            if(this.getMovableObj() instanceof ISelectable && this.getMovableObj() instanceof IMovable){
//                if(((ISelectable) this.getMovableObj()).containsMouse(mouseLoc)){
//                    ((ISelectable) this.getMovableObj()).clicked(mouseLoc,button);
//                }
//            }
        }

        @Override
        public void scroll(int amount) {
            if(this.getMovableObj() instanceof IEditable){
                if(!((IEditable) this.getMovableObj()).canEdit()){
                    return;
                }
            }else{
                return;
            }

            if(this.getMovableObj()!=null){
                if(this.getMovableObj() instanceof IResizable){
                    float mod = amount < 0 ? 1.1f : 0.9f;
                    ((IResizable) this.getMovableObj()).resize(new Vector2(mod,mod));
                }
            }
        }

        String allowedCharacters = ".,?!(){}[];'\"&|%*+-/\n\r";
        String newLineChars = "\n\r";


        @Override
        public boolean keyTyped(char character) {
            if('\t' == character){
                return true;
            }
            if(this.getMovableObj() instanceof ITypeable){
                String input = ((ITypeable) this.getMovableObj()).getInput();
                if(character == '\b'){
                    if(input.length() >=1){
                        ((ITypeable) this.getMovableObj()).setInput(input.substring(0,input.length()-1));
                    }
                }else if(newLineChars.indexOf(character) >= 0){
                    ((ITypeable) this.getMovableObj()).setInput(input+character + "\n");
                }else if(Character.isDigit(character) || Character.isAlphabetic(character) || Character.isWhitespace(character) || allowedCharacters.indexOf(character) >= 0){
                    ((ITypeable) this.getMovableObj()).setInput(input+character);
                }
            }

            return super.keyTyped(character);
        }
    }
}


