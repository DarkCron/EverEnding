package com.ever.ending.management.json;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class JsonTester{
    Json tester;
    Object outputObj;

    public JsonTester(Object test, Class<?> c){
        tester = new Json();
        String base = tester.toJson(test,c);
        //String output = tester.prettyPrint(base);
        //System.out.println(output);
        outputObj = tester.fromJson(c,base);

        FileHandle file = Gdx.files.local("myfile.txt");
        file.writeString(base, false);
    }

    public Object getVal(){
        return outputObj;
    }
}
