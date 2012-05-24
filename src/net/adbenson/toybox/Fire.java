package net.adbenson.toybox;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.util.Random;

import net.adbenson.drawing.Drawable;
import net.adbenson.drawing.DrawingQueue;
import net.adbenson.drawing.DrawingQueueable;

public class Fire implements DrawingQueueable {

	private Vector outerBaseLeft = new Vector(-10, 0);
	private Vector outerBaseRight = new Vector(10, 0);
	
	private BoundVector outerCP1Left = new BoundVector(-25, -18, -15, -10);
	private BoundVector outerCP2Left = new BoundVector(-10, -30, 0, -22);
	private BoundVector outerCPRight = new BoundVector(15, -25, 25, -15);
	
	private BoundVector outerPeak = new BoundVector(-10, -40, 0, -30);
	
	private Vector innerBaseLeft = new Vector(-5, 0);
	private Vector innerBaseRight = new Vector(5, 0);
	
	private BoundVector innerCPLeft = new BoundVector(-10, 0, -10, 0);
	private BoundVector innerCPRight = new BoundVector(10, 0, 10, 0);
	
	private BoundVector innerPeak = new BoundVector(-5, -15, 5, -20);
	
	private BoundVector[] boundVectors = {outerCP1Left, outerCP2Left, outerCPRight, outerPeak, innerCPLeft, innerCPRight, innerPeak};
	
	private Vector position;
	
	public void setPosition(Vector position) {
		this.position = position;
	}

	@Override
	public void enqueueForDraw(DrawingQueue queue) {
		queue.enqueue(new Drawable(50) {
			@Override
			public void draw(Graphics2D g) {
				Random rand = new Random(System.currentTimeMillis());
				int i = rand.nextInt(boundVectors.length);
				boundVectors[i].randomize();
				
				outerPeak.randomize();
				
				g.setTransform(AffineTransform.getTranslateInstance(position.intX(), position.intY()));
//				g.setTransform(AffineTransform.getScaleInstance(2, 2));
				
				GeneralPath outer = new GeneralPath();
				outer.moveTo(outerBaseLeft.x, outerBaseLeft.y);
				outer.curveTo(outerCP1Left.x, outerCP1Left.y, outerCP2Left.x, outerCP2Left.y, outerPeak.x, outerPeak.y);
				outer.quadTo(outerCPRight.x, outerCPRight.y, outerBaseRight.x, outerBaseRight.y);
//				outer.closePath();
				
				g.setColor(Color.red);
				g.fill(outer);
				
				GeneralPath inner = new GeneralPath();
				inner.moveTo(innerBaseLeft.x, innerBaseLeft.y);
				inner.quadTo(innerCPLeft.x, innerCPRight.y, outerPeak.x+5, innerPeak.y);
				inner.quadTo(innerCPRight.x, innerCPRight.y, innerBaseRight.x, innerBaseRight.y);
				inner.closePath();
				
				g.setColor(Color.yellow);
				g.fill(inner);
			}
		});
	}


}

class BoundVector extends Vector {
	
	double lowX;
	double lowY;
	double highX; 
	double highY;
	
	double diffX;
	double diffY;
	
	Random rand;
	
	public BoundVector(double lowX, double lowY, double highX,
			double highY) {
		this.lowX = lowX;
		this.lowY = lowY;
		this.highX = highX;
		this.highY = highY;
		
		this.x = (lowX + highX) / 2.0;
		this.y = (lowY + highY) / 2.0;
		
		diffX = highX - lowX;
		diffY = highY - lowY;
		
		rand = new Random(System.currentTimeMillis());
	}
	
	public void randomize() {
		x = randomize(x, diffX, lowX, highX);
		y = randomize(y, diffY, lowY, highY);
	}
	
	private double randomize(double val, double diff, double low, double high) {
		if (diff > 0) {
			double randFactor = (rand.nextDouble() - 0.5) / 3.0;		
			double delta = diff * randFactor;
			return constrain(val + delta, low, high);
		}
		else {
			return val;
		}
	}
	
	private double constrain(double val, double min, double max) {
		return Math.min(max, Math.max(min, val));
	}
}