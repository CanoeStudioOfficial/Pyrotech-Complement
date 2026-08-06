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
    float rotation = getItemRotation();

    // TFC lays the inventory out as four columns along the two-block sluice.
    // The upper tile entity owns all 32 slots, so the last rows intentionally
    // continue across the lower half of the placed sluice.
    GlStateManager.pushMatrix();
    this.applySluiceTransform(facing, x, y, z);
    for (int slot = 0; slot < tile.getCapacity(); slot++) {
      ItemStack stack = tile.getStackInSlot(slot);
      if (!stack.isEmpty()) {
        int step = slot / 4;
        int across = slot % 4;
        float localX = 0.125f + 0.25f * across;
        float localY = 0.96875f - 0.0125f - 0.125f * step;
        float localZ = 0.15625f - 0.0125f + 0.25f * step;
        this.renderStack(stack, localX, localY, localZ, rotation);
      }
    }
    GlStateManager.popMatrix();

  }

  private void applySluiceTransform(EnumFacing facing, double x, double y, double z) {

    // This is the 1.12.2 equivalent of TFC's PoseStack setup. It keeps the
    // item grid aligned with the rotated sluice model and its two-block slope.
    switch (facing) {
      case NORTH:
        GlStateManager.translate(x + 1.0, y, z + 1.0);
        break;
      case WEST:
        GlStateManager.translate(x + 1.0, y, z);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        break;
      case EAST:
        GlStateManager.translate(x, y, z + 1.0);
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f);
        break;
      case SOUTH:
      default:
        GlStateManager.translate(x, y, z);
        break;
    }
    GlStateManager.rotate(facing.getHorizontalIndex() * 90.0f, 0.0f, 1.0f, 0.0f);
  }

  private void renderStack(ItemStack stack, float localX, float localY, float localZ, float rotation) {

    GlStateManager.pushMatrix();
    GlStateManager.translate(localX, localY, localZ);
    GlStateManager.scale(0.3f, 0.3f, 0.3f);
    GlStateManager.rotate(rotation, 0.0f, 1.0f, 0.0f);
    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
    GlStateManager.popMatrix();
  }

  private static float getItemRotation() {

    return (float) (360.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
  }

}
