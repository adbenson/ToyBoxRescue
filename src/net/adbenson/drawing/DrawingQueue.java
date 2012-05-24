package net.adbenson.drawing;

import java.awt.Graphics2D;
import java.util.Comparator;
import java.util.PriorityQueue;

import net.adbenson.drawing.Drawable.Compare;

public class DrawingQueue {
	
	private static int initialCapacity = 1;
	private static Comparator<Drawable> comparator;
	
	private PriorityQueue<Drawable> q;
	
	public DrawingQueue() {
		if (comparator == null) {
			comparator = new Drawable.Compare();
		}
		
		q = new PriorityQueue<Drawable>(initialCapacity, comparator);
	}
	
	public void enqueue(Drawable d) {
		q.add(d);
	}
	
	public void draw(Graphics2D g) {
		initialCapacity = q.size();
		
		while(! q.isEmpty()) {
			Drawable d = q.poll();
			d.triggerDraw(g);
		}
	}
}
