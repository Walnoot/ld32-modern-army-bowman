package walnoot.ld32;

import walnoot.ld32.components.HealthComponent;
import walnoot.ld32.components.PlayerComponent;
import walnoot.libgdxutils.State;
import walnoot.libgdxutils.Transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameState extends State {
	private static final Color BG_COLOR = new Color(0xdebe87ff);
	
	private SpriteBatch batch = new SpriteBatch();
	private OrthographicCamera gameCamera = new OrthographicCamera();
	private OrthographicCamera uiCamera = new OrthographicCamera();
//	private Box2DDebugRenderer renderer = new Box2DDebugRenderer();
	
	private GameWorld world;
	private boolean useController;
	
	private boolean gameOver;
	
	private String level;
	
	private PlayerData startData;
	
	public GameState(boolean useController) {
		this(useController, "level1.json", new PlayerData());
	}
	
	public GameState(boolean useController, String level, PlayerData data) {
		this.useController = useController;
		this.level = level;
		startData = new PlayerData(data);
		
		world = LevelLoader.loadLevel(level, gameCamera, useController, data);
	}
	
	@Override
	public void update() {
		world.update();
		
		Entity player = world.findEntityWith(PlayerComponent.class);
		
		if (world.getNextLevel() != null) {
			PlayerData data = player.getComponent(PlayerComponent.class).getData();
			
			String nextLevel = world.getNextLevel();
			
			if (nextLevel.equals("end")) manager.transitionTo(new EndState(), new Transition.FadeTransition(1f));
			else manager.transitionTo(new GameState(useController, nextLevel, data), new Transition.FadeTransition(1f));
		}
		
		gameOver = player == null;
		
		if (gameOver && Gdx.input.isKeyJustPressed(Keys.SPACE)) {
			manager.transitionTo(new GameState(useController, level, startData), new Transition.FadeTransition(1f));
		}
	}
	
	@Override
	public void render() {
		Color c = BG_COLOR;
		Gdx.gl20.glClearColor(c.r, c.g, c.b, 1f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		gameCamera.update();
		batch.setProjectionMatrix(gameCamera.combined);
		
		batch.begin();
		world.render(batch);
		
		batch.setProjectionMatrix(uiCamera.combined);
		
		Entity player = world.findEntityWith(PlayerComponent.class);
		if (player != null) {
			String s = "Arrows: " + player.getComponent(PlayerComponent.class).getData().getArrows();
			
			HealthComponent health = player.getComponent(HealthComponent.class);
			if (health != null) {
				s += "   Health: " + player.getComponent(HealthComponent.class).getHealth() + "/"
						+ health.getMaxHealth();
			}
			
			Assets.FONT.draw(batch, s, 10f, Assets.FONT.getCapHeight() + 10f);
		}
		
		if (gameOver) {
			Assets.FONT.draw(batch, "Game over. Press Space to Continue.", 10f, Gdx.graphics.getHeight() - 10f);
		}
		
		batch.end();
		
//		renderer.render(world.getWorld(), camera.combined);
	}
	
	@Override
	public void resize(boolean creation, int width, int height) {
		gameCamera.viewportWidth = 2f * width / height;
		gameCamera.viewportHeight = 2f;
		gameCamera.zoom = 8f;
		
		uiCamera.setToOrtho(false);
	}
	
	public boolean useController() {
		return useController;
	}
}
