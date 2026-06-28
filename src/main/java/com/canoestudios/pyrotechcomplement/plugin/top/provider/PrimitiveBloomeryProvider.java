package com.canoestudios.pyrotechcomplement.plugin.top.provider;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.recipe.PrimitiveBloomeryRecipe;
import com.canoestudios.pyrotechcomplement.tile.TilePrimitiveBloomery;
import com.codetaylor.mc.athenaeum.util.StringHelper;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class PrimitiveBloomeryProvider
    implements IProbeInfoProvider {

  @Override
  public String getID() {

    return Tags.MOD_ID + ":" + this.getClass().getName();
  }

  @Override
  public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {

    BlockPos pos = data.getPos();
    TileEntity tileEntity = world.getTileEntity(pos);

    if (!(tileEntity instanceof TilePrimitiveBloomery)) {
      return;
    }

    TilePrimitiveBloomery tile = (TilePrimitiveBloomery) tileEntity;

    if (!tile.isFormed()) {
      probeInfo.text(TextFormatting.RED + I18n.translateToLocal("gui." + Tags.MOD_ID + ".top.primitive_bloomery.unformed"));
      return;
    }

    probeInfo.text(I18n.translateToLocalFormatted("gui." + Tags.MOD_ID + ".top.primitive_bloomery.capacity", tile.getStoredInputCount(), tile.getCapacity()));

    ItemStack output = tile.getOutput();
    if (!output.isEmpty()) {
      probeInfo.item(output);
      return;
    }

    IProbeInfo horizontal = probeInfo.horizontal();
    ItemStack input = tile.getInput();
    ItemStack fuel = tile.getFuel();

    if (!input.isEmpty()) {
      horizontal.item(input);
    }

    if (!fuel.isEmpty()) {
      horizontal.item(fuel);
    }

    PrimitiveBloomeryRecipe recipe = tile.getRecipe();
    if (recipe != null && tile.getBurnTimeTicks() > 0) {
      horizontal.progress(tile.getProgress(), tile.getBurnTimeTicks(), new ProgressStyle().height(18).width(64).showText(false));
      horizontal.item(recipe.getOutput());
      probeInfo.text(I18n.translateToLocalFormatted(
          "gui." + Tags.MOD_ID + ".top.primitive_bloomery.time",
          StringHelper.ticksToHMS(tile.getProgress()),
          StringHelper.ticksToHMS(tile.getBurnTimeTicks())
      ));
    }
  }
}
