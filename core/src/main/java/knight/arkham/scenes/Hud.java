package knight.arkham.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Hud {

    public final Stage stage;
    private static Label scoreLabel;
    private static Label coinLabel;
    private float timeCount;
    private final Label countDownLabel;
    private int worldTimer = 400;
    private static int score;
    private static int coins;

    public Hud() {

        //Setting this here, because of the web build. Since I'm not disposing of hud I need to always set this values to 0
        score = 0;
        coins = 0;

        Viewport viewport = new FitViewport(300, 300);

        stage = new Stage(viewport);

        Table table = new Table();

        table.top();

        table.setFillParent(true);

        countDownLabel = new Label("400", new Label.LabelStyle(new BitmapFont(),Color.WHITE));
        scoreLabel = new Label("0", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        coinLabel = new Label("x 0", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        Label timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label worldLabel = new Label("World", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label emptyLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label marioLabel = new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(marioLabel).expandX().padTop(10);
        table.add(emptyLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);

        table.row();

        table.add(scoreLabel).expandX();
        table.add(coinLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();

        stage.addActor(table);
    }

    public void update(float deltaTime) {

        timeCount += deltaTime;

        while (timeCount >= 1) {

            worldTimer--;
            timeCount -= 1;
        }

        countDownLabel.setText(worldTimer);
    }

    public static void addScore(int value) {

        score += value;

        scoreLabel.setText(score);
    }

    public static void addCoin(int value) {

        coins += value;

        coinLabel.setText("x "+coins);
    }

    public void dispose(){
        stage.dispose();
    }
}
