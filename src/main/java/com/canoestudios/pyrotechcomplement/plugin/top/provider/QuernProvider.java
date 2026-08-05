package com.canoestudios.pyrotechcomplement.plugin.top.provider;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.tile.TileQuern;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class QuernProvider implements IProbeInfoProvider {

  @Override
  public String getID() {

    return Tags.MOD_ID + ":" + this.getClass().getName();
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world,
      IBlockState blockState, IProbeHitData data) {

    TileEntity tileEntity = world.getTileEntity(data.getPos());
    if (!(tileEntity instanceof TileQuern)) {
      return;
    }

    TileQuern tile = (TileQuern) tileEntity;
    if (!tile.hasHandstone()) {
      probeInfo.text(I18n.translateToLocal("gui.pyrotechcomplement.top.quern.no_handstone"));
      return;
    }

    IProbeInfo row = probeInfo.horizontal();
    row.item(tile.getHandstone());
    if (!tile.getInput().isEmpty()) {
      row.item(tile.getInput());
    }
    if (!tile.getOutput().isEmpty()) {
      probeInfo.horizontal().item(tile.getOutput());
    }

    if (tile.isGrinding()) {
      probeInfo.progress(tile.getProgress(), tile.getGrindingTicks(),
          new ProgressStyle().height(14).width(80).showText(true));
    } else if (tile.getRecipe() != null) {
      ItemStack output = tile.getRecipe().getOutput();
      probeInfo.horizontal().item(output);
    }
  }
}
