package com.canoestudios.pyrotechcomplement.plugin.jei.category;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.plugin.jei.wrapper.JEIRecipeWrapperForgingTable;
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

public class JEIRecipeCategoryForgingTable
    extends PyrotechRecipeCategory<JEIRecipeWrapperForgingTable> {

  public static final String UID = Tags.MOD_ID + ".forging_table";

  private static final int INPUT_X = 6;
  private static final int INPUT_Y = 24;
  private static final int SECONDARY_INPUT_X = 28;
  private static final int SECONDARY_INPUT_Y = 24;
  private static final int HAMMER_X = 0;
  private static final int HAMMER_Y = 0;
  private static final int ARROW_X = 54;
  private static final int ARROW_Y = 24;
  private static final int OUTPUT_X = 84;
  private static final int OUTPUT_Y = 24;

  private final IDrawableAnimated arrow;
  private final IDrawable background;
  private final IDrawable slotBackground;
  private final String title;

  public JEIRecipeCategoryForgingTable(IGuiHelper guiHelper) {

    ResourceLocation resourceLocation = new ResourceLocation("pyrotech", "textures/gui/jei9.png");
    IDrawableStatic arrowDrawable = guiHelper.createDrawable(resourceLocation, 82, 0, 24, 17);
    this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
    this.background = guiHelper.createBlankDrawable(106, 46);
    this.slotBackground = new OffsetDrawable(guiHelper.getSlotDrawable(), -1, -1);
    this.title = Translator.translateToLocal("gui." + Tags.MOD_ID + ".jei.category.forging_table");
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

    this.arrow.draw(minecraft, ARROW_X, ARROW_Y);
  }

  @Override
  public void setRecipe(IRecipeLayout recipeLayout, JEIRecipeWrapperForgingTable recipeWrapper, IIngredients ingredients) {

    super.setRecipe(recipeLayout, recipeWrapper, ingredients);

    IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();

    itemStacks.init(0, true, INPUT_X, INPUT_Y);
    itemStacks.setBackground(0, this.slotBackground);
    itemStacks.set(0, recipeWrapper.getInputStacks());

    if (recipeWrapper.hasSecondaryInput()) {
      itemStacks.init(1, true, SECONDARY_INPUT_X, SECONDARY_INPUT_Y);
      itemStacks.setBackground(1, this.slotBackground);
      itemStacks.set(1, recipeWrapper.getSecondaryInputStacks());
    }

    itemStacks.init(2, true, HAMMER_X, HAMMER_Y);
    itemStacks.setBackground(2, this.slotBackground);
    itemStacks.set(2, recipeWrapper.getHammerStacks());

    itemStacks.init(3, false, OUTPUT_X, OUTPUT_Y);
    itemStacks.setBackground(3, this.slotBackground);
    itemStacks.set(3, recipeWrapper.getOutput());
  }

  @Override
  protected int getOutputSlotIndex() {

    return 3;
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
