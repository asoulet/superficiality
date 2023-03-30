package kb;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

public class TTLShallowReader {

	private static Logger logger = Logger.getLogger(TTLShallowReader.class);

	private ParserState state = ParserState.subject;
	private String subject = null;
	private String property = null;
	private String object = null;
	private TripleConsumer consumer = null;

	public TTLShallowReader(TripleConsumer consumer) {
		this.consumer = consumer;
	}

	private void addLine(String line) {
		String[] tokens = line.split("\\s+");
		updateSPO(tokens);
		state = nextState(tokens);
	}

	private void addTriple() {
		consumer.consume(subject, property, object);
	}

	private boolean updateSPO(String[] tokens) {
		ParserState step = state;
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			if (token.length() >= 1) {
				switch (step) {
				case subject:
					subject = token;
					step = ParserState.property;
					break;
				case property:
					property = token;
					step = ParserState.object;
					break;
				case object:
					if (token.substring(token.length() - 1).equals(",")) {
						object = token.substring(0, token.length() - 1);
						addTriple();
					} else {
						object = token;
						addTriple();
						return true;
					}
				}
			}
		}
		return false;
	}

	private ParserState nextState(String[] tokens) {
		int last = tokens.length - 1;
		while (last >= 0) {
			if (tokens[last].length() > 0) {
				String c = tokens[last].substring(tokens[last].length() - 1);
				if (c.equals("."))
					return ParserState.subject;
				if (c.equals(";"))
					return ParserState.property;
				if (c.equals(","))
					return ParserState.object;
			}
			last--;
		}
		return state;
	}

	public void load(String filename) {
		consumer.begin();
		readGZIPFile(filename);
		consumer.end();
	}

	private void readGZIPFile(String filename) {
		try (FileInputStream fis = new FileInputStream(filename);
				BufferedInputStream bis = new BufferedInputStream(fis);
				GZIPInputStream zis = new GZIPInputStream(bis)) {
			InputStreamReader reader = new InputStreamReader(zis);
			BufferedReader in = new BufferedReader(reader);
			String readed;
			while ((readed = in.readLine()) != null) {
				addLine(readed);
			}
		} catch (FileNotFoundException e) {
			logger.error(e, e);
		} catch (IOException e) {
			logger.error(e, e);
		}
	}

	private void readFile(String filename) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			InputStreamReader reader = new InputStreamReader(bis);
			BufferedReader in = new BufferedReader(reader);
			String readed;
			while ((readed = in.readLine()) != null) {
				addLine(readed);
			}
		} catch (Exception e) {
			logger.error(e, e);
		}
	}

	public void loadPath(String path) {
		consumer.begin();
		try {
			Iterator<Path> files = Files.list(new File(path).toPath()).iterator();
			while (files.hasNext()) {
				String filename = files.next().getFileName().toString();
				if (filename.substring(filename.length() - 3).toLowerCase().equals("ttl"))
					readFile(path + filename);
				else
					readGZIPFile(path + filename);
			}
		} catch (IOException e) {
			logger.error(e, e);
		}
		consumer.end();

	}

}
