package com.dg.smania;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by magnus on 2014-10-14.
 */
public class CameraController {

    private static final String tag = "CameraController";

    private final OrthographicCamera camera = new OrthographicCamera();

    private Entity focus;
    private Vector3 focusPosition = new Vector3();
    private Vector3 tmp = new Vector3();

    float camMinX;
    float camMaxX;;
    float camMinY;;
    float camMaxY;

    int width;
    int height;

    int worldWidth;
    int worldHeight;

    public void resize(int width, int height) {
        Gdx.app.log(tag, "resize " + width + " " + height);
        this.width = width;
        this.height = height;
        camera.setToOrtho(false, width, height);
        camera.update();
        updateCameraBounds();
    }

    public void setFocus(Entity entity) {
        focus = entity;
    }

    public void setWorldSize(int worldWidth, int worldHeight) {
        Gdx.app.log(tag, "setWorldSize: " + worldWidth + " " + worldHeight);
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        updateCameraBounds();
    }

    private void updateCameraBounds() {
        camMinX = camera.viewportWidth / 2;
        camMaxX = worldWidth - camMinX;
        camMinY = camera.viewportHeight / 2;
        camMaxY = worldHeight - camMinY;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void update(float delta) {
        if(focus != null) {

            focusPosition.set((int)focus.position.x, (int)focus.position.y, 0);

            if(focusPosition.x < camMinX)
                focusPosition.x = camMinX;
            if(focusPosition.x > camMaxX)
                focusPosition.x = camMaxX;

            if(focusPosition.y < camMinY)
                focusPosition.y = camMinY;
            if(focusPosition.y > camMaxY)
                focusPosition.y = camMaxY;

            camera.position.lerp(focusPosition, 5f*delta);

            float len = tmp.set(camera.position).sub(focusPosition).len();
            if(len < 0.1f) {
                camera.position.set(focusPosition);
            }

            camera.update();
        }
    }

}
