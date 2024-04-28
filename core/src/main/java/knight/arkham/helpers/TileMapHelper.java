package knight.arkham.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import knight.arkham.Adventure;
import knight.arkham.objects.*;
import knight.arkham.objects.enemies.Enemy;
import knight.arkham.objects.enemies.Goomba;
import knight.arkham.screens.GameOverScreen;

import static knight.arkham.helpers.AssetsHelper.loadMusic;
import static knight.arkham.helpers.CameraController.controlCameraPosition;
import static knight.arkham.helpers.Constants.*;

public class TileMapHelper {

    private final Adventure game = Adventure.INSTANCE;
    public final TiledMap tiledMap;
    private final TextureAtlas atlas = new TextureAtlas("img/character.atlas");
    public final World world;
    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final Player player;
    private final Array<Enemy> enemies = new Array<>();
    private final Music music = loadMusic("mario_music.ogg");
    private float accumulator;
    private boolean isDebugCamera;
    private boolean isDebugRendererActive;
    private boolean isGameOver;

    public TileMapHelper(String mapFilePath) {

        world = new World(new Vector2(0, -40), true);
        world.setContactListener(new GameContactListener());

        player = new Player(new Rectangle(150, 40, 32, 16), world, atlas, 8);

        tiledMap = new TmxMapLoader().load(mapFilePath);
        mapRenderer = setupMap(tiledMap);

        music.play();
        music.setVolume(0.2f);
        music.setLooping(true);
    }

    private OrthogonalTiledMapRenderer setupMap(TiledMap tiledMap) {

        for (MapLayer mapLayer : tiledMap.getLayers())
            parseMapObjectsToBox2DBodies(mapLayer.getObjects(), mapLayer.getName());

        return new OrthogonalTiledMapRenderer(tiledMap, 1 / PIXELS_PER_METER);
    }

    private void parseMapObjectsToBox2DBodies(MapObjects mapObjects, String objectsName) {

        for (MapObject mapObject : mapObjects) {

            Rectangle mapRectangle = getTileMapRectangle(((RectangleMapObject) mapObject).getRectangle());

            switch (objectsName) {

                case "Enemies":
                    enemies.add(new Goomba(mapRectangle, world, atlas.findRegion("goomba"), 3));
                    break;

                case "Enemy-Stopper":
                    Box2DHelper.createStaticFixture(new Box2DBody(mapRectangle, world, null));
                    break;

                default:
                    Box2DHelper.createBody(new Box2DBody(mapRectangle, world, null));
                    break;
            }
        }
    }

    private Rectangle getTileMapRectangle(Rectangle rectangle) {
        return new Rectangle(
            rectangle.x + rectangle.width / 2,
            rectangle.y + rectangle.height / 2,
            rectangle.width, rectangle.height
        );
    }

    public void updateCameraPosition(OrthographicCamera camera) {

        controlCameraPosition(camera);

        if (Gdx.input.isKeyJustPressed(Input.Keys.F5))
            isDebugCamera = !isDebugCamera;

        if (!isDebugCamera && player.getPixelPosition().x > 145 && player.getCurrentState() != Player.AnimationState.DYING)
            camera.position.set(player.getWorldPosition().x, 7, 0);

        camera.update();
    }

    public void update(float deltaTime, OrthographicCamera camera) {

        if (player.getCurrentState() == Player.AnimationState.DYING)
            music.pause();

        if (player.getCurrentState() == Player.AnimationState.DYING && player.getStateTimer() > 2.6f)
            isGameOver = true;

        if (isGameOver)
            game.setScreen(new GameOverScreen());

        else {

            player.update(deltaTime);

            updateCameraPosition(camera);

            for (Enemy enemy : enemies) {

                var distanceBetweenPlayerAndEnemy = player.getPixelPosition().dst(enemy.getPixelPosition());

                if (!enemy.body.isActive() && distanceBetweenPlayerAndEnemy < 300)
                    enemy.body.setActive(true);

                enemy.update(deltaTime);
            }

            doPhysicsTimeStep(deltaTime);
        }
    }

    private void doPhysicsTimeStep(float deltaTime) {

        float frameTime = Math.min(deltaTime, 0.25f);

        accumulator += frameTime;

        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, 6, 2);
            accumulator -= TIME_STEP;
        }
    }


    public void draw(OrthographicCamera camera) {

        if (!isGameOver) {

            mapRenderer.setView(camera);

            if (Gdx.input.isKeyJustPressed(Input.Keys.F1))
                isDebugRendererActive = !isDebugRendererActive;

            if (!isDebugRendererActive) {

                mapRenderer.render();

                mapRenderer.getBatch().setProjectionMatrix(camera.combined);

                mapRenderer.getBatch().begin();

                player.draw(mapRenderer.getBatch());

                for (Enemy enemy : enemies)
                    enemy.draw(mapRenderer.getBatch());

                mapRenderer.getBatch().end();
            }
            else
                debugRenderer.render(world, camera.combined);
        }

    }

    public void dispose() {

        atlas.dispose();
        tiledMap.dispose();
        mapRenderer.dispose();
        world.dispose();
        debugRenderer.dispose();
        music.dispose();
        player.dispose();

        for (Enemy enemy : enemies)
            enemy.dispose();
    }
}
