package vislike.repair.tool.repair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vislike.repair.tool.repair.result.FileStatus;
import vislike.repair.tool.repair.result.RepairResult;
import vislike.repair.tool.repair.result.RepairStatus;

public class RepairFileEntry {

	private static final Logger logger = LoggerFactory.getLogger(RepairFileEntry.class);

	private RepairFileEntry() {
	}

	public static void appendResult(Path file, RepairResult result, String message) {
		result.fileStatus().add(new FileStatus(file.getFileName().toString(), message));
		logger.info("Applied [{}] {}", message, file);
	}

	public static RepairResult repairFile(String file) {
		logger.debug("Starting repair of: {}", file);

		Path infoDat = Path.of(file);
		if (!infoDat.getFileName().toString().equalsIgnoreCase("info.dat")) {
			return new RepairResult(RepairStatus.FAILED,
					"Expected filename to be: info.dat, got: " + infoDat.getFileName().toString());
		}

		try {
			RepairResult result = repairSong(infoDat);
			logger.debug("Repair complete: {}", file);

			if (result.fileStatus().isEmpty()) {
				return new RepairResult(RepairStatus.NOTHING);
			}
			return result;
		} catch (IOException e) {
			logger.error("File problem", e);
			return new RepairResult(RepairStatus.FAILED, e.getMessage());
		}
	}

	private static RepairResult repairSong(Path infoDatPath) throws IOException {
		RepairResult result = new RepairResult(RepairStatus.SUCCESS);

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
