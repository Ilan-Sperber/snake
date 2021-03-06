package ilantsperber.snake.desktop;

import ilantsperber.snake.GameLogic;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ilantsperber.snake.SnakeGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Snake";
		config.width = GameLogic.BOARD_WIDTH * GameLogic.TILE_SIZE;
		config.height = GameLogic.BOARD_HEIGHT * GameLogic.TILE_SIZE;
		new LwjglApplication(new SnakeGame(), config);
	}
}
