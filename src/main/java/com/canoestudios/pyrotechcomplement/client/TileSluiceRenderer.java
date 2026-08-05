package com.canoestudios.pyrotechcomplement.client;

import com.canoestudios.pyrotechcomplement.block.BlockSluice;
import com.canoestudios.pyrotechcomplement.tile.TileSluice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class TileSluiceRenderer
    extends TileEntitySpecialRenderer<TileSluice> {

  @Override
  public void render(@Nonnull TileSluice tile, double x, double y, double z, float partialTicks,
      int destroyStage, float alpha) {

    if (tile.getWorld() == null) {
      return;
    }

    if (!(tile.getWorld().getBlockState(tile.getPos()).getBlock() instanceof BlockSluice)
        || !tile.getWorld().getBlockState(tile.getPos()).getValue(BlockSluice.UPPER)) {
      return;
    }

    EnumFacing facing = tile.getFacing();
    float rotation = getModelRotation(facing);
    int rendered = 0;
    for (int slot = 0; slot < TileSluice.MAX_SOIL && rendered < 6; slot++) {
      ItemStack stack = tile.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        double localZ = 0.22 + rendered * 0.12;
        double localY = 0.67 + localZ * 0.22;
        this.renderStack(stack, x, y, z, rotation, 0.5, localY, localZ, 0.42f);
        rendered++;
      }
    }

  }

  private void renderStack(ItemStack stack, double x, double y, double z, float rotation,
      double localX, double localY, double localZ, float scale) {

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5, y, z + 0.5);
    GlStateManager.rotate(rotation, 0, 1, 0);
    GlStateManager.translate(localX - 0.5, localY, localZ - 0.5);
    GlStateManager.scale(scale, scale, scale);
    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);
    GlStateManager.popMatrix();
  }

  private static float getModelRotation(EnumFacing facing) {

    switch (facing) {
      case EAST:
        return 90;
      case SOUTH:
        return 180;
      case WEST:
        return 270;
      default:
        return 0;
    }
  }

}
