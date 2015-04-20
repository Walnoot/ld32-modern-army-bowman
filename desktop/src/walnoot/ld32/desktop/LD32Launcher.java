package walnoot.ld32.desktop;

import walnoot.ld32.LD32Game;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class LD32Launcher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Modern Army Bowman";
		config.width = 1200;
		config.height = config.width * 9 / 16;
		
		new LwjglApplication(new LD32Game(), config);
	}
}
