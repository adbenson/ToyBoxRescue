package net.adbenson.toybox;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;



public class Handle extends Vector{
	
	public static final int SIZE = 40;
	private int halfSize = SIZE / 2;

	public Handle(int i, int j) {
		super(i, j);
	}

	public void draw(Graphics2D g2) {
        g2.setColor(Color.orange);
        g2.fillOval(intX()-halfSize, intY()-halfSize, SIZE, SIZE);
	}

	public boolean contains(Point point) {
		return (point.distance(this) <= halfSize);
	}

	public boolean contains(Vector point) {
		return (point.distance(this) <= halfSize);
	}
	
	public Rectangle getBounds() {
		return new Rectangle(intX()-halfSize, intY()-halfSize, SIZE, SIZE);
	}

	public boolean intersects(Shape shape) {
		Rectangle shapeBounds = shape.getBounds();
		if (! shapeBounds.intersects(this.getBounds())) {
			System.out.println("Handle collision shortcut passed");
			return false;
		}
		
		Point2D.Double shapeCenter = new Point2D.Double(shapeBounds.getCenterX(), shapeBounds.getCenterY());
		if (shapeCenter.distance(this) <= halfSize) {
			System.out.println("Handle collision shortcut failed");
			return true;
		}
		
		//If the bounding boxes intersect but the center is not inside the circle, we need to do more careful testing.
		System.out.println("Handle collision detail testing started");
		
		PathIterator pi = shape.getPathIterator(null);
		FlatteningPathIterator flattened = new FlatteningPathIterator(pi, 3, 3);
		double[] coords = new double[6];
		while(! flattened.isDone()) {
			int segType = flattened.currentSegment(coords);
			System.out.println("found point "+coords[0]+","+coords[1]);
			if (this.distance(new Point2D.Double(coords[0], coords[1])) <= halfSize) {
				System.out.println("Handle crash");
				return true;
			}
			flattened.next();
		}
		return false;
	}

}
