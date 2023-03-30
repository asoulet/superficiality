package kb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Properties;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;
import org.apache.jena.riot.system.StreamRDFWrapper;
import org.apache.log4j.Logger;

import mbg.MBG;

public class RDFReader {

	private static Logger logger = Logger.getLogger(RDFReader.class);

	private TripleConsumer consumer = null;

	public RDFReader(TripleConsumer consumer) {
		this.consumer = consumer;
	}

	public void load(String path) {
		consumer.begin();
		try {
			Iterator<Path> files = Files.list(new File(path).toPath()).iterator();
			int k = 1;
			StreamRDF output = StreamRDFLib.sinkNull();
			StreamRDF filtered = new CountRDF(output);
			while (files.hasNext()) {
				String filename = files.next().getFileName().toString();
				logger.info(k + " process " + path + filename);
				RDFParserBuilder pb = RDFParser.create().source(path + filename);// .forceLang(Lang.TTL);
				RDFParser parser = pb.build();
				parser.parse(filtered);
				logger.info((k++) + " " + (path + filename) + " processed");
			}
		} catch (IOException e) {
			logger.error(e, e);
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(MBG.CONFIGURATION_FILE));
		} catch (IOException e) {
			logger.error(e, e);
		}
		consumer.end();
	}

	public void loadFile(String filename) {
		consumer.begin();
		StreamRDF output = StreamRDFLib.sinkNull();
		StreamRDF filtered = new CountRDF(output);
		logger.info(" process " + filename);
		RDFParserBuilder pb = RDFParser.create().source(filename);// .forceLang(Lang.TTL);
		RDFParser parser = pb.build();
		parser.parse(filtered);
		logger.info((filename) + " processed");
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(MBG.CONFIGURATION_FILE));
		} catch (IOException e) {
			logger.error(e, e);
		}
		consumer.end();
	}

	public class CountRDF extends StreamRDFWrapper {

		public CountRDF(StreamRDF other) {
			super(other);
		}

		@Override
		public void triple(Triple triple) {
			// logger.info(triple);
			Node s = triple.getSubject();
			Node p = triple.getPredicate();
			Node o = triple.getObject();
			if (o.isURI())
				consumer.consume(s.toString(), p.toString(), o.toString());
		}

	}

}
