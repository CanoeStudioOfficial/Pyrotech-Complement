package com.canoestudios.pyrotechcomplement.init;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
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

  @SuppressWarnings("unchecked")
  public static void initRegistry() {

    LOOM_RECIPES = (IForgeRegistryModifiable<LoomRecipe>) GameRegistry.findRegistry(LoomRecipe.class);
  }

  public static void registerDefaults() {

    if (LOOM_RECIPES == null) {
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
  }

  public static void register(String name, ItemStack output, Ingredient input, int inputCount, int steps, ResourceLocation texture) {

    LoomRecipe recipe = new LoomRecipe(output, input, inputCount, steps, texture);
    LOOM_RECIPES.register(recipe.setRegistryName(new ResourceLocation(Tags.MOD_ID, name)));
  }

  public static void registerOre(String name, ItemStack output, String inputOre, int inputCount, int steps, ResourceLocation texture) {

    register(name, output, new OreIngredient(inputOre), inputCount, steps, texture);
  }

  private ModRecipes() {
    //
  }
}
