package net.adbenson.drawing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Comparator;

public abstract class Drawable {
	
	private int layer;
	
	private Color prevColor;
	private AffineTransform prevTransform;
	private Stroke prevStroke;
	
	public Drawable() {
		this(0);
	}
	
	public Drawable(int layer) {
		this.layer = layer;
	}

	protected final void triggerDraw(Graphics2D g) {
		prevColor = g.getColor();
		prevTransform = g.getTransform();
		prevStroke = g.getStroke();
		
		draw(g);
		
		g.setColor(prevColor);
		g.setTransform(prevTransform);
		g.setStroke(prevStroke);
	}

	public abstract void draw(Graphics2D g);

	public final int getDrawLayer() {
		return layer;
	}
	
	public final void setDrawLayer(int layer) {
		this.layer = layer;
	}
	
	public static final class Compare implements Comparator<Drawable> {
		@Override
		public int compare(Drawable arg0, Drawable arg1) {
			return arg0.layer - arg1.layer;
		}
	}
}
