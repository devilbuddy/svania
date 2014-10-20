package com.dg.smania;

import com.badlogic.gdx.utils.Pool;

/**
 * Created by magnus on 2014-10-20.
 */
public class Bullet extends Entity {


    private static PhysicsProperties bulletPhysics = new PhysicsProperties(100f, 75f, 1, false);


    private static Pool<Bullet> bulletPool = new Pool<Bullet>() {
        @Override
        protected Bullet newObject() {
            return new Bullet(bulletPhysics);
        }
    };

    public static Bullet obtain() {
        return bulletPool.obtain();
    }

    public static void free(Bullet bullet) {
        bulletPool.free(bullet);
    }

    public Bullet(PhysicsProperties physicsProperties) {
        super(physicsProperties);
    }

}
