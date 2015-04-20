package walnoot.ld32;

import walnoot.libgdxutils.State;
import walnoot.libgdxutils.StateApplication;
import walnoot.libgdxutils.input.InputHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class LD32Game extends StateApplication {
	public static final float FPS = 60f;
	private static final boolean DEBUG = false;
	
	public LD32Game() {
		super(FPS, DEBUG);
	}
	
	@Override
	protected void init() {
		Input.i = InputHandler.read(Gdx.files.internal("input.json"));
		Gdx.input.setInputProcessor(Input.i);
		
		PixmapPacker packer = new PixmapPacker(1024, 1024, Format.RGBA8888, 2, true);
		pack(packer, "arrow");
		pack(packer, "bow");
		pack(packer, "box");
		pack(packer, "crate");
		pack(packer, "dot");
		pack(packer, "enemy");
		pack(packer, "exit");
		pack(packer, "fence_straight");
		pack(packer, "fence_corner");
		pack(packer, "player");
		pack(packer, "view_cone");
		
		Assets.ATLAS = packer.generateTextureAtlas(TextureFilter.Linear, TextureFilter.Linear, false);
		
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 32;
		Assets.FONT = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf")).generateFont(parameter);
	}
	
	private void pack(PixmapPacker packer, String name) {
		packer.pack(name, new Pixmap(Gdx.files.internal(name + ".png")));
	}
	
	@Override
	protected void update() {
		super.update();
		
//		if (DEBUG) Timelapse.update();
		
		Input.i.update();
	}
	
	@Override
	protected State getFirstState() {
		return new IntroState(Controllers.getControllers().size > 0);
//		return new GameState();
	}
}
