package com.dg.smania;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by magnus on 2014-10-20.
 */
public class Player extends Entity {

    private class PlayerInput implements EntityInput {

        @Override
        public boolean action() {
            if(isDamaged())
                return false;

            return Gdx.input.isKeyPressed(Input.Keys.X);
        }

        @Override
        public boolean jump() {
            if(isDamaged())
                return false;

            return Gdx.input.isKeyPressed(Input.Keys.Z);
        }

        @Override
        public boolean left() {
            if(isDamaged())
                return false;

            return Gdx.input.isKeyPressed(Input.Keys.LEFT);
        }

        @Override
        public boolean right() {
            if(isDamaged())
                return false;

            return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        }

    }

    public Player() {
        super(new PhysicsProperties(35f, 75f, 0.85f, true));
        entityInput = new PlayerInput();
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        fireTimer+=delta;
        invulnerableTimer-=delta;
    }

    private float fireInterval = 0.25f;
    private float fireTimer = fireInterval;

    private final float INVULNERABLE_TIME = 0.5f;
    private float invulnerableTimer = -1;


    @Override
    protected void onAction() {

        if(fireTimer >= fireInterval) {
            //fire
            Bullet bullet = Bullet.obtain();
            if (direction == Direction.LEFT) {
                bullet.position.set(position.x - 5, position.y + 5);
            } else {
                bullet.position.set(position.x + boundingBox.width + 5, position.y + 5);
            }
            bullet.velocity.set(direction.sign * bullet.physicsProperties.maxVelocity, 0);

            world.addEntity(bullet);
            fireTimer = 0f;
        }
    }

    private boolean isDamaged() {
        return invulnerableTimer > 0;
    }

    @Override
    protected void onCollidedWithEntity(Entity entity) {
        if(invulnerableTimer < 0) {
            invulnerableTimer = INVULNERABLE_TIME;
            velocity.x = -1 * direction.sign * 4;
            velocity.y = 0.5f;
            grounded = false;
        }
    }
}
