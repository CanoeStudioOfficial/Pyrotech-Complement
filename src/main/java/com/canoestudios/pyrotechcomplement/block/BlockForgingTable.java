package com.canoestudios.pyrotechcomplement.block;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.init.ModCreativeTabs;
import com.canoestudios.pyrotechcomplement.tile.TileForgingTable;
import com.codetaylor.mc.athenaeum.spi.BlockPartialBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

public class BlockForgingTable
    extends BlockPartialBase
    implements ITileEntityProvider {

  public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

  private static final AxisAlignedBB SHAPE_X = new AxisAlignedBB(3 / 16.0, 0, 0, 13 / 16.0, 10 / 16.0, 1);
  private static final AxisAlignedBB SHAPE_Z = new AxisAlignedBB(0, 0, 3 / 16.0, 1, 10 / 16.0, 13 / 16.0);

  private final Style style;

  public BlockForgingTable(String name, Style style) {

    super(style.material);
    this.style = style;
    this.setRegistryName(Tags.MOD_ID, name);
    this.setTranslationKey(Tags.MOD_ID + "." + name);
    this.setCreativeTab(ModCreativeTabs.PYROTECH_COMPLEMENT);
    this.setHardness(style.hardness);
    this.setResistance(style.resistance);
    this.setSoundType(style.soundType);
    this.setHarvestLevel("pickaxe", style.harvestLevel);
    this.setLightOpacity(0);
    this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
  }

  public Style getStyle() {

    return this.style;
  }

  @Nonnull
  @Override
  public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

    return state.getValue(FACING).getAxis() == EnumFacing.Axis.Z ? SHAPE_Z : SHAPE_X;
  }

  @Nonnull
  @Override
  public BlockRenderLayer getRenderLayer() {

    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {

    TileEntity tileEntity = world.getTileEntity(pos);
    return tileEntity instanceof TileForgingTable
        && ((TileForgingTable) tileEntity).onRightClick(player, hand);
  }

  @Override
  public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {

    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity instanceof TileForgingTable) {
      ((TileForgingTable) tileEntity).dropContents();
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

    return new TileForgingTable();
  }

  @Override
  public TileEntity createNewTileEntity(@Nonnull World world, int meta) {

    return new TileForgingTable();
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

  public enum Style {
    GRANITE(Material.ROCK, SoundType.STONE, 3.0f, 5.0f, 0),
    OBSIDIAN(Material.ROCK, SoundType.STONE, 50.0f, 2000.0f, 0),
    IRONCLAD(Material.IRON, SoundType.METAL, 5.0f, 10.0f, 1);

    private final Material material;
    private final SoundType soundType;
    private final float hardness;
    private final float resistance;
    private final int harvestLevel;

    Style(Material material, SoundType soundType, float hardness, float resistance, int harvestLevel) {

      this.material = material;
      this.soundType = soundType;
      this.hardness = hardness;
      this.resistance = resistance;
      this.harvestLevel = harvestLevel;
    }
  }
}
