package walnoot.ld32.components;

import walnoot.ld32.Assets;
import walnoot.ld32.Component;
import walnoot.ld32.Input;
import walnoot.ld32.LD32Game;
import walnoot.ld32.OffsetSprite;
import walnoot.ld32.PlayerData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PlayerComponent extends Component {
	private static final int MAX_FIRE_TIME = (int) (1f * LD32Game.FPS);
	private static final float DEADZONE = 0.2f;
	private static final float SPEED = 8f;
	
	private static final float UI_WIDTH = 2f;
	private static final float UI_HEIGHT = .5f;
	private static final float UI_BORDER = .1f;
	
	private Vector2 moveDir = new Vector2();
	private Vector2 aimDir = new Vector2(0f, 1f);
	private boolean firing;
	
	private int firingTimer;
	
	private Vector2 tmp = new Vector2();
	
	private OffsetSprite playerSprite;
	private OffsetSprite bowSprite, arrowSprite;
	private OffsetSprite uiBorderSprite, uiFillSprite;
	
	private Color uiColor = new Color();
	private OrthographicCamera camera;
	
	private final PlayerData data;
	
	public PlayerComponent(OrthographicCamera camera, PlayerData data) {
		this.camera = camera;
		this.data = data;
	}
	
	@Override
	public void init() {
		uiBorderSprite = owner.getComponent(SpriteComponent.class).addSprite(Assets.get("box"), 1f, 0f, -2f, 0f);
		uiBorderSprite.setScale(UI_WIDTH, UI_HEIGHT);
		uiFillSprite = owner.getComponent(SpriteComponent.class).addSprite(Assets.get("box"), 1f, 0f, -2f, 0f);
		uiFillSprite.setScale(0f, 0f);
		uiFillSprite.setColor(Color.BLACK);
		
		bowSprite = owner.getComponent(SpriteComponent.class).addSprite(Assets.get("bow"), 1.5f, 0f, 1f, 0f);
		bowSprite.setAlpha(0f);
		arrowSprite = owner.getComponent(SpriteComponent.class).addSprite(Assets.get("arrow"), 1.5f, 0f, 1f, 0f);
		arrowSprite.setAlpha(0f);
		
		playerSprite = owner.getComponent(SpriteComponent.class).addSprite(Assets.get("player"), 1f);
		playerSprite.setColor(Color.LIGHT_GRAY);
		
		owner.getComponent(SpriteComponent.class).setLevel(1);
	}
	
	@Override
	public void update() {
		//reset input fields
		moveDir.set(0f, 0f);
		aimDir.set(0f, 0f);
		firing = false;
		
		if (owner.world.useController()) {
			Controller c = Controllers.getControllers().get(0);
			
			tmp.set(0f, 0f).add(c.getAxis(1), -c.getAxis(0));
			
			if (tmp.len2() > DEADZONE * DEADZONE) {
				moveDir.add(tmp);
			}
			
			tmp.set(0f, 0f).add(c.getAxis(3), -c.getAxis(2));
			
			if (tmp.len2() > DEADZONE * DEADZONE) {
				aimDir.add(tmp);
			}
			
			if (c.getButton(5) && !aimDir.isZero()) firing = true;
		} else {
			if (Input.i.getKey("up").isTouched()) moveDir.add(0f, 1f);
			if (Input.i.getKey("down").isTouched()) moveDir.add(0f, -1f);
			if (Input.i.getKey("left").isTouched()) moveDir.add(-1f, 0f);
			if (Input.i.getKey("right").isTouched()) moveDir.add(1f, 0f);
			
//			if (Input.i.getKey("shoot").isTouched()) firing = true;
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) firing = true;
			
			Vector2 mousePosition = Input.i.getMousePosition(owner.world.getCamera(), tmp);
			aimDir.set(mousePosition).sub(owner.body.getPosition());
		}
		
		if (data.getArrows() <= 0) firing = false;
		
		aimDir.nor();
		float spriteAngle = aimDir.angle() - 90f;
		
		owner.body.setLinearVelocity(moveDir.limit2(1f).scl(SPEED));
		
		if (firing && firingTimer < MAX_FIRE_TIME) firingTimer++;
		
		boolean canFire = firingTimer > MAX_FIRE_TIME / 2;
		
		if (!aimDir.isZero() && !firing && canFire) {
			owner.world.addEntity(owner.getX() + aimDir.x, owner.getY() + aimDir.y,
					new SpriteComponent(Assets.get("arrow"), 1.5f, spriteAngle), new BulletComponent(
							PlayerComponent.class, aimDir, 15f, 0.5f), new BodyInfoComponent(false));
			data.removeArrow();
		}
		
		if (!firing) firingTimer = 0;
		
		setSprites(spriteAngle, canFire);
		
		moveCamera();
	}
	
	@Override
	public void render(Array<Sprite> sprites) {
		//debug
		if (Gdx.input.isKeyJustPressed(Keys.ALT_LEFT)) {
			Input.i.getMousePosition(camera, tmp);
			
			System.out.printf("int pos: [%d, %d]\nfloat pos: [%.2f, %.2f]\n", (int) Math.floor(tmp.x),
					(int) Math.floor(tmp.y), tmp.x, tmp.y);
		}
	}
	
	private void setSprites(float spriteAngle, boolean canFire) {
		bowSprite.setScale(1.5f, 1.5f * getFiringStrength() + .5f);
		if (firingTimer == MAX_FIRE_TIME) {
			bowSprite.offsetX = MathUtils.random(.05f);
			bowSprite.offsetY = MathUtils.random(.05f) + 1f;
		}
		
		bowSprite.setAlpha(aimDir.isZero() ? 0f : 1f);
		bowSprite.setRotation(spriteAngle);
		
		arrowSprite.setAlpha(firing ? 1f : 0f);
		arrowSprite.offsetY = 1.75f - getFiringStrength() * 0.75f;
		arrowSprite.setRotation(spriteAngle);
		
		playerSprite.setRotation(spriteAngle);
		
		uiFillSprite.setScale((UI_WIDTH - UI_BORDER) * ((float) firingTimer / MAX_FIRE_TIME), UI_HEIGHT - UI_BORDER);
		
		if (firingTimer == MAX_FIRE_TIME) {
			uiColor.lerp(Color.RED, 0.2f);
		} else if (canFire) {
			uiColor.lerp(Color.GREEN, 0.2f);
		} else {
			uiColor.lerp(Color.BLACK, 0.2f);
		}
		
		uiFillSprite.setColor(uiColor);
	}
	
	private void moveCamera() {
		Vector3 cpos = camera.position;
		cpos.set(tmp.set(owner.body.getPosition()).sub(cpos.x, cpos.y).scl(0.05f).add(cpos.x, cpos.y), 0f);
	}
	
	private float getFiringStrength() {
		return (float) Math.pow(Math.min((float) firingTimer / MAX_FIRE_TIME, 1f), 1f / 2.5f);
	}
	
	public PlayerData getData() {
		return data;
	}
}
