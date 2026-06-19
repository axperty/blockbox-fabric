package com.axperty.blockbox.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import com.axperty.blockbox.BlockBox;
import com.axperty.blockbox.common.entity.SeatEntity;

import java.util.function.Supplier;

public class ModEntityTypes
{
	public static void register() {}

	private static <T extends EntityType<?>> Supplier<T> register(String name, Supplier<T> entityTypeSupplier) {
		T entityType = entityTypeSupplier.get();
		Registry.register(BuiltInRegistries.ENTITY_TYPE, new ResourceLocation(BlockBox.MOD_ID, name), entityType);
		return () -> entityType;
	}

	public static final Supplier<EntityType<SeatEntity>> SEAT = register("seat", () ->
			EntityType.Builder.<SeatEntity>of(SeatEntity::new, MobCategory.MISC)
					.sized(0.25f, 0.35f)
					.clientTrackingRange(3)
					.updateInterval(Integer.MAX_VALUE)
					.noSave()
					.build("seat"));
}
