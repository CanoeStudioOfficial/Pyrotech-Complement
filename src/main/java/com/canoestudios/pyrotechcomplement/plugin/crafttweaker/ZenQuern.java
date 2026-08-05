package com.canoestudios.pyrotechcomplement.plugin.crafttweaker;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.canoestudios.pyrotechcomplement.recipe.QuernRecipe;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.pyrotechcomplement.Quern")
public class ZenQuern {

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input, @Optional int grindingTicks) {

    CraftTweaker.LATE_ACTIONS.add(new AddRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input),
        grindingTicks <= 0 ? QuernRecipe.DEFAULT_GRINDING_TICKS : grindingTicks
    ));
  }

  @ZenMethod
  public static void removeRecipes(IIngredient input) {

    CraftTweaker.LATE_ACTIONS.add(new RemoveRecipe(CraftTweakerMC.getIngredient(input)));
  }

  @ZenMethod
  public static void removeAllRecipes() {

    CraftTweaker.LATE_ACTIONS.add(new RemoveAllRecipe());
  }

  public static class AddRecipe implements IAction {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;
    private final int grindingTicks;

    public AddRecipe(String name, ItemStack output, Ingredient input, int grindingTicks) {

      this.name = name;
      this.output = output;
      this.input = input;
      this.grindingTicks = grindingTicks;
    }

    @Override
    public void apply() {

      if (ModRecipes.QUERN_RECIPES == null) {
        ModRecipes.initRegistry();
      }
      QuernRecipe recipe = new QuernRecipe(this.output, this.input, this.grindingTicks);
      ModRecipes.QUERN_RECIPES.register(recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name)));
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement quern recipe for " + this.output;
    }
  }

  public static class RemoveRecipe implements IAction {

    private final Ingredient input;

    public RemoveRecipe(Ingredient input) {

      this.input = input;
    }

    @Override
    public void apply() {

      QuernRecipe.removeRecipes(this.input);
    }

    @Override
    public String describe() {

      return "Removing pyrotech complement quern recipes for " + this.input;
    }
  }

  public static class RemoveAllRecipe implements IAction {

    @Override
    public void apply() {

      QuernRecipe.removeAllRecipes();
    }

    @Override
    public String describe() {

      return "Removing all pyrotech complement quern recipes";
    }
  }
}
