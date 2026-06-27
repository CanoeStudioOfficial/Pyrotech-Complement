package com.canoestudios.pyrotechcomplement.plugin.jei.wrapper;

import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import com.codetaylor.mc.pyrotech.library.spi.plugin.jei.IPyrotechRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JEIRecipeWrapperLoom
    implements IPyrotechRecipeWrapper {

  private final ResourceLocation registryName;
  private final List<List<ItemStack>> inputs;
  private final ItemStack output;

  public JEIRecipeWrapperLoom(LoomRecipe recipe) {

    this.registryName = recipe.getRegistryName();
    this.inputs = Collections.singletonList(this.getInputStacks(recipe));
    this.output = recipe.getOutput();
  }

  @Override
  public void getIngredients(@Nonnull IIngredients ingredients) {

    ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
    ingredients.setOutput(VanillaTypes.ITEM, this.output);
  }

  private List<ItemStack> getInputStacks(LoomRecipe recipe) {

    ItemStack[] matchingStacks = recipe.getInput().getMatchingStacks();
    List<ItemStack> result = new ArrayList<>(matchingStacks.length);

    for (ItemStack stack : matchingStacks) {
      ItemStack copy = stack.copy();
      copy.setCount(recipe.getInputCount());
      result.add(copy);
    }

    return result;
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName() {

    return this.registryName;
  }
}
