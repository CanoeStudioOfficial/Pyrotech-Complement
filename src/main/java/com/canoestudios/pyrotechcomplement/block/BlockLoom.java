package com.canoestudios.pyrotechcomplement.block;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.init.ModCreativeTabs;
import com.canoestudios.pyrotechcomplement.tile.TileLoom;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockLoom
    extends Block
    implements ITileEntityProvider {

  public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

  private static final AxisAlignedBB SHAPE_NORTH = new AxisAlignedBB(1 / 16.0, 0, 8 / 16.0, 15 / 16.0, 1, 14 / 16.0);
  private static final AxisAlignedBB SHAPE_SOUTH = new AxisAlignedBB(1 / 16.0, 0, 2 / 16.0, 15 / 16.0, 1, 8 / 16.0);
  private static final AxisAlignedBB SHAPE_WEST = new AxisAlignedBB(8 / 16.0, 0, 1 / 16.0, 14 / 16.0, 1, 15 / 16.0);
  private static final AxisAlignedBB SHAPE_EAST = new AxisAlignedBB(2 / 16.0, 0, 1 / 16.0, 8 / 16.0, 1, 15 / 16.0);

  private final Tier tier;

  public BlockLoom(String name, Tier tier) {

    super(Material.WOOD);
    this.tier = tier;
    this.setRegistryName(Tags.MOD_ID, name);
    this.setTranslationKey(Tags.MOD_ID + "." + name);
    this.setCreativeTab(ModCreativeTabs.PYROTECH_COMPLEMENT);
    this.setHardness(tier == Tier.CRUDE ? 0.5f : 1.0f);
    this.setResistance(5.0f);
    this.setSoundType(SoundType.WOOD);
    this.setHarvestLevel("axe", 0);
    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
  }

  public Tier getTier() {

    return this.tier;
  }

  @Override
  public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {

    return 150;
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    switch (state.getValue(FACING)) {
      case SOUTH:
        return SHAPE_SOUTH;
      case WEST:
        return SHAPE_WEST;
      case EAST:
        return SHAPE_EAST;
      case NORTH:
      default:
        return SHAPE_NORTH;
    }
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

    TileEntity tileEntity = world.getTileEntity(pos);
    return tileEntity instanceof TileLoom
        && ((TileLoom) tileEntity).onRightClick(player, hand);
  }

  @Override
  public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity instanceof TileLoom) {
      ((TileLoom) tileEntity).dropContents();
    }

    super.breakBlock(world, pos, state);
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {

    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(World world, IBlockState state) {

    return new TileLoom();
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int meta) {

    return new TileLoom();
  }

  @Nonnull
  @Override
  public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {

    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {

    return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
  }

  @Override
  public int getMetaFromState(IBlockState state) {

    return state.getValue(FACING).getHorizontalIndex();
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {

    return new BlockStateContainer(this, FACING);
  }

  public enum Tier {
    CRUDE,
    NORMAL
  }
}
