package com.canoestudios.pyrotechcomplement.plugin.jei.wrapper;

import com.canoestudios.pyrotechcomplement.init.ModBlocks;
import com.canoestudios.pyrotechcomplement.recipe.QuernRecipe;
import com.codetaylor.mc.pyrotech.library.spi.plugin.jei.IPyrotechRecipeWrapper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JEIRecipeWrapperQuern implements IPyrotechRecipeWrapper {

  private final ResourceLocation registryName;
  private final List<ItemStack> inputStacks;
  private final List<ItemStack> handstoneStacks;
  private final List<List<ItemStack>> inputs;
  private final ItemStack output;
  private final int grindingTicks;

  public JEIRecipeWrapperQuern(QuernRecipe recipe) {

    this.registryName = recipe.getRegistryName();
    this.inputStacks = this.getInputStacks(recipe);
    this.handstoneStacks = Collections.singletonList(new ItemStack(ModBlocks.HANDSTONE));
    this.inputs = new ArrayList<>();
    this.inputs.add(this.inputStacks);
    this.inputs.add(this.handstoneStacks);
    this.output = recipe.getOutput();
    this.grindingTicks = recipe.getGrindingTicks();
  }

  @Override
  public void getIngredients(@Nonnull IIngredients ingredients) {

    ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
    ingredients.setOutput(VanillaTypes.ITEM, this.output);
  }

  public List<ItemStack> getInputStacks() {

    return copyStacks(this.inputStacks);
  }

  public List<ItemStack> getHandstoneStacks() {

    return copyStacks(this.handstoneStacks);
  }

  public ItemStack getOutput() {

    return this.output.copy();
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    minecraft.fontRenderer.drawString(this.grindingTicks + "t", 48, 20, 0xFF404040);
  }

  private List<ItemStack> getInputStacks(QuernRecipe recipe) {

    ItemStack[] matchingStacks = recipe.getInput().getMatchingStacks();
    List<ItemStack> result = new ArrayList<>(matchingStacks.length);
    for (ItemStack stack : matchingStacks) {
      if (!stack.isEmpty()) {
        result.add(stack.copy());
      }
    }
    return result;
  }

  private static List<ItemStack> copyStacks(List<ItemStack> stacks) {

    List<ItemStack> result = new ArrayList<>(stacks.size());
    for (ItemStack stack : stacks) {
      result.add(stack.copy());
    }
    return result;
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName() {

    return this.registryName;
  }
}
