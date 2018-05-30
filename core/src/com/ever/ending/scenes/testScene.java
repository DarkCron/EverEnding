package com.ever.ending.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ever.ending.gameobject.GameObject;
import com.ever.ending.gameobject.GameSprite;
import com.ever.ending.interfaces.control.IControllableSimple;
import com.ever.ending.management.DeltaTime;
import com.ever.ending.management.DrawableScene;
import com.ever.ending.management.input.Controller;
import com.ever.ending.management.json.JsonTester;
import com.ever.ending.management.resources.GCDB;
import com.ever.ending.management.resources.GameResource;
import com.ever.ending.world.GameWorld;

public class testScene extends DrawableScene {

    private Texture testTexture;
    private GameResource resourceSprite;
    private GameObject someSprite;
    private GameWorld world;

    public testScene(FrameBuffer mainCanvas){
        super(mainCanvas);
        world = new GameWorld();
        someSprite = new GameObject(resourceSprite,new Rectangle(0,0,200,200),world);
        new GameObject(resourceSprite,new Rectangle(-100,0,200,200),world);

        this.setSceneController(new testSceneController(){
            {
                this.setMovableObj(someSprite);
            }
        });
        Gdx.input.setInputProcessor(this.getSceneController());

        GCDB gcdb = new GCDB();
        gcdb.addGameTileSource(resourceSprite);
        world.setUsedDatabase(gcdb);
        JsonTester tester = new JsonTester(gcdb,GCDB.class);
    }

    @Override
    public void loadResources() {
        testTexture = new Texture("badlogic.jpg");

        resourceSprite = new GameResource(new GameSprite("badlogic.jpg"));
    }

    @Override
    public void update(DeltaTime delta) {
        super.update(delta);
        world.update(delta, new Rectangle(((-this.getSceneCamera().viewportWidth+this.getSceneCamera().position.x)/2)*this.getSceneCamera().zoom,((-this.getSceneCamera().viewportHeight+this.getSceneCamera().position.y)/2)*this.getSceneCamera().zoom,this.getSceneCamera().viewportWidth*this.getSceneCamera().zoom,this.getSceneCamera().viewportHeight*this.getSceneCamera().zoom));
    }

    @Override
    public void draw(DeltaTime delta, SpriteBatch batch) {
        super.draw(delta, batch);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        getSceneBatch().setProjectionMatrix(this.getSceneCamera().combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        getSceneBatch().begin();
        //getSceneBatch().draw(testTexture, 0, 0);
        world.draw(delta,null,this.getSceneBatch());
        //someSprite.draw(this.getSceneBatch());
        getSceneBatch().end();
    }

    class testSceneController extends Controller {
        {
            this.setHoldHandler((a)->{
                switch (a) {
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
                    case ZOOM_CAMERA_IN:
                        this.zoomCameraIn();
                        break;
                    case ZOOM_CAMERA_OUT:
                        this.zoomCameraOut();
                        break;
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
                }
            });
            this.setPressHandler((a)->{
                switch (a){
                    case CONFIRM:
                        this.actionKeyConfirm();
                        break;
                    case TAB:
                        this.actionKeyTab();
                        break;
                    case MENU:
                        this.actionKeyMenu();
                        break;
                }
            });
        }
        @Override
        public void actionKeyEsc() {
            Gdx.app.exit();
        }

        @Override
        public void moveCameraKeyLeft() {
            getSceneCamera().translate(-10,0);
        }

        @Override
        public void moveCameraKeyRight() {
            getSceneCamera().translate(+10,0);
        }

        @Override
        public void moveCameraKeyDown() {
            getSceneCamera().translate(0,-10);
        }

        @Override
        public void moveCameraKeyUp() {
            getSceneCamera().translate(0,+10);
        }

        @Override
        public void zoomCameraIn() {
            getSceneCamera().zoom += getSceneCamera().zoom/5;
        }

        @Override
        public void zoomCameraOut() {
            getSceneCamera().zoom -= getSceneCamera().zoom/5;
        }

        @Override
        public void moveKeyUp() {
            if(this.getMovableObj()!=null){
                this.getMovableObj().move(new Vector2(0,+10));
            }
        }

        @Override
        public void moveKeyLeft() {
            if(this.getMovableObj()!=null){
                this.getMovableObj().move(new Vector2(-30,0));
            }
        }

        @Override
        public void moveKeyRight() {
            if(this.getMovableObj()!=null){
                this.getMovableObj().move(new Vector2(+10,0));
            }
        }

        @Override
        public void moveKeyDown() {
            if(this.getMovableObj()!=null){
                this.getMovableObj().move(new Vector2(0,-30));
            }
        }

        @Override
        public void actionKeyConfirm() {
            if(this.getMovableObj() instanceof IControllableSimple){
                ((IControllableSimple) this.getMovableObj()).confirm();
            }
        }

        @Override
        public void actionKeyTab() {

            JsonTester tester = new JsonTester(world,GameWorld.class);
            world = (GameWorld) tester.getVal();
        }

        @Override
        public void actionKeyMenu() {
            this.setMovableObj(world.selectRandomSprite());
        }
    }
}
