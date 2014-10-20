package com.dg.smania;

/**
 * Created by magnus on 2014-10-20.
 */
public class Monster extends Entity {
    private static class MonsterInput implements EntityInput {

        @Override
        public boolean action() {
            return false;
        }

        @Override
        public boolean jump() {
            return true;
        }

        @Override
        public boolean left() {
            return false;
        }

        @Override
        public boolean right() {
            return false;
        }
    }

    private static PhysicsProperties physicsProperties = new PhysicsProperties(35f, 75f, 0.85f, true);

    public Monster() {
        super(new MonsterInput(), physicsProperties);
    }
}
