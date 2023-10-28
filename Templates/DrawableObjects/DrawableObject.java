package Templates.DrawableObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.function.Consumer;

public abstract class DrawableObject {
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