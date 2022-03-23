package vislike.repair.tool.repair;

public record Status(Code code, String message) {
	public static enum Code {
		SUCCESS, FAILED;
	}
}
