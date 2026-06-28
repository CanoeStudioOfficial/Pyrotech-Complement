package com.canoestudios.pyrotechcomplement.plugin.crafttweaker;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.canoestudios.pyrotechcomplement.recipe.ForgingTableRecipe;
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

@ZenClass("mods.pyrotechcomplement.ForgingTable")
public class ZenForgingTable {

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input, int inputCount, int hits) {

    CraftTweaker.LATE_ACTIONS.add(new AddRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input),
        inputCount,
        null,
        0,
        hits
    ));
  }

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input, int inputCount, IIngredient secondaryInput, int secondaryInputCount, int hits) {

    CraftTweaker.LATE_ACTIONS.add(new AddRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input),
        inputCount,
        CraftTweakerMC.getIngredient(secondaryInput),
        secondaryInputCount,
        hits
    ));
  }

  @ZenMethod
  public static void removeRecipes(IIngredient output) {

    CraftTweaker.LATE_ACTIONS.add(new RemoveRecipe(CraftTweakerMC.getIngredient(output)));
  }

  @ZenMethod
  public static void removeAllRecipes() {

    CraftTweaker.LATE_ACTIONS.add(new RemoveAllRecipesAction<>(ModRecipes.FORGING_TABLE_RECIPES, "pyrotech complement forging table"));
  }

  public static class AddRecipe
      implements IAction {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;
    private final int inputCount;
    @Nullable
    private final Ingredient secondaryInput;
    private final int secondaryInputCount;
    private final int hits;

    public AddRecipe(String name, ItemStack output, Ingredient input, int inputCount, @Nullable Ingredient secondaryInput, int secondaryInputCount, int hits) {

      this.name = name;
      this.output = output;
      this.input = input;
      this.inputCount = inputCount;
      this.secondaryInput = secondaryInput;
      this.secondaryInputCount = secondaryInputCount;
      this.hits = hits;
    }

    @Override
    public void apply() {

      ForgingTableRecipe recipe = new ForgingTableRecipe(this.output, this.input, this.inputCount, this.secondaryInput, this.secondaryInputCount, this.hits);
      ModRecipes.FORGING_TABLE_RECIPES.register(recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name)));
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement forging table recipe for " + this.output;
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

      ForgingTableRecipe.removeRecipes(this.output);
    }

    @Override
    public String describe() {

      return "Removing pyrotech complement forging table recipes for " + this.output;
    }
  }
}
