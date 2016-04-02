package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EhentaiProcessor {

	String url;
	String dir;
	boolean isJapanese;
	String URLPattern = "(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+";
	int totalImages;

	public EhentaiProcessor(String url, String dir, boolean isJapanese) {
		this.url = url;
		this.dir = dir;
		this.isJapanese = isJapanese;
	}

	public void process() {
		try {
			long start = System.currentTimeMillis();
			System.out.println("Download Images from \"" + url + "\"");
			String source = getSourceText(new URL(url));
			String imageName = getImageName(source);
			System.out.println("Image Name: " + imageName);
			int totalImages = getTotalImages(source);
			this.totalImages = totalImages;
			System.out.println("Total Images: " + totalImages);
			System.out.println("Getting Image URLs ...");
			String firstUrl = getFirstPage(source);
			ArrayList<String> urlList = getUrlList(firstUrl);
			System.out.println("Download Images ...");
			downloadImages(urlList, totalImages, imageName);
			System.out.println("Finished!");
			long end = System.currentTimeMillis();
			System.out.println("Execution Time: " + (end - start) / 1000 + " s");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void downloadImages(ArrayList<String> list, int totalImages, String imageName) {
		File newDir = new File(dir + "\\\\" + imageName);
		newDir.mkdir();
		int index = 0;
		int threadsNum = Runtime.getRuntime().availableProcessors();
		ExecutorService service = Executors.newFixedThreadPool(threadsNum);
		for (String url : list) {
			if(url.endsWith("509.gif")){
				System.out.println("509 occurred");
				System.exit(0);
			}
			index++;
			service.execute(new DownloaderThread(index, totalImages, newDir, url));
		}
		service.shutdown();
		while (!service.isTerminated()) {
		}
	}

	private ArrayList<String> getUrlList(String firstUrl) {
		String url = firstUrl;
		int index = 1;
		ArrayList<String> list = new ArrayList<String>();
		list.add(getImageUrl(firstUrl, index));
		while (true) {
			index++;
			if (index > totalImages)
				break;
			String nextUrl = getNextUrl(url, index);
			if (nextUrl.equals(""))
				break;
			url = nextUrl;
			list.add(getImageUrl(nextUrl, index));
		}
		return list;
	}

	private String getImageUrl(String url, int index) {
		try {
			String source = getSourceText(new URL(url));
			int beginIndex = source.indexOf("<img id=\"img\" src=\"");
			beginIndex += "<img id=\"img\" src=\"".length();
			int endIndex = source.indexOf("\" style=", beginIndex);
			String imageUrl = source.substring(beginIndex, endIndex);
//			StringBuilder sb = new StringBuilder();
//			sb.append("(").append(index).append("/").append(totalImages).append(") ").append(imageUrl);
//			System.out.println(sb.toString());
			return imageUrl;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getNextUrl(String url, int index) {
		try {
			String source = getSourceText(new URL(url));
			String tmp = "return load_image(" + index;
			int tmpIndex = source.indexOf(tmp);
			int beginIndex = source.indexOf("href=\"", tmpIndex) + "href=\"".length();
			int endIndex = source.indexOf("\"><img", beginIndex);
			if (beginIndex == -1 || endIndex == -1) {
				return "";
			}
			String ret = source.substring(beginIndex, endIndex);
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getImageName(String source) {
		if (isJapanese) {
			int beginIndex = source.indexOf("</h1><h1 id=\"gj\">");
			beginIndex += "</h1><h1 id=\"gj\">".length();
			int endIndex = source.indexOf("</h1></div>");
			String imageName = source.substring(beginIndex, endIndex);
			imageName = imageName.replaceAll("\\!", "");
			return imageName;
		} else {
			int beginIndex = source.indexOf("<title>") + "<title>".length();
			int endIndex = source.indexOf("</title>");
			String imageName = source.substring(beginIndex, endIndex);
			return imageName;
		}
	}

	private boolean isValidName(String name) {
		if (name == null || name.length() < 1) {
			return false;
		}
		return true;
	}

	private int getTotalImages(String source) {
		int beginIndex = source.indexOf("Length:</td><td class=\"gdt2\">");
		beginIndex += "Length:</td><td class=\"gdt2\">".length();
		int endIndex = source.indexOf(" pages</td>");
		String numString = source.substring(beginIndex, endIndex);
		if (isNumber(numString)) {
			return Integer.parseInt(numString);
		} else {
			return 0;
		}
	}

	private static boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private String getFirstPage(String source) {
		Pattern urlPattern = Pattern.compile(URLPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = urlPattern.matcher(source);
		while (matcher.find()) {
			String url = matcher.group();
			if (url.endsWith("-1")) {
				return url;
			}
		}
		return null;
	}

	public static String getSourceText(URL url) throws IOException {
		InputStream in = url.openStream();
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(in));
			String s;
			while ((s = bf.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
			}
		} finally {
			in.close();
		}
		return sb.toString();
	}

}
