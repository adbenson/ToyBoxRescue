

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import net.adbenson.toybox.String;
import net.adbenson.toybox.Vector;

public class Toy{
		
	private static final double FRICTION = 0.95;
	private static final double TURN_RADIUS = 0.5;
	private static final double MAX_ACCELERATION = 2.0;

	Vector position;
	Vector trajectory;
	float acceleration;
	float accelRate;
	
	private Shape prototype;
	private Shape shape;
	
	private Fire fire;
	
	private boolean crashed;
	
	private boolean debug = false;
	
	private String string;
	
	
	public Toy(int posX, int posY) {
		trajectory = new Vector(-1, 0);
		acceleration = 0;
		position = new Vector(posX, posY);
		
		prototype = generateProtoShape();
		translateShape();
		
		string = new String();
		string.trail(trajectory);
		crashed = false;
		
		fire = new Fire();
	}
	
	public void crash() {
		trajectory = new Vector(0, 0);
		string.drop();
		crashed = true;
	}

	public Rectangle getBounds() {
		Rectangle bounds = shape.getBounds();
		return bounds;
	}
	
	protected Shape generateProtoShape() {
		GeneralPath shape = new GeneralPath();
		shape.moveTo(-10, -20);
		shape.lineTo(10, -20);
		shape.lineTo(10, 5);
		
		shape.quadTo(10, 15, 0, 20);		
		shape.quadTo(-10, 15, -10, 5);
		
		shape.lineTo(-10, -20);
		shape.closePath();
		
		return shape;
	}
	
	public boolean isStringHeld() {
		return string.isHeld();
	}
	
	public void grabString() {
		string.grab();
	}
	
	public Vector getPosition() {
		return position;
	}

	public void move() {
		position = trajectory.add(position);
		trajectory = trajectory.scale(FRICTION);
		if (! string.isHeld()) {
			string.trail(trajectory);
		}
	}
		
	public void pull(Vector handle) {
		Vector force = handle.subtract(position);		
		double pull = string.pull(force);
		
		if (pull > 0) {
			pull = Math.max(pull, 0);
			pull = Math.min(pull, MAX_ACCELERATION);
			
			Vector orientation = force.normalize();						
			Vector change = orientation.scale(pull);
			trajectory = trajectory.add(change); 
			
			translateShape();
		}
	}
	
	private void translateShape() {
		AffineTransform tx = new AffineTransform();
		tx.translate(position.x, position.y);
		tx.rotate(trajectory.getAngle());
		shape = tx.createTransformedShape(prototype);
	}
	
	public void draw(Graphics2D g) {
		AffineTransform currentTrans = g.getTransform();
		
		Stroke before = g.getStroke();
		
		g.setStroke(new BasicStroke(1));
        g.setColor(Color.black);
        g.fill(shape);
        
        g.setTransform(currentTrans);
        g.setStroke(before);
               
        string.draw(g, getBow(), position);
        
        if (debug) {
	        g.setColor(Color.green);
	        g.setStroke(new BasicStroke(3));
	        Vector localTrajectory = trajectory.scale(5).add(position);
	        g.drawLine(position.intX(), position.intY(), localTrajectory.intX(), localTrajectory.intY());
	        
	        g.draw(getBounds());
        }
        
        if (crashed) {
        	fire.draw(g, position, 10, shape);
        }
	}
	
	public Vector getBow() {
		return position.add(trajectory.normalize().scale(10));
	}
}