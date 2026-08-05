package com.canoestudios.pyrotechcomplement.client;

import com.canoestudios.pyrotechcomplement.block.BlockLoom;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import com.canoestudios.pyrotechcomplement.tile.TileLoom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * TFC-style loom contents renderer for 1.12.2. The block model supplies the
 * wooden frame; this renderer supplies the recipe-specific fabric strips and
 * the completed fabric panel.
 */
public class TileLoomRenderer
    extends TileEntitySpecialRenderer<TileLoom> {

  private static final float FABRIC_MIN_X = 3.0f / 16.0f;
  private static final float FABRIC_MAX_X = 13.0f / 16.0f;
  private static final float FABRIC_MIN_Y = 4.0f / 16.0f;
  private static final float FABRIC_MAX_Y = 13.0f / 16.0f;
  private static final float FABRIC_Z = 9.015f / 16.0f;

  @Override
  public void render(@Nonnull TileLoom tile, double x, double y, double z, float partialTicks,
      int destroyStage, float alpha) {

    if (tile.getWorld() == null
        || !(tile.getWorld().getBlockState(tile.getPos()).getBlock() instanceof BlockLoom)) {
      return;
    }

    LoomRecipe recipe = tile.getRecipe();
    ResourceLocation texture = recipe != null ? recipe.getTexture() : tile.getLastTexture();
    if (texture == null) {
      return;
    }

    TextureAtlasSprite sprite = this.getSprite(texture);
    if (sprite == null) {
      return;
    }

    EnumFacing facing = tile.getWorld().getBlockState(tile.getPos()).getValue(BlockLoom.FACING);
    float rotation = getModelRotation(facing);
    BufferBuilder buffer = Tessellator.getInstance().getBuffer();
    int light = tile.getWorld().getCombinedLight(tile.getPos(), 0);

    GlStateManager.pushMatrix();
    GlStateManager.translate(x + 0.5, y, z + 0.5);
    GlStateManager.rotate(rotation, 0.0f, 1.0f, 0.0f);
    GlStateManager.translate(-0.5, 0.0, -0.5);
    GlStateManager.enableBlend();
    GlStateManager.disableLighting();
    GlStateManager.disableCull();
    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
    Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

    buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
    if (recipe != null) {
      this.renderMaterial(buffer, sprite, tile, recipe, light, partialTicks);
      this.renderProduct(buffer, sprite, tile.getProgress() / (float) recipe.getSteps(), light);
    } else {
      this.renderProduct(buffer, sprite, 1.0f, light);
    }
    Tessellator.getInstance().draw();

    GlStateManager.enableCull();
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
  }

  private void renderMaterial(BufferBuilder buffer, TextureAtlasSprite sprite, TileLoom tile,
      LoomRecipe recipe, int light, float partialTicks) {

    int count = Math.min(tile.getInputCount(), recipe.getInputCount());
    if (count <= 0) {
      return;
    }

    float animation = tile.getAnimationOffset(partialTicks);
    float pieceWidth = (FABRIC_MAX_X - FABRIC_MIN_X) / recipe.getInputCount();
    for (int i = 0; i < count; i++) {
      float minX = FABRIC_MIN_X + pieceWidth * i;
      float maxX = minX + pieceWidth;
      float offset = (i & 1) == 0 ? animation : -animation;
      this.renderPlane(buffer, sprite, minX, FABRIC_MIN_Y, FABRIC_Z + offset,
          maxX, FABRIC_MAX_Y, FABRIC_Z + offset, light, 0.0f, 0.0f, 16.0f, 16.0f);
    }
  }

  private void renderProduct(BufferBuilder buffer, TextureAtlasSprite sprite, float progress, int light) {

    float clamped = Math.max(0.0f, Math.min(1.0f, progress));
    if (clamped <= 0.0f) {
      return;
    }

    float maxY = FABRIC_MIN_Y + (FABRIC_MAX_Y - FABRIC_MIN_Y) * clamped;
    this.renderPlane(buffer, sprite, FABRIC_MIN_X, FABRIC_MIN_Y, FABRIC_Z - 0.002f,
        FABRIC_MAX_X, maxY, FABRIC_Z - 0.002f, light, 0.0f, 16.0f * (1.0f - clamped), 16.0f, 16.0f);
  }

  private void renderPlane(BufferBuilder buffer, TextureAtlasSprite sprite,
      float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
      int packedLight, float minU, float minV, float maxU, float maxV) {

    float u1 = sprite.getInterpolatedU(minU);
    float u2 = sprite.getInterpolatedU(maxU);
    float v1 = sprite.getInterpolatedV(minV);
    float v2 = sprite.getInterpolatedV(maxV);
    int skyLight = packedLight >> 16 & 65535;
    int blockLight = packedLight & 65535;

    this.vertex(buffer, minX, maxY, minZ, u1, v1, skyLight, blockLight);
    this.vertex(buffer, maxX, maxY, maxZ, u2, v1, skyLight, blockLight);
    this.vertex(buffer, maxX, minY, maxZ, u2, v2, skyLight, blockLight);
    this.vertex(buffer, minX, minY, minZ, u1, v2, skyLight, blockLight);

    this.vertex(buffer, minX, minY, minZ, u1, v2, skyLight, blockLight);
    this.vertex(buffer, maxX, minY, maxZ, u2, v2, skyLight, blockLight);
    this.vertex(buffer, maxX, maxY, maxZ, u2, v1, skyLight, blockLight);
    this.vertex(buffer, minX, maxY, minZ, u1, v1, skyLight, blockLight);
  }

  private void vertex(BufferBuilder buffer, float x, float y, float z, float u, float v,
      int skyLight, int blockLight) {

    buffer.pos(x, y, z).tex(u, v).lightmap(skyLight, blockLight).color(255, 255, 255, 255).endVertex();
  }

  private TextureAtlasSprite getSprite(ResourceLocation texture) {

    String path = texture.getPath();
    if (path.startsWith("block/")) {
      path = "blocks/" + path.substring("block/".length());
    }
    return Minecraft.getMinecraft().getTextureMapBlocks()
        .getAtlasSprite(new ResourceLocation(texture.getNamespace(), path).toString());
  }

  private static float getModelRotation(EnumFacing facing) {

    switch (facing) {
      case EAST:
        return 90.0f;
      case SOUTH:
        return 180.0f;
      case WEST:
        return 270.0f;
      default:
        return 0.0f;
    }
  }
}
