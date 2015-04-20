package walnoot.ld32;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class OffsetSprite extends Sprite implements Comparable<OffsetSprite> {
	public float offsetX, offsetY;
	public int level;
	
	public OffsetSprite(TextureRegion region, int level) {
		super(region);
		this.level = level;
	}
	
	@Override
	public int compareTo(OffsetSprite o) {
		if (o.level == this.level) return Float.compare(o.getY(), getY());
		
		return Integer.compare(level, o.level);
	}
}