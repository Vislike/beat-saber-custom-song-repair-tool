package vislike.repair.tool.repair;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.JsonNode;

import vislike.repair.tool.repair.Status.Code;

public class RepairFileEntry {

	private RepairFileEntry() {
	}

	public static Status repairFile(String file) {
		Path infoDat = Path.of(file);
		if (!infoDat.getFileName().toString().equalsIgnoreCase("info.dat")) {
			return new Status(Code.FAILED,
					"Expected filename to be: info.dat, got: " + infoDat.getFileName().toString());
		}

		try {
			processInfoDat(infoDat);
		} catch (IOException e) {
			return new Status(Code.FAILED, e.getMessage());
		}

		return new Status(Code.SUCCESS, "Nothing to do");
	}

	private static void processInfoDat(Path infoDat) throws IOException {
		String inString = Files.readString(infoDat);
		JsonNode rootNode = Json.getJsonMapper().readTree(inString);
		System.out.println(rootNode);

		String outString = Json.getBeatSaberFormatWriter().writeValueAsString(rootNode);

		if (inString.strip().equals(outString)) {
			System.out.println("Same");
		} else {
			System.out.println("Different");
			Files.writeString(infoDat, outString);
		}

	}
}
