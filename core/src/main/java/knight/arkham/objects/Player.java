package knight.arkham.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import knight.arkham.helpers.Box2DBody;

import static knight.arkham.helpers.AnimationHelper.makeAnimation;
import static knight.arkham.helpers.AssetsHelper.loadSound;
import static knight.arkham.helpers.Box2DHelper.createBody;
import static knight.arkham.helpers.Constants.NOTHING_BIT;
import static knight.arkham.helpers.Constants.PIXELS_PER_METER;

public class Player extends GameObject {

    public enum AnimationState {FALLING, JUMPING, STANDING, RUNNING, DYING}

    private AnimationState currentState = AnimationState.STANDING;
    private AnimationState previousState = AnimationState.STANDING;
    private final TextureRegion idleRegion;
    private final TextureRegion jumpRegion;
    private final TextureRegion dyingRegion;
    private final Animation<TextureRegion> runningAnimation;
    public boolean isMovingRight;
    private boolean isDead;
    private final Sound deathSound = loadSound("mariodie.wav");

    public Player(Rectangle bounds, World world, TextureAtlas atlas, int totalFrames) {
        super(
            bounds, world,
            new TextureRegion(
                atlas.findRegion("little-mario"), 0, 0,
                atlas.findRegion("little-mario").getRegionWidth() / totalFrames,
                atlas.findRegion("little-mario").getRegionHeight()
            )
        );

        idleRegion = new TextureRegion(atlas.findRegion("little-mario"), 0, 0, framesWidth, framesHeight);
        jumpRegion = new TextureRegion(atlas.findRegion("little-mario"), framesWidth * 5, 0, framesWidth, framesHeight);
        dyingRegion = new TextureRegion(atlas.findRegion("little-mario"), framesWidth * 6, 0, framesWidth, framesHeight);

        runningAnimation = makeAnimation(
            atlas.findRegion("little-mario"), framesWidth, framesHeight, 4, 0.1f, 1
        );
    }

    @Override
    protected Body createObjectBody() {

        return createBody(
            new Box2DBody(actualBounds, 10, actualWorld, this)
        );
    }

    private void movement() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && body.getLinearVelocity().y == 0)
            applyLinearImpulse(new Vector2(0, 144));

        if (Gdx.input.isKeyPressed(Input.Keys.D) && body.getLinearVelocity().x <= 12)
            applyLinearImpulse(new Vector2(6, 0));

        if (Gdx.input.isKeyPressed(Input.Keys.A) && body.getLinearVelocity().x >= -12)
            applyLinearImpulse(new Vector2(-6, 0));
    }

    private void applyLinearImpulse(Vector2 impulseDirection) {
        body.applyLinearImpulse(impulseDirection, body.getWorldCenter(), true);
    }

    @Override
    protected void childUpdate(float deltaTime) {

        getAnimationRegion(deltaTime);

        if (!isDead)
            movement();

        if (getPixelPosition().y < -10)
            isDead = true;
    }

    public Vector2 getPixelPosition() {
        return body.getPosition().scl(PIXELS_PER_METER);
    }

    private AnimationState getCurrentAnimationState() {

        if (isDead)
            return AnimationState.DYING;

        else if (body.getLinearVelocity().y > 0 || (body.getLinearVelocity().y < 0 && previousState == AnimationState.JUMPING))
            return AnimationState.JUMPING;

        else if (body.getLinearVelocity().x != 0)
            return AnimationState.RUNNING;

        else if (body.getLinearVelocity().y < 0)
            return AnimationState.FALLING;

        else
            return AnimationState.STANDING;
    }

    private void getAnimationRegion(float deltaTime) {

        currentState = getCurrentAnimationState();

        switch (currentState) {

            case JUMPING:
                actualRegion = jumpRegion;
                break;

            case RUNNING:
                actualRegion = runningAnimation.getKeyFrame(stateTimer, true);
                break;

            case DYING:
                actualRegion = dyingRegion;
                break;

            case FALLING:
            case STANDING:
            default:
                actualRegion = idleRegion;
        }

        flipRegionOnXAxis(actualRegion);

        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
    }

    private void flipRegionOnXAxis(TextureRegion region) {

        if ((body.getLinearVelocity().x > 0 || isMovingRight) && region.isFlipX()) {

            region.flip(true, false);
            isMovingRight = true;
        }
        if ((body.getLinearVelocity().x < 0 || !isMovingRight) && !region.isFlipX()) {

            region.flip(true, false);
            isMovingRight = false;
        }
    }

    public void hitByEnemy() {

        isDead = true;
        deathSound.play();

        Filter filter = new Filter();

        filter.maskBits = NOTHING_BIT;

        for (Fixture fixture : body.getFixtureList())
            fixture.setFilterData(filter);

        applyLinearImpulse(new Vector2(0, 140));
    }

    public AnimationState getCurrentState() {
        return currentState;
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public Vector2 getWorldPosition() {
        return body.getPosition();
    }

    @Override
    public void dispose() {

        deathSound.dispose();
        super.dispose();
    }
}
