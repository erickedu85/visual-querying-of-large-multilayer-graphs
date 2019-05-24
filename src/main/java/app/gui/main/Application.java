package app.gui.main;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Application extends JFrame {

	private static final long serialVersionUID = 1L;
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	public Application() throws MalformedURLException, IOException {

		SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
		SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;
		// 800;//Change in layoutEmbedding also

		setExtendedState(Frame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setTitle(Constants.MAIN_TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(Constants.FRAME_MINIMUM_SIZE_WIDTH, Constants.FRAME_MINIMUM_SIZE_HEIGHT));
		BufferedImage image = ImageIO.read(new File("icon.png"));
		setIconImage(image);

		MainSplitPanel splitPane = new MainSplitPanel();
		setContentPane(splitPane);

		// Deprecated
		// MainMenuBar menuBar = new MainMenuBar();
		// setJMenuBar(menuBar);
	}

	public static void main(String[] args) throws MalformedURLException, IOException {
		Application root = new Application();
		root.setVisible(true);
	}

}
