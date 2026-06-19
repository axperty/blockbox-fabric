package com.axperty.blockbox;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import com.axperty.blockbox.common.event.CommonEvents;
import com.axperty.blockbox.common.registry.*;

public class BlockBox implements ModInitializer
{
	public static final String MOD_ID = "blockbox";
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		BlockBoxConfig.load();
		ModBlocks.register();
		ModItems.register();
		ModEntityTypes.register();
		ModSounds.register();
		ModParticleTypes.register();
		ModCreativeTabs.register();
		CommonEvents.registerTabModifications();
		LOGGER.info("The Block Box Refabricated loaded");
	}
}
