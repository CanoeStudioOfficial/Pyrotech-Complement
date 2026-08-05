package com.canoestudios.pyrotechcomplement.client;

import com.canoestudios.pyrotechcomplement.block.BlockForgingTable;
import com.canoestudios.pyrotechcomplement.tile.TileForgingTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class TileForgingTableRenderer
    extends TileEntitySpecialRenderer<TileForgingTable> {

  @Override
  public void render(@Nonnull TileForgingTable tile, double x, double y, double z, float partialTicks,
      int destroyStage, float alpha) {

    if (tile.getWorld() == null) {
      return;
    }

    if (!(tile.getWorld().getBlockState(tile.getPos()).getBlock() instanceof BlockForgingTable)) {
      return;
    }

    EnumFacing facing = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockForgingTable.FACING);
    float rotation = getModelRotation(facing);

    // Keep placed stacks just above the Pyrotech-style recessed work surface.
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enableRescaleNormal();
    this.renderStack(tile.getInput(), x, y, z, rotation, 0.5, 0.69, 0.31, 0.46f);
    this.renderStack(tile.getSecondaryInput(), x, y, z, rotation, 0.5, 0.69, 0.69, 0.46f);
    this.renderStack(tile.getOutput(), x, y, z, rotation, 0.5, 0.71, 0.50, 0.54f);
    GlStateManager.disableRescaleNormal();
    RenderHelper.disableStandardItemLighting();
  }

  private void renderStack(ItemStack stack, double x, double y, double z, float rotation,
      double localX, double localY, double localZ, float scale) {

    if (stack.isEmpty()) {
      return;
    }

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
