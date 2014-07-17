package mygame;

import java.util.Random;
import model.*;

/**
 * Loads the universe with a few bodies
 *
 * @author Guido J. Celada (celadaguido@gmail.com)
 */
public class UniverseLoader {

    static double min_velocity;
    static double max_velocity;
    static double min_position;
    static double max_position;

    /**
     * Loads the universe with N bodies randomly distributed in a cube
     * @returns the Universe loaded with N bodies
     */
    public static Universe loadUniverse(int N) {
        Universe universe = new Universe();

        min_velocity = -0.0001;
        max_velocity = 0.0001;

        min_position = -400;
        max_position = 400;

        for (int i = 0; i < N; i++) {
            double mass = nextRandom(100000, 100000);
            Body b = new Body(nextRandomPosition(), nextRandomVelocity(), mass);
            universe.addBody(b);
        }

//    double[] positionA = { 0,0,0 };
//    double[] velocityA = { 0, 0.0000001, 0 };
//    double mass = 9999999;
//    Body b = new Body(new Vector(positionA), new Vector(velocityA), mass);
//    universe.addBody(b);
//    
        return universe;
    }
    
    static Random rand = new Random();

    private static double nextRandom(double min, double max) {
        return rand.nextDouble() * (max - min) + min;
    }

    private static Vector nextRandomVelocity() {
        double[] velocityA = {0, 0, 0};
        velocityA[rand.nextInt(2)] = nextRandom(min_velocity, max_velocity);
        return new Vector(velocityA);
    }

    private static Vector nextRandomPosition() {
        double[] positionA = {nextRandom(min_position, max_position), nextRandom(min_position, max_position), nextRandom(min_position, max_position)};
        return new Vector(positionA);
    }
}
