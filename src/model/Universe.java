package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Universe that haves a set of celestial bodies with gravitational forces
 * 
 * @author Guido J. Celada (celadaguido@gmail.com)
 */
public class Universe {

    private List<Body> bodies;

    public Universe() {
        this.bodies = new ArrayList<Body>();
    }

    /**
     * Increase the time passed in the universe and updating the position of the
     * bodies using the Superposition principle
     *
     * @param deltaTime the time passed
     */
    public void increaseTime(final float deltaTime) {
        boolean concurrent = true;
        if (concurrent) {
            increaseTimeConcurrent(deltaTime);
        } else {
            Vector[] forces = new Vector[getBodies().size()];
            for (int i = 0; i < forces.length; i++) {
                forces[i] = getBody(i).calculateNetForce(bodies);
            }
            for (int i = 0; i < forces.length; i++) {
                getBody(i).move(forces[i], deltaTime);
            }
        }
    }

    /**
     * Same as increaseTime but using threads
     *
     * @param deltaTime the time passed
     */
    public void increaseTimeConcurrent(final float deltaTime) {
        final Vector[] forces = new Vector[getBodies().size()];

        ExecutorService pool = Executors.newCachedThreadPool();

        for (int i = 0; i < forces.length; i++) {
            final int index = i;
            final Body body = getBody(i);

            pool.submit(new Runnable() {
                public void run() {
                    forces[index] = body.calculateNetForce(bodies);
                }
            });
        }

        // This will make the executor accept no new threads
        // and finish all existing threads in the queue
        pool.shutdown();
        try {
            // Wait until all threads are finish
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Universe.class.getName()).log(Level.SEVERE, null, ex);
        }

        pool = Executors.newCachedThreadPool();

        for (int i = 0; i < forces.length; i++) {
            final int index = i;

            pool.submit(new Runnable() {
                public void run() {
                    getBody(index).move(forces[index], deltaTime);
                }
            });
        }

        pool.shutdown();
        try {
            pool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Universe.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    
    /*********** GETTERS AND SETTERS **************/
    
    private List<Body> getBodies() {
        return bodies;
    }

    public Body getBody(int i) {
        return bodies.get(i);
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    public int ammountOfBodies() {
        return bodies.size();
    }
}
