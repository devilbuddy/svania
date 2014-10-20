package com.dg.smania;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by magnus on 2014-10-14.
 */
public class World {

    public final Entity player;

    public final List<Entity> entities = new ArrayList<Entity>();
    public Assets.CellType[][] collissionMap;
    int width;
    int height;
    public World() {
        player = new Player();
        player.position.set(10, 8);

        addEntity(player);

        Monster monster1 = new Monster();
        monster1.position.set(100, 100);
        addEntity(monster1);


        Monster monster2 = new Monster();
        monster2.position.set(200, 100);
        addEntity(monster2);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
        entity.world = this;
    }

    public void update(float delta) {
        for(int i = 0; i < entities.size(); i++) {
            entities.get(i).update(delta);
        }
    }

    public void setCollissionMap(Assets.CellType[][] collisionMap) {
        this.collissionMap = collisionMap;
        width = collisionMap[0].length;
        height = collisionMap.length;
    }

    public Assets.CellType getCellType(int x, int y) {
        if(x < 0 || x >= width || y < 0 || y >= height) {
            return Assets.CellType.BLOCK;
        }
        return collissionMap[y][x];
    }
}
