package com.canoestudios.pyrotechcomplement.block;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.init.ModCreativeTabs;
import com.canoestudios.pyrotechcomplement.tile.TileSluice;
import com.codetaylor.mc.athenaeum.spi.BlockPartialBase;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
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

public class BlockSluice
    extends BlockPartialBase
    implements ITileEntityProvider {

  public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  public static final PropertyBool UPPER = PropertyBool.create("upper");

  private static final AxisAlignedBB TOP_SHAPE = new AxisAlignedBB(0, 0, 0, 1, 1, 1);
  private static final AxisAlignedBB BOTTOM_SHAPE = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);

  private final Tier tier;

  public BlockSluice(String name, Tier tier) {

    super(Material.WOOD);
    this.tier = tier;
    this.setRegistryName(Tags.MOD_ID, name);
    this.setTranslationKey(Tags.MOD_ID + "." + name);
    this.setCreativeTab(ModCreativeTabs.PYROTECH_COMPLEMENT);
    this.setHardness(tier == Tier.CRUDE ? 0.5f : 2.0f);
    this.setResistance(3.0f);
    this.setSoundType(SoundType.WOOD);
    this.setHarvestLevel("axe", 0);
    this.setLightOpacity(0);
    this.setDefaultState(this.blockState.getBaseState()
        .withProperty(FACING, EnumFacing.NORTH)
        .withProperty(UPPER, true));
  }

  public Tier getTier() {

    return this.tier;
  }

  public static BlockPos getFluidOutputPos(IBlockState state, BlockPos pos) {

    return pos.offset(state.getValue(FACING), 2).down();
  }

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    return state.getValue(UPPER) ? TOP_SHAPE : BOTTOM_SHAPE;
  }

  @Override
  public boolean isOpaqueCube(IBlockState state) {

    return false;
  }

  @Override
  public boolean isFullCube(IBlockState state) {

    return false;
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
      EnumFacing facing, float hitX, float hitY, float hitZ) {

    BlockPos topPos = state.getValue(UPPER) ? pos : pos.offset(state.getValue(FACING).getOpposite());
    TileEntity tileEntity = world.getTileEntity(topPos);
    return tileEntity instanceof TileSluice && ((TileSluice) tileEntity).onRightClick(player, hand);
  }

  @Override
  public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {

    if (state.getValue(UPPER) && entity instanceof EntityItem) {
      EntityItem item = (EntityItem) entity;
      if (!item.getItem().isEmpty()) {
        item.motionX = 0;
        item.motionY = 0;
        item.motionZ = 0;
      }
    }
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

    super.onBlockPlacedBy(world, pos, state, placer, stack);
    if (!world.isRemote) {
      world.setBlockState(pos.offset(state.getValue(FACING)), state.withProperty(UPPER, false), 3);
    }
  }

  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {

    BlockPos otherPos = state.getValue(UPPER)
        ? pos.offset(state.getValue(FACING))
        : pos.offset(state.getValue(FACING).getOpposite());
    IBlockState otherState = world.getBlockState(otherPos);

    if (otherState.getBlock() != this
        || otherState.getValue(FACING) != state.getValue(FACING)
        || otherState.getValue(UPPER) == state.getValue(UPPER)) {
      if (!world.isRemote) {
        world.setBlockToAir(pos);
      }
    }
  }

  @Override
  public void breakBlock(World world, BlockPos pos, IBlockState state) {

    if (!world.isRemote) {
      if (state.getValue(UPPER)) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileSluice) {
          ((TileSluice) tileEntity).removeOutputWater();
          ((TileSluice) tileEntity).dropContents();
        }
      }

    }

    super.breakBlock(world, pos, state);
  }

  @Override
  public boolean hasTileEntity(IBlockState state) {

    return state.getValue(UPPER);
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(World world, IBlockState state) {

    return state.getValue(UPPER) ? new TileSluice() : null;
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int meta) {

    return new TileSluice();
  }

  @Nullable
  @Override
  public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing,
      float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {

    EnumFacing direction = placer.getHorizontalFacing();
    BlockPos lowerPos = pos.offset(direction);
    if (!isReplaceable(world, pos) || !isReplaceable(world, lowerPos)) {
      return null;
    }

    return this.getDefaultState().withProperty(FACING, direction).withProperty(UPPER, true);
  }

  private static boolean isReplaceable(World world, BlockPos pos) {

    return world.getBlockState(pos).getBlock().isReplaceable(world, pos);
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {

    return this.getDefaultState()
        .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
        .withProperty(UPPER, (meta & 4) != 0);
  }

  @Override
  public int getMetaFromState(IBlockState state) {

    return state.getValue(FACING).getHorizontalIndex() + (state.getValue(UPPER) ? 4 : 0);
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {

    return new BlockStateContainer(this, FACING, UPPER);
  }

  public enum Tier {
    CRUDE,
    NORMAL
  }
}
