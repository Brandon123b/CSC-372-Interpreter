import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class World {

	public static void main(String[] args) {
		JFrame frame = new JFrame("World");
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
			canvas.Init();
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

	int sunLinesLength;
	double cos1;
	int sunLineSize;
	double cos15;
	double sin1;
	double sunLineY;
	Circle sun;
	double sunLineX;
	Box backGround;
	int sunLinesCount;
	int sunLinesGap;
	int sunRadius;
	int sunPosy;
	int sunPosx;
	double sin15;
	List<Line> sunLinesList = new ArrayList<>();


	/* -------------------------------------------------------------------------- */
	/*                            Interpreted Functions                           */
	/* -------------------------------------------------------------------------- */

	public void Init(){

		backGround = new Box();
		drawableObjects_.add(backGround);
		backGround.moveTo(0, 0);
		backGround.setSize(1920, 1080);
		backGround.setColor(new Color(135, 206, 250, 255));
		CreateSun();
	}
	public void CreateSun(){

		sunRadius = 115;
		sunLinesCount = 24;
		sunLinesGap = sunRadius+25;
		sunLineSize = 5;
		sunLinesLength = 75;
		sunPosx = 1600;
		sunPosy = 200;
		sun = new Circle();
		drawableObjects_.add(sun);
		sun.setRadius(sunRadius);
		sun.setColor(new Color(255, 255, 102, 255));
		sun.moveTo(sunPosx, sunPosy);
		sun.setOnClick(obj -> ClickSun((Circle)obj));
		int index = sunLinesCount;
		while (index>0) {
			Line sunLine = new Line();
			drawableObjects_.add(sunLine);
			sunLine.setSize(sunLineSize);
			sunLine.setColor(new Color(255, 255, 102, 255));
			sunLinesList.add(sunLine);
			index = index-1;
		}
		cos15 = 0.96592582628;
		sin15 = 0.2588190451;
		cos1 = 0.99984769515639123915701155881391;
		sin1 = 0.01745240643728351281941897851632;
		sunLineX = 0.0;
		sunLineY = 1.0;
	}
	public void AnimateSun(){

		sunLineX = sunLineX*cos1-sunLineY*sin1;
		sunLineY = sunLineX*sin1+sunLineY*cos1;
		double x = sunLineX;
		double y = sunLineY;
		int index = 0;
		while (index<sunLinesCount) {
			Line sunLine = sunLinesList.get(index);
			int newLineLength = sunLinesLength;
			if (index%2==0) {
				newLineLength = newLineLength*2;
			}
			double tempx = x;
			x = x*cos15-y*sin15;
			y = tempx*sin15+y*cos15;
			double startX = sunPosx+x*sunLinesGap;
			double startY = sunPosy+y*sunLinesGap;
			sunLine.setLine(startX, startY, startX+x*newLineLength, startY+y*newLineLength);
			index = index+1;
		}
	}
	public void Gameloop(){

		AnimateSun();
	}
	public void ClickSun(Circle tempSun){

		sun.setColor(new Color(0, 0, 0, 255));
		int index = 0;
		while (index<sunLinesCount) {
			Line sunLine = sunLinesList.get(index);
			sunLine.setColor(new Color(255, 0, 0, 255));
			index = index+1;
		}
	}


}

/* -------------------------------------------------------------------------- */
/*                                 GUI Objects                                */
/* -------------------------------------------------------------------------- */

abstract class DrawableObject {
	protected double x, y; // Position
	protected Color color;

	private Consumer<DrawableObject> onClicked = input -> {
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
	public void setOnClick(Consumer<DrawableObject> onClicked) {
		this.onClicked = onClicked;
	}

	// Sets the color of the object
	public void setColor(Color color) {
		this.color = color;
	}

	// Calls the onClicked lambda
	public void onClick() {
		onClicked.accept(this);
	}

	// Draws the object to the Graphics object
	public abstract void draw(Graphics g);

	// Used for checking if a point is inside the object (for mouse clicks)
	public abstract boolean contains(int x, int y);
}

class Box extends DrawableObject {
	private double width, height;

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
	public String toString() {
		return String.format("Box(x: %d, y: %d, width: %d, height: %d, color: %s)", (int)x, (int)y, (int)width, (int)height, color.toString());
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect((int)x, (int)y, (int)width, (int)height);
	}

	@Override
	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
	}
}

class Circle extends DrawableObject {
	private double radius;

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
	public String toString() {
		return String.format("Circle(x: %d, y: %d, radius: %d, color: %s)", (int)x, (int)y, (int)radius, color.toString());
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
	}

	@Override
	public boolean contains(int x, int y) {
		double dx = this.x - x;
		double dy = this.y - y;
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

	public Line(double x1, double y1, double x2, double y2, Color color) {
		super(-1, -1, color); // position is not used

		// Define the starting and ending points
		start = new Point((int)x1, (int)y1);
		end = new Point((int)x2, (int)y2);
	}

	// Sets the starting and ending points of the line
	public void setLine(double x1, double y1, double x2, double y2) {
		start.setLocation(x1, y1);
		end.setLocation(x2, y2);
	}
	
    // Sets the line thickness
    public void setSize(int size) {
        this.size = size;
    }

	@Override
	public String toString() {
		return String.format("Line(x1: %d, y1: %d, x2: %d, y2: %d, color: %s)", start.x, start.y, end.x, end.y, color.toString());
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		((Graphics2D) g).setStroke(new BasicStroke((int) size));
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
	public String toString() {
		return String.format("Text(x: %d, y: %d, text: %s, color: %s)", (int)x, (int)y, text, color.toString());
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, (int)x, (int)y);
	}

	@Override
	public boolean contains(int x, int y) {
		// Text objects are not clickable, so always return false
		return false;
	}
}
