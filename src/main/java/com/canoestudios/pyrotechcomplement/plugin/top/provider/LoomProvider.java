package com.canoestudios.pyrotechcomplement.plugin.top.provider;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import com.canoestudios.pyrotechcomplement.tile.TileLoom;
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
import net.minecraft.world.World;

public class LoomProvider
    implements IProbeInfoProvider {

  @Override
  public String getID() {

    return Tags.MOD_ID + ":" + this.getClass().getName();
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

    BlockPos pos = data.getPos();
    TileEntity tileEntity = world.getTileEntity(pos);

    if (!(tileEntity instanceof TileLoom)) {
      return;
    }

    TileLoom tile = (TileLoom) tileEntity;
    ItemStack output = tile.getOutput();

    if (!output.isEmpty()) {
      probeInfo.item(output);
      return;
    }

    ItemStack input = tile.getInput();
    LoomRecipe recipe = tile.getRecipe();

    if (input.isEmpty()) {
      return;
    }

    IProbeInfo horizontal = probeInfo.horizontal();
    horizontal.item(input);

    if (recipe != null) {
      horizontal.progress(tile.getProgress(), recipe.getSteps(), new ProgressStyle().height(18).width(64).showText(false));
      horizontal.item(recipe.getOutput());
    }
  }
}
