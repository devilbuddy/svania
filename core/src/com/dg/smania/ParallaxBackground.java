package com.dg.smania;


import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class ParallaxBackground {

    public static class ParallaxLayer {
        public TextureRegion region;
        public Vector2 parallaxRatio;
        public Vector2 startPosition;
        public Vector2 padding;
        public float scale;

        public ParallaxLayer(TextureRegion region, Vector2 parallaxRatio, Vector2 padding) {
            this(region, parallaxRatio, new Vector2(0, 0), padding, 1);
        }

        /**
         * @param region        the TextureRegion to draw , this can be any width/height
         * @param parallaxRatio the relative speed of x,y {@link (ParallaxLayer[], float, float, Vector2)}
         * @param startPosition the init position of x,y
         * @param padding       the padding of the region at x,y
         */
        public ParallaxLayer(TextureRegion region, Vector2 parallaxRatio, Vector2 startPosition, Vector2 padding, float scale) {
            this.region = region;
            this.parallaxRatio = parallaxRatio;
            this.startPosition = startPosition;
            this.padding = padding;
            this.scale = scale;
        }
    }

    private ParallaxLayer[] layers;
    public Camera camera;
    private SpriteBatch batch;
    private Vector2 speed = new Vector2();

    /**
     * @param layers The  background layers
     * @param width  The screenWith
     * @param height The screenHeight
     * @param speed  A Vector2 attribute to point out the x and y speed
     */
    public ParallaxBackground(ParallaxLayer[] layers, float width, float height, Vector2 speed, SpriteBatch spriteBatch) {
        this.layers = layers;
        this.speed.set(speed);
        camera = new OrthographicCamera(width, height);
        batch = spriteBatch;
    }

    public void setPos(float x, float y) {
        this.camera.position.set(x, y, 0);
    }

    public Color color = new Color(Color.WHITE);

    public void render() {
        batch.setColor(color);
        // this.camera.position.add(speed.x*delta,speed.y*delta, 0);

        for (ParallaxLayer layer : layers) {
            float w = layer.region.getRegionWidth() * layer.scale;
            float h = layer.region.getRegionHeight() * layer.scale;

            batch.setProjectionMatrix(camera.projection);
            batch.begin();
            float currentX = -camera.position.x * layer.parallaxRatio.x
                    % (w + layer.padding.x);

            if (speed.x < 0)
                currentX += -(w + layer.padding.x);
            do {
                float currentY = -camera.position.y * layer.parallaxRatio.y
                        % (h + layer.padding.y);
                if (speed.y < 0)
                    currentY += -(layer.region.getRegionHeight() + layer.padding.y);
                do {
                    batch.draw(layer.region, -this.camera.viewportWidth / 2
                                    + currentX + layer.startPosition.x,
                            -this.camera.viewportHeight / 2 + currentY
                                    + layer.startPosition.y, w, h);
                    currentY += (h + layer.padding.y);
                } while (currentY < camera.viewportHeight);
                currentX += (w + layer.padding.x);
            } while (currentX < camera.viewportWidth);
            batch.end();
        }
    }
}