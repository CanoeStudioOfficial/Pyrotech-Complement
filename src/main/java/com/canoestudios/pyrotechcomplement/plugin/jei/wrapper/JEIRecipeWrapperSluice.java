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
        "gui.pyrotechcomplement.jei.sluice.time",
        SluiceRecipe.DEFAULT_PROCESSING_TICKS,
        SluiceRecipe.DEFAULT_PROCESSING_TICKS * 2
    );
    minecraft.fontRenderer.drawString(time, 4, 30, 0xFF404040);

    String result;
    if (this.automatic) {
      result = I18n.translateToLocal("gui.pyrotechcomplement.jei.sluice.random");
    } else {
      result = I18n.translateToLocalFormatted(
          "gui.pyrotechcomplement.jei.sluice.chance",
          (int) (this.chance * 100.0f)
      );
    }
    minecraft.fontRenderer.drawString(result, 4, 40, 0xFF404040);

    if (this.mineral) {
      String failure = I18n.translateToLocalFormatted(
          "gui.pyrotechcomplement.jei.sluice.crude_failure",
          25
      );
      minecraft.fontRenderer.drawString(failure, 4, 50, 0xFF404040);
    }
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
