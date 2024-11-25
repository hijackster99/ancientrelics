package com.hijackster99.ancientrelics.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;

public class OreBlock extends Block {

    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	
	public OreBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(5.0f, 10.0f));
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.EAST).setValue(AXIS, Direction.Axis.Y));
		
	}
	
	@Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite()).setValue(AXIS, context.getClickedFace().getAxis());
    }
	
//	@Override
//    protected BlockState rotate(BlockState state, Rotation rotation) {
//        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
//    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed blockstate.
     */
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
	
	@Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return rotatePillar(state, rot).setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    public static BlockState rotatePillar(BlockState state, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch ((Direction.Axis)state.getValue(AXIS)) {
                    case X:
                        return state.setValue(AXIS, Direction.Axis.Z);
                    case Z:
                        return state.setValue(AXIS, Direction.Axis.X);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, AXIS);
    }

}
