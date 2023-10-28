package Templates.DrawableObjects;

import java.awt.Color;
import java.awt.Graphics;

public class Circle extends DrawableObject {
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
