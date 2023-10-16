package vislike.repair.tool.repair.result;

import java.util.ArrayList;
import java.util.List;

public record RepairResult(String filePath, RepairStatus code, String message, List<FileStatus> fileStatus) {

	public RepairResult(String filePath, RepairStatus code) {
		this(filePath, code, null);
	}

	public RepairResult(String filePath, RepairStatus code, String message) {
		this(filePath, code, message, new ArrayList<>());
	}
}
