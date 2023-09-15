package vislike.repair.tool.repair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vislike.repair.tool.repair.result.RepairResult;

public class SongHandler {

	public static void handle(Path file, RepairResult result) throws IOException {
		boolean repaired = false;

		String inString = Files.readString(file).stripTrailing();
		JsonNode rootNode = Json.getJsonMapper().readTree(inString);

		// Look for version
		JsonNode version = rootNode.get("_version");
		if (version == null) {
			// Version is missing, add default version
			rootNode = Json.getJsonMapper().createObjectNode().put("_version", "2.0.0").setAll((ObjectNode) rootNode);
			repaired = true;
			RepairFileEntry.appendResult(file, result, "Missing Version (2.0.0)");
		} else {
			// Is version at start
			String firstChild = rootNode.fieldNames().next();
			if (!"_version".equals(firstChild)) {
				// Remove version and add it to start
				((ObjectNode) rootNode).remove("_version");
				rootNode = Json.getJsonMapper().createObjectNode().<ObjectNode>set("_version", version)
						.setAll((ObjectNode) rootNode);
				repaired = true;
				RepairFileEntry.appendResult(file, result, "Move Version (" + version.textValue() + ")");
			}
		}

		// Save file
		if (repaired) {
			Files.writeString(file, Json.getMinifiedWriter().writeValueAsString(rootNode));
		} else {
			// Check if formatting should be applied
			String outString = Json.getMinifiedWriter().writeValueAsString(rootNode);
			if (!inString.equals(outString)) {
				RepairFileEntry.appendResult(file, result, "Json Formatting");
				Files.writeString(file, outString);
			}
		}
	}

}
