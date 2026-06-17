package com.axperty.blockbox.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.axperty.blockbox.common.registry.ModDamageTypes;
import com.axperty.blockbox.common.tag.ModTags;

import java.util.function.Supplier;

public class SpikedPalisadeBlock extends CrossCollisionBlock implements SimpleWaterloggedBlock
{
	public static final MapCodec<SpikedPalisadeBlock> CODEC = simpleCodec(SpikedPalisadeBlock::new);

	protected static final VoxelShape SPIKE_SHAPE = Block.box(4.0D, 8.0D, 4.0D, 12.0D, 16.0D, 12.0D);

	public final Supplier<Block> strippedForm;

	public SpikedPalisadeBlock(Properties properties) {
		this(null, properties);
	}

	public SpikedPalisadeBlock(Supplier<Block> strippedForm, Properties properties) {
		super(4.0F, 4.0F, 8.0F, 8.0F, 8.0F, properties);
		this.strippedForm = strippedForm;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(WATERLOGGED, false));
	}



	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.getItem() instanceof AxeItem && strippedForm != null) {
			level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.addDestroyBlockEffect(pos, state);
			if (player != null) {
				stack.hurtAndBreak(1, player, hand);
			}
			level.setBlock(pos, strippedForm.get().defaultBlockState()
					.setValue(NORTH, state.getValue(NORTH))
					.setValue(EAST, state.getValue(EAST))
					.setValue(SOUTH, state.getValue(SOUTH))
					.setValue(WEST, state.getValue(WEST))
					.setValue(WATERLOGGED, state.getValue(WATERLOGGED)), 11);
			return InteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (isEntityTouchingSpike(entity, pos)) {
			entity.makeStuckInBlock(state, new Vec3(0.8, 0.75, 0.8));
			if (!level.isClientSide() && (entity.xOld != entity.getX() || entity.zOld != entity.getZ())) {
				double d0 = Math.abs(entity.getX() - entity.xOld);
				double d1 = Math.abs(entity.getZ() - entity.zOld);
				if (d0 >= 0.003 || d1 >= 0.003) {
					entity.hurt(ModDamageTypes.getSimpleDamageSource(level, ModDamageTypes.PALISADE), 1.0F);
				}
			}
		}
	}

	protected boolean isEntityTouchingSpike(Entity entity, BlockPos pos) {
		VoxelShape collisionShape = SPIKE_SHAPE.move(pos.getX(), pos.getY(), pos.getZ());
		return Shapes.joinIsNotEmpty(collisionShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
		}
		return direction.getAxis().getPlane() == Direction.Plane.HORIZONTAL ? state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState, neighborState.isFaceSturdy(levelReader, neighborPos, direction.getOpposite()), direction.getOpposite())) : super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockGetter level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		BlockPos north = pos.north();
		BlockPos east = pos.east();
		BlockPos south = pos.south();
		BlockPos west = pos.west();
		BlockState stateNorth = level.getBlockState(north);
		BlockState stateEast = level.getBlockState(east);
		BlockState stateSouth = level.getBlockState(south);
		BlockState stateWest = level.getBlockState(west);
		return super.getStateForPlacement(context)
				.setValue(NORTH, this.connectsTo(stateNorth, stateNorth.isFaceSturdy(level, north, Direction.SOUTH), Direction.SOUTH))
				.setValue(EAST, this.connectsTo(stateEast, stateEast.isFaceSturdy(level, east, Direction.WEST), Direction.WEST))
				.setValue(SOUTH, this.connectsTo(stateSouth, stateSouth.isFaceSturdy(level, south, Direction.NORTH), Direction.NORTH))
				.setValue(WEST, this.connectsTo(stateWest, stateWest.isFaceSturdy(level, west, Direction.EAST), Direction.EAST))
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	public boolean connectsTo(BlockState state, boolean isSideSolid, Direction direction) {
		return !isExceptionForConnection(state) && isSideSolid
				|| state.is(ModTags.PALISADES)
				|| state.is(ModTags.SPIKED_PALISADES)
				|| state.is(BlockTags.WALLS)
				|| state.getBlock() instanceof IronBarsBlock;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	@Override
	protected MapCodec<? extends CrossCollisionBlock> codec() {
		return CODEC;
	}
}
