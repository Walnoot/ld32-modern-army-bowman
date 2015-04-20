package walnoot.ld32;

import walnoot.ld32.components.BodyInfoComponent;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class Entity {
	public Body body;
	public GameWorld world;
	
	private static Array<Component> removedComponents = new Array<Component>();
	
	private Array<Component> components = new Array<Component>();
	private IntMap<Component> componentMap = new IntMap<Component>();
	
	public void init() {
		for (Component c : components) {
			c.init();
		}
	}
	
	public void update() {
		removedComponents.clear();
		
		for (Component c : components) {
			if (!c.removed) c.update();
			else removedComponents.add(c);
		}
		
		components.removeAll(removedComponents, true);
	}
	
	public void render(Array<Sprite> sprites) {
		for (Component c : components) {
			c.render(sprites);
		}
	}
	
	public boolean isStatic() {
		BodyInfoComponent bodyInfoComponent = getComponent(BodyInfoComponent.class);
		if (bodyInfoComponent != null) return bodyInfoComponent.isStatic;
		else return false;
	}
	
	public float getRadius() {
		for (Component c : components) {
			if (c.getRadius() != 0f) return c.getRadius();
		}
		
		return 0.5f;
	}
	
	public float getX() {
		return body.getPosition().x;
	}
	
	public float getY() {
		return body.getPosition().y;
	}
	
	public void addComponent(Component component) {
		component.owner = this;
		
		components.add(component);
		componentMap.put(component.getClass().hashCode(), component);
	}
	
	public <T extends Component> boolean hasComponent(Class<T> c) {
		return getComponent(c) != null;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> c) {
		return (T) componentMap.get(c.hashCode(), null);
	}
	
	public void remove() {
		for (int i = 0; i < components.size; i++) {
			components.get(i).onRemove();
		}
		
		world.removeEntity(this);
	}
}
