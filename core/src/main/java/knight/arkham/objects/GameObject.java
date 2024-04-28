package knight.arkham.objects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import static knight.arkham.helpers.Box2DHelper.getDrawBounds;

public abstract class GameObject {

    protected final Rectangle actualBounds;
    protected final World actualWorld;
    protected TextureRegion actualRegion;
    protected final int framesWidth;
    protected final int framesHeight;
    protected float stateTimer;
    public Body body;

    protected GameObject(Rectangle bounds, World world, TextureRegion region) {

        actualBounds = bounds;
        actualWorld = world;
        actualRegion = region;
        framesWidth = region.getRegionWidth();
        framesHeight = region.getRegionHeight();

        body = createObjectBody();
    }

    protected abstract Body createObjectBody();

    protected abstract void childUpdate(float deltaTime);

    public void update(float deltaTime) {
        childUpdate(deltaTime);
    }

    public void draw(Batch batch) {

        Rectangle drawBounds = getDrawBounds(body.getPosition(), actualBounds.width, actualBounds.height);

        batch.draw(actualRegion, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
    }

    public void dispose() {actualRegion.getTexture().dispose();}
}
