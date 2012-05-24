package net.adbenson.drawing;

import java.awt.Graphics2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class DrawingQueue {
	
	private static Comparator<Drawable> comparator;
	
	private LinkedList<Drawable> list;
	
	public DrawingQueue() {
		if (comparator == null) {
			comparator = new Drawable.Compare();
		}
		
		list = new LinkedList<Drawable>();
	}
	
	public void enqueue(Drawable d) {
		list.add(d);
	}
	
	public void draw(Graphics2D g) {
		Collections.sort(list, comparator);
		
		while(! list.isEmpty()) {
			Drawable d = list.poll();
			d.triggerDraw(g);
		}
	}

	public void enqueue(DrawingQueueable de) {
		de.enqueueForDraw(this);
	}
}
