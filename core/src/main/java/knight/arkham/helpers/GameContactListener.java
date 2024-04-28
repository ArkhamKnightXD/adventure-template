package knight.arkham.helpers;

import com.badlogic.gdx.physics.box2d.*;
import knight.arkham.objects.enemies.Enemy;
import knight.arkham.objects.Player;
import knight.arkham.objects.enemies.Goomba;

import static knight.arkham.helpers.Constants.*;

public class GameContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {

        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int collisionBits = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

        switch (collisionBits) {

            case PLAYER_BIT | ENEMY_HEAD_BIT:

                if (fixtureA.getFilterData().categoryBits == ENEMY_HEAD_BIT)
                    ((Enemy) fixtureA.getUserData()).hitByPlayer(((Player) fixtureB.getUserData()));
                else
                    ((Enemy) fixtureB.getUserData()).hitByPlayer(((Player) fixtureA.getUserData()));
                break;

            case PLAYER_BIT | GOOMBA_BIT:

                if (fixtureA.getFilterData().categoryBits == PLAYER_BIT)
                    ((Player) fixtureA.getUserData()).hitByEnemy();
                else
                    ((Player) fixtureB.getUserData()).hitByEnemy();
                break;

            case GOOMBA_BIT:

                ((Goomba) fixtureA.getUserData()).changeDirection();
                ((Goomba) fixtureB.getUserData()).changeDirection();
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {}

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
