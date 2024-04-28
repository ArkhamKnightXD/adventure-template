package knight.arkham.helpers;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import knight.arkham.objects.Player;
import knight.arkham.objects.enemies.Enemy;

import static knight.arkham.helpers.Constants.*;

public class Box2DHelper {

    public static void createStaticFixture(Box2DBody box2DBody){

        PolygonShape shape = new PolygonShape();

        FixtureDef fixtureDef = createBoxFixtureDef(box2DBody, shape);

        fixtureDef.filter.categoryBits = STOP_ENEMY_BIT;

        Body body = createBox2DBodyByType(box2DBody);

        Fixture fixture = body.createFixture(fixtureDef);

        fixture.setUserData(box2DBody.userData);

        shape.dispose();

    }

    private static Body createBox2DBodyByType(Box2DBody box2DBody) {

        var bodyDef = new BodyDef();

        bodyDef.type = box2DBody.bodyType;

        bodyDef.position.set(box2DBody.bounds.x / PIXELS_PER_METER, box2DBody.bounds.y / PIXELS_PER_METER);

        bodyDef.fixedRotation = true;

        return box2DBody.world.createBody(bodyDef);
    }

    public static Body createBody(Box2DBody box2DBody) {

        var shape = new PolygonShape();

        Body body = createBox2DBodyByType(box2DBody);

        if (box2DBody.userData instanceof Player) {

            var playerFixtureDef = createCircleFixtureDef(box2DBody);
            createPlayerBody(box2DBody, playerFixtureDef, body);
        }

        else if (box2DBody.userData instanceof Enemy) {

            var enemyFixtureDef = createCircleFixtureDef(box2DBody);
            createEnemyBody(box2DBody, enemyFixtureDef, body);
        }

        else {

            var fixtureDef = createBoxFixtureDef(box2DBody, shape);

            fixtureDef.filter.categoryBits = GROUND_BIT;
            body.createFixture(fixtureDef);
        }

        shape.dispose();

        return body;
    }

    private static FixtureDef createBoxFixtureDef(Box2DBody box2DBody, PolygonShape shape) {

        shape.setAsBox(box2DBody.bounds.width / 2 / PIXELS_PER_METER, box2DBody.bounds.height / 2 / PIXELS_PER_METER);

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.shape = shape;
        fixtureDef.density = box2DBody.density;

        return fixtureDef;
    }

    private static FixtureDef createCircleFixtureDef(Box2DBody box2DBody) {

        CircleShape shape = new CircleShape();

        shape.setRadius(8 / PIXELS_PER_METER);

        FixtureDef fixtureDef = new FixtureDef();

        fixtureDef.shape = shape;
        fixtureDef.density = box2DBody.density;

        shape.dispose();

        return fixtureDef;
    }

    private static void createPlayerBody(Box2DBody box2DBody, FixtureDef fixtureDef, Body body) {

        fixtureDef.filter.categoryBits = PLAYER_BIT;

        fixtureDef.filter.maskBits = (short) (GROUND_BIT | FINISH_BIT | GOOMBA_BIT | ENEMY_HEAD_BIT);

        fixtureDef.friction = 1;

        body.createFixture(fixtureDef).setUserData(box2DBody.userData);

        EdgeShape headCollider = getPlayerHeadCollider(fixtureDef);

        body.createFixture(fixtureDef).setUserData(box2DBody.userData);

        headCollider.dispose();
    }

    private static EdgeShape getPlayerHeadCollider(FixtureDef fixtureDef) {

        EdgeShape headCollider = new EdgeShape();

        headCollider.set(
            new Vector2(-4 / PIXELS_PER_METER, 8 / PIXELS_PER_METER),
            new Vector2(4 / PIXELS_PER_METER, 8 / PIXELS_PER_METER)
        );

        fixtureDef.shape = headCollider;
        fixtureDef.filter.categoryBits = PLAYER_HEAD_BIT;
        fixtureDef.isSensor = true;

        return headCollider;
    }

    private static void createEnemyBody(Box2DBody box2DBody, FixtureDef fixtureDef, Body body) {

        fixtureDef.filter.categoryBits = GOOMBA_BIT;

        fixtureDef.filter.maskBits = (short) (GROUND_BIT | GOOMBA_BIT | PLAYER_BIT | STOP_ENEMY_BIT);

        body.createFixture(fixtureDef).setUserData(box2DBody.userData);

        PolygonShape headCollider = getEnemyHeadCollider();

        fixtureDef.shape = headCollider;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = ENEMY_HEAD_BIT;

        body.createFixture(fixtureDef).setUserData(box2DBody.userData);

        headCollider.dispose();
    }

    private static PolygonShape getEnemyHeadCollider() {

        PolygonShape head = new PolygonShape();

        Vector2[] vertices = new Vector2[4];

        vertices[0] = new Vector2(-8, 12).scl(1 / PIXELS_PER_METER);
        vertices[1] = new Vector2(8, 12).scl(1 / PIXELS_PER_METER);
        vertices[2] = new Vector2(-8, 10).scl(1 / PIXELS_PER_METER);
        vertices[3] = new Vector2(8, 10).scl(1 / PIXELS_PER_METER);

        head.set(vertices);

        return head;
    }

    public static Rectangle getDrawBounds(Vector2 position, float width, float height) {

        return new Rectangle(
            position.x - (width / 2 / PIXELS_PER_METER),
            position.y - (height / 2 / PIXELS_PER_METER),
            width / PIXELS_PER_METER,
            height / PIXELS_PER_METER
        );
    }
}
