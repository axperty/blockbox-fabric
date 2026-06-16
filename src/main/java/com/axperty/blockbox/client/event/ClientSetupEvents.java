package com.axperty.blockbox.client.event;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import com.axperty.blockbox.client.particle.SparkleParticle;
import com.axperty.blockbox.common.entity.SeatEntity;
import com.axperty.blockbox.common.registry.ModEntityTypes;
import com.axperty.blockbox.common.registry.ModParticleTypes;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import com.axperty.blockbox.common.registry.ModBlocks;
import net.minecraft.world.level.block.Block;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ClientSetupEvents implements ClientModInitializer
{
	@Override
	public void onInitializeClient() {
		ParticleFactoryRegistry.getInstance().register(ModParticleTypes.SPARKLE.get(), SparkleParticle.Provider::new);
		EntityRendererRegistry.register(ModEntityTypes.SEAT.get(), SeatEntity.Renderer::new);

		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROUGH_GLASS.get(), RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ROUGH_GLASS_PANE.get(), RenderType.translucent());

		Supplier<Block>[] cutoutBlocks = new Supplier[]{
				ModBlocks.GOLDEN_DOOR, ModBlocks.GOLDEN_TRAPDOOR,
				ModBlocks.IRON_PLATE_DOOR, ModBlocks.IRON_PLATE_TRAPDOOR,
				ModBlocks.BRAZIER, ModBlocks.SOUL_BRAZIER,
				ModBlocks.WHITE_SKY_LANTERN, ModBlocks.LIGHT_GRAY_SKY_LANTERN,
				ModBlocks.GRAY_SKY_LANTERN, ModBlocks.BLACK_SKY_LANTERN,
				ModBlocks.BROWN_SKY_LANTERN, ModBlocks.RED_SKY_LANTERN,
				ModBlocks.ORANGE_SKY_LANTERN, ModBlocks.YELLOW_SKY_LANTERN,
				ModBlocks.LIME_SKY_LANTERN, ModBlocks.GREEN_SKY_LANTERN,
				ModBlocks.CYAN_SKY_LANTERN, ModBlocks.LIGHT_BLUE_SKY_LANTERN,
				ModBlocks.BLUE_SKY_LANTERN, ModBlocks.PURPLE_SKY_LANTERN,
				ModBlocks.MAGENTA_SKY_LANTERN, ModBlocks.PINK_SKY_LANTERN,
				ModBlocks.OAK_PALISADE, ModBlocks.SPIKED_OAK_PALISADE, ModBlocks.STRIPPED_OAK_PALISADE, ModBlocks.STRIPPED_SPIKED_OAK_PALISADE,
				ModBlocks.SPRUCE_PALISADE, ModBlocks.SPIKED_SPRUCE_PALISADE, ModBlocks.STRIPPED_SPRUCE_PALISADE, ModBlocks.STRIPPED_SPIKED_SPRUCE_PALISADE,
				ModBlocks.BIRCH_PALISADE, ModBlocks.SPIKED_BIRCH_PALISADE, ModBlocks.STRIPPED_BIRCH_PALISADE, ModBlocks.STRIPPED_SPIKED_BIRCH_PALISADE,
				ModBlocks.JUNGLE_PALISADE, ModBlocks.SPIKED_JUNGLE_PALISADE, ModBlocks.STRIPPED_JUNGLE_PALISADE, ModBlocks.STRIPPED_SPIKED_JUNGLE_PALISADE,
				ModBlocks.ACACIA_PALISADE, ModBlocks.SPIKED_ACACIA_PALISADE, ModBlocks.STRIPPED_ACACIA_PALISADE, ModBlocks.STRIPPED_SPIKED_ACACIA_PALISADE,
				ModBlocks.DARK_OAK_PALISADE, ModBlocks.SPIKED_DARK_OAK_PALISADE, ModBlocks.STRIPPED_DARK_OAK_PALISADE, ModBlocks.STRIPPED_SPIKED_DARK_OAK_PALISADE,
				ModBlocks.MANGROVE_PALISADE, ModBlocks.SPIKED_MANGROVE_PALISADE, ModBlocks.STRIPPED_MANGROVE_PALISADE, ModBlocks.STRIPPED_SPIKED_MANGROVE_PALISADE,
				ModBlocks.CHERRY_PALISADE, ModBlocks.SPIKED_CHERRY_PALISADE, ModBlocks.STRIPPED_CHERRY_PALISADE, ModBlocks.STRIPPED_SPIKED_CHERRY_PALISADE,
				ModBlocks.CRIMSON_PALISADE, ModBlocks.SPIKED_CRIMSON_PALISADE, ModBlocks.STRIPPED_CRIMSON_PALISADE, ModBlocks.STRIPPED_SPIKED_CRIMSON_PALISADE,
				ModBlocks.WARPED_PALISADE, ModBlocks.SPIKED_WARPED_PALISADE, ModBlocks.STRIPPED_WARPED_PALISADE, ModBlocks.STRIPPED_SPIKED_WARPED_PALISADE
		};
		for (Supplier<Block> blockSupplier : cutoutBlocks) {
			BlockRenderLayerMap.INSTANCE.putBlock(blockSupplier.get(), RenderType.cutout());
		}

		Supplier<Block>[] cutoutMippedBlocks = new Supplier[]{
				ModBlocks.GOLDEN_BARS, ModBlocks.COPPER_BARS, ModBlocks.EXPOSED_COPPER_BARS,
				ModBlocks.WEATHERED_COPPER_BARS, ModBlocks.OXIDIZED_COPPER_BARS,
				ModBlocks.WAXED_COPPER_BARS, ModBlocks.WAXED_EXPOSED_COPPER_BARS,
				ModBlocks.WAXED_WEATHERED_COPPER_BARS, ModBlocks.WAXED_OXIDIZED_COPPER_BARS
		};
		for (Supplier<Block> blockSupplier : cutoutMippedBlocks) {
			BlockRenderLayerMap.INSTANCE.putBlock(blockSupplier.get(), RenderType.cutoutMipped());
		}
	}
}
