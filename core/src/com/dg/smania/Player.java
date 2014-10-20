package com.dg.smania;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by magnus on 2014-10-20.
 */
public class Player extends Entity {

    private static class PlayerInput implements EntityInput {

        @Override
        public boolean action() {
            return Gdx.input.isKeyPressed(Input.Keys.X);
        }

        @Override
        public boolean jump() {
            return Gdx.input.isKeyPressed(Input.Keys.Z);
        }

        @Override
        public boolean left() {
            return Gdx.input.isKeyPressed(Input.Keys.LEFT);
        }

        @Override
        public boolean right() {
            return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        }

    }

    public Player() {
        super(new PlayerInput(), new PhysicsProperties(35f, 75f, 0.85f, true));
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        fireTimer+=delta;
    }

    private float fireInterval = 0.25f;
    private float fireTimer = fireInterval;
    @Override
    protected void onAction() {

        if(fireTimer >= fireInterval) {
            //fire
            Bullet bullet = Bullet.obtain();
            bullet.position.set(position.x, position.y + 5);
            bullet.velocity.set(direction.sign * bullet.physicsProperties.maxVelocity, 0);

            world.addEntity(bullet);
            fireTimer = 0f;
        }
    }
}
