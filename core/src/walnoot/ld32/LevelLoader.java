package walnoot.ld32;

import walnoot.ld32.components.ArrowPickupComponent;
import walnoot.ld32.components.BodyInfoComponent;
import walnoot.ld32.components.EnemyComponent;
import walnoot.ld32.components.ExitComponent;
import walnoot.ld32.components.HealthComponent;
import walnoot.ld32.components.LowComponent;
import walnoot.ld32.components.SpriteComponent;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class LevelLoader {
	public static GameWorld loadLevel(String level, OrthographicCamera camera, boolean useController, PlayerData data) {
		JsonValue json = new JsonReader().parse(Gdx.files.internal(level));
		
		JsonValue playerPos = json.get("player_pos");
		
		GameWorld world = new GameWorld(camera, useController, playerPos.getFloat(0), playerPos.getFloat(1), data);
		Array<Shape> shapes = new Array<Shape>();
		
		JsonValue entity = json.get("entities").child();
		while (entity != null) {
			JsonValue pos = entity.get("pos");
			String type = entity.getString("type");
			
			Entity e = new Entity();
			
			if (type.equals("crate")) {
				float size = entity.getFloat("size", 2f);
				FixtureDef fixtureDef = new FixtureDef();
				PolygonShape shape = new PolygonShape();
				fixtureDef.shape = shape;
				shape.setAsBox(size / 2f, size / 2f);
				e.addComponent(new SpriteComponent(Assets.get("crate"), size));
				e.addComponent(new BodyInfoComponent(fixtureDef, entity.getBoolean("static", true)));
				
				shapes.add(shape);
			} else if (type.startsWith("fence")) {
				e = null;
				
				for (int i = 0; i < entity.getInt("amount"); i++) {
					Entity part = new Entity();
					
					float size = 2f;
					FixtureDef fixtureDef = new FixtureDef();
					PolygonShape shape = new PolygonShape();
					fixtureDef.shape = shape;
					shape.setAsBox(size / 2f, size / 2f);
					
					int rot = Integer.parseInt(type.substring(7));
					float rotation = -rot * 90f;
					
					part.addComponent(new SpriteComponent(Assets.get(type.charAt(6) == 's' ? "fence_straight"
							: "fence_corner"), size, rotation));
					part.addComponent(new BodyInfoComponent(fixtureDef, true));
					part.addComponent(new LowComponent());
					
					int xo = (rot % 2 == 1) ? i * 2 : 0;
					int yo = (rot % 2 == 0) ? i * 2 : 0;
					
					world.addEntity(part, pos.getFloat(0) + xo, pos.getFloat(1) + yo);
					
					shapes.add(shape);
				}
			} else if (type.equals("enemy")) {
				e.addComponent(new HealthComponent(1));
				
				Array<Vector2> path = new Array<Vector2>();
				JsonValue point = entity.get("path").child();
				while (point != null) {
					path.add(new Vector2(point.getFloat(0), point.getFloat(1)));
					
					point = point.next();
				}
				
				e.addComponent(new EnemyComponent(path, entity.getFloat("rotation", 0f)));
			} else if (type.equals("exit")) {
				e.addComponent(new ExitComponent(entity.getString("next")));
				e.addComponent(new SpriteComponent(Assets.get("exit"), 2f));
				e.addComponent(new BodyInfoComponent(true, true));
			} else if (type.equals("arrow")) {
				e.addComponent(new SpriteComponent(Assets.get("arrow"), 1f));
				e.addComponent(new ArrowPickupComponent());
				e.addComponent(new BodyInfoComponent(false));
			} else {
				System.out.println("unknown type: " + type);
				e = null;
			}
			
			if (e != null) world.addEntity(e, pos.getFloat(0), pos.getFloat(1));
			
			entity = entity.next();
		}
		
		for (Shape s : shapes) {
			s.dispose();
		}
		
		return world;
	}
}
