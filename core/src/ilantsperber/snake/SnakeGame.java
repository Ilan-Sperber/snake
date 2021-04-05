package ilantsperber.snake;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class SnakeGame extends ApplicationAdapter {
	private static final Logger logger = LogManager.getLogger(SnakeGame.class);

	private static final float MOVE_MILLISECONDS = 100;

	private SpriteBatch batch;
	private Texture img;

	private TextureAtlas atlas;

	private GameLogic game;

	private long lastMove;

	private ArrayList<Direction> dirQueue;
	
	@Override
	public void create () {
		game = new GameLogic(new Coord(5, 5));

		batch = new SpriteBatch();
//		img = new Texture("badlogic.jpg");
		atlas = new TextureAtlas("atlas.txt");

		System.out.println(atlas.findRegion("head-north").getClass());

		lastMove = TimeUtils.nanoTime();
		dirQueue = new ArrayList<>();

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 1, 2, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
//		batch.draw(img, 0, 0);
//		batch.draw(atlas.findRegion("tail-north-west"), 100, 100);
		for (int x = 0; x < GameLogic.BOARD_WIDTH; x++) {
			for (int y = 0; y < GameLogic.BOARD_HEIGHT; y++) {
				TextureRegion img;
				Coord drawAtPixel = new Coord(x * GameLogic.TILE_SIZE, y * GameLogic.TILE_SIZE);
				Coord tile = new Coord(x, y);
				switch (game.getBoard()[x][y]) {
					case WALL:
						img = atlas.findRegion("wall");
						batch.draw(img, drawAtPixel.x(), drawAtPixel.y());
						break;
					case EMPTY:
						break;
					case FOOD:
						img = atlas.findRegion("food");
						batch.draw(img, drawAtPixel.x(), drawAtPixel.y());
						break;
					case TAIL:
						Coord[] info = game.tailPieceInfo(tile);
						Coord front = info[0], back = info[1];

						if (back == null) { // if (tile.equals(board.getTail())
							if (tile.x() > front.x())
								img = atlas.findRegion("end-east");
							else if (tile.x() < front.x())
								img = atlas.findRegion("end-west");
							else if (tile.y() > front.y())
								img = atlas.findRegion("end-north");
							else if (tile.y() < front.y())
								img = atlas.findRegion("end-south");
							else {
								logger.error("Could not figure out which tail to use with Front: " + front + " Tile: " + tile);
								break;
							}
							batch.draw(img, drawAtPixel.x(), drawAtPixel.y());
							break;
						}

						if (back.x() == front.x()) {
							img = atlas.findRegion("tail-vertical");
							batch.draw(img, drawAtPixel.x(), drawAtPixel.y());
							break;
						} else if (back.y() == front.y()) {
							img = atlas.findRegion("tail-horizontal");
							batch.draw(img, drawAtPixel.x(), drawAtPixel.y());
							break;
						}
						boolean north, east;
						north = front.y() > tile.y() || back.y() > tile.y();
						east  = front.x() > tile.x() || back.x() > tile.x();
						img = atlas.findRegion("tail-" + (north ? "north" : "south") + "-" + (east ? "east" : "west"));
						batch.draw(img, drawAtPixel.x(), drawAtPixel.y());
						break;
					case HEAD:
						img = atlas.findRegion("head-" + game.getDirection().name().toLowerCase());
						batch.draw(img, drawAtPixel.x(), drawAtPixel.y());
						break;
				}
			}
		}
		batch.end();

		if (Gdx.input.isKeyJustPressed(Keys.SPACE) || Gdx.input.isKeyJustPressed(Keys.P)) {
			game.setPaused(!game.isPaused());
			logger.info("Game " + (game.isPaused() ? "paused" : "unpaused"));
		}

		if (game.isAlive() && !game.isPaused()) {
			if (TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(lastMove)) > MOVE_MILLISECONDS) {
				if (dirQueue.size() > 0) {
					Direction direction = dirQueue.remove(0);
//					if (!((direction == Direction.NORTH && game.getDirection() == Direction.SOUTH) || (direction == Direction.EAST && game.getDirection() == Direction.WEST) || (di)))
					boolean shouldSwitch = switch (direction) {
						case NORTH -> game.getDirection() != Direction.SOUTH;
						case EAST  -> game.getDirection() != Direction.WEST;
						case SOUTH -> game.getDirection() != Direction.NORTH;
						case WEST  -> game.getDirection() != Direction.EAST;
					};
//					boolean shouldSwitch = !((direction == Direction.NORTH && game.getDirection() == Direction.SOUTH) || (direction == Direction.EAST && game.getDirection() == Direction.WEST) || (direction == Direction.SOUTH && game.getDirection() == Direction.NORTH) || (direction == Direction.WEST && game.getDirection() == Direction.EAST));
					if (shouldSwitch)
						game.setDirection(direction);
				}
				game.move();
				Gdx.graphics.setTitle("Snake - Length " + game.getLength());
				lastMove = TimeUtils.nanoTime();
			}
			if (Gdx.input.isKeyJustPressed(Keys.UP) || Gdx.input.isKeyJustPressed(Keys.W))
				dirQueue.add(Direction.NORTH);
			if (Gdx.input.isKeyJustPressed(Keys.RIGHT) || Gdx.input.isKeyJustPressed(Keys.D))
				dirQueue.add(Direction.EAST);
			if (Gdx.input.isKeyJustPressed(Keys.DOWN) || Gdx.input.isKeyJustPressed(Keys.S))
				dirQueue.add(Direction.SOUTH);
			if (Gdx.input.isKeyJustPressed(Keys.LEFT) || Gdx.input.isKeyJustPressed(Keys.A))
				dirQueue.add(Direction.WEST);
		}

		if (!game.isAlive())
			game = new GameLogic(new Coord(5, 5));

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
