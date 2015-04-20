package walnoot.ld32;

import walnoot.ld32.components.BodyInfoComponent;
import walnoot.ld32.components.HealthComponent;
import walnoot.ld32.components.PlayerComponent;
import walnoot.ld32.components.SpriteComponent;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class GameWorld implements QueryCallback {
	private World world = new World(Vector2.Zero, true);
	private Array<Body> bodies = new Array<Body>();
	private Array<Body> removedBodies = new Array<Body>();
	
	private OrthographicCamera camera;
	private Array<Sprite> sprites = new Array<Sprite>();
	
	private final boolean useController;
	
	private Array<Entity> tempEntities = new Array<Entity>();
	private float queryX, queryY, queryRadius;
	private String nextLevel;
	
	public GameWorld(OrthographicCamera camera, boolean useController, float x, float y, PlayerData data) {
		this.camera = camera;
		this.useController = useController;
		
		camera.position.set(x, y, 0f);
//		addEntity(x, y, new SpriteComponent(Assets.get("player")), new PlayerComponent(camera, data));
		addEntity(x, y, new SpriteComponent(), new PlayerComponent(camera, data), new HealthComponent(3));
	}
	
	public void update() {
		world.getBodies(bodies);
		
		for (Body body : bodies) {
			((Entity) body.getUserData()).update();
		}
		
		for (Body body : removedBodies) {
			world.destroyBody(body);
		}
		removedBodies.clear();
		
		world.step(1f / LD32Game.FPS, 8, 3);
	}
	
	public void render(SpriteBatch batch) {
		world.getBodies(bodies);
		
		sprites.clear();
		
		for (Body body : bodies) {
			((Entity) body.getUserData()).render(sprites);
		}
		
		sprites.sort();
		
		for (Sprite s : sprites) {
			s.draw(batch);
		}
	}
	
	public void addEntity(float x, float y, Component... components) {
		Entity entity = new Entity();
		
		for (Component c : components) {
			entity.addComponent(c);
		}
		
		addEntity(entity, x, y);
	}
	
	public void addEntity(Entity entity, float x, float y) {
		BodyDef def = new BodyDef();
		def.type = entity.isStatic() ? BodyType.StaticBody : BodyType.DynamicBody;
		def.fixedRotation = true;
		def.position.set(x, y);
		def.linearDamping = 4f;
		
		BodyInfoComponent bodyInfo = entity.getComponent(BodyInfoComponent.class);
		
		Body body = world.createBody(def);
		
		body.setUserData(entity);
		entity.body = body;
		entity.world = this;
		
		if (bodyInfo != null && bodyInfo.fixtureDef != null) {
			body.createFixture(bodyInfo.fixtureDef);
		} else if (bodyInfo == null || bodyInfo.solid) {
			CircleShape shape = new CircleShape();
			shape.setRadius(entity.getRadius());
			FixtureDef fDef = new FixtureDef();
			fDef.shape = shape;
			fDef.density = 1f;
			fDef.friction = 0f;
			body.createFixture(fDef);
			
			shape.dispose();
		}
		
		entity.init();
	}
	
	public void removeEntity(Entity e) {
		removedBodies.add(e.body);
	}
	
	public Array<Entity> queryRadius(Entity e, float radius) {
		return queryRadius(e.getX(), e.getY(), radius);
	}
	
	public Array<Entity> queryRadius(float x, float y, float radius) {
		tempEntities.clear();
		
		queryX = x;
		queryY = y;
		queryRadius = radius;
		
		world.QueryAABB(this, x - radius, y - radius, x + radius, y + radius);
		
		return tempEntities;
	}
	
	public <T extends Component> Entity findEntityWith(Class<T> c) {
		world.getBodies(bodies);
		for (int i = 0; i < bodies.size; i++) {
			Entity e = (Entity) bodies.get(i).getUserData();
			if (e.hasComponent(c)) return e;
		}
		
		return null;
	}
	
	@Override
	public boolean reportFixture(Fixture fixture) {
		Body b = fixture.getBody();
		
		if (b.getPosition().dst2(queryX, queryY) < queryRadius * queryRadius) {
			tempEntities.add((Entity) b.getUserData());
		}
		
		return true;
	}
	
	public void markFinished(String level) {
		nextLevel = level;
	}
	
	public String getNextLevel() {
		return nextLevel;
	}
	
	public OrthographicCamera getCamera() {
		return camera;
	}
	
	public World getWorld() {
		return world;
	}
	
	public boolean useController() {
		return useController;
	}
}
