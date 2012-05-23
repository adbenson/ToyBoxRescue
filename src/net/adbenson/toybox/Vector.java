package net.adbenson.toybox;

import java.awt.geom.Point2D;

public class Vector extends Point2D.Double {
		
	static final double FULL_CIRCLE = 2 * Math.PI;
	
	public Vector() {
		x = 0;
		y = 0;
	}
	
	public Vector invert() {
		return new Vector(-x, -y);
	}

	public double getAngle() {
		Vector normal = this.normalize();
		double angle = (FULL_CIRCLE - Math.atan2(normal.x, normal.y));				
		return angle;
	}

	public Vector normalize() {
		double length = this.magnitude();
		if (length != 0) {
			return new Vector(x / length, y / length);
		}
		else {
			return new Vector(0, 0);
		}
	}
	
	public double magnitude() {
		return this.distance(0, 0);
	}

	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector scale(double delta) {
		return new Vector(x*delta, y*delta);
	}
	
	public Vector add(Vector position) {
		return new Vector(this.x + position.x, this.y + position.y);
	}
	
	public Vector subtract(Vector vector) {
		return new Vector(this.x - vector.x, this.y - vector.y);
	}
	
	public int intX() {
		return (int) Math.round(x);
	}
	
	public int intY() {
		return (int) Math.round(y);
	}
}