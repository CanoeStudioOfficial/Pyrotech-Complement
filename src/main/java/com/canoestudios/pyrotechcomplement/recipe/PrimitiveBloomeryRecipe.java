package com.canoestudios.pyrotechcomplement.recipe;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.codetaylor.mc.athenaeum.recipe.IRecipeSingleOutput;
import com.codetaylor.mc.athenaeum.util.RecipeHelper;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.ModuleTechBloomery;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.util.BloomHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Random;

public class PrimitiveBloomeryRecipe
    extends IForgeRegistryEntry.Impl<PrimitiveBloomeryRecipe>
    implements IRecipeSingleOutput {

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

    return RecipeHelper.removeRecipesByOutput(ModRecipes.PRIMITIVE_BLOOMERY_RECIPES, output);
  }

  private final ItemStack output;
  private final Ingredient input;
  private final int inputCount;
  private final Ingredient fuel;
  private final int fuelCount;
  private final int burnTimeTicks;
  private final boolean generatedBloomOutput;
  private final int bloomYieldMin;
  private final int bloomYieldMax;
  private final float experience;
  @Nullable
  private final String bloomRecipeId;
  @Nullable
  private final String bloomLangKey;

  public PrimitiveBloomeryRecipe(ItemStack output, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks) {

    this(output, input, inputCount, fuel, fuelCount, burnTimeTicks, false, 0, 0, 0, null, null);
  }

  public PrimitiveBloomeryRecipe(Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks, int bloomYieldMin, int bloomYieldMax, float experience, @Nullable String bloomRecipeId, @Nullable String bloomLangKey) {

    this(ItemStack.EMPTY, input, inputCount, fuel, fuelCount, burnTimeTicks, true, bloomYieldMin, bloomYieldMax, experience, bloomRecipeId, bloomLangKey);
  }

  private PrimitiveBloomeryRecipe(ItemStack output, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks, boolean generatedBloomOutput, int bloomYieldMin, int bloomYieldMax, float experience, @Nullable String bloomRecipeId, @Nullable String bloomLangKey) {

    this.output = output.copy();
    this.input = input;
    this.inputCount = Math.max(1, inputCount);
    this.fuel = fuel;
    this.fuelCount = Math.max(1, fuelCount);
    this.burnTimeTicks = Math.max(20, burnTimeTicks);
    this.generatedBloomOutput = generatedBloomOutput;
    this.bloomYieldMin = Math.max(1, Math.min(bloomYieldMin, bloomYieldMax));
    this.bloomYieldMax = Math.max(this.bloomYieldMin, bloomYieldMax);
    this.experience = Math.max(0, experience);
    this.bloomRecipeId = bloomRecipeId;
    this.bloomLangKey = bloomLangKey;
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

  public int getBloomYieldMin() {

    return this.bloomYieldMin;
  }

  public int getBloomYieldMax() {

    return this.bloomYieldMax;
  }

  public float getExperience() {

    return this.experience;
  }

  @Nullable
  public String getBloomRecipeId() {

    return this.bloomRecipeId;
  }

  @Nullable
  public String getBloomLangKey() {

    return this.bloomLangKey;
  }

  public ItemStack getOutput(int batches, @Nullable Random random) {

    int batchCount = Math.max(1, batches);

    if (this.generatedBloomOutput) {
      return this.createBloomOutput(batchCount, random);
    }

    ItemStack result = this.output.copy();
    int count = Math.max(1, result.getCount() * batchCount);
    result.setCount(Math.min(result.getMaxStackSize(), count));
    return result;
  }

  @Override
  public ItemStack getOutput() {

    return this.getOutput(1, null);
  }

  private ItemStack createBloomOutput(int batches, @Nullable Random random) {

    if (ModuleTechBloomery.Blocks.BLOOM == null) {
      return ItemStack.EMPTY;
    }

    int maxIntegrity = this.bloomYieldMax * batches;
    int integrity = 0;

    for (int i = 0; i < batches; i++) {
      if (random == null || this.bloomYieldMin == this.bloomYieldMax) {
        integrity += this.bloomYieldMax;
      } else {
        integrity += this.bloomYieldMin + random.nextInt(this.bloomYieldMax - this.bloomYieldMin + 1);
      }
    }

    String recipeId = this.bloomRecipeId;
    ResourceLocation registryName = this.getRegistryName();
    if (recipeId == null && registryName != null) {
      recipeId = registryName.toString();
    }

    return BloomHelper.createBloomAsItemStack(new ItemStack(ModuleTechBloomery.Blocks.BLOOM), maxIntegrity, integrity, this.experience * batches, recipeId, this.bloomLangKey);
  }
}
