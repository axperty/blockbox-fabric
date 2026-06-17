package com.axperty.blockbox.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import com.axperty.blockbox.common.tag.ModTags;


public class BrazierBlock extends Block implements SimpleWaterloggedBlock
{
	public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private final int fireDamage;

	private static final VoxelShape SHAPE_STANDING = Shapes.or(
			Block.box(4.0, 0.0, 4.0, 12.0, 6.0, 12.0),
			Block.box(1.0, 6.0, 1.0, 15.0, 12.0, 15.0)
	);
	private static final VoxelShape SHAPE_HANGING = Shapes.or(
			Block.box(1.0, 0.0, 1.0, 15.0, 6.0, 15.0)
	);

	protected static final VoxelShape SHAPE_FLAME = Block.box(1.0, 12.0, 1.0, 15.0, 16.0, 15.0);
	protected static final VoxelShape SHAPE_FLAME_HANGING = Block.box(1.0, 6.0, 1.0, 15.0, 12.0, 15.0);

	public BrazierBlock(int fireDamage, Properties properties) {
		super(properties);
		this.fireDamage = fireDamage;
		this.registerDefaultState(this.stateDefinition.any().setValue(HANGING, false).setValue(LIT, true).setValue(WATERLOGGED, false));
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (stack.getItem() instanceof FlintAndSteelItem || stack.getItem() instanceof FireChargeItem) {
			if (canLight(state)) {
				level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
				level.setBlock(pos, state.setValue(BlockStateProperties.LIT, true), 11);
				level.gameEvent(player, GameEvent.BLOCK_PLACE, pos);
				if (player != null) {
					stack.hurtAndBreak(1, player, hand);
				}
				return InteractionResult.SUCCESS;
			}
		}
		if (stack.getItem() instanceof ShovelItem) {
			if (state.getValue(LIT)) {
				level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
				level.setBlock(pos, state.setValue(BlockStateProperties.LIT, false), 11);
				if (player != null) {
					stack.hurtAndBreak(1, player, hand);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean flag) {
		if (state.getValue(LIT) && entity instanceof LivingEntity && isEntityTouchingFlame(entity, pos, state)) {
			entity.hurt(level.damageSources().campfire(), (float)this.fireDamage);
		}

		super.entityInside(state, level, pos, entity, applier, flag);
	}

	protected boolean isEntityTouchingFlame(Entity entity, BlockPos pos, BlockState state) {
		VoxelShape collisionShape = state.getValue(HANGING)
				? SHAPE_FLAME_HANGING.move(pos.getX(), pos.getY(), pos.getZ())
				: SHAPE_FLAME.move(pos.getX(), pos.getY(), pos.getZ());
		return Shapes.joinIsNotEmpty(collisionShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		boolean isFlooded = fluidState.getType() == Fluids.WATER;

		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis() == Direction.Axis.Y) {
				BlockState blockstate = this.defaultBlockState().setValue(HANGING, direction == Direction.UP);
				if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
					return blockstate.setValue(WATERLOGGED, isFlooded).setValue(LIT, !isFlooded);
				}
			}
		}
		return null;
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
		}

		return getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(levelReader, pos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return state.getValue(HANGING) ? SHAPE_HANGING : SHAPE_STANDING;
	}

	@Override
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
		if (!state.getValue(LIT)) {
			return;
		}
		if (random.nextInt(10) == 0) {
			level.playLocalSound(
					(double) pos.getX() + 0.5,
					(double) pos.getY() + 0.5,
					(double) pos.getZ() + 0.5,
					SoundEvents.CAMPFIRE_CRACKLE,
					SoundSource.BLOCKS,
					0.5F + random.nextFloat(),
					random.nextFloat() * 0.7F + 0.6F,
					false
			);
		}
		for (int i = 0; i < 3; i++) {
			double d0 = (double) pos.getX() + random.nextDouble();
			double d1 = (double) pos.getY() + random.nextDouble() * 0.5 + 0.5;
			double d2 = (double) pos.getZ() + random.nextDouble();
			level.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		if (!state.getValue(BlockStateProperties.WATERLOGGED) && fluidState.getType() == Fluids.WATER) {
			boolean flag = state.getValue(LIT);
			if (flag) {
				if (!level.isClientSide()) {
					level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
				}

				douse(null, level, pos, state);
			}

			level.setBlock(pos, state.setValue(WATERLOGGED, true).setValue(LIT, false), 3);
			level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
			return true;
		} else {
			return false;
		}
	}

	public static void douse(Entity entity, LevelAccessor level, BlockPos pos, BlockState state) {
		if (level.isClientSide()) {
			for (int i = 0; i < 20; i++) {
				makeParticles((Level) level, pos);
			}
		}

		BlockEntity blockentity = level.getBlockEntity(pos);

		level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
	}

	public static void makeParticles(Level level, BlockPos pos) {
		RandomSource randomsource = level.getRandom();
		level.addAlwaysVisibleParticle(
				ParticleTypes.LARGE_SMOKE,
				true,
				(double) pos.getX() + 0.5 + randomsource.nextDouble() / 3.0 * (double) (randomsource.nextBoolean() ? 1 : -1),
				(double) pos.getY() + randomsource.nextDouble() + randomsource.nextDouble(),
				(double) pos.getZ() + 0.5 + randomsource.nextDouble() / 3.0 * (double) (randomsource.nextBoolean() ? 1 : -1),
				0.0,
				0.07,
				0.0
		);
		level.addParticle(
				ParticleTypes.SMOKE,
				(double) pos.getX() + 0.5 + randomsource.nextDouble() / 4.0 * (double) (randomsource.nextBoolean() ? 1 : -1),
				(double) pos.getY() + 0.4,
				(double) pos.getZ() + 0.5 + randomsource.nextDouble() / 4.0 * (double) (randomsource.nextBoolean() ? 1 : -1),
				0.0,
				0.005,
				0.0
		);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HANGING, LIT, WATERLOGGED);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = getConnectedDirection(state).getOpposite();
		return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
	}

	protected static Direction getConnectedDirection(BlockState state) {
		return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	public static boolean canLight(BlockState state) {
		return state.is(ModTags.BRAZIERS, stateBase -> stateBase.hasProperty(WATERLOGGED) && stateBase.hasProperty(LIT))
				&& !state.getValue(WATERLOGGED)
				&& !state.getValue(LIT);
	}
}
