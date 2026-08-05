package com.canoestudios.pyrotechcomplement.client;

import com.canoestudios.pyrotechcomplement.block.BlockQuern;
import com.canoestudios.pyrotechcomplement.tile.TileQuern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/** Renders the installed TFC-style handstone and the quern contents in-world. */
public class TileQuernRenderer extends TileEntitySpecialRenderer<TileQuern> {

  @Override
  public void render(@Nonnull TileQuern tile, double x, double y, double z, float partialTicks,
      int destroyStage, float alpha) {

    if (tile.getWorld() == null || !(tile.getWorld().getBlockState(tile.getPos()).getBlock() instanceof BlockQuern)) {
      return;
    }

    RenderHelper.enableStandardItemLighting();
    GlStateManager.enableRescaleNormal();

    this.renderStack(tile.getHandstone(), x, y, z, 0.5, 0.705, 0.5, 1.25f,
        tile.getRotationAngle(partialTicks));
    this.renderStack(tile.getInput(), x, y, z, 0.5, 0.875, 0.5, 0.50f, 45.0f);

    ItemStack output = tile.getOutput();
    int amount = Math.min(output.getCount(), 64);
    for (int i = 0; i < amount; i++) {
      double localX;
      double localZ;
      int side = i / 16;
      int offset = i % 16;
      switch (side) {
        case 0:
          localX = 0.125;
          localZ = 0.125 + 0.046875 * offset;
          break;
        case 1:
          localX = 0.125 + 0.046875 * offset;
          localZ = 0.875;
          break;
        case 2:
          localX = 0.875;
          localZ = 0.875 - 0.046875 * offset;
          break;
        default:
          localX = 0.875 - 0.046875 * offset;
          localZ = 0.125;
          break;
      }
      this.renderStack(output, x, y, z, localX, 0.625, localZ, 0.125f, 75.0f);
    }

    GlStateManager.disableRescaleNormal();
    RenderHelper.disableStandardItemLighting();
  }

  private void renderStack(ItemStack stack, double x, double y, double z, double localX, double localY,
      double localZ, float scale, float rotation) {

    if (stack.isEmpty()) {
      return;
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5, y, z + 0.5);
    GlStateManager.translate(localX - 0.5, localY, localZ - 0.5);
    GlStateManager.rotate(rotation, 0, 1, 0);
    GlStateManager.scale(scale, scale, scale);
    Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.FIXED);
    GlStateManager.popMatrix();
  }
}
