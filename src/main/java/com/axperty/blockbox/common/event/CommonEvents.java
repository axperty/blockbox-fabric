package com.axperty.blockbox.common.event;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import com.axperty.blockbox.BlockBoxConfig;
import com.axperty.blockbox.common.registry.ModItems;

public class CommonEvents
{
	public static void registerTabModifications() {
		/*
		if (!BlockBoxConfig.ADD_ITEMS_TO_VANILLA_TABS) {
			return;
		}

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> {
			VanillaTabOrdering.BUILDING_BLOCKS.reversed().forEach((item, startingPoint) -> {
				entries.addAfter(startingPoint, new ItemStack(item.get()));
			});
		});

		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(entries -> {
			entries.addAfter(Items.TINTED_GLASS, new ItemStack(ModItems.ROUGH_GLASS_PANE.get()));
			entries.addAfter(Items.TINTED_GLASS, new ItemStack(ModItems.ROUGH_GLASS.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.WARPED_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.CRIMSON_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.CHERRY_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.BAMBOO_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.MANGROVE_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.DARK_OAK_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.ACACIA_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.JUNGLE_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.BIRCH_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.SPRUCE_SEAT.get()));
			entries.addAfter(Items.RESPAWN_ANCHOR, new ItemStack(ModItems.OAK_SEAT.get()));
			entries.addAfter(Items.SOUL_CAMPFIRE, new ItemStack(ModItems.SOUL_BRAZIER.get()));
			entries.addAfter(Items.SOUL_CAMPFIRE, new ItemStack(ModItems.BRAZIER.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.PINK_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.MAGENTA_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.PURPLE_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.BLUE_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.LIGHT_BLUE_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.CYAN_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.GREEN_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.LIME_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.YELLOW_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.ORANGE_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.RED_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.BROWN_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.BLACK_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.GRAY_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.LIGHT_GRAY_SKY_LANTERN.get()));
			entries.addAfter(Items.PINK_CANDLE, new ItemStack(ModItems.WHITE_SKY_LANTERN.get()));
		});
		*/
	}
}
