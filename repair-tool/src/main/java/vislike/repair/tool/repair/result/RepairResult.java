package vislike.repair.tool.repair.result;

import java.util.ArrayList;
import java.util.List;

public record RepairResult(RepairStatus code, String message, List<FileStatus> fileStatus) {

	public RepairResult(RepairStatus code) {
		this(code, null);
	}

	public RepairResult(RepairStatus code, String message) {
		this(code, message, new ArrayList<>());
	}
}
