package com.canoestudios.pyrotechcomplement.plugin.crafttweaker;

import com.codetaylor.mc.athenaeum.integration.crafttweaker.mtlib.helpers.CTLogHelper;
import com.codetaylor.mc.athenaeum.util.StackHelper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.ModuleTechBasic;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.ModuleTechBloomery;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.init.recipe.BloomeryRecipesAdd;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipeBase;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipeBuilder;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Arrays;

@ZenClass("mods.pyrotechcomplement.Bloomery")
public class ZenBloomery {

  @ZenMethod
  public static void setHammerOutput(String recipeId, IItemStack output) {

    CraftTweaker.LATE_ACTIONS.add(new SetHammerOutput(recipeId, CraftTweakerMC.getItemStack(output)));
  }

  public static class SetHammerOutput
      implements IAction {

    private final String recipeId;
    private final ItemStack output;

    public SetHammerOutput(String recipeId, ItemStack output) {

      this.recipeId = recipeId;
      this.output = output;
    }

    @Override
    public void apply() {

      ResourceLocation registryName = this.parseRecipeId(this.recipeId);
      BloomeryRecipe existing = ModuleTechBloomery.Registries.BLOOMERY_RECIPE.getValue(registryName);

      if (existing == null) {
        CTLogHelper.logError("Unable to find Pyrotech bloomery recipe for id: " + registryName);
        return;
      }

      BloomeryRecipe replacement = this.copyWithOutput(registryName, existing, this.output);

      ModuleTechBloomery.Registries.BLOOMERY_RECIPE.remove(registryName);
      ModuleTechBloomery.Registries.BLOOMERY_RECIPE.register(replacement);

      if (ModuleTechBasic.Registries.ANVIL_RECIPE != null) {
        ModuleTechBasic.Registries.ANVIL_RECIPE.remove(registryName);
        BloomeryRecipesAdd.registerBloomAnvilRecipe(ModuleTechBasic.Registries.ANVIL_RECIPE, replacement);
      }
    }

    @Override
    public String describe() {

      return "Setting Pyrotech bloomery hammer output for " + this.recipeId + " to " + this.output;
    }

    private ResourceLocation parseRecipeId(String recipeId) {

      if (recipeId.indexOf(':') == -1) {
        return new ResourceLocation(ModuleTechBloomery.MOD_ID, recipeId);
      }

      return new ResourceLocation(recipeId);
    }

    private BloomeryRecipe copyWithOutput(ResourceLocation registryName, BloomeryRecipe existing, ItemStack output) {

      BloomeryRecipeBuilder builder = new BloomeryRecipeBuilder(registryName, output.copy(), existing.getInput())
          .setBurnTimeTicks(existing.getTimeTicks())
          .setExperience(this.getExperience(existing))
          .setFailureChance(existing.getFailureChance())
          .setBloomYield(existing.getBloomYieldMin(), existing.getBloomYieldMax())
          .setSlagItem(existing.getSlagItemStack(), existing.getSlagCount())
          .setAnvilTiers(Arrays.copyOf(existing.getAnvilTiers(), existing.getAnvilTiers().length))
          .setLangKey(existing.getLangKey());

      for (BloomeryRecipeBase.FailureItem failureItem : existing.getFailureItems()) {
        builder.addFailureItem(failureItem.getItemStack().copy(), failureItem.getWeight());
      }

      return builder.create();
    }

    private float getExperience(BloomeryRecipeBase<?> recipe) {

      ItemStack outputBloom = recipe.getOutputBloom();
      NBTTagCompound tag = outputBloom.getTagCompound();

      if (tag == null) {
        return 0;
      }

      NBTTagCompound tileTag = tag.getCompoundTag(StackHelper.BLOCK_ENTITY_TAG);
      return tileTag.getFloat("experiencePerComplete");
    }
  }
}
