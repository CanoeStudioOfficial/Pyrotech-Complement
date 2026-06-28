package com.canoestudios.pyrotechcomplement.init;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.recipe.ForgingTableRecipe;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import com.canoestudios.pyrotechcomplement.recipe.PrimitiveBloomeryRecipe;
import com.codetaylor.mc.pyrotech.modules.core.ModuleCore;
import com.codetaylor.mc.pyrotech.modules.core.item.ItemMaterial;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.registries.IForgeRegistryModifiable;

public final class ModRecipes {

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
        new OreIngredient("oreIron"),
        1,
        Ingredient.fromStacks(new ItemStack(Items.COAL, 1, 0)),
        1,
        20 * 60 * 6,
        12,
        15,
        0.25f,
        "pyrotech:bloom_from_oreiron",
        "tile.oreIron"
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_charcoal",
        new OreIngredient("oreIron"),
        1,
        Ingredient.fromStacks(new ItemStack(Items.COAL, 1, 1)),
        1,
        20 * 60 * 6,
        12,
        15,
        0.25f,
        "pyrotech:bloom_from_oreiron",
        "tile.oreIron"
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_coal_pieces",
        new OreIngredient("oreIron"),
        1,
        Ingredient.fromStacks(ItemMaterial.EnumType.COAL_PIECES.asStack()),
        2,
        20 * 60 * 6,
        12,
        15,
        0.25f,
        "pyrotech:bloom_from_oreiron",
        "tile.oreIron"
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_charcoal_flakes",
        new OreIngredient("oreIron"),
        1,
        Ingredient.fromStacks(ItemMaterial.EnumType.CHARCOAL_FLAKES.asStack()),
        2,
        20 * 60 * 6,
        12,
        15,
        0.25f,
        "pyrotech:bloom_from_oreiron",
        "tile.oreIron"
    );

    registerPrimitiveBloomeryBloom("iron_bloom_from_ore_and_coal_coke",
        new OreIngredient("oreIron"),
        1,
        new OreIngredient("fuelCoke"),
        1,
        20 * 60 * 5,
        12,
        15,
        0.25f,
        "pyrotech:bloom_from_oreiron",
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

  public static void registerPrimitiveBloomeryBloom(String name, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks, int bloomYieldMin, int bloomYieldMax, float experience, String bloomRecipeId, String bloomLangKey) {

    PrimitiveBloomeryRecipe recipe = new PrimitiveBloomeryRecipe(input, inputCount, fuel, fuelCount, burnTimeTicks, bloomYieldMin, bloomYieldMax, experience, bloomRecipeId, bloomLangKey);
    PRIMITIVE_BLOOMERY_RECIPES.register(recipe.setRegistryName(new ResourceLocation(Tags.MOD_ID, name)));
  }

  private ModRecipes() {
    //
  }
}
