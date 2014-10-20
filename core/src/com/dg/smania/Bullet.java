package com.dg.smania;

import com.badlogic.gdx.utils.Pool;

/**
 * Created by magnus on 2014-10-20.
 */
public class Bullet extends Entity {


    private static PhysicsProperties bulletPhysics = new PhysicsProperties(150f, 75f, 1, false);


    private static Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet(bulletPhysics);
        }
    };

    public static Bullet obtain() {
        Bullet bullet = bulletPool.obtain();
        bullet.alive = true;
        return bullet;
    }

    public static void free(Bullet bullet) {
        bulletPool.free(bullet);
    }

    private boolean alive;

    public Bullet(PhysicsProperties physicsProperties) {
        super(physicsProperties);
        width = 3;
        height = 1;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    protected void onCollission() {
        alive = false;
    }

    @Override
    protected void onRemoved() {
        free(this);
    }


}
