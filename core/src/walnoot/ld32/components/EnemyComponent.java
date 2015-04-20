package walnoot.ld32.components;

import walnoot.ld32.Assets;
import walnoot.ld32.Component;
import walnoot.ld32.Entity;
import walnoot.ld32.LD32Game;
import walnoot.ld32.OffsetSprite;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class EnemyComponent extends Component {
	private static final float TURN_SPEED = 200f;
	private static final float HEAR_RANGE = 1f;
	private static final float SIGHT_RANGE = 7f;
	private static final int TURN_TIME = (int) (4f * LD32Game.FPS);
	
	private Vector2 tmp1 = new Vector2();
	private Vector2 tmp2 = new Vector2();
	private Vector2 aimDir = new Vector2(1f, 0f);
	private OffsetSprite sprite, coneSprite;
	
	private int fireTimer, seeingTimer, turnTimer = TURN_TIME / 4;
	private boolean aggro = false;
	
	private Array<Vector2> path = new Array<Vector2>();
	private int pathIndex = 0;
	
	public EnemyComponent(Array<Vector2> path, float rotation) {
		this.path.addAll(path);
		
		aimDir.rotate(rotation);
	}
	
	@Override
	public void init() {
		owner.addComponent(new SpriteComponent());
		sprite = owner.getComponent(SpriteComponent.class).addSprite(Assets.get("enemy"), 1f);
		coneSprite = owner.getComponent(SpriteComponent.class).addSprite(Assets.get("view_cone"), SIGHT_RANGE,
				SIGHT_RANGE / 2f, SIGHT_RANGE / 2f, 0f);
		coneSprite.level = -2;
	}
	
	@Override
	public void render(Array<Sprite> sprites) {
		sprite.setRotation(aimDir.angle() - 90f);
		coneSprite.setRotation(aimDir.angle() - 45f);
	}
	
	@Override
	public void update() {
		Entity player = null;
		
		for (Entity e : owner.world.queryRadius(owner, aggro ? 2 * SIGHT_RANGE : SIGHT_RANGE)) {
			if (e.getComponent(PlayerComponent.class) != null) player = e;
		}
		
		boolean firing = false;
		
		Vector2 goal = null;
		
		if (seeingTimer-- > 0 && player != null) goal = player.body.getPosition();
		
		if (player != null) {
			Vector2 sub = tmp1.set(player.body.getPosition()).sub(owner.body.getPosition());
			float len2 = sub.len2();
			Vector2 dir = sub.nor();
			
			if (len2 < HEAR_RANGE * HEAR_RANGE || dir.dot(aimDir) > MathUtils.cosDeg(50f)) {
				firing = true;
				aggro = true;
				
				seeingTimer = (int) (LD32Game.FPS / 1f);
				
				if (fireTimer++ == 30) {
					fireTimer = 0;
					
					SpriteComponent spriteComponent = new SpriteComponent();
					OffsetSprite bulletSprite = spriteComponent.addSprite(Assets.get("dot"), 0.25f);
					bulletSprite.setScale(.5f, 2f);
					bulletSprite.setRotation(aimDir.angle() - 90f);
					spriteComponent.setLevel(-1);
					
					owner.world.addEntity(owner.getX(), owner.getY(), spriteComponent, new BulletComponent(
							EnemyComponent.class, aimDir, 20f), new BodyInfoComponent(false));
					aimDir.set(aimDir);
				}
			}
		}
		
		if (goal == null && !firing && path.size > 0) {//walk mode
			goal = path.get(pathIndex);
			
			if (owner.body.getPosition().dst2(goal) < 1f) {
				pathIndex++;
				if (pathIndex == path.size) pathIndex = 0;
			}
		}
		
		if (goal != null) {
			tmp1.set(owner.body.getPosition()).sub(goal).nor();
			
			if (-aimDir.dot(tmp1) > MathUtils.cosDeg(5f)) {
				owner.body.setLinearVelocity(tmp1.set(aimDir).scl(2f));
			} else {
				tmp2.set(aimDir).rotate90(1);
				
				if (tmp2.dot(tmp1.set(goal).sub(owner.body.getPosition())) > 0f) {
					aimDir.rotate(TURN_SPEED / LD32Game.FPS);
				} else {
					aimDir.rotate(-TURN_SPEED / LD32Game.FPS);
				}
			}
		} else {
			turnTimer = (turnTimer + 1) % TURN_TIME;
			
			if (turnTimer > TURN_TIME / 2) aimDir.rotate(0.25f * TURN_SPEED / LD32Game.FPS);
			else aimDir.rotate(-0.25f * TURN_SPEED / LD32Game.FPS);
		}
	}
	
	@Override
	public void onRemove() {
		int amount = MathUtils.random(1, 3);
		for (int i = 0; i < amount; i++) {
			Entity e = new Entity();
			e.addComponent(new SpriteComponent(Assets.get("arrow"), 1f));
			e.addComponent(new ArrowPickupComponent());
			e.addComponent(new BodyInfoComponent(false));
			owner.world.addEntity(e, owner.getX() + i / 5f, owner.getY());
		}
	}
}
