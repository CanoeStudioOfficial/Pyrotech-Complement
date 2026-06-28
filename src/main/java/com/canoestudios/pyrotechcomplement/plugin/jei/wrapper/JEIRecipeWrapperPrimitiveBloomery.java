package com.canoestudios.pyrotechcomplement.plugin.jei.wrapper;

import com.canoestudios.pyrotechcomplement.recipe.PrimitiveBloomeryRecipe;
import com.codetaylor.mc.pyrotech.library.spi.plugin.jei.IPyrotechRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class JEIRecipeWrapperPrimitiveBloomery
    implements IPyrotechRecipeWrapper {

  private final ResourceLocation registryName;
  private final List<List<ItemStack>> inputs;
  private final List<ItemStack> inputStacks;
  private final List<ItemStack> fuelStacks;
  private final ItemStack output;
  private final int burnTimeTicks;

  public JEIRecipeWrapperPrimitiveBloomery(PrimitiveBloomeryRecipe recipe) {

    this.registryName = recipe.getRegistryName();
    this.inputStacks = this.getInputStacks(recipe.getInput(), recipe.getInputCount());
    this.fuelStacks = this.getInputStacks(recipe.getFuel(), recipe.getFuelCount());
    this.output = recipe.getOutput();
    this.burnTimeTicks = recipe.getBurnTimeTicks();

    this.inputs = new ArrayList<>();
    this.inputs.add(this.inputStacks);
    this.inputs.add(this.fuelStacks);
  }

  @Override
  public void getIngredients(@Nonnull IIngredients ingredients) {

    ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
    ingredients.setOutput(VanillaTypes.ITEM, this.output);
  }

  public List<ItemStack> getInputStacks() {

    return this.inputStacks;
  }

  public List<ItemStack> getFuelStacks() {

    return this.fuelStacks;
  }

  public ItemStack getOutput() {

    return this.output;
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    int seconds = Math.max(1, this.burnTimeTicks / 20);
    minecraft.fontRenderer.drawString(seconds + "s", 44, 5, 0xFF404040);
  }

  private List<ItemStack> getInputStacks(Ingredient ingredient, int count) {

    ItemStack[] matchingStacks = ingredient.getMatchingStacks();
    List<ItemStack> result = new ArrayList<>(matchingStacks.length);

    for (ItemStack stack : matchingStacks) {
      ItemStack copy = stack.copy();
      copy.setCount(count);
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
