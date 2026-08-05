package com.canoestudios.pyrotechcomplement.plugin.jei.wrapper;

import com.canoestudios.pyrotechcomplement.recipe.SluiceRecipe;
import com.codetaylor.mc.pyrotech.library.spi.plugin.jei.IPyrotechRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JEIRecipeWrapperSluice
    implements IPyrotechRecipeWrapper {

  private final List<List<ItemStack>> inputs;
  private final List<ItemStack> inputStacks;
  private final List<ItemStack> outputStacks;
  private final boolean automatic;
  private final boolean mineral;
  private final float chance;

  public JEIRecipeWrapperSluice(SluiceRecipe.JeiRecipe recipe) {

    this.inputStacks = recipe.getInputs();
    this.outputStacks = recipe.getOutputs();
    this.inputs = Collections.singletonList(this.inputStacks);
    this.automatic = recipe.isAutomatic();
    this.mineral = recipe.isMineral();
    this.chance = recipe.getChance();
  }

  @Override
  public void getIngredients(@Nonnull IIngredients ingredients) {

    ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
    ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(this.outputStacks));
  }

  public List<ItemStack> getInputStacks() {

    return copyStacks(this.inputStacks);
  }

  public List<ItemStack> getOutputStacks() {

    return copyStacks(this.outputStacks);
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    String time = I18n.translateToLocalFormatted(
        "gui.pyrotechcomplement.jei.sluice.time_short",
        SluiceRecipe.DEFAULT_PROCESSING_TICKS,
        SluiceRecipe.DEFAULT_PROCESSING_TICKS * 2
    );
    this.drawLine(minecraft, time, 30, recipeWidth);

    String result;
    if (this.automatic) {
      result = I18n.translateToLocal("gui.pyrotechcomplement.jei.sluice.random_short");
    } else {
      result = I18n.translateToLocalFormatted(
          "gui.pyrotechcomplement.jei.sluice.chance_short",
          (int) (this.chance * 100.0f)
      );
    }
    this.drawLine(minecraft, result, 40, recipeWidth);

    if (this.mineral) {
      String failure = I18n.translateToLocalFormatted(
          "gui.pyrotechcomplement.jei.sluice.crude_failure_short",
          25
      );
      this.drawLine(minecraft, failure, 50, recipeWidth);
    }
  }

  private void drawLine(Minecraft minecraft, String text, int y, int recipeWidth) {

    String clipped = minecraft.fontRenderer.trimStringToWidth(text, Math.max(1, recipeWidth - 8));
    minecraft.fontRenderer.drawString(clipped, 4, y, 0xFF404040);
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName() {

    return null;
  }

  private static List<ItemStack> copyStacks(List<ItemStack> stacks) {

    List<ItemStack> result = new ArrayList<>(stacks.size());
    for (ItemStack stack : stacks) {
      result.add(stack.copy());
    }
    return result;
  }
}
