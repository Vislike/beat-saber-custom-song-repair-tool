package vislike.repair.tool.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings {

	private static final Logger logger = LoggerFactory.getLogger(Settings.class);
	private static final Path settingsFile = Path.of("settings.properties");

	private static class SettingsHolder {
		private static final Settings settings = new Settings();
	}

	public static Settings getInstance() {
		return SettingsHolder.settings;
	}

	private final Properties properties;

	private Settings() {
		properties = new Properties();
		logger.debug("Loading settings");

		// Load from file
		try (InputStream in = Files.newInputStream(settingsFile)) {
			properties.load(in);
		} catch (IOException e) {
			if (e instanceof NoSuchFileException) {
				logger.debug("Will start a new empty settings instance");
			} else {
				logger.warn("Can not load {}", settingsFile, e);
			}
		}
	}

	private void saveFile() {
		// Save file
		try (OutputStream out = Files.newOutputStream(settingsFile)) {
			properties.store(out, "Repair Tool Settings");
		} catch (IOException e) {
			logger.warn("Can not save {}", settingsFile, e);
		}
	}

	public String getBrowseDirectory() {
		return properties.getProperty("browse.directory", "");
	}

	public void saveBrowseDirectory(String path) {
		if (!path.equals(getBrowseDirectory())) {
			properties.setProperty("browse.directory", path);
			saveFile();
		}
	}

	public String getInputFile() {
		return properties.getProperty("input.file", "");
	}

	public void saveInputFile(String file) {
		if (!file.equals(getInputFile())) {
			properties.setProperty("input.file", file);
			saveFile();
		}
	}
}
