package com.axperty.blockbox.common.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import com.axperty.blockbox.common.block.state.PalisadeConnection;
import com.axperty.blockbox.common.registry.ModSounds;
import com.axperty.blockbox.common.tag.ModTags;

import java.util.Map;
import java.util.function.Supplier;

public class PalisadeBlock extends Block implements SimpleWaterloggedBlock
{
	public static final MapCodec<PalisadeBlock> CODEC = simpleCodec(PalisadeBlock::new);

	public static final EnumProperty<PalisadeConnection> TYPE_NORTH = EnumProperty.create("north", PalisadeConnection.class);
	public static final EnumProperty<PalisadeConnection> TYPE_EAST = EnumProperty.create("east", PalisadeConnection.class);
	public static final EnumProperty<PalisadeConnection> TYPE_SOUTH = EnumProperty.create("south", PalisadeConnection.class);
	public static final EnumProperty<PalisadeConnection> TYPE_WEST = EnumProperty.create("west", PalisadeConnection.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public final Supplier<Block> strippedForm;
	public final Supplier<Block> spikedForm;

	public static final Map<Direction, EnumProperty<PalisadeConnection>> PROPERTY_BY_DIRECTION = Map.of(
		Direction.NORTH, TYPE_NORTH,
		Direction.EAST, TYPE_EAST,
		Direction.SOUTH, TYPE_SOUTH,
		Direction.WEST, TYPE_WEST
	);

	public PalisadeBlock(Properties properties) {
		this(null, null, 4.0F, 4.0F, 16.0F, 16.0F, 16.0F, properties);
	}

	public PalisadeBlock(Supplier<Block> spikedForm, Properties properties) {
		this(spikedForm, null, 4.0F, 4.0F, 16.0F, 16.0F, 16.0F, properties);
	}

	public PalisadeBlock(Supplier<Block> spikedForm, Supplier<Block> strippedForm, Properties properties) {
		this(spikedForm, strippedForm, 4.0F, 4.0F, 16.0F, 16.0F, 16.0F, properties);
	}

	public PalisadeBlock(Supplier<Block> spikedForm, Supplier<Block> strippedForm, float nodeWidth, float extensionWidth, float nodeHeight, float extensionHeight, float collisionHeight, Properties properties) {
		super(properties);
		this.spikedForm = spikedForm;
		this.strippedForm = strippedForm;
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(TYPE_NORTH, PalisadeConnection.NONE)
				.setValue(TYPE_EAST, PalisadeConnection.NONE)
				.setValue(TYPE_SOUTH, PalisadeConnection.NONE)
				.setValue(TYPE_WEST, PalisadeConnection.NONE)
				.setValue(WATERLOGGED, false));
		
		float minNode = 8.0F - nodeWidth;
		float maxNode = 8.0F + nodeWidth;
		float minExt = 8.0F - extensionWidth;
		float maxExt = 8.0F + extensionWidth;
		VoxelShape nodeShape = Block.box(minNode, 0.0F, minNode, maxNode, nodeHeight, maxNode);
		VoxelShape northExt = Block.box(minExt, 0.0F, 0.0F, maxExt, extensionHeight, maxExt);
		VoxelShape southExt = Block.box(minExt, 0.0F, minExt, maxExt, extensionHeight, 16.0F);
		VoxelShape westExt = Block.box(0.0F, 0.0F, minExt, maxExt, extensionHeight, maxExt);
		VoxelShape eastExt = Block.box(minExt, 0.0F, minExt, 16.0F, extensionHeight, maxExt);

		Map<BlockState, VoxelShape> map = Maps.newHashMap();
		for (BlockState state : this.stateDefinition.getPossibleStates()) {
			VoxelShape shape = nodeShape;
			if (state.getValue(TYPE_NORTH) != PalisadeConnection.NONE) shape = Shapes.or(shape, northExt);
			if (state.getValue(TYPE_SOUTH) != PalisadeConnection.NONE) shape = Shapes.or(shape, southExt);
			if (state.getValue(TYPE_WEST) != PalisadeConnection.NONE) shape = Shapes.or(shape, westExt);
			if (state.getValue(TYPE_EAST) != PalisadeConnection.NONE) shape = Shapes.or(shape, eastExt);
			map.put(state, shape);
		}
		this.shapesCache = map;
	}

	private final Map<BlockState, VoxelShape> shapesCache;

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return this.shapesCache.get(state);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return this.shapesCache.get(state);
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
					.setValue(TYPE_NORTH, state.getValue(TYPE_NORTH))
					.setValue(TYPE_EAST, state.getValue(TYPE_EAST))
					.setValue(TYPE_SOUTH, state.getValue(TYPE_SOUTH))
					.setValue(TYPE_WEST, state.getValue(TYPE_WEST))
					.setValue(WATERLOGGED, state.getValue(WATERLOGGED)), 11);
			return InteractionResult.SUCCESS;
		}
		if (spikedForm == null) {
			return InteractionResult.PASS;
		}
		if (stack.is(ItemTags.SWORDS) && level.getBlockState(pos.above()).isAir()) {
			level.playSound(null, pos, ModSounds.ITEM_SWORD_CARVE.get(), SoundSource.BLOCKS, 1.0F, 0.9F);
			level.addDestroyBlockEffect(pos, state);
			stack.hurtAndBreak(2, player, hand);
			level.setBlock(pos, spikedForm.get().defaultBlockState()
					.setValue(SpikedPalisadeBlock.NORTH, !state.getValue(TYPE_NORTH).equals(PalisadeConnection.NONE))
					.setValue(SpikedPalisadeBlock.EAST, !state.getValue(TYPE_EAST).equals(PalisadeConnection.NONE))
					.setValue(SpikedPalisadeBlock.SOUTH, !state.getValue(TYPE_SOUTH).equals(PalisadeConnection.NONE))
					.setValue(SpikedPalisadeBlock.WEST, !state.getValue(TYPE_WEST).equals(PalisadeConnection.NONE))
					.setValue(WATERLOGGED, state.getValue(WATERLOGGED)), 11);
			return InteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}



	@Override
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
				.setValue(TYPE_NORTH, this.getConnectionType(stateNorth, stateNorth.isFaceSturdy(level, north, Direction.SOUTH), Direction.SOUTH))
				.setValue(TYPE_EAST, this.getConnectionType(stateEast, stateEast.isFaceSturdy(level, east, Direction.WEST), Direction.WEST))
				.setValue(TYPE_SOUTH, this.getConnectionType(stateSouth, stateSouth.isFaceSturdy(level, south, Direction.NORTH), Direction.NORTH))
				.setValue(TYPE_WEST, this.getConnectionType(stateWest, stateWest.isFaceSturdy(level, west, Direction.EAST), Direction.EAST))
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
		}

		return direction.getAxis().getPlane() == Direction.Plane.HORIZONTAL
				? state.setValue(PROPERTY_BY_DIRECTION.get(direction), this.getConnectionType(neighborState, neighborState.isFaceSturdy(levelReader, neighborPos, direction.getOpposite()), direction.getOpposite()))
				: super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
	}

	public static boolean isExceptionForConnection(BlockState state) {
		return state.is(BlockTags.FENCES) || state.getBlock() instanceof PalisadeBlock || state.getBlock() instanceof SpikedPalisadeBlock;
	}

	// removed getAABBIndex

	public PalisadeConnection getConnectionType(BlockState state, boolean isSideSolid, Direction direction) {
		PalisadeConnection type = PalisadeConnection.NONE;
		if (state.is(ModTags.SPIKED_PALISADES) || state.getBlock() instanceof SpikedPalisadeBlock) {
			return PalisadeConnection.SPIKED;
		}
		boolean isFenceGateAligned = state.getBlock() instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(state, direction);
		if (state.is(ModTags.PALISADES) || state.is(BlockTags.WALLS) || state.getBlock() instanceof IronBarsBlock || (!isExceptionForConnection(state) && isSideSolid) || isFenceGateAligned) {
			return PalisadeConnection.FULL;
		}
		return type;
	}

	@Override
	protected @NotNull MapCodec<PalisadeBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(TYPE_NORTH, TYPE_EAST, TYPE_WEST, TYPE_SOUTH, WATERLOGGED);
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rot) {
		switch (rot) {
			case CLOCKWISE_180 -> {
				return state.setValue(TYPE_NORTH, state.getValue(TYPE_SOUTH)).setValue(TYPE_EAST, state.getValue(TYPE_WEST)).setValue(TYPE_SOUTH, state.getValue(TYPE_NORTH)).setValue(TYPE_WEST, state.getValue(TYPE_EAST));
			}
			case COUNTERCLOCKWISE_90 -> {
				return state.setValue(TYPE_NORTH, state.getValue(TYPE_EAST)).setValue(TYPE_EAST, state.getValue(TYPE_SOUTH)).setValue(TYPE_SOUTH, state.getValue(TYPE_WEST)).setValue(TYPE_WEST, state.getValue(TYPE_NORTH));
			}
			case CLOCKWISE_90 -> {
				return state.setValue(TYPE_NORTH, state.getValue(TYPE_WEST)).setValue(TYPE_EAST, state.getValue(TYPE_NORTH)).setValue(TYPE_SOUTH, state.getValue(TYPE_EAST)).setValue(TYPE_WEST, state.getValue(TYPE_SOUTH));
			}
			default -> {
				return state;
			}
		}
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT -> {
				return state.setValue(TYPE_NORTH, state.getValue(TYPE_SOUTH)).setValue(TYPE_SOUTH, state.getValue(TYPE_NORTH));
			}
			case FRONT_BACK -> {
				return state.setValue(TYPE_EAST, state.getValue(TYPE_WEST)).setValue(TYPE_WEST, state.getValue(TYPE_EAST));
			}
			default -> {
				return super.mirror(state, mirror);
			}
		}
	}
}
