import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import Templates.DrawableObjects.*;
import Templates.DrawableObjects.Box;

public class MainTemplate { // TODO: Replace with program name

	public static void main(String[] args) {
		JFrame frame = new JFrame("Simple Drawing Program"); // TODO: Replace with program name
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

class DrawingCanvas extends JPanel {
	public List<DrawableObject> drawableObjects = new ArrayList<>();
	private List<DrawableObject> objectsToRemove = new ArrayList<>(); // Avoid concurrent modification

	private double scaleX; // Scaling factor for X-axis
	private double scaleY; // Scaling factor for Y-axis

	public DrawingCanvas() {

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				// Convert mouse click coordinates to logical coordinates
				int logicalX = (int) (e.getPoint().x / scaleX);
				int logicalY = (int) (e.getPoint().y / scaleY);

				// Check if any objects were clicked
				for (DrawableObject drawableObject : drawableObjects) {

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
				scaleX = (double) getWidth() / 1920;	// Default logical size
				scaleY = (double) getHeight() / 1080;	// Default logical size
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
		g2d.scale(scaleX, scaleY);

		// Draw all objects
		for (DrawableObject drawableObject : drawableObjects) {
			drawableObject.draw(g);
		}

		// Remove the objects marked for removal
		drawableObjects.removeAll(objectsToRemove);
		objectsToRemove.clear();
	}

	/* -------------------------------------------------------------------------- */
	/* User Added Functions */
	/* -------------------------------------------------------------------------- */

	/* {Global_Vars} */
	Circle circle1; // To be placed at top of function

	/* {Functions} */

	// A test function to show how some of these commands will work for the GUI (TODO: Remove)
	public void DisplayCommands() {

		/* ----------------------------------- Box ---------------------------------- */

		// Create a Box called box1 # Same for Circle, line, and text
		Box box1; // To be placed at top of function
		box1 = new Box(0, 0, 0, 0, Color.BLACK);
		drawableObjects.add(box1);

		// Set the size of Box box1 to 1900 and 540 # Width, height
		box1.setSize(1900, 540);
		// Move box1 to 10 and 150 # x and y pos
		box1.moveTo(10, 150);

		// If box1 is clicked call function OnBox1Clicked
		box1.setOnClicked(input -> OnBox1Clicked());

		/* --------------------------------- Circle --------------------------------- */

		// Create a global circle called circle1
		circle1 = new Circle(0, 0, 0, Color.BLACK);
		drawableObjects.add(circle1);

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
		drawableObjects.add(line1);

		// Set the line1 to go from 0, 0 to 100, 100
		line1.setLine(0, 0, 100, 100);

		/* ---------------------------------- Text ---------------------------------- */

		// Create a text called text1
		Text text1; // To be placed at top of functions
		text1 = new Text(0, 0, "Hello World!", Color.BLACK);
		drawableObjects.add(text1);

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
		objectsToRemove.add(circle1);
	}
}
