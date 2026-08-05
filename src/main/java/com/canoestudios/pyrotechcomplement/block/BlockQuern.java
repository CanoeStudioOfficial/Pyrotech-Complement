package com.canoestudios.pyrotechcomplement.block;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.init.ModCreativeTabs;
import com.canoestudios.pyrotechcomplement.tile.TileQuern;
import com.codetaylor.mc.athenaeum.spi.BlockPartialBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockQuern extends BlockPartialBase implements ITileEntityProvider {

  public static final PropertyBool HAS_HANDSTONE = PropertyBool.create("has_handstone");

  private static final AxisAlignedBB BASE_SHAPE = new AxisAlignedBB(0, 0, 0, 1, 10 / 16.0, 1);
  private static final AxisAlignedBB FULL_SHAPE = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

  public BlockQuern(String name) {

    super(Material.ROCK);
    this.setRegistryName(Tags.MOD_ID, name);
    this.setTranslationKey(Tags.MOD_ID + "." + name);
    this.setCreativeTab(ModCreativeTabs.PYROTECH_COMPLEMENT);
    this.setHardness(2.0f);
    this.setResistance(10.0f);
    this.setSoundType(SoundType.STONE);
    this.setHarvestLevel("pickaxe", 0);
    this.setDefaultState(this.blockState.getBaseState().withProperty(HAS_HANDSTONE, false));
  }

  @Nonnull
  @Override
  public BlockRenderLayer getRenderLayer() {

    return BlockRenderLayer.SOLID;
  }

  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    return state.getValue(HAS_HANDSTONE) ? FULL_SHAPE : BASE_SHAPE;
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
      EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

    TileEntity tileEntity = world.getTileEntity(pos);
    return tileEntity instanceof TileQuern
        && ((TileQuern) tileEntity).onRightClick(player, hand, hitX, hitY, hitZ);
  }

  @Override
  public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {

    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity instanceof TileQuern) {
      ((TileQuern) tileEntity).dropContents();
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

    return new TileQuern();
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int meta) {

    return new TileQuern();
  }

  @Nonnull
  @Override
  protected BlockStateContainer createBlockState() {

    return new BlockStateContainer(this, HAS_HANDSTONE);
  }

  @Nonnull
  @Override
  public IBlockState getStateFromMeta(int meta) {

    return this.getDefaultState().withProperty(HAS_HANDSTONE, (meta & 1) != 0);
  }

  @Override
  public int getMetaFromState(IBlockState state) {

    return state.getValue(HAS_HANDSTONE) ? 1 : 0;
  }

  @Nonnull
  @Override
  public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing,
      float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand) {

    return this.getDefaultState();
  }
}
