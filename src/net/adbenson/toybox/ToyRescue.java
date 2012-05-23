package net.adbenson.toybox;

import static net.adbenson.toybox.ToyRescue.State.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JFrame;

public class ToyRescue {
	
	public static void main(String[] args) {
		new ToyRescue();
	}
	
	public enum State {
		START,
		DROPPED,
		GRABBED,
		GAMEOVER
	}
	
	private State state;
		
	private Window window;
	
	private Painter painter;
	
	private Vector mousePos;
	
	private Boat toy;
	
	private Handle handle;
	
	private LinkedList<Person> survivors;
	private LinkedList<Person> floating;
	
	
	public ToyRescue() {
		state = START;
		
		window = new Window();
		toy = new Boat(400, 300);
		painter = new Painter();
		survivors = new LinkedList<Person>();
		floating = new LinkedList<Person>();
		Random r = new Random(System.currentTimeMillis());
		for(int i=0; i < 10; i++) {
			Vector location = new Vector(r.nextInt(760)+20, r.nextInt(540)+40);
			Person p = new Person(Color.getHSBColor(r.nextFloat(), 1.0f, 1.0f), location, (r.nextDouble()*0.25)+0.25);
			survivors.add(p);
			floating.add(p);
		}
		
		window.setPreferredSize(new Dimension(800, 600));
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent mouse) {
				if (state == GRABBED) {
					handle.setLocation(mouse.getPoint());
				}
			}
		});
		
		window.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				mouseDown(arg0);
			}
			public void mouseReleased(MouseEvent arg0) {
				mouseUp(arg0);
			}
			
		});
		
		handle = new Handle(100, 100);
		
		window.pack();
		window.setVisible(true);
		painter.start();
	}
	
	private void mouseDown(MouseEvent mouse) {
		if ((state == START || state == DROPPED) && handle.contains(mouse.getPoint())) {
			state = GRABBED;
			window.showCursor(false);
		}
	}
	
	private void mouseUp(MouseEvent mouse) {
		window.showCursor(true);
		if (state == GRABBED) {
			state = DROPPED;
		}
	}
	
	class Window extends JFrame {
		
		private Area wall;
		
		public static final int WALL_WIDTH = 20;
		public static final int MENU_HEIGHT = 20;
		
		private final Cursor blankCursor;
		
		public Window() {
	        this.getRootPane().addComponentListener(new ComponentAdapter() {
	            public void componentResized(ComponentEvent e) {
	                wall = generateWall();
	            }
	        });
			
			wall = generateWall();
			
			// Transparent 16 x 16 pixel cursor image.
			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

			// Create a new blank cursor.
			blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			    cursorImg, new Point(0, 0), "blank cursor");
		}
		
		public void showCursor(boolean show) {
			if (show) {
				getContentPane().setCursor(null);
			}
			else {
				getContentPane().setCursor(blankCursor);
			}
		}
		
		protected Area generateWall() {
			GeneralPath wall = new GeneralPath();
			int width = this.getWidth();
			int height = this.getHeight();
			int ww = WALL_WIDTH;
			int wm = ww + MENU_HEIGHT;
			
			//Inner border
			wall.moveTo(ww, wm);
			wall.lineTo(width - ww, wm);
			wall.lineTo(width - ww, height - ww);
			wall.lineTo(ww, height - ww);
			wall.lineTo(ww, wm);
			wall.closePath();
			
			return new Area(wall);
		}
		
		public Shape getWall() {
			return wall;
		}
				
		public void paint(Graphics g) {
			Image offscreen = createImage(this.getWidth(), this.getHeight());
			
			Graphics2D g2 = (Graphics2D) offscreen.getGraphics();			
			
	        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING,
	                              RenderingHints.VALUE_ANTIALIAS_ON );
	        g2.setRenderingHint( RenderingHints.KEY_RENDERING,
	                              RenderingHints.VALUE_RENDER_QUALITY );
	        
	        g2.setColor(Color.orange);
	        g2.fillRect(0, 0, this.getWidth(), this.getHeight());
	       
	        g2.setColor(Color.blue);
	        g2.fill(wall);

	        toy.draw(g2);
	       
	        handle.draw(g2);
	        
	        for(Person p : floating) {
	        	p.draw(g2);
	        }
	        
	        new Person(Color.green, handle, 0.3).draw(g2);
	        
	        g.drawImage(offscreen, 0, 0, this);
		}

	}
		
	protected void tick() {
		if (state == DROPPED || state == GRABBED) {
			toy.pull(handle);
			toy.move();
			detectCollisions();
		}
	}
		
	private void detectCollisions() {
		if (state != START && state != GAMEOVER){ 
			Rectangle toyBounds = toy.getBounds();
			
			boolean wallCrash = ! window.getWall().contains(toyBounds);
			boolean handleCrash = false;//handle.contains(toy.getShape());
			if (wallCrash || handleCrash) { 
				System.out.println("Crash: wall?"+wallCrash+" handle?"+handleCrash);
				toy.crash();
				state = GAMEOVER;
			}
			
			LinkedList<Person> pickedUp = new LinkedList<Person>();
			for(Person p : floating) {
				if (toy.intersects(p)) {
					toy.pickup(p);
					pickedUp.add(p);
				}
			}
			floating.removeAll(pickedUp);
		}
		
	}

	class Painter {
		private static final int TICK_ms = 20;
		private long lastTick = 0;
		private volatile boolean run;
		
		public synchronized void start() {
			run = true;
			loop();
		}
		
		private void loop() {
			while(run) {
				tick();
				window.repaint();
				try {
					Thread.sleep(TICK_ms);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}
		
		public synchronized void stop() {
			run = false;
		}
		
	}

}