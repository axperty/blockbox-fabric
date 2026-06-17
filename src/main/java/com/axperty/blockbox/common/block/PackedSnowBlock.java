package com.axperty.blockbox.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.resources.Identifier;
import com.axperty.blockbox.common.registry.ModBlocks;


public class PackedSnowBlock extends Block
{
	public PackedSnowBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.is(Items.STICK) || stack.is(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "wooden_rods"))) || stack.is(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "rods/wooden")))) {
			Direction hitDirection = hitResult.getDirection();
			Direction facingDirection = hitDirection.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : hitDirection;
			level.playSound(null, pos, SoundEvents.SNOW_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.setBlock(pos, ModBlocks.CARVED_SNOW.get().defaultBlockState().setValue(CarvedPumpkinBlock.FACING, facingDirection), 11);
			return InteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
}
