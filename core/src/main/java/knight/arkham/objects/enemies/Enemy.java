package knight.arkham.objects.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import knight.arkham.objects.Player;

import static knight.arkham.helpers.AssetsHelper.loadSound;
import static knight.arkham.helpers.Box2DHelper.getDrawBounds;
import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public abstract class Enemy {

    public Body body;
    protected final Rectangle actualBounds;
    protected final World actualWorld;
    protected TextureRegion actualRegion;
    protected final int framesWidth;
    protected final int framesHeight;
    protected boolean isMovingRight;
    protected boolean setToDestroy;
    protected boolean isDestroyed;
    protected boolean shouldFlipYRegion;
    protected float stateTimer;
    protected final Sound hitSound = loadSound("stomp.wav");

    protected Enemy(Rectangle bounds, World world, TextureRegion region) {

        actualBounds = bounds;
        actualWorld = world;
        actualRegion = region;
        framesWidth = region.getRegionWidth();
        framesHeight = region.getRegionHeight();

        body = createObjectBody();
        body.setActive(false);
    }

    protected abstract Body createObjectBody();

    protected void destroyBody(TextureRegion hitRegion) {

        actualWorld.destroyBody(body);
        isDestroyed = true;

        actualRegion = hitRegion;
        stateTimer = 0;
    }

    protected abstract void childUpdate(float deltaTime);

    public void update(float deltaTime) {

        if (body.isActive())
            childUpdate(deltaTime);
    }

    public Vector2 getPixelPosition() {return body.getPosition().scl(PIXELS_PER_METER);}

    protected void movement() {

        if (isMovingRight && body.getLinearVelocity().x <= 4)
            applyLinearImpulse(new Vector2((float) 4, 0));

        else if (!isMovingRight && body.getLinearVelocity().x >= -4)
            applyLinearImpulse(new Vector2(-(float) 4, 0));
    }

    protected void applyLinearImpulse(Vector2 impulseDirection) {
        body.applyLinearImpulse(impulseDirection, body.getWorldCenter(), true);
    }


    public void draw(Batch batch) {

        Rectangle drawBounds = getDrawBounds(body.getPosition(), actualBounds.width, actualBounds.height);

        batch.draw(actualRegion, drawBounds.x, drawBounds.y, drawBounds.width, drawBounds.height);
    }

    protected void flipRegionOnXAxis(TextureRegion region) {

        //With this code most of the time if my enemy stop with a collision it will change direction automatically.
        if ((body.getLinearVelocity().x < 0 || !isMovingRight) && region.isFlipX()) {

            region.flip(true, shouldFlipYRegion);
            isMovingRight = false;
        }
        else if ((body.getLinearVelocity().x > 0 || isMovingRight) && !region.isFlipX()) {

            region.flip(true, shouldFlipYRegion);
            isMovingRight = true;
        }
    }

    public void changeDirection() {
        isMovingRight = !isMovingRight;
    }

    public abstract void hitByPlayer(Player userData);

    public abstract void childDispose();

    public void dispose() {

        actualRegion.getTexture().dispose();
        hitSound.dispose();
        childDispose();
    }
}
