package com.dg.smania;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SGame extends ApplicationAdapter {

    private static final int MAX_UPDATE_ITERATIONS = 5;
    private static final float FIXED_TIMESTEP = 1f / 60f;
    private float accumulator = 0;

    // viewport width
    private int width = 160;
    private int height;

    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;

    private World world;

    private Assets assets;
    private GameScreen gameScreen;

    @Override
	public void create () {
		spriteBatch = new SpriteBatch();
        camera = new OrthographicCamera();

        world = new World();

        assets = new Assets();
        gameScreen = new GameScreen(spriteBatch, assets, world);
	}

    @Override
    public void dispose () {
        spriteBatch.dispose();
    }

    @Override
    public void resize (int w, int h) {
        float ratio = (float)h/(float)w;
        height = (int)(width * ratio);
        camera.setToOrtho(false, width, height);
        camera.update();

        gameScreen.resize(width, height);
    }

    @Override
	public void render () {
        // step
        accumulator += Gdx.graphics.getRawDeltaTime();
        int iterations = 0;
        while (accumulator > FIXED_TIMESTEP && iterations < MAX_UPDATE_ITERATIONS) {
            update(FIXED_TIMESTEP);
            accumulator -= FIXED_TIMESTEP;
            iterations++;
        }
        draw();
	}

    private void update(float delta) {
        gameScreen.update(delta);
    }


    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameScreen.draw();
    }
}
