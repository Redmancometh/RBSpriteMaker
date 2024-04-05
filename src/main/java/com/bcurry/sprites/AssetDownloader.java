package com.bcurry.sprites;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import com.bcurry.sprites.pojo.AssetLink;

import kong.unirest.GetRequest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;

/**
 * 
 */
public class AssetDownloader {
	private Pattern idRegex = Pattern.compile("id=(\\d+)");
	private String baseURL = "https://assetdelivery.roblox.com/v1/asset/?id=";
	private List<AssetLink> uriList = new ArrayList();

	/**
	 * 
	 * @param urlStrings
	 * @throws IOException
	 */
	public void parseURIs(List<String> urlStrings) throws IOException {
		System.out.println("Preprocessing URLs...");
		for (String urlString : urlStrings) {
			Matcher matcher = idRegex.matcher(urlString);
			if (matcher.find()) {
				try {
					String id = matcher.group(1);
					String constructedURLString = baseURL + id;
					uriList.add(new AssetLink(urlString, id, new URL(constructedURLString)));
				} catch (Exception e) {
					e.printStackTrace();
					Logger.getLogger(this.getClass().getName())
							.warning("URI with string " + urlString + " had to be skipped!");
					continue;
				}
			}
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<File> download() throws IOException {
		List<File> downloadedList = new ArrayList();
		for (AssetLink assLink : uriList) {
			try {
				File downloadFile = new File("assets" + File.separator + assLink.getId() + ".png");
				URL downloadURL = assLink.getDownloadURL();
				GetRequest req = Unirest.get(downloadURL.toString());
				req.header("User-Agent",
						"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:124.0) Gecko/20100101 Firefox/124.0");
				req.header("Content-Type", "image/png");
				HttpResponse<byte[]> resp = req.asBytes();
				try (ByteArrayInputStream bais = new ByteArrayInputStream(resp.getBody())) {
					BufferedImage img = ImageIO.read(bais);
					try (FileOutputStream out = new FileOutputStream(downloadFile)) {
						ImageIO.write(img, "png", downloadFile);
						downloadedList.add(downloadFile);
					}
				}
				Logger.getLogger(this.getClass().getName())
						.info("Downloaded asset with id of " + assLink.getId() + " successfully!");
			} catch (UnirestException e) {
				e.printStackTrace();
			}

		}
		return downloadedList;
	}
}
