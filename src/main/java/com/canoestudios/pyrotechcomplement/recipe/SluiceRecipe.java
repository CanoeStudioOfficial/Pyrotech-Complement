package com.canoestudios.pyrotechcomplement.recipe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import net.minecraft.item.crafting.Ingredient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * A small OreDictionary based adapter for TFC-like sluice inputs. 1.12.2
 * does not have the data-manager and loot-table APIs used by the newer TFC.
 */
public final class SluiceRecipe {

  public static final int DEFAULT_PROCESSING_TICKS = 100;
  private static final float ORE_CHANCE = 0.55f;
  private static final float LOOSE_ROCK_CHANCE = 0.775f;
  private static final float GEM_CHANCE = 0.784f;
  private static final List<CustomRecipe> CUSTOM_RECIPES = new ArrayList<>();
  private static final List<Ingredient> DISABLED_INPUTS = new ArrayList<>();
  private static boolean automaticRecipesEnabled = true;

  public static void addCustomRecipe(String name, ItemStack output, Ingredient input) {

    CUSTOM_RECIPES.add(new CustomRecipe(name, output, input));
  }

  public static void removeRecipes(Ingredient input) {

    for (Iterator<CustomRecipe> iterator = CUSTOM_RECIPES.iterator(); iterator.hasNext(); ) {
      if (iterator.next().matchesIngredient(input)) {
        iterator.remove();
      }
    }
    DISABLED_INPUTS.add(input);
  }

  public static void removeAllRecipes() {

    CUSTOM_RECIPES.clear();
    DISABLED_INPUTS.clear();
    automaticRecipesEnabled = false;
  }

  public static boolean isValidInput(ItemStack stack) {

    if (stack.isEmpty()) {
      return false;
    }

    if (findCustomRecipe(stack) != null) {
      return true;
    }
    if (!automaticRecipesEnabled || isDisabled(stack)) {
      return false;
    }

    for (int id : OreDictionary.getOreIDs(stack)) {
      if (OreDictionary.getOreName(id).toLowerCase(Locale.ROOT).startsWith("ore")) {
        return true;
      }
    }

    Item item = stack.getItem();
    if (item == Item.getItemFromBlock(Blocks.GRAVEL)
        || item == Item.getItemFromBlock(Blocks.SAND)
        || item == Item.getItemFromBlock(Blocks.SOUL_SAND)) {
      return true;
    }

    ResourceLocation registryName = item.getRegistryName();
    if (registryName == null) {
      return false;
    }

    String path = registryName.getPath().toLowerCase(Locale.ROOT);
    return path.contains("deposit") || path.startsWith("ore") || path.contains("/ore") || path.contains("_ore");
  }

  @Nullable
  public static ItemStack rollOutput(ItemStack input, Random random) {

    CustomRecipe customRecipe = findCustomRecipe(input);
    if (customRecipe != null) {
      return customRecipe.getOutput();
    }
    if (!isValidInput(input)) {
      return null;
    }

    float roll = random.nextFloat();
    String material = findMaterial(input);

    if (roll < ORE_CHANCE) {
      ItemStack oreOutput = findOreOutput(material);
      return oreOutput.isEmpty() ? null : oreOutput;
    }

    if (roll < LOOSE_ROCK_CHANCE) {
      if (input.getItem() == Item.getItemFromBlock(Blocks.GRAVEL)) {
        return new ItemStack(Items.FLINT);
      }
      return new ItemStack(Blocks.GRAVEL);
    }

    if (roll < GEM_CHANCE) {
      ItemStack gemOutput = findFirst("gem" + capitalize(material));
      return gemOutput.isEmpty() ? null : gemOutput;
    }

    return null;
  }

  private static boolean isDisabled(ItemStack stack) {

    for (Ingredient input : DISABLED_INPUTS) {
      if (input.apply(stack)) {
        return true;
      }
    }
    return false;
  }

  @Nullable
  private static CustomRecipe findCustomRecipe(ItemStack stack) {

    for (CustomRecipe recipe : CUSTOM_RECIPES) {
      if (recipe.matches(stack)) {
        return recipe;
      }
    }
    return null;
  }

  private static class CustomRecipe {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;

    private CustomRecipe(String name, ItemStack output, Ingredient input) {

      this.name = name;
      this.output = output.copy();
      this.input = input;
    }

    private boolean matches(ItemStack stack) {

      return this.input.apply(stack);
    }

    private boolean matchesIngredient(Ingredient other) {

      for (ItemStack stack : other.getMatchingStacks()) {
        if (this.matches(stack)) {
          return true;
        }
      }
      return false;
    }

    private ItemStack getOutput() {

      return this.output.copy();
    }
  }

  private static ItemStack findOreOutput(String material) {

    if (material.isEmpty()) {
      return ItemStack.EMPTY;
    }

    String capitalized = capitalize(material);
    ItemStack result = findFirst("nugget" + capitalized);
    if (!result.isEmpty()) {
      result.setCount(1);
      return result;
    }

    result = findFirst("dust" + capitalized);
    if (!result.isEmpty()) {
      result.setCount(1);
      return result;
    }

    result = findFirst("ingot" + capitalized);
    if (!result.isEmpty()) {
      result.setCount(1);
    }
    return result;
  }

  private static String findMaterial(ItemStack stack) {

    for (int id : OreDictionary.getOreIDs(stack)) {
      String name = OreDictionary.getOreName(id);
      if (name.toLowerCase(Locale.ROOT).startsWith("ore") && name.length() > 3) {
        return normalizeMaterial(name.substring(3));
      }
    }

    ResourceLocation registryName = stack.getItem().getRegistryName();
    String path = registryName == null ? "" : registryName.getPath().toLowerCase(Locale.ROOT);
    if (path.contains("native_copper")) {
      return "copper";
    }
    if (path.contains("native_gold")) {
      return "gold";
    }
    if (path.contains("native_silver")) {
      return "silver";
    }
    if (path.contains("cassiterite") || path.contains("tin")) {
      return "tin";
    }
    if (path.contains("hematite") || path.contains("limonite") || path.contains("magnetite") || path.contains("iron")) {
      return "iron";
    }
    if (path.contains("sphalerite") || path.contains("zinc")) {
      return "zinc";
    }

    return "";
  }

  private static String normalizeMaterial(String material) {

    String normalized = material.toLowerCase(Locale.ROOT).replace("_", "");
    if (normalized.contains("nativecopper")) {
      return "copper";
    }
    if (normalized.contains("nativegold")) {
      return "gold";
    }
    if (normalized.contains("nativesilver")) {
      return "silver";
    }
    if (normalized.contains("cassiterite") || normalized.contains("tin")) {
      return "tin";
    }
    if (normalized.contains("hematite") || normalized.contains("limonite") || normalized.contains("magnetite") || normalized.contains("iron")) {
      return "iron";
    }
    if (normalized.contains("sphalerite") || normalized.contains("zinc")) {
      return "zinc";
    }
    return material;
  }

  private static ItemStack findFirst(String oreName) {

    for (ItemStack stack : OreDictionary.getOres(oreName)) {
      if (!stack.isEmpty()) {
        return stack.copy();
      }
    }
    return ItemStack.EMPTY;
  }

  private static String capitalize(String value) {

    return value.isEmpty() ? value : Character.toUpperCase(value.charAt(0)) + value.substring(1);
  }

  private SluiceRecipe() {
    // Utility class.
  }
}
