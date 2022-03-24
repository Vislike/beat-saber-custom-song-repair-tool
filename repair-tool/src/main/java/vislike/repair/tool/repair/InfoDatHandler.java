package vislike.repair.tool.repair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import vislike.repair.tool.repair.result.RepairResult;

public class InfoDatHandler {

	public static List<String> handle(Path file, RepairResult result) throws IOException {
		List<String> songFiles = new ArrayList<>();
		boolean repaired = false;

		String inString = Files.readString(file).stripTrailing();
		JsonNode rootNode = Json.getJsonMapper().readTree(inString);

		// Find Songs & Lightshow
		JsonNode beatMapSets = rootNode.get("_difficultyBeatmapSets");
		if (beatMapSets != null && beatMapSets.isArray()) {
			var it = beatMapSets.iterator();
			while (it.hasNext()) {
				JsonNode jsonNode = it.next();
				// Remove Lightshow
				if ("Lightshow".equalsIgnoreCase(jsonNode.get("_beatmapCharacteristicName").textValue())) {
					it.remove();
					repaired = true;
					RepairFileEntry.appendResult(file, result, "Removing Lightshow");
				} else {
					// Add song files
					jsonNode.findValues("_beatmapFilename").forEach(v -> songFiles.add(v.textValue()));
				}
			}
		}

		// Save file
		if (repaired) {
			Files.writeString(file, Json.getBeatSaberFormatWriter().writeValueAsString(rootNode));
		} else {
			// Check if formating should be applied
			String outString = Json.getBeatSaberFormatWriter().writeValueAsString(rootNode);
			if (!inString.equals(outString)) {
				RepairFileEntry.appendResult(file, result, "Json Formatting");
				Files.writeString(file, outString);
			}
		}

		return songFiles;
	}
}
