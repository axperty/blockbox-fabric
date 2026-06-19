package com.axperty.blockbox.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import com.axperty.blockbox.BlockBox;

import java.util.function.Supplier;

public class ModSounds
{
	public static void register() {}

	private static Supplier<SoundEvent> register(String name, Supplier<SoundEvent> soundEventSupplier) {
		SoundEvent soundEvent = soundEventSupplier.get();
		Registry.register(BuiltInRegistries.SOUND_EVENT, new ResourceLocation(BlockBox.MOD_ID, name), soundEvent);
		return () -> soundEvent;
	}

	// Stove
	public static final Supplier<SoundEvent> ITEM_SWORD_CARVE = register("item.sword.carve",
			() -> SoundEvent.createVariableRangeEvent(new ResourceLocation(BlockBox.MOD_ID, "item.sword.carve")));
}
