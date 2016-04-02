package main;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloaderThread implements Runnable {
	String url;
	int index;
	int totalImages;
	File newDir;

	DownloaderThread(int index, int totalImages, File newDir, String url){
		this.index = index;
		this.totalImages = totalImages;
		this.url = url;
		this.newDir = newDir;
	}

	@Override
	public void run() {
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
				conn.disconnect();
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
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
