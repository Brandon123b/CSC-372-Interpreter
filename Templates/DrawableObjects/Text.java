package Templates.DrawableObjects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Text extends DrawableObject {
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
