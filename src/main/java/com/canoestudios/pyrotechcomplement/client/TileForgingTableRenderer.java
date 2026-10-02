package com.canoestudios.pyrotechcomplement.client;

import com.canoestudios.pyrotechcomplement.block.BlockForgingTable;
import com.canoestudios.pyrotechcomplement.tile.TileForgingTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nonnull;

public class TileForgingTableRenderer
    extends TileEntitySpecialRenderer<TileForgingTable> {

  private static final float WORK_SURFACE_Y = 11 / 16.0f;
  private static final float INPUT_SCALE = 0.46f;
  private static final float OUTPUT_SCALE = 0.54f;

  // Coordinates are in the unrotated TFC-anvil model space. The model's long
  // axis is X, so the two inputs sit next to each other along that axis.
  private static final double INPUT_X = 0.42;
  private static final double SECONDARY_INPUT_X = 0.70;
  private static final double WORK_SURFACE_CENTER_X = 0.5625;
  private static final double CENTER_Z = 0.5;

  @Override
  public void render(@Nonnull TileForgingTable tile, double x, double y, double z, float partialTicks,
      int destroyStage, float alpha) {

    if (tile.getWorld() == null) {
      return;
    }

    IBlockState state = tile.getWorld().getBlockState(tile.getPos());
    if (!(state.getBlock() instanceof BlockForgingTable)) {
      return;
    }

    EnumFacing facing = state.getValue(BlockForgingTable.FACING);
    float rotation = getModelRotation(facing);

    // Keep placed stacks just above the TFC-style raised work surface.
    RenderHelper.enableStandardItemLighting();
    GlStateManager.enableRescaleNormal();
    this.renderStack(tile.getInput(), x, y, z, rotation, INPUT_X, WORK_SURFACE_Y, CENTER_Z, INPUT_SCALE);
    this.renderStack(tile.getSecondaryInput(), x, y, z, rotation, SECONDARY_INPUT_X, WORK_SURFACE_Y, CENTER_Z, INPUT_SCALE);
    this.renderStack(tile.getOutput(), x, y, z, rotation, WORK_SURFACE_CENTER_X, WORK_SURFACE_Y + 0.02, CENTER_Z, OUTPUT_SCALE);
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
      case NORTH:
        return 90;
      case EAST:
        return 180;
      case SOUTH:
        return 270;
      case WEST:
        return 0;
      default:
        return 0;
    }
  }
}
