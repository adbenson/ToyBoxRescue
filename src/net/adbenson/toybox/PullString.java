package net.adbenson.toybox;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import net.adbenson.drawing.Drawable;
import net.adbenson.drawing.DrawingQueueable;
import net.adbenson.drawing.DrawingQueue;

public class PullString implements DrawingQueueable {
	
	public static final int LENGTH_MIN = 30;
	public static final int LENGTH_MAX = 300;
	public static final double BASE_WIDTH = 5;
	public static final double MIN_WIDTH = 0.5;

	private static final double ELASTICITY = 0.01;
	
	private Vector end;
	private Vector start;
	
	private Color color = Color.green;
	
	private double width;
	
	private boolean held;
	
	public PullString() {
		drop();
		start = new Vector(0, 0);
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
	
	public void setStart(Vector start) {
		this.start = start;
	}
	
	public void setEnd(Vector end) {
		this.end = end;		
	}

	public void trail(Vector trajectory) {
		end = trajectory.normalize().invert().scale(PullString.LENGTH_MIN*2);
	}

	public Vector getEnd() {
		return end;
	}

	@Override
	public void enqueueForDraw(DrawingQueue queue) {
		queue.enqueue(new Drawable(10) {
			@Override
			public void draw(Graphics2D g) {
				g.setStroke(new BasicStroke((float) width));
				g.setColor(color);
			    g.drawLine(start.intX(), start.intY(), end.intX(), end.intY());
			}
			
		});
	}

}
