package vislike.repair.tool.repair;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Json {

	private static class JsonHolder {
		private static final Json INSTANCE = new Json();
	}

	public static ObjectMapper getJsonMapper() {
		return JsonHolder.INSTANCE.jsonMapper;
	}

	public static ObjectWriter getMinifiedWriter() {
		return JsonHolder.INSTANCE.minifiedWriter;
	}

	public static ObjectWriter getBeatSaberFormatWriter() {
		return JsonHolder.INSTANCE.beatSaberFormatWriter;
	}

	private final ObjectMapper jsonMapper;
	private final ObjectWriter minifiedWriter;
	private final ObjectWriter beatSaberFormatWriter;

	private static class BeatSaberPrettyPrinter extends DefaultPrettyPrinter {
		private static final long serialVersionUID = 1L;

		public BeatSaberPrettyPrinter() {
			super();
			DefaultIndenter indenter = new DefaultIndenter("  ", "\n");
			indentArraysWith(indenter);
			indentObjectsWith(indenter);
		}

		public BeatSaberPrettyPrinter(BeatSaberPrettyPrinter beatSaberPrettyPrinter) {
			super(beatSaberPrettyPrinter);
		}

		@Override
		public DefaultPrettyPrinter createInstance() {
			return new BeatSaberPrettyPrinter(this);
		}

		@Override
		public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
			g.writeRaw(": ");
		}

		@Override
		public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
			if (!_objectIndenter.isInline()) {
				--_nesting;
			}
			if (nrOfEntries > 0) {
				_objectIndenter.writeIndentation(g, _nesting);
			}
			g.writeRaw('}');
		}

		@Override
		public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
			if (!_arrayIndenter.isInline()) {
				--_nesting;
			}
			if (nrOfValues > 0) {
				_arrayIndenter.writeIndentation(g, _nesting);
			}
			g.writeRaw(']');
		}
	}

	private Json() {
		jsonMapper = new ObjectMapper();
		minifiedWriter = jsonMapper.writer();
		beatSaberFormatWriter = jsonMapper.writer(new BeatSaberPrettyPrinter());
	}
}
