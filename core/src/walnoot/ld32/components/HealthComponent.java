package walnoot.ld32.components;

import walnoot.ld32.Component;
import walnoot.ld32.LD32Game;

public class HealthComponent extends Component {
	private static final int INVINCIBILITY_TIME = (int) (.25f * LD32Game.FPS);
	private int health, maxHealth;
	private int invincibilityTimer;
	
	public HealthComponent(int maxHealth) {
		this.maxHealth = maxHealth;
		health = maxHealth;
	}
	
	@Override
	public void update() {
		if (invincibilityTimer > 0) invincibilityTimer--;
	}
	
	public int hit(int amount) {
		if (amount < 0) return 0;
		
		if (invincibilityTimer > 0) {
			return 0;
		} else {
			invincibilityTimer = INVINCIBILITY_TIME;
			
			int damageDone = Math.min(amount, amount - health);
			
			health -= amount;
			if (health <= 0) {
				health = 0;
				
				owner.remove();
			}
			
			return damageDone;
		}
	}
	
	public int getHealth() {
		return health;
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}
}
