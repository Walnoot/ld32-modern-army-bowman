package walnoot.ld32.components;

import walnoot.ld32.Component;
import walnoot.ld32.Entity;
import walnoot.ld32.LD32Game;

import com.badlogic.gdx.physics.box2d.Fixture;

public class ExitComponent extends Component {
	private String level;
	private int timer;
	
	private boolean cleared = false;
	
	public ExitComponent(String level) {
		this.level = level;
	}
	
	@Override
	public void update() {
		if (!cleared) {
			if (owner.world.findEntityWith(EnemyComponent.class) == null) {
				cleared = true;
				
				for (Fixture f : owner.body.getFixtureList()) {
					owner.body.destroyFixture(f);
				}
			}
		} else {
			
			for (Entity e : owner.world.queryRadius(owner, 2f)) {
				if (e.hasComponent(PlayerComponent.class)) timer++;
			}
			
			if (timer > 0) timer++;
			if (timer > LD32Game.FPS / 2f) owner.world.markFinished(level);
		}
	}
}
