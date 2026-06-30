package com.canoestudios.pyrotechcomplement.init;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.recipe.ForgingTableRecipe;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import com.canoestudios.pyrotechcomplement.recipe.PrimitiveBloomeryRecipe;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCore;
import com.codetaylor.mc.pyrotech.modules.core.item.ItemMaterial;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.ModuleTechBloomery;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.ModuleTechBloomeryConfig;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomAnvilRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipeBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public final class ModRecipes {

  private static final int DEFAULT_BLOOMERY_BURN_TIME_TICKS = 24 * 60 * 20;
  private static final float DEFAULT_BLOOMERY_FAILURE_CHANCE = 0.25f;
  private static final float DEFAULT_BLOOMERY_EXPERIENCE = 0.25f;
  private static final int DEFAULT_BLOOMERY_BLOOM_YIELD_MIN = 12;
  private static final int DEFAULT_BLOOMERY_BLOOM_YIELD_MAX = 15;

  public static IForgeRegistryModifiable<LoomRecipe> LOOM_RECIPES;
  public static IForgeRegistryModifiable<ForgingTableRecipe> FORGING_TABLE_RECIPES;
  public static IForgeRegistryModifiable<PrimitiveBloomeryRecipe> PRIMITIVE_BLOOMERY_RECIPES;

  @SuppressWarnings("unchecked")
  public static void initRegistry() {

    LOOM_RECIPES = (IForgeRegistryModifiable<LoomRecipe>) GameRegistry.findRegistry(LoomRecipe.class);
    FORGING_TABLE_RECIPES = (IForgeRegistryModifiable<ForgingTableRecipe>) GameRegistry.findRegistry(ForgingTableRecipe.class);
    PRIMITIVE_BLOOMERY_RECIPES = (IForgeRegistryModifiable<PrimitiveBloomeryRecipe>) GameRegistry.findRegistry(PrimitiveBloomeryRecipe.class);
  }

  public static void registerDefaults() {

    if (LOOM_RECIPES == null || FORGING_TABLE_RECIPES == null || PRIMITIVE_BLOOMERY_RECIPES == null) {
      initRegistry();
    }

    register("twine_from_plant_fibers",
        new ItemStack(com.codetaylor.mc.pyrotech.modules.core.ModuleCore.Items.MATERIAL, 1, ItemMaterial.EnumType.TWINE.getMeta()),
        Ingredient.fromStacks(new ItemStack(com.codetaylor.mc.pyrotech.modules.core.ModuleCore.Items.MATERIAL, 1, ItemMaterial.EnumType.PLANT_FIBERS.getMeta())),
        4,
        6,
        new ResourceLocation("pyrotech", "blocks/drying_rack_crude")
    );

    register("wool_from_string",
        new ItemStack(Blocks.WOOL),
        Ingredient.fromStacks(new ItemStack(Items.STRING)),
        4,
        8,
        new ResourceLocation("minecraft", "blocks/wool_colored_white")
    );

    register("leather_sheet_from_leather",
        new ItemStack(com.codetaylor.mc.pyrotech.modules.core.ModuleCore.Items.MATERIAL, 1, ItemMaterial.EnumType.LEATHER_SHEET.getMeta()),
        Ingredient.fromStacks(new ItemStack(Items.LEATHER)),
        2,
        8,
        new ResourceLocation("pyrotech", "blocks/bag_top_cloth")
    );

    registerForgingTable("flint_shards_from_flint",
        ItemMaterial.EnumType.FLINT_SHARD.asStack(3),
        Ingredient.fromStacks(new ItemStack(Items.FLINT)),
        1,
        4
    );

    registerForgingTable("bone_shards_from_bone",
        ItemMaterial.EnumType.BONE_SHARD.asStack(3),
        Ingredient.fromStacks(new ItemStack(Items.BONE)),
        1,
        4
    );

    registerForgingTable("iron_shards_from_ingot",
        ItemMaterial.EnumType.IRON_SHARD.asStack(9),
        new OreIngredient("ingotIron"),
        1,
        6
    );

    registerForgingTable("stone_tool_shaft_from_rod_and_flint",
        ItemMaterial.EnumType.STONE_TOOL_SHAFT.asStack(1),
        new OreIngredient("stickStone"),
        1,
        Ingredient.fromStacks(ItemMaterial.EnumType.FLINT_SHARD.asStack()),
        1,
        4
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_coal",
        new ItemStack(Items.IRON_NUGGET),
        new OreIngredient("oreIron"),
        1,
        new OreIngredient("coal"),
        1,
        DEFAULT_BLOOMERY_BURN_TIME_TICKS,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MIN,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MAX,
        DEFAULT_BLOOMERY_EXPERIENCE,
        DEFAULT_BLOOMERY_FAILURE_CHANCE,
        "tile.oreIron"
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_charcoal",
        new ItemStack(Items.IRON_NUGGET),
        new OreIngredient("oreIron"),
        1,
        Ingredient.fromStacks(new ItemStack(Items.COAL, 1, 1)),
        1,
        DEFAULT_BLOOMERY_BURN_TIME_TICKS,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MIN,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MAX,
        DEFAULT_BLOOMERY_EXPERIENCE,
        DEFAULT_BLOOMERY_FAILURE_CHANCE,
        "tile.oreIron"
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_coal_pieces",
        new ItemStack(Items.IRON_NUGGET),
        new OreIngredient("oreIron"),
        1,
        Ingredient.fromStacks(ItemMaterial.EnumType.COAL_PIECES.asStack()),
        2,
        DEFAULT_BLOOMERY_BURN_TIME_TICKS,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MIN,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MAX,
        DEFAULT_BLOOMERY_EXPERIENCE,
        DEFAULT_BLOOMERY_FAILURE_CHANCE,
        "tile.oreIron"
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_charcoal_flakes",
        new ItemStack(Items.IRON_NUGGET),
        new OreIngredient("oreIron"),
        1,
        Ingredient.fromStacks(ItemMaterial.EnumType.CHARCOAL_FLAKES.asStack()),
        2,
        DEFAULT_BLOOMERY_BURN_TIME_TICKS,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MIN,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MAX,
        DEFAULT_BLOOMERY_EXPERIENCE,
        DEFAULT_BLOOMERY_FAILURE_CHANCE,
        "tile.oreIron"
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_coal_coke",
        new ItemStack(Items.IRON_NUGGET),
        new OreIngredient("oreIron"),
        1,
        new OreIngredient("fuelCoke"),
        1,
        20 * 60 * 5,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MIN,
        DEFAULT_BLOOMERY_BLOOM_YIELD_MAX,
        DEFAULT_BLOOMERY_EXPERIENCE,
        DEFAULT_BLOOMERY_FAILURE_CHANCE,
        "tile.oreIron"
    );
  }

  public static void register(String name, ItemStack output, Ingredient input, int inputCount, int steps, ResourceLocation texture) {

    LoomRecipe recipe = new LoomRecipe(output, input, inputCount, steps, texture);
    LOOM_RECIPES.register(recipe.setRegistryName(new ResourceLocation(Tags.MOD_ID, name)));
  }

  public static void registerOre(String name, ItemStack output, String inputOre, int inputCount, int steps, ResourceLocation texture) {

    register(name, output, new OreIngredient(inputOre), inputCount, steps, texture);
  }

  public static void registerForgingTable(String name, ItemStack output, Ingredient input, int inputCount, int hits) {

    ForgingTableRecipe recipe = new ForgingTableRecipe(output, input, inputCount, hits);
    FORGING_TABLE_RECIPES.register(recipe.setRegistryName(new ResourceLocation(Tags.MOD_ID, name)));
  }

  public static void registerForgingTable(String name, ItemStack output, Ingredient input, int inputCount, Ingredient secondaryInput, int secondaryInputCount, int hits) {

    ForgingTableRecipe recipe = new ForgingTableRecipe(output, input, inputCount, secondaryInput, secondaryInputCount, hits);
    FORGING_TABLE_RECIPES.register(recipe.setRegistryName(new ResourceLocation(Tags.MOD_ID, name)));
  }

  public static void registerPrimitiveBloomery(String name, ItemStack output, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks) {

    PrimitiveBloomeryRecipe recipe = new PrimitiveBloomeryRecipe(output, input, inputCount, fuel, fuelCount, burnTimeTicks);
    PRIMITIVE_BLOOMERY_RECIPES.register(recipe.setRegistryName(new ResourceLocation(Tags.MOD_ID, name)));
  }

  public static void registerPrimitiveBloomeryBloom(String name, ItemStack output, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks, int bloomYieldMin, int bloomYieldMax, float experience, float failureChance, String bloomLangKey) {

    PrimitiveBloomeryRecipe recipe = createPrimitiveBloomeryBloomRecipe(output, input, inputCount, fuel, fuelCount, burnTimeTicks, bloomYieldMin, bloomYieldMax, experience, failureChance, bloomLangKey);
    recipe.setRegistryName(new ResourceLocation(Tags.MOD_ID, name));
    registerPrimitiveBloomeryRecipe(recipe);
  }

  public static PrimitiveBloomeryRecipe createPrimitiveBloomeryBloomRecipe(ItemStack output, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks, int bloomYieldMin, int bloomYieldMax, float experience, float failureChance, String bloomLangKey) {

    return new PrimitiveBloomeryRecipe(
        output,
        input,
        inputCount,
        fuel,
        fuelCount,
        burnTimeTicks,
        bloomYieldMin,
        bloomYieldMax,
        experience,
        failureChance,
        4,
        defaultSlagItem(),
        defaultBloomFailureItems(),
        new AnvilRecipe.EnumTier[]{
            AnvilRecipe.EnumTier.GRANITE,
            AnvilRecipe.EnumTier.IRONCLAD,
            AnvilRecipe.EnumTier.OBSIDIAN
        },
        bloomLangKey
    );
  }

  public static void registerPrimitiveBloomeryRecipe(PrimitiveBloomeryRecipe recipe) {

    PRIMITIVE_BLOOMERY_RECIPES.register(recipe);

    if (recipe.hasGeneratedBloomOutput()) {
      registerPrimitiveBloomeryAnvilRecipe(recipe);
    }
  }

  public static void registerPrimitiveBloomeryAnvilRecipe(PrimitiveBloomeryRecipe recipe) {

    ResourceLocation registryName = recipe.getRegistryName();
    if (registryName == null
        || ModuleTechBasic.Registries.ANVIL_RECIPE == null
        || ModuleTechBloomery.Items.BLOOM == null) {
      return;
    }

    removePrimitiveBloomeryAnvilRecipe(registryName);
    ModuleTechBasic.Registries.ANVIL_RECIPE.register(new BloomAnvilRecipe(
        recipe.getOutput(),
        Ingredient.fromStacks(recipe.getOutputBloom()),
        ModuleTechBloomeryConfig.BLOOM.HAMMER_HITS_IN_ANVIL_REQUIRED,
        AnvilRecipe.EnumType.HAMMER,
        recipe.getAnvilTiers(),
        recipe
    ).setRegistryName(registryName));
  }

  public static void removePrimitiveBloomeryAnvilRecipe(ResourceLocation registryName) {

    if (ModuleTechBasic.Registries.ANVIL_RECIPE != null) {
      ModuleTechBasic.Registries.ANVIL_RECIPE.remove(registryName);
    }
  }

  public static void removeAllPrimitiveBloomeryRecipes() {

    if (PRIMITIVE_BLOOMERY_RECIPES == null) {
      return;
    }

    for (PrimitiveBloomeryRecipe recipe : PRIMITIVE_BLOOMERY_RECIPES.getValuesCollection().toArray(new PrimitiveBloomeryRecipe[0])) {
      ResourceLocation registryName = recipe.getRegistryName();

      if (registryName != null) {
        removePrimitiveBloomeryAnvilRecipe(registryName);
        PRIMITIVE_BLOOMERY_RECIPES.remove(registryName);
      }
    }
  }

  private static ItemStack defaultSlagItem() {

    if (ModuleTechBloomery.Items.SLAG == null) {
      return ItemStack.EMPTY;
    }

    return new ItemStack(ModuleTechBloomery.Items.SLAG);
  }

  private static BloomeryRecipeBase.FailureItem[] defaultBloomFailureItems() {

    if (ModuleTechBloomery.Items.SLAG == null || ModuleCore.Blocks.ROCK == null) {
      return new BloomeryRecipeBase.FailureItem[0];
    }

    return new BloomeryRecipeBase.FailureItem[]{
        new BloomeryRecipeBase.FailureItem(new ItemStack(ModuleTechBloomery.Items.SLAG), 2),
        new BloomeryRecipeBase.FailureItem(new ItemStack(ModuleCore.Blocks.ROCK), 1)
    };
  }

  private ModRecipes() {
    //
  }
}
