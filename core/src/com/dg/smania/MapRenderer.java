package com.dg.smania;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by magnus on 2014-10-14.
 */
public class MapRenderer {

    private final SpriteBatch spriteBatch;
    private final CameraController cameraController;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    public MapRenderer(SpriteBatch spriteBatch, CameraController cameraController) {
        this.spriteBatch = spriteBatch;
        this.cameraController = cameraController;
    }

    public void setMap(TiledMap tiledMap) {
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, spriteBatch);
    }

    public void draw() {
        if (tiledMapRenderer != null) {

            spriteBatch.setColor(Color.WHITE);
            tiledMapRenderer.setView(cameraController.getCamera());
            tiledMapRenderer.render();
        }
    }


}
