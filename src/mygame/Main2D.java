package mygame;

/**
 * 2D simulation using Java2D
 *
 * @author Guido J. Celada (celadaguido@gmail.com)
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import model.Body;
import model.Universe;

class Surface extends JPanel { //container of the app

    Graphics2D g2d; //surface to draw
    Universe universe;

    public Surface() {
        universe = UniverseLoader.loadUniverse(1000);
        this.setBackground(Color.black);
    }

    private void updateSpheres() {
        ExecutorService pool = Executors.newCachedThreadPool();

        for (int i = 0; i < universe.ammountOfBodies(); i++) {
            final Body b = universe.getBody(i);

            pool.submit(new Runnable() {
                public void run() {
                    g2d.fillOval((int) b.getPosition().cartesian(0), (int) b.getPosition().cartesian(1), 3, 3);
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
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.translate(400, 260);
        g2d = (Graphics2D) g;
        g2d.setColor(Color.white);

        universe.increaseTime(1000);
        updateSpheres();

        try {
            Thread.sleep(0, 1);
        } catch (InterruptedException ex) {
            Logger.getLogger(Surface.class.getName()).log(Level.SEVERE, null, ex);
        }

        repaint();
    }
}

class Skeleton extends JFrame { //Frame or window of the app

    public Skeleton() {
        setTitle("N-body simulation");
        add(new Surface());
        setBackground(Color.black);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Skeleton sk = new Skeleton();
                sk.setVisible(true);
            }
        });
    }
}