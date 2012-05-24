package net.adbenson.toybox;



import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.LinkedList;


public class Boat{
		
	private static final double FRICTION = 0.95;
	private static final double TURN_RADIUS = 0.5;
	private static final double MAX_ACCELERATION = 2.0;

	Vector position;
	Vector trajectory;
	float acceleration;
	float accelRate;
	
	private Path2D prototype;
	private Area shape;
	
	private Fire fire;
	
	private boolean crashed;
	
	private boolean debug = false;
	
	private PullString string;
	
	private LinkedList<Person> passengers;	
	
	public Boat(int posX, int posY) {
		trajectory = new Vector(-1, 0);
		acceleration = 0;
		position = new Vector(posX, posY);
		
		prototype = generateProtoShape();
		prototype.transform(AffineTransform.getScaleInstance(1.5, 1.5));
		translateShape();
		
		string = new PullString();
		string.trail(trajectory);
		crashed = false;
		
		fire = new Fire();
		passengers = new LinkedList<Person>();
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
	
	protected Path2D generateProtoShape() {
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
		position = position.add(trajectory);
		trajectory = trajectory.scale(FRICTION);
		if (! string.isHeld()) {
			string.trail(trajectory);
		}
		translateShape();
	}
		
	public void pull(Handle handle) {
		Vector force = handle.subtract(position);		
		double pull = string.pull(force);
		
		if (pull > 0) {
			pull = Math.max(0, Math.min(pull, MAX_ACCELERATION));
			
			Vector orientation = force.normalize();						
			Vector change = orientation.scale(pull);
			trajectory = trajectory.add(change); 
		}
		if (! string.isHeld() && handle.contains(string.getEnd().add(position))) {
			string.grab();
		}
	}
	
	private void translateShape() {
		AffineTransform tx = new AffineTransform();
		tx.translate(position.x, position.y);
		tx.rotate(trajectory.getAngle());
		shape = new Area(tx.createTransformedShape(prototype));
	}
	
	public void draw(Graphics2D g) {
		AffineTransform currentTrans = g.getTransform();
		
		Stroke before = g.getStroke();
		
		g.setStroke(new BasicStroke(1));
        g.setColor(Color.black);
        g.fill(shape);
        
        g.setTransform(currentTrans);
        g.setStroke(before);
                      
        if (debug) {
	        g.setColor(Color.pink);
	        g.setStroke(new BasicStroke(3));
	        Vector localTrajectory = trajectory.scale(5).add(position);
	        g.drawLine(position.intX(), position.intY(), localTrajectory.intX(), localTrajectory.intY());
	        
	        g.draw(getBounds());
        }
        
        if (! passengers.isEmpty()) { 
	        int space = 30 / passengers.size();
	        int offset = -15;
	        for (int i=0; i<passengers.size(); i++) {
	        	passengers.get(i).draw(g, position.add(new Vector(offset, -10)));
	        	offset += space;
	        }
        }
        
        if (!crashed) {
        	string.draw(g, getBow(), position);
        }
        else {
        	fire.draw(g, position, 10, shape);
        }
	}
	
	public Vector getBow() {
		return position.add(trajectory.normalize().scale(10));
	}

	public Shape getShape() {
		return shape;
	}

	public boolean intersects(Person p) {
		Rectangle personBounds = p.getBounds();
		//Coarse test
		if (! getBounds().intersects(p.getBounds())) {
			return false;
		}
		
		//Fine test
		if (shape.intersects(personBounds)) {
			return true;
		}
		else {
			return false;
		}
		
	}

	public void pickup(Person p) {
		System.out.println("Picked up passenger");
		passengers.add(p);
		Collections.sort(passengers);
	}
}