package com.canoestudios.pyrotechcomplement.client;

import com.canoestudios.pyrotechcomplement.block.BlockSluice;
import com.canoestudios.pyrotechcomplement.tile.TileSluice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class TileSluiceRenderer
    extends TileEntitySpecialRenderer<TileSluice> {

  private static final ResourceLocation WATER_TEXTURE =
      new ResourceLocation("minecraft", "blocks/water_still");

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

    if (tile.hasWaterFlow()) {
      this.renderWaterFlow(tile, x, y, z, facing);
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

  private void renderWaterFlow(TileSluice tile, double x, double y, double z, EnumFacing facing) {

    World world = tile.getWorld();
    TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks()
        .getAtlasSprite(WATER_TEXTURE.toString());
    BlockPos colorPos = tile.getWaterOutputPos();
    int color = Minecraft.getMinecraft().getBlockColors()
        .colorMultiplier(Blocks.WATER.getDefaultState(), world, colorPos, 0);
    float red = (color >> 16 & 255) / 255.0f;
    float green = (color >> 8 & 255) / 255.0f;
    float blue = (color & 255) / 255.0f;
    int light = world.getCombinedLight(tile.getPos(), 0);
    int skyLight = light >> 16 & 65535;
    int blockLight = light & 65535;

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5, y, z + 0.5);
    GlStateManager.rotate(getModelRotation(facing), 0.0f, 1.0f, 0.0f);
    GlStateManager.translate(-0.5, 0.0, -0.5);
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    GlStateManager.disableLighting();
    GlStateManager.disableCull();
    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01f);
    GlStateManager.enablePolygonOffset();
    GlStateManager.doPolygonOffset(-1.0f, -1.0f);
    GlStateManager.depthMask(false);
    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

    BufferBuilder buffer = Tessellator.getInstance().getBuffer();
    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

    // The high end is fed from the block behind the upper half. The line then
    // runs down both halves and exits through the lower front.
    float highY = 1.053f;
    float highZ = 1.0f;
    float lowY = -0.13f;
    float lowZ = -2.45f;

    this.renderFlowSurface(buffer, sprite, 0.05f, 0.95f, highY, highZ, lowY, lowZ,
        red, green, blue, skyLight, blockLight);

    Tessellator.getInstance().draw();
    GlStateManager.depthMask(true);
    GlStateManager.disablePolygonOffset();
    GlStateManager.doPolygonOffset(0.0f, 0.0f);
    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }

  private void renderFlowSurface(BufferBuilder buffer, TextureAtlasSprite sprite,
      float minX, float maxX, float highY, float highZ, float lowY, float lowZ,
      float red, float green, float blue, int skyLight, int blockLight) {

    this.vertex(buffer, minX, highY, highZ, sprite.getMinU(), sprite.getMinV(),
        red, green, blue, skyLight, blockLight);
    this.vertex(buffer, minX, lowY, lowZ, sprite.getMinU(), sprite.getMaxV(),
        red, green, blue, skyLight, blockLight);
    this.vertex(buffer, maxX, lowY, lowZ, sprite.getMaxU(), sprite.getMaxV(),
        red, green, blue, skyLight, blockLight);
    this.vertex(buffer, maxX, highY, highZ, sprite.getMaxU(), sprite.getMinV(),
        red, green, blue, skyLight, blockLight);
  }

  private void vertex(BufferBuilder buffer, float x, float y, float z, float u, float v,
      float red, float green, float blue, int skyLight, int blockLight) {

    buffer.pos(x, y, z).tex(u, v).lightmap(skyLight, blockLight)
        .color(red, green, blue, 0.78f).endVertex();
  }
}
