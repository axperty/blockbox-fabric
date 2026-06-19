package com.axperty.blockbox.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperPillarBlock extends RotatedPillarBlock implements WeatheringCopper
{
	
	private final WeatheringCopper.WeatherState weatherState;

	public WeatheringCopperPillarBlock(WeatheringCopper.WeatherState weatherState, Properties properties) {
		super(properties);
		this.weatherState = weatherState;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		this.onRandomTick(state, level, pos, random);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return WeatheringCopper.getNext(state.getBlock()).isPresent();
	}

	public WeatheringCopper.WeatherState getAge() {
		return this.weatherState;
	}

	}
