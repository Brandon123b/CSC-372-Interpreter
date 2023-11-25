import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class BulletHell {

	public static void main(String[] args) {
		JFrame frame = new JFrame("BulletHell");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920 / 2, 1080 / 2); // Default window size
		frame.setLocationRelativeTo(null); // Center the window

		// Create a canvas and add it to the frame
		DrawingCanvas canvas = new DrawingCanvas();
		frame.add(canvas);
		frame.setVisible(true);

		if(args.length != 1) {
			System.err.println("Invalid number of arguments specified!");
			System.exit(1);
		}
		try {
			canvas.Main(Integer.parseInt(args[0]));
		} catch (Exception e) {
			System.err.println("Invalid arguments specified!");
			System.exit(1);
		} // consider program args

		canvas.startDrawing(); // Start drawing on the canvas
	}

}

/* -------------------------------------------------------------------------- */

class DrawingCanvas extends JPanel {
	public List<DrawableObject> drawableObjects_ = Collections.synchronizedList(new ArrayList<>());
	private List<DrawableObject> objectstoAdd_ = Collections.synchronizedList(new ArrayList<>());
	private List<DrawableObject> objectsToRemove_ = Collections.synchronizedList(new ArrayList<>());

	private List<Integer> keysPressed_ = new ArrayList<>(); // List of keys currently pressed

	private double scaleX_; // Scaling factor for X-axis
	private double scaleY_; // Scaling factor for Y-axis

	public DrawingCanvas() {

		setFocusable(true);
		requestFocus();

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						// Convert mouse click coordinates to logical coordinates
						int logicalX = (int) (e.getPoint().x / scaleX_);
						int logicalY = (int) (e.getPoint().y / scaleY_);

						// Create a copy of the list to avoid concurrent modification
						List<DrawableObject> copyOfList = new ArrayList<>(drawableObjects_);

						// Check if any objects were clicked in the copied list
						for (DrawableObject drawableObject : copyOfList) {
							// An object was clicked if it contains the mouse click
							if (drawableObject.contains(logicalX, logicalY)) {
								drawableObject.onClick();
							}
						}
					}
				});
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				// Calculate the scaling factors
				scaleX_ = (double) getWidth() / 1920; // Default logical size
				scaleY_ = (double) getHeight() / 1080; // Default logical size
			}
		});

		// Add listener for keysPressed_
		addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				if (!keysPressed_.contains(evt.getKeyCode())) {
					keysPressed_.add(evt.getKeyCode());
				}
			}

			public void keyReleased(java.awt.event.KeyEvent evt) {
				keysPressed_.remove((Object) evt.getKeyCode());
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
		drawableObjects_.addAll(objectstoAdd_);
		objectstoAdd_.clear();
		drawableObjects_.removeAll(objectsToRemove_);
		objectsToRemove_.clear();
	}

	/* -------------------------------------------------------------------------- */
	/* Interpreted Global Vars */
	/* -------------------------------------------------------------------------- */

		double pSize;
	int intRet;
	int seed;
	List<Integer> bulletXVelList = new ArrayList<>();
	Box hpBar;
	List<Integer> bulletYVelList = new ArrayList<>();
	List<Box> platformsList = new ArrayList<>();
	List<Double> platformXPos = new ArrayList<>();
	int hp;
	double bulletRadius;
	int attackCooldown;
	double pSpeed;
	List<Circle> bulletsList = new ArrayList<>();
	boolean isGrounded;
	Text titleText;
	Box basePlatform;
	Box player;
	Text startButtonText;
	List<Double> platformXSizes = new ArrayList<>();
	Box startButtonBox;
	double pX;
	List<Double> platformYPos = new ArrayList<>();
	double pY;
	List<Integer> bulletYPosList = new ArrayList<>();
	boolean isAlive;
	List<Double> platformYSizes = new ArrayList<>();
	List<Integer> bulletXPosList = new ArrayList<>();
	Box backGround;
	double pVelY;


	/* -------------------------------------------------------------------------- */
	/* Interpreted Functions */
	/* -------------------------------------------------------------------------- */

		public void Main(int _seed){

		System.out.println(_seed);
		seed = _seed;
		intRet = 0;
		attackCooldown = 0;
		LoadMenu();
	}
	public void LoadMenu(){

		KillAll();
		backGround = new Box();
		drawableObjects_.add(backGround);
		backGround.moveTo(0, 0);
		backGround.setSize(1920, 1080);
		backGround.setColor(new Color(10, 10, 25, 255));
		InitHPBar();
		titleText = new Text();
		drawableObjects_.add(titleText);
		titleText.moveTo(500, 340);
		titleText.setSize(100);
		titleText.setColor(new Color(255, 255, 255, 255));
		titleText.setText("A Simple Bullet Hell");
		startButtonBox = new Box();
		drawableObjects_.add(startButtonBox);
		startButtonBox.moveTo(600, 500);
		startButtonBox.setSize(500, 100);
		startButtonBox.setColor(new Color(255, 255, 255, 255));
		startButtonBox.setOnClick(obj -> OnStart());
		startButtonText = new Text();
		drawableObjects_.add(startButtonText);
		startButtonText.moveTo(600, 580);
		startButtonText.setSize(100);
		startButtonText.setColor(new Color(0, 0, 0, 255));
		startButtonText.setText("Start");
		OnStart();
	}
	public void OnStart(){

		objectsToRemove_.add(titleText);
		objectsToRemove_.add(startButtonBox);
		objectsToRemove_.add(startButtonText);
		StartGame();
	}
	public void LoadDeathMenu(){

		isAlive = false;
		titleText = new Text();
		drawableObjects_.add(titleText);
		titleText.moveTo(500, 340);
		titleText.setSize(100);
		titleText.setColor(new Color(255, 0, 0, 255));
		titleText.setText("You Died");
		startButtonBox = new Box();
		drawableObjects_.add(startButtonBox);
		startButtonBox.moveTo(600, 500);
		startButtonBox.setSize(500, 100);
		startButtonBox.setColor(new Color(255, 255, 255, 255));
		startButtonBox.setOnClick(obj -> LoadMenu());
		startButtonText = new Text();
		drawableObjects_.add(startButtonText);
		startButtonText.moveTo(600, 580);
		startButtonText.setSize(100);
		startButtonText.setColor(new Color(0, 0, 0, 255));
		startButtonText.setText("Main Menu");
	}
	public void StartGame(){

		isAlive = true;
		SpawnPlatforms();
		InitPlayer();
		CreateBullet(100, 100, 10, 10);
	}
	public void SpawnPlatforms(){

		basePlatform = new Box();
		drawableObjects_.add(basePlatform);
		double xSize = 1920*2/3.0;
		double ySize = 150.0;
		double xPos = 1920/5.0;
		double yPos = 1080*4/5.0;
		basePlatform.setSize(xSize, ySize);
		basePlatform.moveTo(xPos, yPos);
		basePlatform.setColor(new Color(255, 255, 255, 255));
		platformsList.add(basePlatform);
		platformXSizes.add(xSize);
		platformYSizes.add(ySize);
		platformXPos.add(xPos);
		platformYPos.add(yPos);
	}
	public void InitPlayer(){

		pX = 1920.0/2.0;
		pY = 300.0;
		pSpeed = 15.0;
		pVelY = 0.0;
		pSize = 50.0;
		isGrounded = false;
		hp = 100;
		player = new Box();
		drawableObjects_.add(player);
		player.setSize(pSize, pSize);
		player.setColor(new Color(0, 255, 0, 255));
		player.moveTo(pX, pY);
	}
	public void InitHPBar(){

		hpBar = new Box();
		drawableObjects_.add(hpBar);
		hpBar.setSize(300, 50);
		hpBar.setColor(new Color(0, 255, 0, 255));
		hpBar.moveTo(30, 30);
	}
	public void AttackManager(){

		attackCooldown = attackCooldown-1;
		if (attackCooldown<=0) {
			Random(1);
			if (intRet==0) {
				CreateVerticalLine();
				attackCooldown = 60;
			}
		}
	}
	public void CreateVerticalLine(){

		Random(2);
		int side = intRet;
		if (side==0) {
			side = 0-1;
		}
		int bulletCount = 20;
		int i = 0;
		while (i<bulletCount) {
			int posX = 1920/2+side*1920*2/3;
			int posY = 1080/bulletCount*i;
			int velX = (0-side)*10;
			CreateBullet(posX, posY, velX, 0);
			i = i+1;
		}
	}
	public void CreateBullet(int posX, int posY, int velX, int velY){

		bulletRadius = 10.0;
		Circle bullet = new Circle();
		objectstoAdd_.add(bullet);
		bullet.setRadius(bulletRadius);
		bullet.setColor(new Color(255, 0, 0, 255));
		bullet.moveTo(posX, posY);
		bulletsList.add(bullet);
		bulletXPosList.add(posX);
		bulletYPosList.add(posY);
		bulletXVelList.add(velX);
		bulletYVelList.add(velY);
	}
	public void KillAll(){

		int i = 0;
		while (i<bulletsList.size()) {
			Circle bullet = bulletsList.get(i);
			bulletsList.remove(i);
			bulletXPosList.remove(i);
			bulletYPosList.remove(i);
			bulletXVelList.remove(i);
			bulletYVelList.remove(i);
			objectsToRemove_.add(bullet);
		}
		i = 0;
		while (i<platformsList.size()) {
			Box platform = platformsList.get(i);
			platformsList.remove(i);
			platformXPos.remove(i);
			platformYPos.remove(i);
			platformXSizes.remove(i);
			platformYSizes.remove(i);
			objectsToRemove_.add(platform);
		}
	}
	public void Gameloop(){

		if (isAlive) {
			MovePlayer();
			MoveBullets();
			HandleBulletCollisions();
			AttackManager();
		}
	}
	public void UpdateHPBar(){

		int hpSize = hp*3;
		hpBar.setSize(hpSize, 50);
	}
	public void MovePlayer(){

		double gravity = 2.0;
		double terminalVelocity = 40.0;
		if (keysPressed_.contains(65)) {
			pX = pX-pSpeed;
		}
		if (keysPressed_.contains(68)) {
			pX = pX+pSpeed;
		}
		pVelY = pVelY+gravity;
		if (pVelY>terminalVelocity) {
			pVelY = terminalVelocity;
		}
		if (keysPressed_.contains(87)&&isGrounded) {
			pVelY = 0-30.0;
			pY = pY-1.0;
			isGrounded = false;
		}
		if (pVelY>gravity*2) {
			isGrounded = false;
		}
		pY = pY+pVelY;
		player.moveTo(pX, pY);
		int i = 0;
		while (i<platformsList.size()) {
			Box block = platformsList.get(i);
			double blockX = platformXPos.get(i);
			double blockY = platformYPos.get(i);
			double BlockXSize = platformXSizes.get(i);
			double BlockYSize = platformYSizes.get(i);
			if (pX+pSize>blockX&&pX<blockX+BlockXSize&&pY+pSize>blockY&&pY<blockY+BlockYSize) {
				double overlapX = pX+pSize-blockX;
				double overlapX2 = blockX+BlockXSize-pX;
				if (overlapX2<overlapX) {
					overlapX = overlapX2;
				}
				double overlapY = pY+pSize-blockY;
				double overlapY2 = blockY+BlockYSize-pY;
				if (overlapY2<overlapY) {
					overlapY = overlapY2;
				}
				if (overlapY<=overlapX) {
					if (pY<blockY) {
						pY = pY-overlapY;
						isGrounded = true;
						pVelY = 0.0;
					}
					if (pY>blockY) {
						pY = pY+overlapY;
					}
				}
				if (overlapX<overlapY) {
					if (pX<blockX) {
						pX = pX-overlapX;
					}
					if (pX>blockX) {
						pX = pX+overlapX;
					}
				}
			}
			i = i+1;
		}
	}
	public void MoveBullets(){

		int i = 0;
		while (i<bulletsList.size()) {
			Circle bullet = bulletsList.get(0);
			int bulletX = bulletXPosList.get(0);
			int bulletY = bulletYPosList.get(0);
			int bulletVelX = bulletXVelList.get(0);
			int bulletVelY = bulletYVelList.get(0);
			bulletsList.remove(0);
			bulletXPosList.remove(0);
			bulletYPosList.remove(0);
			bulletXVelList.remove(0);
			bulletYVelList.remove(0);
			bulletX = bulletX+bulletVelX;
			bulletY = bulletY+bulletVelY;
			bulletsList.add(bullet);
			bulletXPosList.add(bulletX);
			bulletYPosList.add(bulletY);
			bulletXVelList.add(bulletVelX);
			bulletYVelList.add(bulletVelY);
			bullet.moveTo(bulletX, bulletY);
			i = i+1;
		}
	}
	public void HandleBulletCollisions(){

		int i = 0;
		while (i<bulletsList.size()) {
			Circle block = bulletsList.get(i);
			double blockX = bulletXPosList.get(i)-bulletRadius;
			double blockY = bulletYPosList.get(i)-bulletRadius;
			double BlockXSize = bulletRadius*2;
			double BlockYSize = bulletRadius*2;
			seed = seed+37;
			if (isGrounded&&pX+pSize>blockX&&pX<blockX+BlockXSize&&pY+pSize>blockY&&pY<blockY+BlockYSize) {
				hp = hp-20;
				UpdateHPBar();
				System.out.println("Hit");
				if (hp<=0) {
					LoadDeathMenu();
					System.out.println("Dead");
				}
				bulletsList.remove(i);
				bulletXPosList.remove(i);
				bulletYPosList.remove(i);
				bulletXVelList.remove(i);
				bulletYVelList.remove(i);
				objectsToRemove_.add(block);
			}
			i = i+1;
		}
	}
	public void Random(int max){

		seed = seed*1103515245+12345;
		while (seed<0) {
			seed = seed+2147483647;
		}
		intRet = seed%max;
	}


}

/* -------------------------------------------------------------------------- */
/* GUI Objects */
/* -------------------------------------------------------------------------- */

abstract class DrawableObject {
	protected double x, y; // Position
	protected Color color;

	private Consumer<DrawableObject> onClicked = input -> {
	}; // Default empty lambda

	public DrawableObject(double x, double y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	// Moves the object to the specified position
	public void moveTo(double x, double y) {
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
	public abstract boolean contains(double x, double y);
}

class Box extends DrawableObject {
	private double width, height;

	public Box() {
		super(0, 0, Color.BLACK);
		this.width = 100;
		this.height = 100;
	}

	public Box(double x, double y, double width, double height, Color color) {
		super(x, y, color);
		this.width = width;
		this.height = height;
	}

	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString() {
		return String.format("Box(x: %d, y: %d, width: %d, height: %d, color: %s)", (int) x, (int) y, (int) width,
				(int) height, color.toString());
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect((int) x, (int) y, (int) width, (int) height);
	}

	@Override
	public boolean contains(double x, double y) {
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

	public void setRadius(double radius) {
		this.radius = radius;
	}

	@Override
	public String toString() {
		return String.format("Circle(x: %d, y: %d, radius: %d, color: %s)", (int) x, (int) y, (int) radius,
				color.toString());
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillOval((int) (x - radius), (int) (y - radius), (int) (2 * radius), (int) (2 * radius));
	}

	@Override
	public boolean contains(double x, double y) {
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
		start = new Point((int) x1, (int) y1);
		end = new Point((int) x2, (int) y2);
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
		return String.format("Line(x1: %d, y1: %d, x2: %d, y2: %d, color: %s)", start.x, start.y, end.x, end.y,
				color.toString());
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		((Graphics2D) g).setStroke(new BasicStroke((int) size));
		g.drawLine(start.x, start.y, end.x, end.y);
	}

	@Override
	public boolean contains(double x, double y) {
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

	public Text(double x, double y, String text, Color color) {
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
		return String.format("Text(x: %d, y: %d, text: %s, color: %s)", (int) x, (int) y, text, color.toString());
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.setFont(font);
		g.drawString(text, (int) x, (int) y);
	}

	@Override
	public boolean contains(double x, double y) {
		// Text objects are not clickable, so always return false
		return false;
	}
}
