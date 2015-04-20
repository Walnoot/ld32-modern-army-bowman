package walnoot.ld32;

import walnoot.libgdxutils.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class IntroState extends State {
	private Texture texture;
	private SpriteBatch batch = new SpriteBatch();
	private boolean controller;
	
	public IntroState(boolean controller) {
		this.controller = controller;
		texture = new Texture(Gdx.files.internal(controller ? "intro_controller.png" : "intro_keyboard.png"), true);
		
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
	}
	
	@Override
	public void render() {
		Gdx.gl20.glClearColor(1f, 1f, 1f, 1f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		float x = (Gdx.graphics.getWidth() - Gdx.graphics.getHeight()) / 2f;
		batch.begin();
		batch.draw(texture, x, 0f, Gdx.graphics.getHeight(), Gdx.graphics.getHeight());
		batch.end();
		
		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) manager.setState(new IntroState(false));
		if (Gdx.input.isKeyJustPressed(Keys.SPACE)) manager.setState(new GameState(controller));
		if (controller && Controllers.getControllers().get(0).getButton(0)) manager.setState(new GameState(controller));
	}
	
	@Override
	public void resize(boolean creation, int width, int height) {
		batch.getProjectionMatrix().setToOrtho2D(0f, 0f, width, height);
	}
}
