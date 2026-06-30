package com.canoestudios.pyrotechcomplement.recipe;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipeBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimitiveBloomeryRecipe
    extends BloomeryRecipeBase<PrimitiveBloomeryRecipe> {

  @Nullable
  public static PrimitiveBloomeryRecipe getRecipe(ItemStack input, ItemStack fuel) {

    if (input.isEmpty() || fuel.isEmpty() || ModRecipes.PRIMITIVE_BLOOMERY_RECIPES == null) {
      return null;
    }

    for (PrimitiveBloomeryRecipe recipe : ModRecipes.PRIMITIVE_BLOOMERY_RECIPES) {
      if (recipe.matches(input, fuel)) {
        return recipe;
      }
    }

    return null;
  }

  @Nullable
  public static PrimitiveBloomeryRecipe getRecipeForInput(ItemStack input) {

    if (input.isEmpty() || ModRecipes.PRIMITIVE_BLOOMERY_RECIPES == null) {
      return null;
    }

    for (PrimitiveBloomeryRecipe recipe : ModRecipes.PRIMITIVE_BLOOMERY_RECIPES) {
      if (recipe.matchesInput(input)) {
        return recipe;
      }
    }

    return null;
  }

  @Nullable
  public static PrimitiveBloomeryRecipe getRecipeForFuel(ItemStack fuel) {

    if (fuel.isEmpty() || ModRecipes.PRIMITIVE_BLOOMERY_RECIPES == null) {
      return null;
    }

    for (PrimitiveBloomeryRecipe recipe : ModRecipes.PRIMITIVE_BLOOMERY_RECIPES) {
      if (recipe.matchesFuel(fuel)) {
        return recipe;
      }
    }

    return null;
  }

  public static boolean removeRecipes(Ingredient output) {

    if (ModRecipes.PRIMITIVE_BLOOMERY_RECIPES == null) {
      return false;
    }

    List<ResourceLocation> toRemove = new ArrayList<>();

    for (PrimitiveBloomeryRecipe recipe : ModRecipes.PRIMITIVE_BLOOMERY_RECIPES) {
      if (output.apply(recipe.getOutput())) {
        ResourceLocation registryName = recipe.getRegistryName();

        if (registryName != null) {
          toRemove.add(registryName);
        }
      }
    }

    for (ResourceLocation registryName : toRemove) {
      ModRecipes.PRIMITIVE_BLOOMERY_RECIPES.remove(registryName);
      ModRecipes.removePrimitiveBloomeryAnvilRecipe(registryName);
    }

    return !toRemove.isEmpty();
  }

  private static final AnvilRecipe.EnumTier[] DEFAULT_ANVIL_TIERS = AnvilRecipe.EnumTier.values();
  private static final FailureItem[] EMPTY_FAILURE_ITEMS = new FailureItem[0];

  private final int inputCount;
  private final Ingredient fuel;
  private final int fuelCount;
  private final boolean generatedBloomOutput;

  public PrimitiveBloomeryRecipe(ItemStack output, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks) {

    this(output, input, inputCount, fuel, fuelCount, burnTimeTicks, false, 0, 0, 0, 0, 0, ItemStack.EMPTY, EMPTY_FAILURE_ITEMS, DEFAULT_ANVIL_TIERS, null);
  }

  public PrimitiveBloomeryRecipe(
      ItemStack output,
      Ingredient input,
      int inputCount,
      Ingredient fuel,
      int fuelCount,
      int burnTimeTicks,
      int bloomYieldMin,
      int bloomYieldMax,
      float experience,
      float failureChance,
      int slagCount,
      ItemStack slagItem,
      FailureItem[] failureItems,
      AnvilRecipe.EnumTier[] anvilTiers,
      @Nullable String langKey
  ) {

    this(output, input, inputCount, fuel, fuelCount, burnTimeTicks, true, bloomYieldMin, bloomYieldMax, experience, failureChance, slagCount, slagItem, failureItems, anvilTiers, langKey);
  }

  private PrimitiveBloomeryRecipe(
      ItemStack output,
      Ingredient input,
      int inputCount,
      Ingredient fuel,
      int fuelCount,
      int burnTimeTicks,
      boolean generatedBloomOutput,
      int bloomYieldMin,
      int bloomYieldMax,
      float experience,
      float failureChance,
      int slagCount,
      ItemStack slagItem,
      FailureItem[] failureItems,
      AnvilRecipe.EnumTier[] anvilTiers,
      @Nullable String langKey
  ) {

    super(
        input,
        output,
        Math.max(20, burnTimeTicks),
        Math.max(0, experience),
        failureChance,
        generatedBloomOutput ? Math.max(1, Math.min(bloomYieldMin, bloomYieldMax)) : 0,
        generatedBloomOutput ? Math.max(Math.max(1, Math.min(bloomYieldMin, bloomYieldMax)), bloomYieldMax) : 0,
        Math.max(0, slagCount),
        failureItems,
        slagItem,
        anvilTiers,
        langKey
    );

    this.inputCount = Math.max(1, inputCount);
    this.fuel = fuel;
    this.fuelCount = Math.max(1, fuelCount);
    this.generatedBloomOutput = generatedBloomOutput;
  }

  public boolean matches(ItemStack input, ItemStack fuel) {

    return this.matchesInput(input) && this.matchesFuel(fuel);
  }

  public boolean matchesWithCounts(ItemStack input, ItemStack fuel) {

    return this.matches(input, fuel)
        && input.getCount() >= this.inputCount
        && fuel.getCount() >= this.fuelCount;
  }

  public boolean matchesInput(ItemStack input) {

    return !input.isEmpty() && this.input.apply(input);
  }

  public boolean matchesFuel(ItemStack fuel) {

    return !fuel.isEmpty() && this.fuel.apply(fuel);
  }

  public int getBatches(ItemStack input, ItemStack fuel) {

    if (!this.matches(input, fuel)) {
      return 0;
    }

    return Math.min(input.getCount() / this.inputCount, fuel.getCount() / this.fuelCount);
  }

  @Override
  public Ingredient getInput() {

    return this.input;
  }

  public int getInputCount() {

    return this.inputCount;
  }

  public Ingredient getFuel() {

    return this.fuel;
  }

  public int getFuelCount() {

    return this.fuelCount;
  }

  public int getBurnTimeTicks() {

    return this.burnTimeTicks;
  }

  public boolean hasGeneratedBloomOutput() {

    return this.generatedBloomOutput;
  }

  @Nullable
  public String getBloomRecipeId() {

    ResourceLocation registryName = this.getRegistryName();
    return registryName == null ? null : registryName.toString();
  }

  @Nullable
  public String getBloomLangKey() {

    return this.getLangKey();
  }

  public ItemStack getPrimitiveBloomeryOutput(int batches, @Nullable Random random) {

    int batchCount = Math.max(1, batches);

    if (this.generatedBloomOutput) {
      return this.getUniqueBloomFromOutput(batchCount);
    }

    ItemStack result = this.getOutput();
    int count = Math.max(1, result.getCount() * batchCount);
    result.setCount(Math.min(result.getMaxStackSize(), count));
    return result;
  }

  public ItemStack getPrimitiveBloomeryOutput() {

    if (this.generatedBloomOutput) {
      return this.getOutputBloom();
    }

    return this.getOutput();
  }
}
