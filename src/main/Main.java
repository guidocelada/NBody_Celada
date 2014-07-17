package main;

import model.loader.UniverseLoader;
import swing.MainSwing;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import java.awt.Dimension;
import java.util.concurrent.Callable;
import javax.swing.JFrame;
import model.Body;
import model.Universe;

/**
 * 3D Simulation of N-Body problem
 *
 * @author Guido J. Celada (celadaguido@gmail.com)
 */
public class Main extends SimpleApplication {

    Universe universe;
    private int velocity = 20000;
    private boolean isRunning;
    private BitmapText hud;

    public static void main(String[] args) {
//        Main app = new Main();
//        app.start();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AppSettings settings = new AppSettings(true);
                settings.setWidth(800);
                settings.setHeight(600);
                Main canvasApplication = new Main();
                canvasApplication.setSettings(settings);
                canvasApplication.createCanvas(); // create canvas!
                JmeCanvasContext ctx = (JmeCanvasContext) canvasApplication.getContext();
                ctx.setSystemListener(canvasApplication);
                Dimension dim = new Dimension(800, 600);
                ctx.getCanvas().setPreferredSize(dim);
                MainSwing window = new MainSwing(ctx.getCanvas(), canvasApplication);
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.pack();
                window.setVisible(true);
                canvasApplication.setRunning(false);
                canvasApplication.startCanvas();
            }
        });
    }

    @Override
    public void simpleInitApp() {
        //turn off stats view 
        setDisplayStatView(false);
        setDisplayFps(false);
        flyCam.setMoveSpeed(500);
        flyCam.setDragToRotate(true);
        setPauseOnLostFocus(false);
        // rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/space.jpg", true));

        bindKeys();
        createHud();

        //Set glow of spheres
        addGlow();

        //camera render distance
        cam.setFrustumFar(5000);
        cam.onFrameChange();
        isRunning = false;
        setBodies(1000);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (isRunning()) {//Check for pause
            universe.increaseTime(tpf * getVelocity());
            updateSpheres();
            hud.setText("Velocity: " + getVelocity() / 100f); //Update HUD
        } else {
            hud.setText("PAUSED"); //Update HUD
        }
    }

    private void createHud() {
        hud = new BitmapText(guiFont, false);
        hud.setSize(guiFont.getCharSet().getRenderedSize());      // font size
        hud.setColor(ColorRGBA.White);                             // font color
        hud.setText("PAUSED");             // the text
        hud.setLocalTranslation(3, 40, 0); // position
        hud.setName("HUD");
        guiNode.attachChild(hud);
    }

    private void updateSpheres() {
        for (int i = 0; i < universe.ammountOfBodies(); i++) {
            rootNode.getChild("Particle" + i).setLocalTranslation(new Vector3f((float) universe.getBody(i).getPosition().cartesian(0), (float) universe.getBody(i).getPosition().cartesian(1), (float) universe.getBody(i).getPosition().cartesian(2)));
        }
    }

    private void createSpheres() {
        for (int i = 0; i < universe.ammountOfBodies(); i++) {
            Body body = universe.getBody(i);
            Sphere sphereMesh = new Sphere(32, 32, 2);
            Geometry sphereGeo = new Geometry("Body", sphereMesh);
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("GlowColor", new ColorRGBA(1f, 0.91f, 0f, 1f)); //Yellow
            sphereGeo.setMaterial(mat);
            sphereMesh.setTextureMode(Sphere.TextureMode.Projected); // better quality on spheres
            sphereGeo.setLocalTranslation(new Vector3f((float) body.getPosition().cartesian(0), (float) body.getPosition().cartesian(1), (float) body.getPosition().cartesian(2)));
            sphereGeo.setName("Particle" + i);
            rootNode.attachChild(sphereGeo);
        }
    }

    private void addGlow() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBloomIntensity(4f);
        bloom.setBlurScale(2.1f);
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
    }

    //KEY BINDINGS
    public void bindKeys() {
        inputManager.addMapping("acelerate", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("decelerate", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("pause", new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(actionListener, "pause");
        inputManager.addListener(analogListener, "acelerate", "decelerate");
    }
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("acelerate")) {
                setVelocity(getVelocity() + 100);
            } else if (name.equals("decelerate")) {
                setVelocity(getVelocity() - 100);
            }
        }
    };
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("pause") & !isPressed) {
                setRunning(!isRunning());
            }
        }
    };

    /**
     * @return the velocity
     */
    public int getVelocity() {
        return velocity;
    }

    /**
     * @param velocity the velocity to set
     */
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    /**
     * @return the isRunning
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * @param isRunning the isRunning to set
     */
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public void setBodies(int N) {
        universe = UniverseLoader.loadUniverse(N); //init universe
        this.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                rootNode.detachAllChildren();
                createSpheres();
                return null;
            }
        });
        //init spheres representing the bodies
    }
}