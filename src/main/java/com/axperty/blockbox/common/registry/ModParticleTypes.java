package com.axperty.blockbox.common.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import com.axperty.blockbox.BlockBox;

import java.util.function.Supplier;

public class ModParticleTypes
{
	public static void register() {}

	private static <T extends ParticleType<?>> Supplier<T> register(String name, Supplier<T> particleTypeSupplier) {
		T particleType = particleTypeSupplier.get();
		Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.fromNamespaceAndPath(BlockBox.MOD_ID, name), particleType);
		return () -> particleType;
	}

	public static final Supplier<SimpleParticleType> SPARKLE = register("sparkle",
			() -> new SimpleParticleType(true));
}
