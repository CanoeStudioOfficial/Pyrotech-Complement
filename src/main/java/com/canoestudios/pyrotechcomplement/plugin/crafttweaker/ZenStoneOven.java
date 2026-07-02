package com.canoestudios.pyrotechcomplement.plugin.crafttweaker;

import com.codetaylor.mc.athenaeum.util.RecipeHelper;
import com.codetaylor.mc.pyrotech.modules.tech.machine.ModuleTechMachine;
import com.codetaylor.mc.pyrotech.modules.tech.machine.init.recipe.BrickOvenRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.machine.recipe.StoneOvenRecipe;
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

@ZenClass("mods.pyrotechcomplement.StoneOven")
public class ZenStoneOven {

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input, int cookTimeTicks, @Optional boolean inherited) {

    CraftTweaker.LATE_ACTIONS.add(new AddRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input),
        cookTimeTicks,
        inherited
    ));
  }

  public static class AddRecipe
      implements IAction {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;
    private final int cookTimeTicks;
    private final boolean inherited;

    public AddRecipe(String name, ItemStack output, Ingredient input, int cookTimeTicks, boolean inherited) {

      this.name = name;
      this.output = output;
      this.input = input;
      this.cookTimeTicks = Math.max(1, cookTimeTicks);
      this.inherited = inherited;
    }

    @Override
    public void apply() {

      StoneOvenRecipe recipe = new StoneOvenRecipe(
          this.output,
          this.input,
          this.cookTimeTicks
      );

      recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name));
      ModuleTechMachine.Registries.STONE_OVEN_RECIPES.register(recipe);

      if (this.inherited) {
        RecipeHelper.inherit("stone_oven", ModuleTechMachine.Registries.BRICK_OVEN_RECIPES, BrickOvenRecipesAdd.INHERIT_TRANSFORMER, recipe);
      }
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement timed stone oven recipe for " + this.output + ", cookTimeTicks=" + this.cookTimeTicks + ", inherited=" + this.inherited;
    }
  }
}
