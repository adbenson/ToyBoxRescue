package net.adbenson.toybox;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;


public class Person implements Comparable{
	private static final Area proto = generatePrototype();
	
	private Area fixedShape;
	private Area floatingShape;
	private Color color;
	private Vector location;
	private double size;
	
	public Person(Color color, Vector location, double scale) {
		floatingShape = (Area) proto.clone();
		
		floatingShape.transform(AffineTransform.getScaleInstance(scale, scale));
		fixedShape = (Area) floatingShape.clone();
		
		floatingShape.transform(AffineTransform.getTranslateInstance(location.x, location.y));
		
		this.color = color;
		this.location = location;
		this.size = scale;
	}
	
	private static Area generatePrototype() {
		Path2D.Double body = new Path2D.Double();
		body.moveTo(1, 30);//between feet
		body.lineTo(10, 30);//R foot
		body.lineTo(10, 0);//R armpit
		body.lineTo(25, -15);//R hand
		body.lineTo(20, -20);//R hand
		body.lineTo(10, -10);//R shoulder
		body.lineTo(-10, -10);//L shoulder
		body.lineTo(-20, -20);//L hand
		body.lineTo(-25, -15);//L hand
		body.lineTo(-10, 0);//L armpit
		body.lineTo(-10, 30);//L foot
		body.lineTo(-1, 30);//between feet
		body.lineTo(-1, 10);//crotch
		body.lineTo(1, 10);//crotch
		body.closePath();
		
		Ellipse2D.Double head = new Ellipse2D.Double(-10, -30, 20, 20);
		
		Area area = new Area(body);
		area.add(new Area(head));
		return area;
	}

	public Vector getLocation() {
		return location;
	}
	
	public void draw(Graphics2D g) {
		g.setStroke(new BasicStroke(1));
		g.setColor(color);
		g.fill(floatingShape);
		g.setColor(Color.black);
		g.draw(floatingShape);
	}

	public void draw(Graphics2D g, Vector location) {
		AffineTransform oldTrans = g.getTransform();
		
		g.setTransform(AffineTransform.getTranslateInstance(location.x, location.y));
		g.setStroke(new BasicStroke(1));
		g.setColor(color);
		g.fill(fixedShape);
		g.setColor(Color.black);
		g.draw(fixedShape);
		
		g.setTransform(oldTrans);
	}

	@Override
	public int compareTo(Object arg0) {
		return (int) Math.signum(((Person)arg0).size - this.size);
	}
	
	public Rectangle getBounds() {
		return floatingShape.getBounds();
	}
}
