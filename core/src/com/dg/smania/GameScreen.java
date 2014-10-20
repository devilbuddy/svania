package com.dg.smania;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by magnus on 2014-10-14.
 */
public class GameScreen {

    private final SpriteBatch spriteBatch;
    private final Assets assets;
    private final World world;

    private final ShapeRenderer shapeRenderer;

    private final OrthographicCamera hudCamera;
    private final CameraController cameraController;
    private final MapRenderer mapRenderer;

    private ParallaxBackground parallaxBackground;
    int buttonSize;

    public GameScreen(SpriteBatch spriteBatch, Assets assets, World world) {
        this.spriteBatch = spriteBatch;
        this.assets = assets;
        this.world = world;

        hudCamera = new OrthographicCamera();
        cameraController = new CameraController();
        mapRenderer = new MapRenderer(spriteBatch, cameraController);

        ParallaxBackground.ParallaxLayer layer = new ParallaxBackground.ParallaxLayer(assets.background,
                new Vector2(0.5f, 0.5f), new Vector2(0, 0), new Vector2(0, 0), 2.5f);

        parallaxBackground = new ParallaxBackground(
                new ParallaxBackground.ParallaxLayer[] { layer }, 100, 100, new Vector2(100, 100),
                spriteBatch);


        Assets.MapData mapData = assets.loadMap("level.json");
        mapRenderer.setMap(mapData.tiledMap);
        world.setCollissionMap(mapData.collisionMap);
        int worldWidth = mapData.width * Assets.TILE_SIZE;
        int worldHeight = mapData.height * Assets.TILE_SIZE;
        cameraController.setWorldSize(worldWidth, worldHeight);
        cameraController.setFocus(world.player);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    public void resize(int width, int height) {
        buttonSize = width/6;
        hudCamera.setToOrtho(false, width, height);
        cameraController.resize(width, height);
    }
float foo = 0;
    public void update(float delta) {
        cameraController.update(delta);
        world.update(delta);

        foo+=delta;
        if(foo > 5) {
            cameraController.shakeScreen(0.5f, 0.5f);
            foo = 0;
        }
    }

    public void draw() {



        parallaxBackground.camera.viewportWidth = cameraController.width;
        parallaxBackground.camera.viewportHeight = cameraController.height;
        parallaxBackground.camera.update();
        parallaxBackground.setPos(cameraController.getCamera().position.x, cameraController.getCamera().position.y);

        parallaxBackground.color.set(Color.WHITE);
        parallaxBackground.render();


        mapRenderer.draw();

        spriteBatch.setProjectionMatrix(cameraController.getCamera().combined);
        spriteBatch.begin();
        spriteBatch.setColor(Color.WHITE);
        for(int i = 0; i < world.entities.size(); i++) {
            Entity entity = world.entities.get(i);
            Animation animation;
            switch (entity.state) {
                case STANDING: {
                    if (entity.direction == Entity.Direction.RIGHT) {
                        animation = assets.standRightAnimation;
                    } else {
                        animation = assets.standLeftAnimation;
                    }
                    break;
                }
                case JUMPING: {
                    if (entity.direction == Entity.Direction.RIGHT) {
                        animation = assets.jumpRightAnimation;
                    } else {
                        animation = assets.jumpLeftAnimation;
                    }
                    break;
                }
                default:{
                    if(entity.direction == Entity.Direction.RIGHT) {
                        animation = assets.walkRightAnimation;
                    } else {

                        animation = assets.walkLeftAnimation;
                    }
                }
            }
            spriteBatch.draw(animation.getKeyFrame(entity.stateTime), entity.position.x - 2, entity.position.y);
        }

        spriteBatch.setProjectionMatrix(hudCamera.combined);
        spriteBatch.setColor(1, 1, 1, 0.5f);
        assets.patch.draw(spriteBatch, 1, 1, buttonSize, buttonSize);


        spriteBatch.end();

        /*
        shapeRenderer.setProjectionMatrix(cameraController.getCamera().combined);
        shapeRenderer.begin();
        shapeRenderer.rect(world.player.position.x, world.player.position.y, world.player.width, world.player.height);
        shapeRenderer.end();
        */
    }
}
