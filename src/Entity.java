

import java.awt.Rectangle;

public abstract class Entity {

	/** current (x, y) location of the entity */
	protected float xc, yc; 
	/** current directionality for (x, y) movement */
	protected float dx, dy;
	/** identifies interaction layer. -1 interacts with all other layers (not implemented) */
	protected int layer;
	/** sprite representation of this entity */
	protected Sprite sprite; 
	/** The screen space that the sprite images occupies */
	private Rectangle spriteSpace = new Rectangle();
	/** Indicates the entities border position status */
	protected int borderStatus = 0;
	private Rectangle me = new Rectangle();
	private Rectangle him = new Rectangle();
	
	// GlobalBorders sets the border lines that will be checked for border events and should be added to every extended class
	/** Contains the default rectangular area of allowed movement for all of [class] */
	// private static Rectangle globalBorders;
	/** Contains the personal rectangular area of allowed movement. */
	protected Rectangle personalBorders;
	/** Indicates whether the PersonalBorders should override the class GlobalBorders */
	protected boolean usePersonalBorders = false;
	
	protected Entity(Sprite sprite, int x, int y) {
		this.sprite = sprite;
		this.xc = x;
		this.yc = y;
		spriteSpace.setBounds(
				x - (sprite.getWidth() / 2), 
				y - (sprite.getHeight() / 2), 
				sprite.getWidth(), // this value does not change for the life of the Entity 
				sprite.getHeight() // this value does not change for the life of the Entity
			);
	}
	
	/** Set the movement border triggers for all of a particular Entity */
	public abstract void setGlobalBorders(int leftBorder, int rightBorder, int topBorder, int bottomBorder);
	
	/** Set the movement border trigger for this specific Entity.
	 * This overrides the borders set in setGlobalBorders(). */
	public abstract void setBorders(int leftBorder, int rightBorder, int topBorder, int bottomBorder);
	
	/** Nullifies personal border use for this Entity object. */
	public void clearPersonalBorders() {
		usePersonalBorders = false;
	}
	/**
	 * Request that this entity move itself based on a certain amount of time
	 * passing.
	 * @param delta The amount of time that has passed in milliseconds
	 * @param testBounds Whether or not to test and report movement outside of the set border limits
	 */
	public void move(long delta) {
		xc += (delta * dx) / 1000;
		yc += (delta * dy) / 1000;
		// update the space occupied by the sprite on the screen
		spriteSpace.setLocation((int)xc - (sprite.getWidth() / 2), (int)yc - (sprite.getHeight() / 2) );
	}
	
	/**  */
	protected boolean isInsideBoundary(Rectangle movementBounds) {
		return movementBounds.contains(spriteSpace);
	}
	
	/**
	 * Set the horizontal speed of this entity.
	 * @param directionX The horizontal speed of this entity (pixels/sec)
	 */
	public void setHorizontalMovement(float directionX) {
		this.dx = directionX;
	}
	
	/**
	 * Set the vertical speed of this entity
	 * @param directionY The vertical speed of this entity (pixels/sec)
	 */
	public void setVerticalMovement(float directionY) {
		this.dy = directionY;
	}
	
	public float getHorizontalMovement() { return dx; }
	public float getVerticalMovement() { return dy; }
	
	public void draw() {
		sprite.draw( (int)xc, (int)yc );
	}
	
	/**
	 * Do logic associated with the entity; called based on game events.
	 */
	public abstract void doLogic();

	public int getX() { return (int)xc; }
	public int getY() { return (int)yc; }
	
	public boolean collidesWith(Entity other) {
		// check for collisions with (x, y) as top-left corner of draw
		/* me.setBounds( (int) x, (int) y, sprite.getWidth(), sprite.getHeight());
		him.setBounds( (int) other.x, (int) other.y, other.sprite.getWidth(), other.sprite.getHeight());
		return me.intersects(him); */
		// check for collisions with (x, y) as center of draw
		int halfWidth = sprite.getWidth() / 2;
		int halfHeight = sprite.getHeight() / 2;
		me.setBounds((int) xc - halfWidth, (int) yc - halfHeight, sprite.getWidth(), sprite.getHeight());
		halfWidth = other.sprite.getWidth() / 2;
		halfHeight = other.sprite.getHeight() / 2;
		him.setBounds((int) other.xc - halfWidth, (int) other.yc - halfHeight, other.sprite.getWidth(), other.sprite.getHeight());
		return me.intersects(him);
	}
	
	public abstract void collidedWith(Entity other);
}
