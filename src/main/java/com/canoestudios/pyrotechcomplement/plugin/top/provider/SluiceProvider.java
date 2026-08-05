package com.canoestudios.pyrotechcomplement.plugin.top.provider;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.block.BlockSluice;
import com.canoestudios.pyrotechcomplement.recipe.SluiceRecipe;
import com.canoestudios.pyrotechcomplement.tile.TileSluice;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class SluiceProvider
    implements IProbeInfoProvider {

  @Override
  public String getID() {

    return Tags.MOD_ID + ":" + this.getClass().getName();
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world,
      IBlockState blockState, IProbeHitData data) {

    TileSluice tile = getSluiceTile(world, data.getPos(), blockState);
    if (tile == null) {
      return;
    }

    probeInfo.text(I18n.translateToLocalFormatted(
        "gui." + Tags.MOD_ID + ".top.sluice.capacity",
        tile.getStoredInputCount(),
        tile.getCapacity()
    ));
    probeInfo.text(I18n.translateToLocal(
        "gui." + Tags.MOD_ID + ".top.sluice." + (tile.isWaterReady() ? "water_ready" : "water_missing")
    ));

    ItemStack input = tile.getFirstInput();
    if (input.isEmpty()) {
      return;
    }

    IProbeInfo horizontal = probeInfo.horizontal();
    horizontal.item(input);
    for (ItemStack output : SluiceRecipe.getPossibleOutputs(input)) {
      horizontal.item(output);
    }
    if (tile.isWaterReady()) {
      horizontal.progress(tile.getProgress(), tile.getProcessingTicks(),
          new ProgressStyle().height(18).width(64).showText(false));
    }
  }

  private static TileSluice getSluiceTile(World world, BlockPos pos, IBlockState state) {

    TileEntity tileEntity = world.getTileEntity(pos);
    if (tileEntity instanceof TileSluice) {
      return (TileSluice) tileEntity;
    }

    if (state.getBlock() instanceof BlockSluice && !state.getValue(BlockSluice.UPPER)) {
      BlockPos upperPos = pos.offset(state.getValue(BlockSluice.FACING).getOpposite());
      tileEntity = world.getTileEntity(upperPos);
      if (tileEntity instanceof TileSluice) {
        return (TileSluice) tileEntity;
      }
    }

    return null;
  }
}
