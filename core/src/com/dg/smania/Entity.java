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

    public interface EntityInput {
        boolean action();
        boolean jump();
        boolean left();
        boolean right();
    }

    private static EntityInput voidInput = new EntityInput() {

        @Override
        public boolean action() {
            return false;
        }

        @Override
        public boolean jump() {
            return false;
        }

        @Override
        public boolean left() {
            return false;
        }

        @Override
        public boolean right() {
            return false;
        }
    };

    public enum Direction {
        LEFT(-1),
        RIGHT(1);

        public final int sign;
        private Direction(int sign) {
            this.sign = sign;
        }
    }

    public enum State {
        STANDING,
        WALKING,
        JUMPING
    }

    private static class CollisionRect extends Rectangle {
        public Assets.CellType cellType;
    };

    public static class PhysicsProperties {

        public PhysicsProperties(float maxVelocity, float jumpVelocity, float damping, boolean affectedByGravity) {
            this.maxVelocity = maxVelocity;
            this.jumpVelocity = jumpVelocity;
            this.damping = damping;
            this.affectedByGravity = affectedByGravity;
        }

        public float maxVelocity;
        public float jumpVelocity;
        public float damping;
        boolean affectedByGravity;
    }

    private static final String tag = "Entity";
    private static final float GRAVITY = -2.5f;
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

    private final EntityInput entityInput;
    public final PhysicsProperties physicsProperties;

    public Entity(PhysicsProperties physicsProperties) {
        this(voidInput, physicsProperties);
    }

    public Entity(EntityInput entityInput, PhysicsProperties physicsProperties) {
        this.entityInput = entityInput;
        this.physicsProperties = physicsProperties;
    }

    public boolean isAlive() {
        return true;
    }

    private void setState(State state) {
        if(state != this.state) {
            this.state = state;
            stateTime = 0;
        }
    }

    public void update(float delta) {
        stateTime += delta;

        if (entityInput.action()) {
            onAction();
        }
        if (entityInput.jump() && grounded) {
            velocity.y += physicsProperties.jumpVelocity;
            setState(State.JUMPING);
            grounded = false;
        }
        if (entityInput.left()) {
            velocity.x = -physicsProperties.maxVelocity;
            if (grounded) {
                setState(State.WALKING);
            }
            direction = Direction.LEFT;
        }
        if (entityInput.right()) {
            velocity.x = physicsProperties.maxVelocity;
            if (grounded) {
                setState(State.WALKING);
            }
            direction = Direction.RIGHT;
        }

        if(physicsProperties.affectedByGravity) {
            // apply gravity
            velocity.add(0, GRAVITY);
        }
        // clamp the velocity to the maximum, x-axis only
        if (Math.abs(velocity.x) > physicsProperties.maxVelocity) {
            velocity.x = Math.signum(velocity.x) * physicsProperties.maxVelocity;
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
                onCollission();
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
                    onCollission();
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
        velocity.x *= physicsProperties.damping;
    }

    protected void onAction() {
    }

    protected void onCollission() {
    }

    protected void onRemoved() {

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
