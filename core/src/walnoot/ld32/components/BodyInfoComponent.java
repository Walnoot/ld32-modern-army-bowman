package walnoot.ld32.components;

import walnoot.ld32.Component;

import com.badlogic.gdx.physics.box2d.FixtureDef;

public class BodyInfoComponent extends Component {
	public boolean solid = true, isStatic = false;
	public FixtureDef fixtureDef = null;
	
	public BodyInfoComponent(boolean solid) {
		this.solid = solid;
	}
	
	public BodyInfoComponent(boolean solid, boolean isStatic) {
		this.solid = solid;
		this.isStatic = isStatic;
	}
	
	public BodyInfoComponent(FixtureDef fixtureDef, boolean isStatic) {
		this.fixtureDef = fixtureDef;
		this.isStatic = isStatic;
	}
}
