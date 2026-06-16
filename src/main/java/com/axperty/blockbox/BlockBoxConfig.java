package com.axperty.blockbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class BlockBoxConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "blockbox-config.json");

	public static boolean ADD_ITEMS_TO_VANILLA_TABS = true;

	public static void load() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				ConfigData data = GSON.fromJson(reader, ConfigData.class);
				if (data != null) {
					ADD_ITEMS_TO_VANILLA_TABS = data.addItemsToVanillaTabs;
				}
			} catch (Exception e) {
				BlockBox.LOGGER.error("Failed to load config", e);
			}
		}
		save();
	}

	public static void save() {
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			ConfigData data = new ConfigData();
			data.addItemsToVanillaTabs = ADD_ITEMS_TO_VANILLA_TABS;
			GSON.toJson(data, writer);
		} catch (IOException e) {
			BlockBox.LOGGER.error("Failed to save config", e);
		}
	}

	private static class ConfigData {
		public boolean addItemsToVanillaTabs = ADD_ITEMS_TO_VANILLA_TABS;
	}
}
