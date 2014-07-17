package model;

import java.util.List;

/**
 * Celestial body
 * 
 * @author Guido J. Celada (celadaguido@gmail.com)
 */
public class Body {
  public static final float GRAVITATIONAL_CONSTANT = (float) 6.67300E-11;
  public static final float SOFTENING_PARAMETER = (float) 0.001;
  
  private Vector position;
  private Vector velocity;
  private double mass;
  
  public Body(Vector position, Vector velocity, double mass) {
    this.position = position;
    this.velocity = velocity;
    this.mass     = mass;
  }
  
  /**
   * Moves the body affected by an netForce, in a deltaTime 
   */
  public void move(Vector netForce, double deltaTime) {
    Vector acceleration = netForce.times(1/mass); //Calculate acceleration with Newton's second law of motion (a = F / m)
    velocity = velocity.plus(acceleration.times(deltaTime)); //Calculate new velocity with Newton's second law of motion (V1 = V0 + a ∆t)
    position = position.plus(velocity.times(deltaTime)); //Calculate new position, adding ∆x to the existing one (∆x = V ∆t)
  }
  
  /**
   * @returns the gravitational force that attracts this and the otherBody
   */
  public Vector forceFrom(Body otherBody) {
    Vector delta = otherBody.position.minus(position);
    double distance = delta.magnitude();
    
    double forceMagnitude = (GRAVITATIONAL_CONSTANT * this.mass * otherBody.mass) / (distance * distance + SOFTENING_PARAMETER * SOFTENING_PARAMETER); //Newton's law of universal gravitation (F = G m1 m2 / r^2)
    
    return delta.direction().times(forceMagnitude);
  }
  
  /**
   * Calculates the force vector that the other bodies create that affects this body 
   */
  public Vector calculateNetForce(List<Body> otherBodies) {
      Vector netForce = new Vector(3); //x,y,z
      
      for(Body body : otherBodies) 
          if (body != this)
              netForce = netForce.plus(this.forceFrom(body));
      
      return netForce;
  }
  
  
  
  /********* GETTERS ***********/
  
  public Vector getPosition() {
      return position;
  }

  public double getMass() {
      return mass;
  }
}
