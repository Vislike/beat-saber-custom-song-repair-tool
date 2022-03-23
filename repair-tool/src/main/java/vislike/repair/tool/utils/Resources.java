package vislike.repair.tool.utils;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resources {

	private Resources() {
	}

	private static final Logger logger = LoggerFactory.getLogger(Resources.class);

	private static String versionInfo = null;
	private static String infoTxt = null;

	public static String getVersionInfo() {
		if (versionInfo == null) {
			try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("version.info")) {
				versionInfo = new String(in.readAllBytes());
			} catch (Exception e) {
				logger.warn("Failed to read version.info", e);
				versionInfo = "Unknown";
			}
		}
		return versionInfo;
	}

	public static String getInfoTxt() {
		if (infoTxt == null) {
			try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("info.txt")) {
				infoTxt = new String(in.readAllBytes());
			} catch (IOException e) {
				logger.warn("Failed to read info.txt", e);
				infoTxt = "Failed to read info.txt";
			}
		}
		return infoTxt;
	}

	public static Image getIcon(Display display) {
		try (InputStream in = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("icon_credit_Hiram-Abiff_DeviantArt.png")) {
			return new Image(display, in);
		} catch (IOException e) {
			logger.warn("Failed to read icon file", e);
			return null;
		}
	}
}
