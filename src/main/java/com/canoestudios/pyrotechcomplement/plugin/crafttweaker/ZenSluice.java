package com.canoestudios.pyrotechcomplement.plugin.crafttweaker;

import com.canoestudios.pyrotechcomplement.recipe.SluiceRecipe;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.pyrotechcomplement.Sluice")
public class ZenSluice {

  /**
   * Adds a deterministic CraftTweaker recipe. Custom recipes take priority
   * over the built-in OreDictionary based fallback recipes.
   */
  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input) {

    CraftTweaker.LATE_ACTIONS.add(new AddRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input)
    ));
  }

  @ZenMethod
  public static void removeRecipes(IIngredient input) {

    CraftTweaker.LATE_ACTIONS.add(new RemoveRecipe(CraftTweakerMC.getIngredient(input)));
  }

  @ZenMethod
  public static void removeAllRecipes() {

    CraftTweaker.LATE_ACTIONS.add(new RemoveAllRecipes());
  }

  private static class AddRecipe
      implements IAction {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;

    private AddRecipe(String name, ItemStack output, Ingredient input) {

      this.name = name;
      this.output = output;
      this.input = input;
    }

    @Override
    public void apply() {

      SluiceRecipe.addCustomRecipe(this.name, this.output, this.input);
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement sluice recipe for " + this.output;
    }
  }

  private static class RemoveRecipe
      implements IAction {

    private final Ingredient input;

    private RemoveRecipe(Ingredient input) {

      this.input = input;
    }

    @Override
    public void apply() {

      SluiceRecipe.removeRecipes(this.input);
    }

    @Override
    public String describe() {

      return "Removing pyrotech complement sluice recipes for " + this.input;
    }
  }

  private static class RemoveAllRecipes
      implements IAction {

    @Override
    public void apply() {

      SluiceRecipe.removeAllRecipes();
    }

    @Override
    public String describe() {

      return "Removing all pyrotech complement sluice recipes";
    }
  }
}
