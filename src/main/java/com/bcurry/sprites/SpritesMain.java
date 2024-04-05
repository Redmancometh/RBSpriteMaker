package com.bcurry.sprites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.Data;

/**
 * 
 * 
 * @author Redmancometh
 *
 */
public class SpritesMain {
	private static Type listType = new TypeToken<ArrayList<String>>() {
	}.getType();

	/**
	 * We're not worried about throwing the IOException here that'll never happen
	 * from the config initialization, and it's compartmentalized downstream.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (!initializeConfigs()) {
			Logger.getLogger(SpritesMain.class.getName()).info(
					"Preloaded configuration files have been loaded into the root folder. Please list assets in assets.json and configure config.json to desired settings and run again!");
			System.exit(0);
		}
		try {
			ProcessorConfig cfg = loadConfig();
			AssetDownloader downloader = new AssetDownloader();
			Gson gson = new Gson();
			try (FileReader reader = new FileReader("assets.json")) {
				List<String> assetStrings = gson.fromJson(reader, listType);
				downloader.parseURIs(assetStrings);
				List<File> downloaded = downloader.download();
				SpriteProcessor proc = new SpriteProcessor(cfg.getWidth(), cfg.getHeight(), cfg.getImgPerRow(),
						downloaded, cfg.getOutputFile());
				proc.process();
			} catch (IOException e1) {
				Logger.getLogger("This requires an assets.json file to be present!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static boolean initializeConfigs() throws IOException {
		File assets = new File("assets.json");
		File config = new File("config.json");
		boolean assetsExist = assets.exists();
		boolean configExists = config.exists();
		if (!assetsExist) {
			URL assetsURI = SpritesMain.class.getClassLoader().getResource("assets.json");
			FileUtils.copyURLToFile(assetsURI, assets);
		}
		if (!configExists) {
			URL configURI = SpritesMain.class.getClassLoader().getResource("config.json");
			FileUtils.copyURLToFile(configURI, config);
		}
		if (!(assetsExist && configExists))
			return false;
		return true;
	}

	/**
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static ProcessorConfig loadConfig() throws FileNotFoundException, IOException {
		Gson gson = new Gson();
		try (FileReader reader = new FileReader("config.json")) {
			ProcessorConfig assetStrings = gson.fromJson(reader, ProcessorConfig.class);
			return assetStrings;
		}
	}

	@Data
	class ProcessorConfig {
		private int hOff, vOff, width, height, imgPerRow;
		private String outputFile;
	}
}
