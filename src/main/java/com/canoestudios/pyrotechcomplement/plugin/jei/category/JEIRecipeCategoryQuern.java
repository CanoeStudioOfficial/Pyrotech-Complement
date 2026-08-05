package com.canoestudios.pyrotechcomplement.plugin.jei.category;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.plugin.jei.wrapper.JEIRecipeWrapperQuern;
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

public class JEIRecipeCategoryQuern extends PyrotechRecipeCategory<JEIRecipeWrapperQuern> {

  public static final String UID = Tags.MOD_ID + ".quern";

  private final IDrawableAnimated arrow;
  private final IDrawable background;
  private final IDrawable slotBackground;
  private final String title;

  public JEIRecipeCategoryQuern(IGuiHelper guiHelper) {

    ResourceLocation texture = new ResourceLocation("pyrotech", "textures/gui/jei9.png");
    IDrawableStatic arrowDrawable = guiHelper.createDrawable(texture, 82, 0, 24, 17);
    this.arrow = guiHelper.createAnimatedDrawable(arrowDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
    this.background = guiHelper.createBlankDrawable(106, 30);
    this.slotBackground = guiHelper.getSlotDrawable();
    this.title = Translator.translateToLocal("gui." + Tags.MOD_ID + ".jei.category.quern");
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
  public void setRecipe(IRecipeLayout recipeLayout, JEIRecipeWrapperQuern recipeWrapper, IIngredients ingredients) {

    super.setRecipe(recipeLayout, recipeWrapper, ingredients);

    IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
    itemStacks.init(0, true, 5, 5);
    itemStacks.setBackground(0, this.slotBackground);
    itemStacks.set(0, recipeWrapper.getInputStacks());

    itemStacks.init(1, true, 27, 5);
    itemStacks.setBackground(1, this.slotBackground);
    itemStacks.set(1, recipeWrapper.getHandstoneStacks());

    itemStacks.init(2, false, 84, 5);
    itemStacks.setBackground(2, this.slotBackground);
    itemStacks.set(2, recipeWrapper.getOutput());
  }

  @Override
  protected int getOutputSlotIndex() {

    return 2;
  }
}
