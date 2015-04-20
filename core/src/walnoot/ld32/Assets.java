package walnoot.ld32;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
	public static BitmapFont FONT;
	public static TextureAtlas ATLAS;
	private static HashMap<String, TextureRegion> cache = new HashMap<String, TextureRegion>();
	
	public static TextureRegion get(String name) {
		TextureRegion cached = cache.get(name);
		
		if (cached != null) return cached;
		else {
			AtlasRegion region = ATLAS.findRegion(name);
			cache.put(name, region);
			
			return region;
		}
	}
}
