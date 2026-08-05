package com.canoestudios.pyrotechcomplement.plugin.jei.category;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.plugin.jei.wrapper.JEIRecipeWrapperSluice;
import com.codetaylor.mc.pyrotech.library.spi.plugin.jei.PyrotechRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class JEIRecipeCategorySluice
    extends PyrotechRecipeCategory<JEIRecipeWrapperSluice> {

  public static final String UID = Tags.MOD_ID + ".sluice";

  private final IDrawableAnimated arrow;
  private final IDrawable background;
  private final IDrawable slotBackground;
  private final String title;

  public JEIRecipeCategorySluice(IGuiHelper guiHelper) {

    ResourceLocation resourceLocation = new ResourceLocation("pyrotech", "textures/gui/jei9.png");
    IDrawableStatic arrowDrawable = guiHelper.createDrawable(resourceLocation, 82, 0, 24, 17);
    this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
    this.background = guiHelper.createBlankDrawable(116, 62);
    this.slotBackground = new OffsetDrawable(guiHelper.getSlotDrawable(), -1, -1);
    this.title = Translator.translateToLocal("gui." + Tags.MOD_ID + ".jei.category.sluice");
  }

  @Nonnull
  @Override
  public String getUid() {

    return UID;
  }

  @Nonnull
  @Override
  public String getTitle() {

    return this.title;
  }

  @Nonnull
  @Override
  public String getModName() {

    return Tags.MOD_ID;
  }

  @Nonnull
  @Override
  public IDrawable getBackground() {

    return this.background;
  }

  @Override
  public void drawExtras(Minecraft minecraft) {

    this.arrow.draw(minecraft, 48, 5);
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, JEIRecipeWrapperSluice recipeWrapper, IIngredients ingredients) {

    super.setRecipe(recipeLayout, recipeWrapper, ingredients);

    IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
    itemStacks.init(0, true, 8, 5);
    itemStacks.setBackground(0, this.slotBackground);
    itemStacks.set(0, recipeWrapper.getInputStacks());

    itemStacks.init(1, false, 84, 5);
    itemStacks.setBackground(1, this.slotBackground);
    itemStacks.set(1, recipeWrapper.getOutputStacks());
  }

  @Override
  protected int getOutputSlotIndex() {

    return 1;
  }

  private static class OffsetDrawable
      implements IDrawable {

    private final IDrawable drawable;
    private final int xOffset;
    private final int yOffset;

    private OffsetDrawable(IDrawable drawable, int xOffset, int yOffset) {

      this.drawable = drawable;
      this.xOffset = xOffset;
      this.yOffset = yOffset;
    }

    @Override
    public int getWidth() {

      return this.drawable.getWidth();
    }

    @Override
    public int getHeight() {

      return this.drawable.getHeight();
    }

    @Override
    public void draw(Minecraft minecraft, int xOffset, int yOffset) {

      this.drawable.draw(minecraft, xOffset + this.xOffset, yOffset + this.yOffset);
    }
  }
}
