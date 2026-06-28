package com.canoestudios.pyrotechcomplement.plugin.jei.wrapper;

import com.canoestudios.pyrotechcomplement.recipe.ForgingTableRecipe;
import com.codetaylor.mc.pyrotech.library.spi.plugin.jei.IPyrotechRecipeWrapper;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCoreConfig;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JEIRecipeWrapperForgingTable
    implements IPyrotechRecipeWrapper {

  private final ResourceLocation registryName;
  private final List<List<ItemStack>> inputs;
  private final List<ItemStack> inputStacks;
  private final List<ItemStack> secondaryInputStacks;
  private final List<ItemStack> hammerStacks;
  private final ItemStack output;
  private final int hits;

  public JEIRecipeWrapperForgingTable(ForgingTableRecipe recipe) {

    this.registryName = recipe.getRegistryName();
    this.inputStacks = this.getInputStacks(recipe.getInput(), recipe.getInputCount());
    this.secondaryInputStacks = recipe.hasSecondaryInput() && recipe.getSecondaryInput() != null
        ? this.getInputStacks(recipe.getSecondaryInput(), recipe.getSecondaryInputCount())
        : Collections.emptyList();
    this.hammerStacks = this.createHammerStacks();
    this.output = recipe.getOutput();
    this.hits = recipe.getHits();

    this.inputs = new ArrayList<>();
    this.inputs.add(this.inputStacks);

    if (!this.secondaryInputStacks.isEmpty()) {
      this.inputs.add(this.secondaryInputStacks);
    }

    this.inputs.add(this.hammerStacks);
  }

  @Override
  public void getIngredients(@Nonnull IIngredients ingredients) {

    ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
    ingredients.setOutput(VanillaTypes.ITEM, this.output);
  }

  public List<ItemStack> getInputStacks() {

    return this.inputStacks;
  }

  public boolean hasSecondaryInput() {

    return !this.secondaryInputStacks.isEmpty();
  }

  public List<ItemStack> getSecondaryInputStacks() {

    return this.secondaryInputStacks;
  }

  public List<ItemStack> getHammerStacks() {

    return this.hammerStacks;
  }

  public ItemStack getOutput() {

    return this.output;
  }

  @Override
  public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    minecraft.fontRenderer.drawString("x" + this.hits, 20, 5, 0xFFFFFFFF, true);

    if (this.hasSecondaryInput()) {
      minecraft.fontRenderer.drawString("+", 47, 30, 0xFF404040);
    }
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

  private List<ItemStack> createHammerStacks() {

    Map<String, ItemStack> result = new LinkedHashMap<>();

    for (String entry : ModuleCoreConfig.HAMMERS.HAMMER_LIST) {
      String itemName = entry.split(";")[0];
      Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));

      if (item != null) {
        ItemStack stack = new ItemStack(item);
        result.put(this.getKey(stack), stack);
      }
    }

    for (ItemStack stack : OreDictionary.getOres("toolHammer")) {
      ItemStack copy = stack.copy();
      copy.setCount(1);
      result.put(this.getKey(copy), copy);
    }

    return new ArrayList<>(result.values());
  }

  private String getKey(ItemStack stack) {

    ResourceLocation registryName = stack.getItem().getRegistryName();
    return (registryName == null ? "" : registryName.toString()) + ":" + stack.getMetadata();
  }

  @Nullable
  @Override
  public ResourceLocation getRegistryName() {

    return this.registryName;
  }
}
