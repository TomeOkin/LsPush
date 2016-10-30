package com.tomeokin.lspush;

import android.graphics.Color;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tomeokin.lspush.data.model.Image;
import com.tomeokin.lspush.data.support.ImageTypeConverter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(JUnit4.class)
public class ExampleUnitTest {
    Gson gson = new GsonBuilder().registerTypeAdapter(Image.class, new ImageTypeConverter(new Gson())).create();

    @Test
    public void test() {

        Image image = new Image();
        image.setColor(Color.WHITE);
        image.setWidth(1);
        image.setHeight(1);
        image.setUrl("https");

        Body body = new Body();
        body.image = image;
        String json = gson.toJson(body);
        System.out.printf("image json: %s\n", json);

        Body one = gson.fromJson(json, Body.class);
        System.out.printf("image object %s", one.toString());
    }

    private class Body {
        public Image image;

        @Override
        public String toString() {
            return "Body{" +
                "image=" + image +
                '}';
        }
    }
}