package Templates.DrawableObjects;

import java.awt.Color;
import java.awt.Graphics;

public class Box extends DrawableObject {
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