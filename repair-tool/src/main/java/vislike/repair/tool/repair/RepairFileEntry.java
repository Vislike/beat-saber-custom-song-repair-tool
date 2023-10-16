package vislike.repair.tool.repair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vislike.repair.tool.repair.result.FileStatus;
import vislike.repair.tool.repair.result.RepairResult;
import vislike.repair.tool.repair.result.RepairStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RepairFileEntry {

	private static final Logger logger = LoggerFactory.getLogger(RepairFileEntry.class);

	public static final String DIR = "\\";

	private RepairFileEntry() {
	}

	public static void appendResult(Path file, RepairResult result, String message) {
		result.fileStatus().add(new FileStatus(file.getFileName().toString(), message));
		logger.info("Applied [{}] {}", message, file);
	}

	public static List<RepairResult> repairFile(String file, RepairType repairType) {

		List<RepairResult> repairResults = new ArrayList<>();

		if(RepairType.SONG_DIRECTORY == repairType) {

			File mainDirectory = new File(file);

			String[] directories = Objects.requireNonNull(mainDirectory.list((dir, name) -> new File(dir, name).isDirectory()));

			for (String song : directories) {
				String songPath = file + DIR + song;
				String songDatPath = songPath + DIR + "info.dat";
				logger.debug("Starting repair of: {}", songPath);
				File songDat = new File(songDatPath);

				RepairResult result = new RepairResult(songPath, RepairStatus.NOTHING);

				if (songDat.exists()) {
					try {
						result = repairSong(Path.of(songDatPath));
						logger.debug("Repair complete: {}", songPath);

						if (result.fileStatus().isEmpty()) {
							result = new RepairResult(songPath, RepairStatus.NOTHING);
						}

					} catch (IOException e) {
						logger.error("File problem", e);
						result = new RepairResult(songPath, RepairStatus.FAILED, e.getClass().getSimpleName() + ": " + e.getMessage());
					}
				}
				repairResults.add(result);
			}
		} else if (RepairType.SINGLE_SONG == repairType) {
			try {
				Path infoDat = Path.of(file);
				RepairResult result = repairSong(infoDat);
				logger.debug("Repair complete: {}", file);

				if (result.fileStatus().isEmpty()) {
					return Collections.singletonList(new RepairResult(infoDat.toString(), RepairStatus.NOTHING));
				}
				return Collections.singletonList(result);
			} catch (IOException e) {
				logger.error("File problem", e);
				return Collections.singletonList(
						new RepairResult(file, RepairStatus.FAILED, e.getClass().getSimpleName() + ": " + e.getMessage()));
			}
		}
		return repairResults;
	}

	private static RepairResult repairSong(Path infoDatPath) throws IOException {
		RepairResult result = new RepairResult(infoDatPath.toString(), RepairStatus.SUCCESS);

		// Handle info.dat
		List<String> songFiles = InfoDatHandler.handle(infoDatPath, result);

		// Handle songs
		for (String songFile : songFiles) {
			Path songPath = infoDatPath.getParent().resolve(songFile);
			// Check paths parents
			if (songPath.getParent().equals(infoDatPath.getParent())) {
				SongHandler.handle(songPath, result);
			} else {
				throw new UnsupportedOperationException(
						"Problem with filepath: \"" + songFile + "\" in: " + infoDatPath);
			}
		}

		return result;
	}
}
