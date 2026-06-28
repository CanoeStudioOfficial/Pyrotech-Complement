package com.canoestudios.pyrotechcomplement.block;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.init.ModCreativeTabs;
import com.canoestudios.pyrotechcomplement.tile.TilePrimitiveBloomery;
import com.codetaylor.mc.athenaeum.spi.BlockPartialBase;
import com.codetaylor.mc.pyrotech.library.spi.block.IBlockIgnitableWithIgniterItem;
import com.codetaylor.mc.pyrotech.modules.ignition.item.ItemIgniterBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

public class BlockPrimitiveBloomery
    extends BlockPartialBase
    implements ITileEntityProvider, IBlockIgnitableWithIgniterItem {

  public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
  public static final PropertyBool LIT = PropertyBool.create("lit");
  public static final PropertyBool OPEN = PropertyBool.create("open");

  private static final AxisAlignedBB CLOSED_NORTH = new AxisAlignedBB(0, 0, 0, 1, 1, 2 / 16.0);
  private static final AxisAlignedBB CLOSED_SOUTH = new AxisAlignedBB(0, 0, 14 / 16.0, 1, 1, 1);
  private static final AxisAlignedBB CLOSED_WEST = new AxisAlignedBB(0, 0, 0, 2 / 16.0, 1, 1);
  private static final AxisAlignedBB CLOSED_EAST = new AxisAlignedBB(14 / 16.0, 0, 0, 1, 1, 1);
  private static final AxisAlignedBB OPEN_SHAPE = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

  public BlockPrimitiveBloomery(String name) {

    super(Material.ROCK);
    this.setRegistryName(Tags.MOD_ID, name);
    this.setTranslationKey(Tags.MOD_ID + "." + name);
    this.setCreativeTab(ModCreativeTabs.PYROTECH_COMPLEMENT);
    this.setHardness(2.5f);
    this.setResistance(10.0f);
    this.setSoundType(SoundType.STONE);
    this.setHarvestLevel("pickaxe", 0);
    this.setLightOpacity(0);
    this.setDefaultState(this.blockState.getBaseState()
        .withProperty(FACING, EnumFacing.NORTH)
        .withProperty(LIT, false)
        .withProperty(OPEN, false));
  }

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    if (state.getValue(OPEN)) {
      return OPEN_SHAPE;
    }

    switch (state.getValue(FACING)) {
      case SOUTH:
        return CLOSED_SOUTH;
      case WEST:
        return CLOSED_WEST;
      case EAST:
        return CLOSED_EAST;
      case NORTH:
      default:
        return CLOSED_NORTH;
    }
  }

  @Override
  public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

    return state.getValue(LIT) ? 12 : 0;
  }

  @Nonnull
  @Override
  public BlockRenderLayer getRenderLayer() {

    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

    TileEntity tileEntity = world.getTileEntity(pos);

    if (!(tileEntity instanceof TilePrimitiveBloomery)) {
      return false;
    }

    TilePrimitiveBloomery tile = (TilePrimitiveBloomery) tileEntity;

    if (tile.onRightClick(player, hand)) {
      return true;
    }

    ItemStack heldItem = player.getHeldItem(hand);
    if (!state.getValue(LIT) && this.isIgniter(heldItem)) {
      boolean lit = tile.ignite();

      if (lit && !world.isRemote) {
        this.consumeIgniter(player, heldItem);
      }

      return lit;
    }

    if (!state.getValue(LIT) && heldItem.isEmpty()) {
      if (!world.isRemote) {
        boolean open = !state.getValue(OPEN);
        world.setBlockState(pos, state.withProperty(OPEN, open), 3);
        world.playSound(null, pos, open ? SoundEvents.BLOCK_FENCE_GATE_OPEN : SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 0.8f, 0.9f + world.rand.nextFloat() * 0.2f);
      }
      return true;
    }

    return false;
  }

  @Override
  public void igniteWithIgniterItem(World world, BlockPos pos, IBlockState blockState, EnumFacing facing) {

    if (blockState.getValue(LIT)) {
      return;
    }

    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity instanceof TilePrimitiveBloomery) {
      ((TilePrimitiveBloomery) tileEntity).ignite();
    }
  }

  @Override
  public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity instanceof TilePrimitiveBloomery) {
      ((TilePrimitiveBloomery) tileEntity).dropContents();
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

    return new TilePrimitiveBloomery();
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int meta) {

    return new TilePrimitiveBloomery();
  }

  @Nonnull
  @Override
  public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {

    return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {

    return this.getDefaultState()
        .withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3))
        .withProperty(LIT, (meta & 4) != 0)
        .withProperty(OPEN, (meta & 8) != 0);
  }

  @Override
  public int getMetaFromState(IBlockState state) {

    int meta = state.getValue(FACING).getHorizontalIndex();
    if (state.getValue(LIT)) {
      meta |= 4;
    }
    if (state.getValue(OPEN)) {
      meta |= 8;
    }
    return meta;
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {

    return new BlockStateContainer(this, FACING, LIT, OPEN);
  }

  @Override
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

    if (!state.getValue(LIT)) {
      return;
    }

    if (rand.nextDouble() < 0.1) {
      world.playSound(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);
    }

    EnumFacing facing = state.getValue(FACING);
    double x = pos.getX() + 0.5 + facing.getXOffset() * 0.48;
    double y = pos.getY() + 0.35 + rand.nextDouble() * 0.35;
    double z = pos.getZ() + 0.5 + facing.getZOffset() * 0.48;
    double side = rand.nextDouble() * 0.45 - 0.225;

    if (facing.getAxis() == EnumFacing.Axis.X) {
      z += side;
    } else {
      x += side;
    }

    world.spawnParticle(net.minecraft.util.EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0.02, 0);
    world.spawnParticle(net.minecraft.util.EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
  }

  private boolean isIgniter(ItemStack stack) {

    return !stack.isEmpty()
        && (stack.getItem() == Items.FLINT_AND_STEEL
        || stack.getItem() == Items.FIRE_CHARGE
        || stack.getItem() instanceof ItemIgniterBase);
  }

  private void consumeIgniter(EntityPlayer player, ItemStack stack) {

    if (player.capabilities.isCreativeMode) {
      return;
    }

    if (stack.getItem() == Items.FIRE_CHARGE) {
      stack.shrink(1);
      return;
    }

    if (stack.isItemStackDamageable()) {
      stack.damageItem(1, player);
    }
  }
}
