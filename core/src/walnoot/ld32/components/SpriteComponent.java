package walnoot.ld32.components;

import walnoot.ld32.Component;
import walnoot.ld32.OffsetSprite;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class SpriteComponent extends Component {
	private Array<OffsetSprite> sprites = new Array<OffsetSprite>();
	private Vector2 tmp = new Vector2();
	
	public SpriteComponent() {
	}
	
	public SpriteComponent(TextureRegion region) {
		this(region, 1f);
	}
	
	public SpriteComponent(TextureRegion region, float size) {
		this(region, size, 0f);
	}
	
	public SpriteComponent(TextureRegion region, float size, float rotation) {
		addSprite(region, size, 0f, 0f, rotation);
	}
	
	public void render(Array<Sprite> spriteList) {
		for (OffsetSprite s : sprites) {
			tmp.set(s.offsetX, s.offsetY).rotate(s.getRotation());
			
			s.setCenter(owner.getX() + tmp.x, owner.getY() + tmp.y);
			spriteList.add(s);
		}
	}
	
	public OffsetSprite addSprite(TextureRegion region, float size) {
		return addSprite(region, size, 0f, 0f, 0f);
	}
	
	public OffsetSprite addSprite(TextureRegion region, float size, float offsetX, float offsetY, float rotation) {
		return addSprite(region, size, offsetX, offsetY, rotation, 0);
	}
	
	public OffsetSprite addSprite(TextureRegion region, float size, float offsetX, float offsetY, float rotation,
			int level) {
		OffsetSprite sprite = new OffsetSprite(region, level);
		sprite.offsetX = offsetX;
		sprite.offsetY = offsetY;
		sprite.setSize(size, size);
		
		sprite.setOriginCenter();
		sprite.setRotation(rotation);
		
		sprites.add(sprite);
		
		return sprite;
	}
	
	public SpriteComponent setLevel(int level) {
		for (OffsetSprite sprite : sprites) {
			sprite.level = level;
		}
		
		return this;
	}
}
