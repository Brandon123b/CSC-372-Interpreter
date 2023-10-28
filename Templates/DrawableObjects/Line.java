package Templates.DrawableObjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Line extends DrawableObject {
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
