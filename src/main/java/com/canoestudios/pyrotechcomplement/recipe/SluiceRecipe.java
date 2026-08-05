package com.canoestudios.pyrotechcomplement.recipe;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import net.minecraft.item.crafting.Ingredient;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

/**
 * A small OreDictionary based adapter for TFC-like sluice inputs. 1.12.2
 * does not have the data-manager and loot-table APIs used by the newer TFC.
 */
public final class SluiceRecipe {

  public static final int DEFAULT_PROCESSING_TICKS = 100;
  /** The built-in crude-tier penalty. CraftTweaker recipes use their own per-input chance. */
  private static final float CRUDE_MINERAL_FAILURE_CHANCE = 0.25f;
  private static final float ORE_CHANCE = 0.55f;
  private static final float LOOSE_ROCK_CHANCE = 0.775f;
  private static final float GEM_CHANCE = 0.784f;
  private static final List<CustomRecipe> CUSTOM_RECIPES = new ArrayList<>();
  private static final List<Ingredient> DISABLED_INPUTS = new ArrayList<>();
  private static boolean automaticRecipesEnabled = true;

  public static void addCustomRecipe(String name, ItemStack output, Ingredient input) {

    addCustomRecipe(name, output, input, 1.0f);
  }

  public static void addCustomRecipe(String name, ItemStack output, Ingredient input, float chance) {

    CUSTOM_RECIPES.add(new CustomRecipe(name, output, input, clampChance(chance)));
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

  /**
   * Returns a client-friendly snapshot of the recipes that can be shown in JEI.
   *
   * <p>The sluice has random built-in outputs rather than one fixed output, so each
   * entry contains every possible output for its representative input. CraftTweaker
   * recipes are included before the automatic fallback recipes.</p>
   */
  public static List<JeiRecipe> getJeiRecipes() {

    List<JeiRecipe> result = new ArrayList<>();
    Set<String> seenInputs = new HashSet<>();

    for (CustomRecipe recipe : CUSTOM_RECIPES) {
      List<ItemStack> inputs = getMatchingStacks(recipe.input);
      if (!inputs.isEmpty()) {
        result.add(new JeiRecipe(
            recipe.name,
            inputs,
            singletonOutput(recipe.output),
            recipe.chance,
            false,
            false
        ));
        for (ItemStack input : inputs) {
          seenInputs.add(getStackKey(input));
        }
      }
    }

    if (!automaticRecipesEnabled) {
      return result;
    }

    addAutomaticJeiRecipe(result, seenInputs, "gravel", new ItemStack(Blocks.GRAVEL));
    addAutomaticJeiRecipe(result, seenInputs, "sand", new ItemStack(Blocks.SAND));
    addAutomaticJeiRecipe(result, seenInputs, "soul_sand", new ItemStack(Blocks.SOUL_SAND));

    for (String oreName : OreDictionary.getOreNames()) {
      if (!oreName.toLowerCase(Locale.ROOT).startsWith("ore") || oreName.length() <= 3) {
        continue;
      }

      for (ItemStack stack : OreDictionary.getOres(oreName)) {
        if (!stack.isEmpty() && isAutomaticMineralInput(stack)) {
          addAutomaticJeiRecipe(result, seenInputs, "automatic_" + oreName, stack);
          break;
        }
      }
    }

    // Some TFC-style deposits are identified by their registry path instead of an
    // OreDictionary entry. Include one representative metadata-0 stack for those.
    for (Item item : ForgeRegistries.ITEMS.getValuesCollection()) {
      ItemStack stack = new ItemStack(item, 1, 0);
      if (isAutomaticMineralInput(stack)) {
        addAutomaticJeiRecipe(result, seenInputs, "automatic_" + item.getRegistryName(), stack);
      }
    }

    return result;
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

    Item item = stack.getItem();
    if (item == Item.getItemFromBlock(Blocks.GRAVEL)
        || item == Item.getItemFromBlock(Blocks.SAND)
        || item == Item.getItemFromBlock(Blocks.SOUL_SAND)) {
      return true;
    }

    return isAutomaticMineralInput(stack);
  }

  @Nullable
  public static ItemStack rollOutput(ItemStack input, Random random) {

    return rollOutput(input, random, false);
  }

  @Nullable
  public static ItemStack rollOutput(ItemStack input, Random random, boolean crudeTier) {

    CustomRecipe customRecipe = findCustomRecipe(input);
    if (customRecipe != null) {
      return customRecipe.getOutput(random);
    }
    if (!isValidInput(input)) {
      return null;
    }

    boolean mineralInput = isAutomaticMineralInput(input);
    if (mineralInput && crudeTier && random.nextFloat() < CRUDE_MINERAL_FAILURE_CHANCE) {
      return null;
    }

    float roll = random.nextFloat();
    String material = findMaterial(input);

    if (roll < ORE_CHANCE) {
      ItemStack oreOutput = findOreOutput(material);
      return oreOutput.isEmpty() ? getMineralFallback(mineralInput, material) : oreOutput;
    }

    if (roll < LOOSE_ROCK_CHANCE) {
      if (input.getItem() == Item.getItemFromBlock(Blocks.GRAVEL)) {
        return new ItemStack(Items.FLINT);
      }
      return new ItemStack(Blocks.GRAVEL);
    }

    if (roll < GEM_CHANCE) {
      ItemStack gemOutput = findFirst("gem" + capitalize(material));
      return gemOutput.isEmpty() ? getMineralFallback(mineralInput, material) : gemOutput;
    }

    return getMineralFallback(mineralInput, material);
  }

  @Nullable
  private static ItemStack getMineralFallback(boolean mineralInput, String material) {

    if (!mineralInput) {
      return null;
    }

    ItemStack oreOutput = findOreOutput(material);
    return oreOutput.isEmpty() ? new ItemStack(Blocks.GRAVEL) : oreOutput;
  }

  private static boolean isAutomaticMineralInput(ItemStack stack) {

    for (int id : OreDictionary.getOreIDs(stack)) {
      if (OreDictionary.getOreName(id).toLowerCase(Locale.ROOT).startsWith("ore")) {
        return true;
      }
    }

    ResourceLocation registryName = stack.getItem().getRegistryName();
    if (registryName == null) {
      return false;
    }

    String path = registryName.getPath().toLowerCase(Locale.ROOT);
    return path.contains("deposit") || path.startsWith("ore") || path.contains("/ore") || path.contains("_ore");
  }

  private static void addAutomaticJeiRecipe(List<JeiRecipe> recipes, Set<String> seenInputs, String name, ItemStack input) {

    if (input.isEmpty() || !isValidInput(input)) {
      return;
    }

    CustomRecipe customRecipe = findCustomRecipe(input);
    if (customRecipe != null || isDisabled(input)) {
      return;
    }

    String key = getStackKey(input);
    if (!seenInputs.add(key)) {
      return;
    }

    List<ItemStack> outputs = getPossibleOutputs(input);
    if (outputs.isEmpty()) {
      seenInputs.remove(key);
      return;
    }

    recipes.add(new JeiRecipe(
        name,
        singletonOutput(input),
        outputs,
        1.0f,
        true,
        isAutomaticMineralInput(input)
    ));
  }

  private static List<ItemStack> getMatchingStacks(Ingredient ingredient) {

    List<ItemStack> result = new ArrayList<>();
    for (ItemStack stack : ingredient.getMatchingStacks()) {
      if (!stack.isEmpty()) {
        result.add(stack.copy());
      }
    }
    return result;
  }

  public static List<ItemStack> getPossibleOutputs(ItemStack input) {

    List<ItemStack> result = new ArrayList<>();
    if (input.isEmpty()) {
      return result;
    }

    CustomRecipe customRecipe = findCustomRecipe(input);
    if (customRecipe != null) {
      return singletonOutput(customRecipe.output);
    }

    if (!isValidInput(input)) {
      return result;
    }

    String material = findMaterial(input);
    ItemStack oreOutput = findOreOutput(material);

    if (!oreOutput.isEmpty()) {
      addUniqueOutput(result, oreOutput);
    }

    if (input.getItem() == Item.getItemFromBlock(Blocks.GRAVEL)) {
      addUniqueOutput(result, new ItemStack(Items.FLINT));
    } else if (isAutomaticMineralInput(input)) {
      addUniqueOutput(result, new ItemStack(Blocks.GRAVEL));
    }

    ItemStack gemOutput = findFirst("gem" + capitalize(material));
    if (!gemOutput.isEmpty()) {
      gemOutput.setCount(1);
      addUniqueOutput(result, gemOutput);
    }

    if (result.isEmpty() && input.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
      addUniqueOutput(result, new ItemStack(Blocks.GRAVEL));
    }
    if (result.isEmpty() && input.getItem() == Item.getItemFromBlock(Blocks.SOUL_SAND)) {
      addUniqueOutput(result, new ItemStack(Blocks.GRAVEL));
    }

    return result;
  }

  private static void addUniqueOutput(List<ItemStack> outputs, ItemStack output) {

    if (output.isEmpty()) {
      return;
    }

    String key = getStackKey(output);
    for (ItemStack existing : outputs) {
      if (getStackKey(existing).equals(key)) {
        return;
      }
    }
    outputs.add(output.copy());
  }

  private static List<ItemStack> singletonOutput(ItemStack stack) {

    List<ItemStack> result = new ArrayList<>(1);
    result.add(stack.copy());
    return result;
  }

  private static String getStackKey(ItemStack stack) {

    ResourceLocation registryName = stack.getItem().getRegistryName();
    return (registryName == null ? "unknown" : registryName.toString()) + ":" + stack.getMetadata();
  }

  private static float clampChance(float chance) {

    if (Float.isNaN(chance)) {
      return 0.0f;
    }
    return Math.max(0.0f, Math.min(1.0f, chance));
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
    private final float chance;

    private CustomRecipe(String name, ItemStack output, Ingredient input, float chance) {

      this.name = name;
      this.output = output.copy();
      this.input = input;
      this.chance = chance;
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

    @Nullable
    private ItemStack getOutput(Random random) {

      return random.nextFloat() < this.chance ? this.output.copy() : null;
    }
  }

  public static final class JeiRecipe {

    private final String name;
    private final List<ItemStack> inputs;
    private final List<ItemStack> outputs;
    private final float chance;
    private final boolean automatic;
    private final boolean mineral;

    private JeiRecipe(String name, List<ItemStack> inputs, List<ItemStack> outputs, float chance,
        boolean automatic, boolean mineral) {

      this.name = name;
      this.inputs = copyStacks(inputs);
      this.outputs = copyStacks(outputs);
      this.chance = chance;
      this.automatic = automatic;
      this.mineral = mineral;
    }

    public String getName() {

      return this.name;
    }

    public List<ItemStack> getInputs() {

      return copyStacks(this.inputs);
    }

    public List<ItemStack> getOutputs() {

      return copyStacks(this.outputs);
    }

    public float getChance() {

      return this.chance;
    }

    public boolean isAutomatic() {

      return this.automatic;
    }

    public boolean isMineral() {

      return this.mineral;
    }

    private static List<ItemStack> copyStacks(List<ItemStack> stacks) {

      List<ItemStack> result = new ArrayList<>(stacks.size());
      for (ItemStack stack : stacks) {
        result.add(stack.copy());
      }
      return result;
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
