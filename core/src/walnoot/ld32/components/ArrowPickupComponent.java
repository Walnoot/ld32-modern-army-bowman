package walnoot.ld32.components;

import walnoot.ld32.Component;
import walnoot.ld32.Entity;

public class ArrowPickupComponent extends Component {
	@Override
	public void update() {
		for (Entity e : owner.world.queryRadius(owner, 1f)) {
			if (e.hasComponent(PlayerComponent.class)) {
				e.getComponent(PlayerComponent.class).getData().addArrow();
				owner.remove();
			}
		}
	}
}
