package com.dg.smania;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

import static com.badlogic.gdx.graphics.g2d.Animation.PlayMode.*;

/**
 * Created by magnus on 2014-10-14.
 */
public class Assets {

    private static final String tag = "Assets";

    public static class MapTile implements TiledMapTile {

        private int id;
        private TextureRegion textureRegion;
        private BlendMode blendMode;
        private float offsetX;
        private float offsetY;
        private CellType cellType;

        public MapTile(int id, CellType cellType, TextureRegion textureRegion) {
            this(id, cellType, textureRegion, false);
        }

        public MapTile(int id, CellType cellType, TextureRegion textureRegion, boolean transparency) {
            this.id = id;
            this.cellType = cellType;
            this.textureRegion = textureRegion;
            this.blendMode = transparency ? BlendMode.ALPHA : BlendMode.NONE;
        }

        public CellType getCellType() {
            return cellType;
        }

        @Override
        public int getId() {
            return id;
        }

        @Override
        public void setId(int id) {
            this.id = id;
        }

        @Override
        public BlendMode getBlendMode() {
            return blendMode;
        }

        @Override
        public void setBlendMode(TiledMapTile.BlendMode blendMode) {
            this.blendMode = blendMode;
        }

        @Override
        public TextureRegion getTextureRegion() {
            return textureRegion;
        }

        @Override
        public float getOffsetX() {
            return offsetX;
        }

        @Override
        public void setOffsetX(float offsetX) {
            this.offsetX = offsetX;
        }

        @Override
        public float getOffsetY() {
            return offsetY;
        }

        @Override
        public void setOffsetY(float offsetY) {
            this.offsetY = offsetY;
        }

        @Override
        public MapProperties getProperties() {
            return null;
        }

    }

    public static class MapData {
        int width;
        int height;
        TiledMap tiledMap;
        CellType[][] collisionMap;
    }

    private static class TileSet {
        private TiledMapTileSet tiledMapTileSet = new TiledMapTileSet();
        private Map<Integer, MapTile> tiles = new HashMap<Integer, MapTile>();

        private Map<Integer, CellType> cellTypes = new HashMap<Integer, CellType>();

        public void addTile(int id, CellType cellType, TextureRegion textureRegion) {
            addTile(id, cellType, textureRegion, false, 0, 0);
        }

        public void addTile(int id, CellType cellType, TextureRegion textureRegion, boolean transparency, int offsetX, int offsetY) {
            MapTile mapTile = new MapTile(id, cellType, textureRegion, transparency);
            mapTile.setOffsetX(offsetX);
            mapTile.setOffsetY(offsetY);
            tiledMapTileSet.putTile(id, mapTile);
            tiles.put(id, mapTile);
            cellTypes.put(id, cellType);
        }

        public MapTile getMapTile(int id) {
            return tiles.get(id);
        }

        public CellType getCellType(int id) {
            return cellTypes.get(id);
        }

        public TiledMapTileSet getTiledMapTileSet() {
            return tiledMapTileSet;
        }
    }

    private interface AnimationLookup {
        Animation getAnimation(Entity.State state, Entity.Direction direction);
    }

    public static final int TILE_SIZE = 8;

    private Texture tilesTexture;
    private Texture astronautTexture;
    private Texture hudTexture;
    private Texture effectsTexture;

    private TileSet tileSet;
    public TextureRegion background;
    public NinePatch patch;

    private Map<Class, AnimationLookup> entityAnimations = new HashMap<Class, AnimationLookup>();

    public Assets() {

        tilesTexture = new Texture(Gdx.files.internal("8x8_tiles.png"));
        astronautTexture = new Texture(Gdx.files.internal("astro.png"));
        hudTexture = new Texture(Gdx.files.internal("hud_tiles.png"));
        effectsTexture = new Texture(Gdx.files.internal("effects.png"));

        background = new TextureRegion(tilesTexture, 0,72,24,24);

        TextureRegion[][] tileRegions = TextureRegion.split(tilesTexture, 8, 8);
        TextureRegion platform = new TextureRegion(tilesTexture, 168, 0, 8, 5);
        tileSet = new TileSet();
        tileSet.addTile(1, CellType.BLOCK,  tileRegions[0][1]);
        tileSet.addTile(2, CellType.JUMP_THROUGH, platform, true, 0, 3);
        tileSet.addTile(3, CellType.BLOCK, tileRegions[5][5]);

        setupAnimations();

        TextureRegion buttonRegion = new TextureRegion(hudTexture, 0, 0, 8, 8);
        patch = new NinePatch(buttonRegion, 2, 2, 2, 2);
    }

    private void setupAnimations() {

        TextureRegion[] astronautRightFrames = TextureRegion.split(astronautTexture, 10, 10)[0];
        final Animation walkRightAnimation = new Animation(0.15f, astronautRightFrames[0], astronautRightFrames[1], astronautRightFrames[2], astronautRightFrames[3], astronautRightFrames[4], astronautRightFrames[5]);
        walkRightAnimation.setPlayMode(LOOP);
        final Animation standRightAnimation = new Animation(0.15f,astronautRightFrames[0]);
        final Animation jumpRightAnimation = new Animation(0.15f, astronautRightFrames[6], astronautRightFrames[7]);

        TextureRegion[] astronautLeftFrames = TextureRegion.split(astronautTexture, 10, 10)[0];
        for(int i = 0; i < astronautLeftFrames.length; i++) {
            astronautLeftFrames[i].flip(true, false);
        }
        final Animation walkLeftAnimation = new Animation(0.15f, astronautLeftFrames[0], astronautLeftFrames[1], astronautLeftFrames[2], astronautLeftFrames[3], astronautLeftFrames[4], astronautLeftFrames[5]);
        walkLeftAnimation.setPlayMode(LOOP);
        final Animation standLeftAnimation = new Animation(0.15f, astronautLeftFrames[0]);
        final Animation jumpLeftAnimation = new Animation(0.15f, astronautLeftFrames[6], astronautLeftFrames[7]);

        entityAnimations.put(Player.class, new AnimationLookup() {

            @Override
            public Animation getAnimation(Entity.State state, Entity.Direction direction) {
                Animation animation;

                switch (state) {
                    case STANDING: {
                        if (direction == Entity.Direction.RIGHT) {
                            animation = standRightAnimation;
                        } else {
                            animation = standLeftAnimation;
                        }
                        break;
                    }
                    case JUMPING: {
                        if (direction == Entity.Direction.RIGHT) {
                            animation = jumpRightAnimation;
                        } else {
                            animation = jumpLeftAnimation;
                        }
                        break;
                    }
                    default:{
                        if(direction == Entity.Direction.RIGHT) {
                            animation = walkRightAnimation;
                        } else {

                            animation = walkLeftAnimation;
                        }
                    }
                }
                return animation;
            }
        });

        final Animation monsterAnimation = new Animation(0.15f, astronautRightFrames[0], astronautRightFrames[1], astronautRightFrames[2], astronautRightFrames[3], astronautRightFrames[4], astronautRightFrames[5]);
        monsterAnimation.setPlayMode(LOOP);

        entityAnimations.put(Monster.class, new AnimationLookup() {
            @Override
            public Animation getAnimation(Entity.State state, Entity.Direction direction) {
                return monsterAnimation;
            }
        });

        entityAnimations.put(Bullet.class, new AnimationLookup() {
            Animation animation = new Animation(0.15f, new TextureRegion(effectsTexture,5, 5, 3, 1));
            @Override
            public Animation getAnimation(Entity.State state, Entity.Direction direction) {
                return animation;
            }
        });

    }

    public Animation getAnimation(Entity entity) {
        return entityAnimations.get(entity.getClass()).getAnimation(entity.state, entity.direction);
    }

    public enum CellType {
        VOID(0),
        BLOCK(1),
        JUMP_THROUGH(2);

        private int id;
        private CellType(int id) {
            this.id = id;
        }
    }

    public MapData loadMap(String name) {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonValue = jsonReader.parse(Gdx.files.internal(name));

        int width = jsonValue.getInt("width");
        int height = jsonValue.getInt("height");
        Gdx.app.log(tag, "width:" + width + " height:" + height);

        JsonValue dataValue = jsonValue.get("data");


        TiledMap tiledMap = new TiledMap();
        tiledMap.getTileSets().addTileSet(tileSet.getTiledMapTileSet());

        CellType[][] collissionMap = new CellType[height][width];

        TiledMapTileLayer tileLayer = new TiledMapTileLayer(width, height, 8, 8);
        for (int y = 0; y < height; y++) {
            String row = dataValue.get(y).asString();
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);
                int id =0;
                switch (c) {
                    case '·':
                        id = 0;
                        break;
                    case '#':
                        id = 1;
                        break;
                    case '=':
                        id = 2;
                        break;
                }
                //int id = row[x];oasIntArray();

                if (id != 0) {
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    MapTile mapTile = tileSet.getMapTile(id);
                    cell.setTile(mapTile);
                    // tiles are indexed top to bottom
                    tileLayer.setCell(x, height - 1 - y, cell);
                    collissionMap[height - 1 - y][x] = mapTile.getCellType();
                } else {

                    collissionMap[height - 1 - y][x] = CellType.VOID;
                }
            }
        }

        tiledMap.getLayers().add(tileLayer);

        MapData mapData = new MapData();
        mapData.width = width;
        mapData.height = height;
        mapData.tiledMap = tiledMap;
        mapData.collisionMap = collissionMap;
        return mapData;
    }
}
