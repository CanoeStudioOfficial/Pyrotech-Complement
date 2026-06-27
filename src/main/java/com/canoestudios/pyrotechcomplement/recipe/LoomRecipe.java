package com.canoestudios.pyrotechcomplement.recipe;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.codetaylor.mc.athenaeum.recipe.IRecipeSingleOutput;
import com.codetaylor.mc.athenaeum.util.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class LoomRecipe
    extends IForgeRegistryEntry.Impl<LoomRecipe>
    implements IRecipeSingleOutput {

  @Nullable
  public static LoomRecipe getRecipe(ItemStack input) {

    if (input.isEmpty() || ModRecipes.LOOM_RECIPES == null) {
      return null;
    }

    for (LoomRecipe recipe : ModRecipes.LOOM_RECIPES) {
      if (recipe.matches(input)) {
        return recipe;
      }
    }

    return null;
  }

  public static boolean removeRecipes(Ingredient output) {

    return RecipeHelper.removeRecipesByOutput(ModRecipes.LOOM_RECIPES, output);
  }

  private final ItemStack output;
  private final Ingredient input;
  private final int inputCount;
  private final int steps;
  private final ResourceLocation texture;

  public LoomRecipe(ItemStack output, Ingredient input, int inputCount, int steps, ResourceLocation texture) {

    this.output = output.copy();
    this.input = input;
    this.inputCount = Math.max(1, inputCount);
    this.steps = Math.max(1, steps);
    this.texture = texture;
  }

  public boolean matches(ItemStack input) {

    return this.input.apply(input);
  }

  public Ingredient getInput() {

    return this.input;
  }

  public int getInputCount() {

    return this.inputCount;
  }

  public int getSteps() {

    return this.steps;
  }

  public ResourceLocation getTexture() {

    return this.texture;
  }

  @Override
  public ItemStack getOutput() {

    return this.output.copy();
  }
}
