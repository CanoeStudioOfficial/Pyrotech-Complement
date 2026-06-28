package com.canoestudios.pyrotechcomplement.block;

import com.canoestudios.pyrotechcomplement.Tags;
import com.codetaylor.mc.athenaeum.spi.BlockPartialBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockPrimitiveBloomeryMolten
    extends BlockPartialBase {

  public static final PropertyInteger LAYERS = PropertyInteger.create("layers", 1, 4);
  public static final PropertyBool LIT = PropertyBool.create("lit");

  private static final AxisAlignedBB[] BOUNDS = new AxisAlignedBB[] {
      new AxisAlignedBB(0, 0, 0, 1, 4 / 16.0, 1),
      new AxisAlignedBB(0, 0, 0, 1, 8 / 16.0, 1),
      new AxisAlignedBB(0, 0, 0, 1, 12 / 16.0, 1),
      new AxisAlignedBB(0, 0, 0, 1, 1, 1)
  };

  public BlockPrimitiveBloomeryMolten(String name) {

    super(Material.ROCK);
    this.setRegistryName(Tags.MOD_ID, name);
    this.setTranslationKey(Tags.MOD_ID + "." + name);
    this.setHardness(0.3f);
    this.setResistance(2.0f);
    this.setSoundType(SoundType.STONE);
    this.setLightOpacity(0);
    this.setDefaultState(this.blockState.getBaseState()
        .withProperty(LAYERS, 1)
        .withProperty(LIT, false));
  }

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    return BOUNDS[state.getValue(LAYERS) - 1];
  }

  @Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {

    return this.getBoundingBox(state, world, pos);
  }

  @Override
  public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {

    return state.getValue(LIT) ? 10 : 0;
  }

  @Nonnull
  @Override
  public BlockRenderLayer getRenderLayer() {

    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public void onEntityWalk(World world, BlockPos pos, Entity entity) {

    IBlockState state = world.getBlockState(pos);
    if (!world.isRemote
        && state.getBlock() == this
        && state.getValue(LIT)
        && entity instanceof EntityLivingBase
        && !entity.isImmuneToFire()) {
      entity.attackEntityFrom(net.minecraft.util.DamageSource.HOT_FLOOR, 1.0f);
      entity.setFire(2);
    }

    super.onEntityWalk(world, pos, entity);
  }

  @Override
  public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {

    if (!state.getValue(LIT) || !world.isAirBlock(pos.up())) {
      return;
    }

    double x = pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.45;
    double y = pos.getY() + state.getValue(LAYERS) * 0.25 + 0.02;
    double z = pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.45;

    if (rand.nextInt(8) == 0) {
      world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, 0, 0.02, 0);
    }

    world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0, 0.025, 0);
  }

  @Override
  public Item getItemDropped(IBlockState state, Random rand, int fortune) {

    return Items.AIR;
  }

  @Override
  public int quantityDropped(IBlockState state, int fortune, Random random) {

    return 0;
  }

  @Nonnull
  @Override
  public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {

    return ItemStack.EMPTY;
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {

    int layers = (meta & 3) + 1;
    return this.getDefaultState()
        .withProperty(LAYERS, layers)
        .withProperty(LIT, (meta & 4) != 0);
  }

  @Override
  public int getMetaFromState(IBlockState state) {

    int meta = state.getValue(LAYERS) - 1;
    if (state.getValue(LIT)) {
      meta |= 4;
    }
    return meta;
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {

    return new BlockStateContainer(this, LAYERS, LIT);
  }
}
