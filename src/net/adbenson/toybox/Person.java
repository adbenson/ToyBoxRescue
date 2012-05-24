package net.adbenson.toybox;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.Random;


public class Person implements Comparable{
	private static final Area PROTO = generatePrototype();
	private static final Path2D WAVE = generateWaveShape();
	
	public static final double swayRate = 0.1;
	
	private Area fixedShape;
	private Area floatingShape;
	private Path2D wave;
	private Color color;
	private Vector location;
	private double size;
	
	private double sway;
	private double swaySpeed;
	private double swayOffset;
	
	public Person(Color color, Vector location, double scale) {
		floatingShape = (Area) PROTO.clone();
		wave = (Path2D) WAVE.clone();
		
		AffineTransform scaler = AffineTransform.getScaleInstance(scale, scale);
		AffineTransform translate = AffineTransform.getTranslateInstance(location.x, location.y);
		
		floatingShape.transform(scaler);
		wave.transform(scaler);
		fixedShape = (Area) floatingShape.clone();
		
		wave.transform(translate);
		floatingShape.transform(translate);
		
		this.color = color;
		this.location = location;
		this.size = scale;
		this.swayOffset = new Random().nextDouble();
		this.swaySpeed = (new Random().nextDouble() / 4);
	}
	
	private void updateSway() {
		swayOffset = Math.sin(sway += swaySpeed) * 3;
	}
	
	private static Path2D generateWaveShape() {
		Path2D.Double wave = new Path2D.Double();
		wave.moveTo(-15, 0);
		wave.curveTo(-15, 10, -5, 10, -5, 0);
		wave.curveTo(-5, 10, 5, 10, 5, 0);
		wave.curveTo(5, 10, 15, 10, 15, 0);
		wave.lineTo(15, 35);
		wave.lineTo(-15, 35);
		wave.closePath();
		
		return wave;
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
		draw(g, null, true);
	}

	public void draw(Graphics2D g, Vector location) {
		draw(g, location, false);
	}
	
	public void draw(Graphics2D g, Vector drawLocation, boolean inWater) {
		AffineTransform oldTrans = g.getTransform();
		
		if (inWater) {
			updateSway();
			g.setTransform(AffineTransform.getTranslateInstance(0, swayOffset));
		}
		else {
			g.setTransform(AffineTransform.getTranslateInstance(drawLocation.x, drawLocation.y));
		}
				
		g.setStroke(new BasicStroke(1));
		g.setColor(color);
		g.fill(inWater? floatingShape : fixedShape);
		g.setColor(Color.black);
		g.draw(inWater? floatingShape : fixedShape);
		

		
		if (inWater) {
			g.setColor(Color.blue);
//			g.setTransform(AffineTransform.getTranslateInstance(location.x, location.y));
			g.fill(wave);
		}
		
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
