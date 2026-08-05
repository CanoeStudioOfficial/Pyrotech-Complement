package com.canoestudios.pyrotechcomplement.recipe;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/** A small, registry-backed recipe type for the TFC-style hand quern. */
public class QuernRecipe extends IForgeRegistryEntry.Impl<QuernRecipe> {

  public static final int DEFAULT_GRINDING_TICKS = 90;

  @Nullable
  public static QuernRecipe getRecipe(ItemStack input) {

    if (input.isEmpty() || ModRecipes.QUERN_RECIPES == null) {
      return null;
    }

    for (QuernRecipe recipe : ModRecipes.QUERN_RECIPES) {
      if (recipe.matches(input)) {
        return recipe;
      }
    }

    return null;
  }

  public static boolean removeRecipes(Ingredient input) {

    if (ModRecipes.QUERN_RECIPES == null) {
      return false;
    }

    boolean removed = false;
    for (QuernRecipe recipe : ModRecipes.QUERN_RECIPES.getValuesCollection().toArray(new QuernRecipe[0])) {
      if (ingredientsOverlap(recipe.getInput(), input)) {
        ResourceLocation name = recipe.getRegistryName();
        if (name != null) {
          ModRecipes.QUERN_RECIPES.remove(name);
          removed = true;
        }
      }
    }
    return removed;
  }

  private static boolean ingredientsOverlap(Ingredient first, Ingredient second) {

    for (ItemStack stack : first.getMatchingStacks()) {
      if (second.apply(stack)) {
        return true;
      }
    }
    for (ItemStack stack : second.getMatchingStacks()) {
      if (first.apply(stack)) {
        return true;
      }
    }
    return false;
  }

  public static void removeAllRecipes() {

    if (ModRecipes.QUERN_RECIPES == null) {
      return;
    }

    for (QuernRecipe recipe : ModRecipes.QUERN_RECIPES.getValuesCollection().toArray(new QuernRecipe[0])) {
      ResourceLocation name = recipe.getRegistryName();
      if (name != null) {
        ModRecipes.QUERN_RECIPES.remove(name);
      }
    }
  }

  private final Ingredient input;
  private final ItemStack output;
  private final int grindingTicks;

  public QuernRecipe(ItemStack output, Ingredient input, int grindingTicks) {

    this.input = input;
    this.output = output.copy();
    this.grindingTicks = Math.max(1, grindingTicks);
  }

  public boolean matches(ItemStack stack) {

    return this.input.apply(stack);
  }

  public Ingredient getInput() {

    return this.input;
  }

  public ItemStack getOutput() {

    return this.output.copy();
  }

  public int getGrindingTicks() {

    return this.grindingTicks;
  }
}
