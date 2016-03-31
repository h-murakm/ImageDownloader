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
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ImageDownloader {

	public static void main(String[] args) {
		Options options = makeOptions();
		boolean isJapanese = false;;
		try {
			BasicParser parser = new BasicParser();
			CommandLine cl = parser.parse(options, args);
			if (cl.hasOption('h')) {
				printHelp(options);
				return;
			}
			if (cl.hasOption('j')) {
				isJapanese = true;
			}
			if (cl.hasOption('f')) {
				ArrayList<String> urlList = getUrlList(cl.getOptionValue("f"));
				String dir = cl.getOptionValue("d");
				processFile(urlList, dir, isJapanese);
			} else if (cl.hasOption('u')) {
				String url = cl.getOptionValue("u");
				String dir = cl.getOptionValue("d");
				processUrl(url, dir, isJapanese);
			} else {
				printHelp(options);
				return;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	private static Options makeOptions() {
		Options options = new Options();
		options.addOption("j", false, "Make directory with Japanese name");
		options.addOption("h", false, "Print help");
		Option option_d = OptionBuilder.withArgName("dir").hasArg().isRequired()
				.withDescription("Distination directory").create("d");
		options.addOption(option_d);
		Option option_u = OptionBuilder.withArgName("url").hasArg()
				.withDescription("Target URL").create("u");
		options.addOption(option_u);
		Option option_f = OptionBuilder.withArgName("file").hasArg()
				.withDescription("File containing target URLs").create("f");
		options.addOption(option_f);
		return options;
	}

	private static void processUrl(String url, String dir, boolean isJapanese) {
		if (isEhentaiUrl(url)) {
			EhentaiProcessor processor = new EhentaiProcessor(url, dir, isJapanese);
			processor.process();
		} else if (isNhentaiUrl(url)) {
			NhentaiProcessor processor = new NhentaiProcessor(url, dir, isJapanese);
			processor.process();
		} else {
			System.out.println(url + " is not supported");
		}
	}

	private static void processFile(ArrayList<String> urlList, String dir, boolean isJapanese) {
		for (String url : urlList) {
			if (isEhentaiUrl(url)) {
				EhentaiProcessor processor = new EhentaiProcessor(url, dir, isJapanese);
				processor.process();
			} else if (isNhentaiUrl(url)) {
				NhentaiProcessor processor = new NhentaiProcessor(url, dir, isJapanese);
				processor.process();
			} else {
				System.out.println(url + " is not supported");
			}
		}
	}

	private static void printHelp(Options options) {
		HelpFormatter help = new HelpFormatter();
		help.printHelp(ImageDownloader.class.getName(), options, true);
	}

	private static boolean isEhentaiUrl(String url) {
		if (url.contains("e-hentai")) {
			return true;
		}
		return false;
	}

	private static boolean isNhentaiUrl(String url) {
		if (url.contains("nhentai")) {
			return true;
		}
		return false;
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
