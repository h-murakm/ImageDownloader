package main;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NhentaiProcessor {

	String url;
	String dir;
	boolean isJapanese;
	String URLPattern = "(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+";
	String pagePattern = "<div>.+?pages</div>";
	String imageUrlPattern = "data-src=\".*?\" />";
	int totalImages;

	public NhentaiProcessor(String url, String dir, boolean isJapanese) {
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
			ArrayList<String> urlList = getUrlList(source);
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
			sb.append("(").append(index).append("/").append(totalImages).append(") ").append(url);
			System.out.println(sb.toString());
			try {
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
				byte[] b = new byte[8192];
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

	private ArrayList<String> getUrlList(String source) {
		ArrayList<String> urlList = new ArrayList<String>();
		Pattern urlPattern = Pattern.compile(imageUrlPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = urlPattern.matcher(source);
		while (matcher.find()) {
			String tmp = matcher.group();
			int beginIndex = tmp.indexOf("data-src=\"//t");
			beginIndex += "data-src=\"//t".length();
			if(tmp.contains(".jpg")){
				int endIndex = tmp.indexOf("t.jpg\" />");
				String imageUrl = "http://i" + tmp.substring(beginIndex, endIndex) + ".jpg";
				urlList.add(imageUrl);
			}else{
				int endIndex = tmp.indexOf("t.png\" />");
				String imageUrl = "http://i" + tmp.substring(beginIndex, endIndex) + ".png";
				urlList.add(imageUrl);
			}
		}
		return urlList;
	}

	private int getTotalImages(String source) {
		Pattern urlPattern = Pattern.compile(pagePattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = urlPattern.matcher(source);
		while (matcher.find()) {
			String tmp = matcher.group();
			int beginIndex = tmp.indexOf("<div>");
			beginIndex += "<div>".length();
			int endIndex = tmp.indexOf(" pages");
			String pageString = tmp.substring(beginIndex, endIndex);
			if (isNumber(pageString)) {
				return Integer.parseInt(pageString);
			} else {
				return 0;
			}
		}
		return 0;
	}

	private static boolean isNumber(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private String getImageName(String source) {
		if(isJapanese){
			int beginIndex = source.indexOf("<h2>");
			beginIndex += "<h2>".length();
			int endIndex = source.indexOf("</h2>");
			String imageName = source.substring(beginIndex, endIndex);
			imageName = imageName.replaceAll("\\!", "");
			return imageName;
		}else{
			int beginIndex = source.indexOf("<h1>");
			beginIndex += "<h1>".length();
			int endIndex = source.indexOf("</h1>");
			String imageName = source.substring(beginIndex, endIndex);
			return imageName;
		}
	}

	public static String getSourceText(URL url) throws IOException {
		StringBuffer sb = new StringBuffer();
		try {
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-agent", "Mozilla/5.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sb.toString();
	}
}
