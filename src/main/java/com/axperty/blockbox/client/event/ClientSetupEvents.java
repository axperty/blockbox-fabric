package com.axperty.blockbox.client.event;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import com.axperty.blockbox.client.particle.SparkleParticle;
import com.axperty.blockbox.common.entity.SeatEntity;
import com.axperty.blockbox.common.registry.ModEntityTypes;
import com.axperty.blockbox.common.registry.ModParticleTypes;

@Environment(EnvType.CLIENT)
public class ClientSetupEvents implements ClientModInitializer
{
	@Override
	public void onInitializeClient() {
		ParticleProviderRegistry.getInstance().register(ModParticleTypes.SPARKLE.get(), SparkleParticle.Provider::new);
		EntityRendererRegistry.register(ModEntityTypes.SEAT.get(), context -> new EntityRenderer<SeatEntity, EntityRenderState>(context) {
			@Override
			public EntityRenderState createRenderState() { return new EntityRenderState(); }
			@Override
			public void extractRenderState(SeatEntity entity, EntityRenderState state, float tickDelta) {
				super.extractRenderState(entity, state, tickDelta);
			}
		});

	}
}
