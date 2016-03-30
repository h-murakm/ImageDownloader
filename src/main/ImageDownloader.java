package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
		options.addOption("f", true, "File containing target URLs");

		try {
			BasicParser parser = new BasicParser();
			CommandLine cl = parser.parse(options, args);

			if (cl.hasOption('f') && cl.hasOption('d')) {
				ArrayList<String> urlList = getUrlList(cl.getOptionValue("f"));
				String dir = cl.getOptionValue("d");
				for(String url : urlList){
					Processor processor = new Processor(url, dir);
					processor.process();
				}
			} else if (cl.hasOption('u') && cl.hasOption('d')) {
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

	private static ArrayList<String> getUrlList(String f) {
		ArrayList<String> urlList = new ArrayList<String>();
		try {
			File file = new File(f);
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				urlList.add(str);
			}
			br.close();
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		return urlList;
	}

}
