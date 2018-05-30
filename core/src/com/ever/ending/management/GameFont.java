package com.ever.ending.management;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.ever.ending.interfaces.resources.IResource;

public class GameFont implements Json.Serializable, IResource {
    public static final String DEFAULT_FONT = "Fonts/SourceSansPro-Regular.otf";
    private BitmapFont font;
    private String fontPath;
    private long resourceID;
    private int fontSize;

    public GameFont(){

    }

    public GameFont(String fontPath, int fontSize){
        this.fontPath = fontPath;
        this.fontSize = fontSize;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameters.size = fontSize;
        this.font = generator.generateFont(parameters);
        this.font.getData().setLineHeight(fontSize);
        generator.dispose();
    }


    public GameFont(String fontPath, int fontSize, FreeTypeFontGenerator.FreeTypeFontParameter parameters){
        this.fontPath = fontPath;
        this.fontSize = fontSize;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontPath));
        parameters.size = fontSize;
        this.font = generator.generateFont(parameters);
        this.font.getData().setLineHeight(fontSize);
        generator.dispose();
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }

    @Override
    public void setID(long id) {
        this.resourceID = id;
    }

    @Override
    public long getID() {
        return this.resourceID;
    }
}
