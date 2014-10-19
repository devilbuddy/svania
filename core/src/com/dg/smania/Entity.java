package com.dg.smania;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by magnus on 2014-10-14.
 */
public class Entity {

    public enum Direction {
        LEFT,
        RIGHT
    }

    public enum State {
        STANDING,
        WALKING,
        JUMPING
    }

    private static class CollisionRect extends Rectangle {
        public Assets.CellType cellType;
    }

    private static final String tag = "Entity";
    private static final float GRAVITY = -2.5f;
    static float MAX_VELOCITY = 35f;
    static float JUMP_VELOCITY = 75f;
    static float DAMPING = 0.93f;
    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public World world;
    public State state = State.STANDING;
    public float stateTime;
    public Direction direction = Direction.RIGHT;
    float width = 5;
    float height = 7.5f;
    Rectangle boundingBox = new Rectangle();
    boolean grounded = false;
    private Pool<CollisionRect> rectPool = new Pool<CollisionRect>() {
        @Override
        protected CollisionRect newObject() {
            return new CollisionRect();
        }
    };
    private Array<CollisionRect> tiles = new Array<CollisionRect>();

    public Entity() {

    }

    private void setState(State state) {
        if(state != this.state) {
            this.state = state;
            stateTime = 0;
        }
    }

    public void update(float delta) {
        stateTime += delta;

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SPACE) && grounded) {
            velocity.y += JUMP_VELOCITY;
            setState(State.JUMPING);
            grounded = false;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            velocity.x = -MAX_VELOCITY;
            if (grounded) {
                setState(State.WALKING);
            }
            direction = Direction.LEFT;
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            velocity.x = MAX_VELOCITY;
            if (grounded) {
                setState(State.WALKING);
            }
            direction = Direction.RIGHT;
        }

        // apply gravity if we are falling
        velocity.add(0, GRAVITY);

        // clamp the velocity to the maximum, x-axis only
        if (Math.abs(velocity.x) > MAX_VELOCITY) {
            velocity.x = Math.signum(velocity.x) * MAX_VELOCITY;
        }

        // clamp the velocity to 0 if it's < 1, and set the state to standing
        if (Math.abs(velocity.x) < 5) {
            velocity.x = 0;
            if (grounded) {
                setState(State.STANDING);
            }
        }

        // multiply by delta time so we know how far we go
        // in this frame
        velocity.scl(delta);

        // perform collision detection & response, on each axis, separately
        // if the moving right, check the tiles to the right of it's
        // right bounding box edge, otherwise check the ones to the left

        boundingBox.set(position.x, position.y, width, height);
        int startX;
        int startY;
        int endX;
        int endY;

        if (velocity.x > 0) {
            startX = endX = (int) ((position.x + width + velocity.x) / Assets.TILE_SIZE);
        } else {
            startX = endX = (int) ((position.x + velocity.x) / Assets.TILE_SIZE);
        }
        startY = (int) ((position.y) / Assets.TILE_SIZE);
        endY = (int) ((position.y + height) / Assets.TILE_SIZE);
        getCollissionTiles(startX, startY, endX, endY, tiles);
        boundingBox.x += velocity.x;
        for (CollisionRect rect : tiles) {
            boolean solid = rect.cellType == Assets.CellType.BLOCK;
            if (solid && boundingBox.overlaps(rect)) {
                velocity.x = 0;
                break;
            }
        }
        boundingBox.x = position.x;

        // if the moving upwards, check the tiles to the top of it's
        // top bounding box edge, otherwise check the ones to the bottom
        if (velocity.y > 0) {
            startY = endY = (int) ((position.y + height + velocity.y) / Assets.TILE_SIZE);
        } else {
            startY = endY = (int) ((position.y + velocity.y) / Assets.TILE_SIZE);
        }
        startX = (int) ((position.x) / Assets.TILE_SIZE);
        endX = (int) ((position.x + width) / Assets.TILE_SIZE);
        getCollissionTiles(startX, startY, endX, endY, tiles);

        // bottom position prior to movement
        float oldBottomY = boundingBox.y;

        boundingBox.y += velocity.y;
        for (CollisionRect rect : tiles) {
            if (boundingBox.overlaps(rect)) {
                boolean solid = rect.cellType == Assets.CellType.BLOCK;

                if (rect.cellType == Assets.CellType.JUMP_THROUGH) {
                    if (oldBottomY >= rect.y + rect.height) {
                        solid = true;
                    }
                }

                if(solid) {
                    // reset the y-position here so it is just below/above the rect we collided with
                    // this removes bouncing
                    if (velocity.y > 0) {
                        position.y = rect.y - height;
                    } else {
                        position.y = rect.y + rect.height;
                        // if we hit the ground, mark us as grounded so we can jump
                        grounded = true;
                        setState(State.WALKING);
                    }
                    velocity.y = 0;
                    break;
                }
            }
        }

        position.add(velocity);

        // unscale the velocity by the inverse delta time and set
        // the latest position
        velocity.scl(1 / delta);

        // Apply damping to the velocity on the x-axis so we don't
        // walk infinitely once a key was pressed
        velocity.x *= DAMPING;
    }

    private void getCollissionTiles(int startX, int startY, int endX, int endY, Array<CollisionRect> tiles) {
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                Assets.CellType cellType = world.getCellType(x, y);
                if (cellType != Assets.CellType.VOID) {
                    CollisionRect rect = rectPool.obtain();
                    rect.set(x * Assets.TILE_SIZE, y * Assets.TILE_SIZE, Assets.TILE_SIZE, Assets.TILE_SIZE);
                    rect.cellType = cellType;
                    tiles.add(rect);
                }
            }
        }
    }

}
