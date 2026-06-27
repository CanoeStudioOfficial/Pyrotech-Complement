package com.canoestudios.pyrotechcomplement.plugin.crafttweaker;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import com.codetaylor.mc.pyrotech.library.crafttweaker.RemoveAllRecipesAction;
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

@ZenClass("mods.pyrotechcomplement.Loom")
public class ZenLoom {

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input, int inputCount, int steps, @Optional String texture) {

    ResourceLocation textureLocation = texture == null || texture.isEmpty()
        ? new ResourceLocation("pyrotech", "blocks/drying_rack_crude")
        : new ResourceLocation(texture);

    CraftTweaker.LATE_ACTIONS.add(new AddRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input),
        inputCount,
        steps,
        textureLocation
    ));
  }

  @ZenMethod
  public static void removeRecipes(IIngredient output) {

    CraftTweaker.LATE_ACTIONS.add(new RemoveRecipe(CraftTweakerMC.getIngredient(output)));
  }

  @ZenMethod
  public static void removeAllRecipes() {

    CraftTweaker.LATE_ACTIONS.add(new RemoveAllRecipesAction<>(ModRecipes.LOOM_RECIPES, "pyrotech complement loom"));
  }

  public static class AddRecipe
      implements IAction {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;
    private final int inputCount;
    private final int steps;
    private final ResourceLocation texture;

    public AddRecipe(String name, ItemStack output, Ingredient input, int inputCount, int steps, ResourceLocation texture) {

      this.name = name;
      this.output = output;
      this.input = input;
      this.inputCount = inputCount;
      this.steps = steps;
      this.texture = texture;
    }

    @Override
    public void apply() {

      LoomRecipe recipe = new LoomRecipe(this.output, this.input, this.inputCount, this.steps, this.texture);
      ModRecipes.LOOM_RECIPES.register(recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name)));
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement loom recipe for " + this.output;
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

      LoomRecipe.removeRecipes(this.output);
    }

    @Override
    public String describe() {

      return "Removing pyrotech complement loom recipes for " + this.output;
    }
  }
}
