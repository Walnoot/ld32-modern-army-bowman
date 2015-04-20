package walnoot.ld32;

public class PlayerData {
	private int arrows;
	
	public PlayerData() {
	}
	
	public PlayerData(PlayerData source) {
		this.arrows = source.arrows;
	}
	
	public void addArrow() {
		arrows++;
	}
	
	public void removeArrow() {
		arrows--;
	}
	
	public int getArrows() {
		return arrows;
	}
}
