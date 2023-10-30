import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class test {

	public static void main(String[] args) {
		JFrame frame = new JFrame("test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920 / 2, 1080 / 2); // Default window size
		frame.setLocationRelativeTo(null); // Center the window

		// Create a canvas and add it to the frame
		DrawingCanvas canvas = new DrawingCanvas();
		frame.add(canvas);
		frame.setVisible(true);

		/* {Call_Start} */ // consider program args
		canvas.DisplayCommands();

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

			/* {Call_GameLoop} */ // Only if it exists
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

/* {Global_Vars} */
	Circle circle1;

	/* -------------------------------------------------------------------------- */
	/*                            Interpreted Functions                           */
	/* -------------------------------------------------------------------------- */

	public static void fun0(){

	}
	public static void Fun1(int hi){

	}
	public static void Fun2(int hi, boolean foo){

	}
	public static void Fun3(int int0, boolean bool1, boolean foo){

	}


	/* -------------------------------------------------------------------------- */
	/*                                    Temp                                    */
	/* -------------------------------------------------------------------------- */

	// A test function to show how some of these commands will work for the GUI (TODO: Remove)
	public void DisplayCommands() {

		/* ----------------------------------- Box ---------------------------------- */

		// Create a Box called box1 # Same for Circle, line, and text
		Box box1; // To be placed at top of function
		box1 = new Box(0, 0, 0, 0, Color.BLACK);
		drawableObjects_.add(box1);

		// Set the size of Box box1 to 1900 and 540 # Width, height
		box1.setSize(1900, 540);
		// Move box1 to 10 and 150 # x and y pos
		box1.moveTo(10, 150);

		// If box1 is clicked call function OnBox1Clicked
		box1.setOnClicked(input -> OnBox1Clicked());

		/* --------------------------------- Circle --------------------------------- */

		// Create a global circle called circle1
		circle1 = new Circle(0, 0, 0, Color.BLACK);
		drawableObjects_.add(circle1);

		// Set the size of Circle circle1 to 30 # Radius
		circle1.setRadius(30);
		// Move circle1 to 300 and 150 # x and y pos
		circle1.moveTo(300, 150);

		// Set the color of circle1 to red # Hex or some premade colors?
		circle1.setColor(Color.RED);

		// If circle1 is clicked call function OnCircle1Clicked
		circle1.setOnClicked(input -> OnCircle1Clicked());

		/* ---------------------------------- Line ---------------------------------- */

		// Create a line called line1
		Line line1; // To be placed at top of functions
		line1 = new Line(0, 0, 0, 0, Color.BLACK);
		drawableObjects_.add(line1);

		// Set the line1 to go from 0, 0 to 100, 100
		line1.setLine(0, 0, 100, 100);

		/* ---------------------------------- Text ---------------------------------- */

		// Create a text called text1
		Text text1; // To be placed at top of functions
		text1 = new Text(0, 0, "Hello World!", Color.BLACK);
		drawableObjects_.add(text1);

		// Set the size of text1 to 50
		text1.setFontSize(50);

		// Move text1 to 100 and 50
		text1.moveTo(100, 50);
	}

	// A test function to show how the onClicked functions will work (TODO: Remove)
	public void OnBox1Clicked() {
		System.out.println("Box 1 Clicked!");
	}

	// A test function to show how the onClicked functions will work (TODO: Remove)
	public void OnCircle1Clicked() {
		System.out.println("Circle 1 killed!");

		// Remove circle1 from the canvas. (circle1 is global)
		objectsToRemove_.add(circle1);
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

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.drawLine(start.x, start.y, end.x, end.y);
	}

	@Override
	public boolean contains(int x, int y) {
		return false; // Lines are not clickable (yet?)
	}
}

class Text extends DrawableObject {
	private String text;
	private Font font = new Font("Arial", Font.PLAIN, 12); // Default font

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
	public void setFontSize(int size) {
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
