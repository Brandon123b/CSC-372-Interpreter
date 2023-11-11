import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Test {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920 / 2, 1080 / 2); // Default window size
		frame.setLocationRelativeTo(null); // Center the window

		// Create a canvas and add it to the frame
		DrawingCanvas canvas = new DrawingCanvas();
		frame.add(canvas);
		frame.setVisible(true);

		if(args.length != 0) {
			System.err.println("Invalid number of arguments specified!");
			System.exit(1);
		}
		try {
			canvas.pie();
		} catch (Exception e) {
			System.err.println("Invalid arguments specified!");
			System.exit(1);
		} // consider program args

		canvas.startDrawing(); // Start drawing on the canvas
	}

}

/* -------------------------------------------------------------------------- */

class DrawingCanvas extends JPanel {
	public List<DrawableObject> drawableObjects_ = new ArrayList<>();
	private List<DrawableObject> objectsToRemove_ = new ArrayList<>(); // Avoid concurrent modification

	private double scaleX_; // Scaling factor for X-axis
	private double scaleY_; // Scaling factor for Y-axis

	public DrawingCanvas() {

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				// Convert mouse click coordinates to logical coordinates
				int logicalX = (int) (e.getPoint().x / scaleX_);
				int logicalY = (int) (e.getPoint().y / scaleY_);

				// Check if any objects were clicked
				for (DrawableObject drawableObject : drawableObjects_) {

					// An object was clicked if it contains the mouse click
					if (drawableObject.contains(logicalX, logicalY)) {
						drawableObject.onClick();
					}
				}

			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// Calculate the scaling factors
				scaleX_ = (double) getWidth() / 1920;	// Default logical size
				scaleY_ = (double) getHeight() / 1080;	// Default logical size
			}
		});
	}

	// Starts the gameloop (and draws)
	public void startDrawing() {
		Timer timer = new Timer(20, e -> {

			Gameloop(); // Only if it exists
			repaint();
		});
		timer.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Scale the graphics context by applying the scaling factors
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(scaleX_, scaleY_);

		// Draw all objects
		for (DrawableObject drawableObject : drawableObjects_) {
			drawableObject.draw(g);
		}

		// Remove the objects marked for removal
		drawableObjects_.removeAll(objectsToRemove_);
		objectsToRemove_.clear();
	}

	/* -------------------------------------------------------------------------- */
	/*                           Interpreted Global Vars                          */
	/* -------------------------------------------------------------------------- */

	int line1ly;
	Circle cirk;
	int line1lx;
	int line1rx;
	int xPos;
	Line line1;
	int line1ry;


	/* -------------------------------------------------------------------------- */
	/*                            Interpreted Functions                           */
	/* -------------------------------------------------------------------------- */

	public void pie(){

		Box Boxey = new Box();
		drawableObjects_.add(Boxey);
		Boxey.moveTo((int)100.0, (int)100.0);
		Boxey.setSize((int)200, (int)500);
		Boxey.setColor(new Color((int)255, (int)0, (int)0, (int)50));
		cirk = new Circle();
		drawableObjects_.add(cirk);
		cirk.setRadius((int)30);
		xPos = 0;
		line1 = new Line();
		drawableObjects_.add(line1);
		line1lx = 0;
		line1ly = 0;
		line1rx = 1920;
		line1ry = 1080;
		line1.setSize((int)10);
		Text text1 = new Text();
		drawableObjects_.add(text1);
		text1.setText("Hello World!" + "  Goodbye!");
		text1.moveTo((int)100, (int)100);
		text1.setSize((int)80);
	}
	public void Gameloop(){

		xPos = xPos+2;
		cirk.moveTo((int)xPos, (int)500);
		line1lx = line1lx+3;
		line1rx = line1rx-3;
		line1.setLine((int)line1lx, (int)line1ly, (int)line1rx, (int)line1ry);
	}


}

/* -------------------------------------------------------------------------- */
/*                                 GUI Objects                                */
/* -------------------------------------------------------------------------- */

abstract class DrawableObject {
	protected int x, y; // Position
	protected Color color;

	private Consumer<Integer> onClicked = input -> {
	}; // Default empty lambda

	public DrawableObject(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	// Moves the object to the specified position
	public void moveTo(int x, int y) {
		this.x = x;
		this.y = y;
	}

	// Sets the onClicked lambda
	public void setOnClicked(Consumer<Integer> onClicked) {
		this.onClicked = onClicked;
	}

	// Sets the color of the object
	public void setColor(Color color) {
		this.color = color;
	}

	// Calls the onClicked lambda
	public void onClick() {
		onClicked.accept(0);
	}

	// Draws the object to the Graphics object
	public abstract void draw(Graphics g);

	// Used for checking if a point is inside the object (for mouse clicks)
	public abstract boolean contains(int x, int y);
}

class Box extends DrawableObject {
	private int width, height;

	public Box() {
		super(0, 0, Color.BLACK);
		this.width = 100;
		this.height = 100;
	}

	public Box(int x, int y, int width, int height, Color color) {
		super(x, y, color);
		this.width = width;
		this.height = height;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
	}

	@Override
	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
	}
}

class Circle extends DrawableObject {
	private int radius;

	public Circle() {
		super(0, 0, Color.BLACK);
		this.radius = 50;
	}

	public Circle(int x, int y, int radius, Color color) {
		super(x, y, color);
		this.radius = radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
	}

	@Override
	public boolean contains(int x, int y) {
		int dx = this.x - x;
		int dy = this.y - y;
		return dx * dx + dy * dy <= radius * radius;
	}
}

class Line extends DrawableObject {
	private Point start; // Starting point of the line
	private Point end; // Ending point of the line
	private int size = 1; // Line thickness

	public Line() {
		super(-1, -1, Color.BLACK); // position is not used

		// Define the starting and ending points
		start = new Point(0, 0);
		end = new Point(0, 0);
	}

	public Line(int x1, int y1, int x2, int y2, Color color) {
		super(-1, -1, color); // position is not used

		// Define the starting and ending points
		start = new Point(x1, y1);
		end = new Point(x2, y2);
	}

	// Sets the starting and ending points of the line
	public void setLine(int x1, int y1, int x2, int y2) {
		start.setLocation(x1, y1);
		end.setLocation(x2, y2);
	}
	
    // Sets the line thickness
    public void setSize(int size) {
        this.size = size;
    }

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		((Graphics2D) g).setStroke(new BasicStroke(size));
		g.drawLine(start.x, start.y, end.x, end.y);
	}

	@Override
	public boolean contains(int x, int y) {
		return false; // Lines are not clickable (yet?)
	}
}

class Text extends DrawableObject {
	private String text;
	private Font font = new Font("Arial", Font.PLAIN, 24); // Default font

	public Text() {
		super(0, 0, Color.BLACK);
		this.text = "";
	}

	public Text(int x, int y, String text, Color color) {
		super(x, y, color);
		this.text = text;
	}

	// Sets the text of the object
	public void setText(String text) {
		this.text = text;
	}

	// Sets the font of the text (Maybe add a font size parameter? or command?)
	public void setFont(Font font) {
		this.font = font;
	}

	// Sets the size of the font
	public void setSize(int size) {
		font = new Font(font.getName(), font.getStyle(), size);
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, x, y);
	}

	@Override
	public boolean contains(int x, int y) {
		// Text objects are not clickable, so always return false
		return false;
	}
}
