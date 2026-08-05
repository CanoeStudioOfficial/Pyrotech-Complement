package com.canoestudios.pyrotechcomplement.recipe;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.codetaylor.mc.athenaeum.recipe.IRecipeSingleOutput;
import com.codetaylor.mc.athenaeum.util.RecipeHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class LoomRecipe
    extends IForgeRegistryEntry.Impl<LoomRecipe>
    implements IRecipeSingleOutput {

  public static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("pyrotech", "blocks/drying_rack_crude");

  @Nullable
  public static LoomRecipe getRecipe(ItemStack input) {

    if (input.isEmpty() || ModRecipes.LOOM_RECIPES == null) {
      return null;
    }

    for (LoomRecipe recipe : ModRecipes.LOOM_RECIPES) {
      if (recipe.matches(input)) {
        return recipe;
      }
    }

    return null;
  }

  public static boolean removeRecipes(Ingredient output) {

    return RecipeHelper.removeRecipesByOutput(ModRecipes.LOOM_RECIPES, output);
  }

  /**
   * Resolves the CraftTweaker rendering selector. Named selectors keep common
   * recipes readable, while a full resource location remains supported for
   * pack-specific textures.
   */
  public static ResourceLocation resolveTexture(@Nullable String selector) {

    if (selector == null || selector.trim().isEmpty()) {
      return DEFAULT_TEXTURE;
    }

    String value = selector.trim();
    String key = value.toLowerCase(java.util.Locale.ROOT).replace('-', '_').replace(' ', '_');
    if (key.equals("line") || key.equals("string") || key.equals("thread")
        || key.equals("yarn") || key.equals("wool")) {
      return new ResourceLocation("minecraft", "blocks/wool_colored_white");
    }
    if (key.equals("plant") || key.equals("fiber") || key.equals("plant_fiber")
        || key.equals("plant_fibers")) {
      return new ResourceLocation("pyrotech", "blocks/thatch");
    }
    if (key.equals("cloth") || key.equals("fabric") || key.equals("leather")) {
      return new ResourceLocation("pyrotech", "blocks/bag_top_cloth");
    }
    if (key.equals("crude") || key.equals("drying_rack") || key.equals("drying_rack_crude")) {
      return DEFAULT_TEXTURE;
    }

    try {
      ResourceLocation location = new ResourceLocation(value);
      String path = location.getPath();
      // TFC 1.21 names these textures "block/..."; Forge 1.12 block atlas
      // entries use the older "blocks/..." path.
      if (path.startsWith("block/")) {
        path = "blocks/" + path.substring("block/".length());
      }
      return new ResourceLocation(location.getNamespace(), path);
    } catch (RuntimeException ignored) {
      return DEFAULT_TEXTURE;
    }
  }

  private final ItemStack output;
  private final Ingredient input;
  private final int inputCount;
  private final int steps;
  private final ResourceLocation texture;

  public LoomRecipe(ItemStack output, Ingredient input, int inputCount, int steps, ResourceLocation texture) {

    this.output = output.copy();
    this.input = input;
    this.inputCount = Math.max(1, inputCount);
    this.steps = Math.max(1, steps);
    this.texture = texture;
  }

  public boolean matches(ItemStack input) {

    return this.input.apply(input);
  }

  public Ingredient getInput() {

    return this.input;
  }

  public int getInputCount() {

    return this.inputCount;
  }

  public int getSteps() {

    return this.steps;
  }

  public ResourceLocation getTexture() {

    return this.texture;
  }

  @Override
  public ItemStack getOutput() {

    return this.output.copy();
  }
}
