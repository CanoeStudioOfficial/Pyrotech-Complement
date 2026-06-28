package com.canoestudios.pyrotechcomplement.plugin.crafttweaker;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.canoestudios.pyrotechcomplement.recipe.PrimitiveBloomeryRecipe;
import com.codetaylor.mc.pyrotech.library.crafttweaker.RemoveAllRecipesAction;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;

@ZenClass("mods.pyrotechcomplement.PrimitiveBloomery")
public class ZenPrimitiveBloomery {

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input, int inputCount, IIngredient fuel, int fuelCount, int burnTimeTicks) {

    CraftTweaker.LATE_ACTIONS.add(new AddRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input),
        inputCount,
        CraftTweakerMC.getIngredient(fuel),
        fuelCount,
        burnTimeTicks
    ));
  }

  @ZenMethod
  public static void addBloomRecipe(String name, IIngredient input, int inputCount, IIngredient fuel, int fuelCount, int burnTimeTicks, int bloomYieldMin, int bloomYieldMax, float experience, String bloomRecipeId, String bloomLangKey) {

    CraftTweaker.LATE_ACTIONS.add(new AddBloomRecipe(
        name,
        CraftTweakerMC.getIngredient(input),
        inputCount,
        CraftTweakerMC.getIngredient(fuel),
        fuelCount,
        burnTimeTicks,
        bloomYieldMin,
        bloomYieldMax,
        experience,
        bloomRecipeId,
        bloomLangKey
    ));
  }

  @ZenMethod
  public static void removeRecipes(IIngredient output) {

    CraftTweaker.LATE_ACTIONS.add(new RemoveRecipe(CraftTweakerMC.getIngredient(output)));
  }

  @ZenMethod
  public static void removeAllRecipes() {

    CraftTweaker.LATE_ACTIONS.add(new RemoveAllRecipesAction<>(ModRecipes.PRIMITIVE_BLOOMERY_RECIPES, "pyrotech complement primitive bloomery"));
  }

  public static class AddRecipe
      implements IAction {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;
    private final int inputCount;
    private final Ingredient fuel;
    private final int fuelCount;
    private final int burnTimeTicks;

    public AddRecipe(String name, ItemStack output, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks) {

      this.name = name;
      this.output = output;
      this.input = input;
      this.inputCount = inputCount;
      this.fuel = fuel;
      this.fuelCount = fuelCount;
      this.burnTimeTicks = burnTimeTicks;
    }

    @Override
    public void apply() {

      PrimitiveBloomeryRecipe recipe = new PrimitiveBloomeryRecipe(this.output, this.input, this.inputCount, this.fuel, this.fuelCount, this.burnTimeTicks);
      ModRecipes.PRIMITIVE_BLOOMERY_RECIPES.register(recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name)));
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement primitive bloomery recipe for " + this.output;
    }
  }

  public static class AddBloomRecipe
      implements IAction {

    private final String name;
    private final Ingredient input;
    private final int inputCount;
    private final Ingredient fuel;
    private final int fuelCount;
    private final int burnTimeTicks;
    private final int bloomYieldMin;
    private final int bloomYieldMax;
    private final float experience;
    @Nullable
    private final String bloomRecipeId;
    @Nullable
    private final String bloomLangKey;

    public AddBloomRecipe(String name, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks, int bloomYieldMin, int bloomYieldMax, float experience, @Nullable String bloomRecipeId, @Nullable String bloomLangKey) {

      this.name = name;
      this.input = input;
      this.inputCount = inputCount;
      this.fuel = fuel;
      this.fuelCount = fuelCount;
      this.burnTimeTicks = burnTimeTicks;
      this.bloomYieldMin = bloomYieldMin;
      this.bloomYieldMax = bloomYieldMax;
      this.experience = experience;
      this.bloomRecipeId = bloomRecipeId;
      this.bloomLangKey = bloomLangKey;
    }

    @Override
    public void apply() {

      PrimitiveBloomeryRecipe recipe = new PrimitiveBloomeryRecipe(this.input, this.inputCount, this.fuel, this.fuelCount, this.burnTimeTicks, this.bloomYieldMin, this.bloomYieldMax, this.experience, this.bloomRecipeId, this.bloomLangKey);
      ModRecipes.PRIMITIVE_BLOOMERY_RECIPES.register(recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name)));
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement primitive bloomery bloom recipe " + this.name;
    }
  }

  public static class RemoveRecipe
      implements IAction {

    private final Ingredient output;

    public RemoveRecipe(Ingredient output) {

      this.output = output;
    }

    @Override
    public void apply() {

      PrimitiveBloomeryRecipe.removeRecipes(this.output);
    }

    @Override
    public String describe() {

      return "Removing pyrotech complement primitive bloomery recipes for " + this.output;
    }
  }
}
