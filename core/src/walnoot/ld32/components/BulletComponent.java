package walnoot.ld32.components;

import walnoot.ld32.Component;
import walnoot.ld32.Entity;
import walnoot.ld32.LD32Game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class BulletComponent extends Component implements RayCastCallback {
	private Vector2 dir = new Vector2();
	private Class<? extends Component> sender;
	
	private Entity nearestEntity;
	private Vector2 tmp = new Vector2();
	
	private float length;
	private float speed;
	
	private int timeAlive;
	
	public BulletComponent(Class<? extends Component> sender, Vector2 dir, float speed) {
		this(sender, dir, speed, 0f);
	}
	
	public BulletComponent(Class<? extends Component> sender, Vector2 dir, float speed, float length) {
		this.sender = sender;
		this.speed = speed;
		this.dir.set(dir).scl(speed);
		this.length = length;
	}
	
	@Override
	public void update() {
		owner.body.setLinearVelocity(dir);
		
		nearestEntity = null;
		Vector2 pos = owner.body.getPosition();
		owner.body.getWorld().rayCast(this, pos, tmp.set(dir).scl((1f / LD32Game.FPS) + (length / speed)).add(pos));
		
		if (nearestEntity != null) {
			HealthComponent healthComponent = nearestEntity.getComponent(HealthComponent.class);
			if (!nearestEntity.hasComponent(sender)) {
				if (healthComponent != null) healthComponent.hit(1);
				
//				owner.body.setLinearVelocity(0f, 0f);
//				remove();
				owner.remove();
			}
		}
		
		if (timeAlive++ > 5 * LD32Game.FPS) owner.remove();
	}
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		Entity entity = (Entity) fixture.getBody().getUserData();
		
		if (entity.hasComponent(LowComponent.class)) {
			return 1f;
		} else {
			nearestEntity = entity;
			return fraction;
		}
		
	}
}
