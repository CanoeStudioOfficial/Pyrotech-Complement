package com.canoestudios.pyrotechcomplement.recipe;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.codetaylor.mc.athenaeum.recipe.IRecipeSingleOutput;
import com.codetaylor.mc.athenaeum.util.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class ForgingTableRecipe
    extends IForgeRegistryEntry.Impl<ForgingTableRecipe>
    implements IRecipeSingleOutput {

  @Nullable
  public static ForgingTableRecipe getRecipe(ItemStack input) {

    if (input.isEmpty() || ModRecipes.FORGING_TABLE_RECIPES == null) {
      return null;
    }

    ForgingTableRecipe secondaryRecipe = null;

    for (ForgingTableRecipe recipe : ModRecipes.FORGING_TABLE_RECIPES) {
      if (recipe.matchesInput(input)) {
        if (!recipe.hasSecondaryInput()) {
          return recipe;
        }

        if (secondaryRecipe == null) {
          secondaryRecipe = recipe;
        }
      }
    }

    return secondaryRecipe;
  }

  @Nullable
  public static ForgingTableRecipe getRecipeForSecondary(ItemStack input, ItemStack secondaryInput) {

    if (input.isEmpty() || secondaryInput.isEmpty() || ModRecipes.FORGING_TABLE_RECIPES == null) {
      return null;
    }

    for (ForgingTableRecipe recipe : ModRecipes.FORGING_TABLE_RECIPES) {
      if (recipe.matchesInput(input) && recipe.matchesSecondaryInput(secondaryInput)) {
        return recipe;
      }
    }

    return null;
  }

  @Nullable
  public static ForgingTableRecipe getRecipe(ItemStack input, ItemStack secondaryInput) {

    if (input.isEmpty() || ModRecipes.FORGING_TABLE_RECIPES == null) {
      return null;
    }

    for (ForgingTableRecipe recipe : ModRecipes.FORGING_TABLE_RECIPES) {
      if (recipe.matches(input, secondaryInput)) {
        return recipe;
      }
    }

    return null;
  }

  public static boolean removeRecipes(Ingredient output) {

    return RecipeHelper.removeRecipesByOutput(ModRecipes.FORGING_TABLE_RECIPES, output);
  }

  private final ItemStack output;
  private final Ingredient input;
  private final int inputCount;
  @Nullable
  private final Ingredient secondaryInput;
  private final int secondaryInputCount;
  private final int hits;

  public ForgingTableRecipe(ItemStack output, Ingredient input, int inputCount, int hits) {

    this(output, input, inputCount, null, 0, hits);
  }

  public ForgingTableRecipe(ItemStack output, Ingredient input, int inputCount, @Nullable Ingredient secondaryInput, int secondaryInputCount, int hits) {

    this.output = output.copy();
    this.input = input;
    this.inputCount = Math.max(1, inputCount);
    this.secondaryInput = secondaryInput;
    this.secondaryInputCount = secondaryInput == null ? 0 : Math.max(1, secondaryInputCount);
    this.hits = Math.max(1, hits);
  }

  public boolean matches(ItemStack input, ItemStack secondaryInput) {

    if (!this.matchesInput(input)) {
      return false;
    }

    if (!this.hasSecondaryInput()) {
      return secondaryInput.isEmpty();
    }

    return this.matchesSecondaryInput(secondaryInput);
  }

  public boolean matchesWithCounts(ItemStack input, ItemStack secondaryInput) {

    return this.matches(input, secondaryInput)
        && input.getCount() >= this.inputCount
        && (!this.hasSecondaryInput() || secondaryInput.getCount() >= this.secondaryInputCount);
  }

  public boolean matchesInput(ItemStack input) {

    return !input.isEmpty() && this.input.apply(input);
  }

  public boolean matchesSecondaryInput(ItemStack secondaryInput) {

    return this.secondaryInput != null
        && !secondaryInput.isEmpty()
        && this.secondaryInput.apply(secondaryInput);
  }

  public boolean hasSecondaryInput() {

    return this.secondaryInput != null;
  }

  public Ingredient getInput() {

    return this.input;
  }

  public int getInputCount() {

    return this.inputCount;
  }

  @Nullable
  public Ingredient getSecondaryInput() {

    return this.secondaryInput;
  }

  public int getSecondaryInputCount() {

    return this.secondaryInputCount;
  }

  public int getHits() {

    return this.hits;
  }

  @Override
  public ItemStack getOutput() {

    return this.output.copy();
  }
}
