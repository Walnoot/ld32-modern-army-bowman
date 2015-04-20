package walnoot.ld32;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

public abstract class Component {
	public Entity owner;
	public boolean removed;
	
	public void init() {
	}
	
	public void update() {
	}
	
	public void render(Array<Sprite> sprites) {
	}
	
	public float getRadius() {
		return 0f;
	}
	
	public void remove() {
		removed = true;
	}
	
	public void onRemove() {
	}
}
