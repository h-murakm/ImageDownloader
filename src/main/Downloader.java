package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Downloader {

	String url;
	String dir;
	String URLPattern = "(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+";

	public Downloader(String url, String dir) {
		this.url = url;
		this.dir = dir;
	}

	public void process() {
		try {
			long start = System.currentTimeMillis();
			System.out.println("Download Images from \"" + url + "\"");
			String source = getSourceText(new URL(url));
			String imageName = getImageName(source);
			System.out.println("Image Name: " + imageName);
			int totalImages = getTotalImages(source);
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
		for (String url : list) {
			index++;
			StringBuilder sb = new StringBuilder();
			sb.append("( ").append(index).append(" / ").append(totalImages).append(" ) ").append(url);
			System.out.println(sb.toString());
			try {
				//URL url = new URL("http://loto6.thekyo.jp/data/loto6.csv");
				HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
				conn.setAllowUserInteraction(false);
				conn.setInstanceFollowRedirects(true);
				conn.setRequestMethod("GET");
				conn.connect();
				int httpStatusCode = conn.getResponseCode();
				if (httpStatusCode != HttpURLConnection.HTTP_OK) {
					throw new Exception();
				}
				String fileName = url.substring(url.lastIndexOf("/"));
				String dist = newDir.toString() + fileName;
				DataInputStream dataInStream = new DataInputStream(conn.getInputStream());
				DataOutputStream dataOutStream = new DataOutputStream(new BufferedOutputStream(
						new FileOutputStream(dist)));
				byte[] b = new byte[4096];
				int readByte = 0;
				while (-1 != (readByte = dataInStream.read(b))) {
					dataOutStream.write(b, 0, readByte);
				}
				dataInStream.close();
				dataOutStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ArrayList<String> getUrlList(String firstUrl) {
		String url = firstUrl;
		int index = 1;
		ArrayList<String> list = new ArrayList<String>();
		list.add(getImageUrl(firstUrl));
		while (true) {
			index++;
			String nextIndex = "-" + index;
			String nextUrl = getNextUrl(url, nextIndex);
			if (nextUrl.equals(""))
				break;
			url = nextUrl;
			list.add(getImageUrl(nextUrl));
			//System.out.println(nextIndex);
		}
		return list;
	}

	private String getImageUrl(String url) {
		try {
			String source = getSourceText(new URL(url));
			int beginIndex = source.indexOf("<img id=\"img\" src=\"");
			beginIndex += "<img id=\"img\" src=\"".length();
			int endIndex = source.indexOf("\" style=", beginIndex);
			String imageUrl = source.substring(beginIndex, endIndex);
			return imageUrl;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getNextUrl(String url, String pageIndex) {
		try {
			String source = getSourceText(new URL(url));
			Pattern urlPattern = Pattern.compile(URLPattern, Pattern.CASE_INSENSITIVE);
			Matcher matcher = urlPattern.matcher(source);
			while (matcher.find()) {
				String nextUrl = matcher.group();
				if (nextUrl.endsWith(pageIndex)) {
					return nextUrl;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";

	}

	private String getImageName(String source) {
		int beginIndex = source.indexOf("<title>");
		beginIndex += "<title>".length();
		int endIndex = source.indexOf("</title>");
		String imageName = source.substring(beginIndex, endIndex);
		return imageName;
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
