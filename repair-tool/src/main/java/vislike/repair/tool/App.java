package vislike.repair.tool;

import vislike.repair.tool.gui.MainWindow;

public class App {

	public static void main(String[] args) throws Exception {
		try (MainWindow mainWindow = new MainWindow()) {
			mainWindow.run();
		}
	}
}
