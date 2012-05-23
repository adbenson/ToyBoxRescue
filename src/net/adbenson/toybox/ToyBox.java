package net.adbenson.toybox;

import Toy;

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

import javax.swing.JFrame;

public class ToyBox {
	
	public static void main(String[] args) {
		new ToyBox();
	}
	
	private enum state {
		START,
		RUN,
		PULLING,
		
		GAMEOVER
	}
		
	private Window window;
	
	private Painter painter;
	
	private Vector mousePos;
	
	private Toy toy;
	
	private Handle handle;
	
	boolean started;
	
	public ToyBox() {
		
		window = new Window();
		toy = new Toy(400, 300);
		painter = new Painter();
		mousePos = new Vector(0, 0);
		
		window.setPreferredSize(new Dimension(800, 600));
		
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		window.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent mouse) {
				mousePos = new Vector(mouse.getX(), mouse.getY());
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
		window.showCursor(false);
	}
	
	private void mouseUp(MouseEvent mouse) {
		window.showCursor(true);
	}
	
	class Window extends JFrame {
		
		private Shape wall;
		
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
		
		protected Shape generateWall() {
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
			
			return wall;
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
	       
	        g2.setColor(Color.orange);
	        g2.fillOval(mousePos.intX()-20, mousePos.intY()-20, 40, 40);

	        g.drawImage(offscreen, 0, 0, this);
		}

	}
		
	protected void tick() {
		toy.pull(handle);
		toy.move();
		detectCollisions();
	}
		
	private void detectCollisions() {
		Rectangle toyBounds = toy.getBounds();
		
		Area wallArea = new Area(window.getWall());
		if (! wallArea.contains(toyBounds)) { 
			System.out.println("Breaking wall");
			toy.crash();
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