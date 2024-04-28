package knight.arkham.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import knight.arkham.Adventure;
import knight.arkham.helpers.TileMapHelper;
import knight.arkham.scenes.Hud;

public class GameScreen extends ScreenAdapter {

    private final Adventure game = Adventure.INSTANCE;
    private final OrthographicCamera camera;
    private final TileMapHelper mapHelper;
    private final Hud hud;

    public GameScreen() {

        camera = game.camera;

        mapHelper = new TileMapHelper("maps/level1.tmx");

        hud = new Hud();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height);
    }

    @Override
    public void render(float deltaTime) {

        ScreenUtils.clear(0, 0, 0, 0);

        mapHelper.update(deltaTime, camera);

        mapHelper.draw(camera);

        hud.update(deltaTime);
        hud.stage.draw();
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        mapHelper.dispose();
        hud.dispose();
    }
}
