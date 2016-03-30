package main;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ImageDownloader {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("u", true, "Target URL");
		options.addOption("d", true, "Distination directory");

		try {
			BasicParser parser = new BasicParser();
			CommandLine cl = parser.parse(options, args);

			if (cl.hasOption('u') && cl.hasOption('d')) {
				String url = cl.getOptionValue("u");
				String dir = cl.getOptionValue("d");
				Processor processor = new Processor(url, dir);
				processor.process();
			} else {
				HelpFormatter help = new HelpFormatter();
				help.printHelp(ImageDownloader.class.getName(), options, true);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
