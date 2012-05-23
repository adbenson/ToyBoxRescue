package net.adbenson.toybox;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class String {
	
	public static final int LENGTH_MIN = 30;
	public static final int LENGTH_MAX = 300;
	public static final double BASE_WIDTH = 5;
	public static final double MIN_WIDTH = 0.5;

	private static final double ELASTICITY = 0.01;
	
	private Vector end;
	
	private Color color = Color.red;
	
	private double width;
	
	private boolean held;
	
	public String() {
		drop();
		end = new Vector(0, 0);
	}
	
	public void drop() {
		held = false;
		width = BASE_WIDTH;
	}
	
	public void grab() {
		held = true;
	}

	public boolean isHeld() {
		return held;
	}
	
	public double pull(Vector force) {
		double distance = force.magnitude();
				
		if (distance > LENGTH_MAX) {
			drop();
		}
		
		setWidth(distance);
		
		if (held) {
			end = force;
			return (distance - LENGTH_MIN) * ELASTICITY;
		}
		else {
			return 0;
		}

	}
	
	private void setWidth(double distance) {
		if (held) {
			double distanceRatio = ((distance - LENGTH_MIN) / LENGTH_MAX);
			width = BASE_WIDTH - (distanceRatio * BASE_WIDTH);
			width = Math.max(MIN_WIDTH, width); 
		}
		else {
			width = BASE_WIDTH;
		}
	}

	public void draw(Graphics2D g, Vector bow, Vector position) {
        Stroke oldStroke = g.getStroke();
        AffineTransform oldTrans = g.getTransform();
		        
		g.setStroke(new BasicStroke((float) width));
		g.setColor(color);
		g.setTransform(AffineTransform.getTranslateInstance(position.x, position.y));
		
	    g.drawLine(bow.intX(), bow.intY(), end.intX(), end.intY());
	    
        g.setStroke(oldStroke);
        g.setTransform(oldTrans);
	}

	public void setEnd(Vector end2) {
		this.end = end2;		
	}

	public void trail(Vector trajectory) {
		end = trajectory.normalize().invert().scale(String.LENGTH_MIN*2);
	}

}
