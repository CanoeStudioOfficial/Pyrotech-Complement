package com.canoestudios.pyrotechcomplement.plugin.jei;

import com.canoestudios.pyrotechcomplement.init.ModBlocks;
import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.canoestudios.pyrotechcomplement.plugin.jei.category.JEIRecipeCategoryForgingTable;
import com.canoestudios.pyrotechcomplement.plugin.jei.category.JEIRecipeCategoryLoom;
import com.canoestudios.pyrotechcomplement.plugin.jei.wrapper.JEIRecipeWrapperForgingTable;
import com.canoestudios.pyrotechcomplement.plugin.jei.wrapper.JEIRecipeWrapperLoom;
import com.canoestudios.pyrotechcomplement.recipe.ForgingTableRecipe;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginJEI
    implements IModPlugin {

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {

    IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    registry.addRecipeCategories(
        new JEIRecipeCategoryLoom(guiHelper),
        new JEIRecipeCategoryForgingTable(guiHelper)
    );
  }

  @Override
  public void register(IModRegistry registry) {

    registry.addRecipeCatalyst(new ItemStack(ModBlocks.CRUDE_LOOM), JEIRecipeCategoryLoom.UID);
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.LOOM), JEIRecipeCategoryLoom.UID);
    registry.handleRecipes(LoomRecipe.class, JEIRecipeWrapperLoom::new, JEIRecipeCategoryLoom.UID);
    registry.addRecipes(this.getLoomRecipes(), JEIRecipeCategoryLoom.UID);

    registry.addRecipeCatalyst(new ItemStack(ModBlocks.FORGING_TABLE_GRANITE), JEIRecipeCategoryForgingTable.UID);
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.FORGING_TABLE_OBSIDIAN), JEIRecipeCategoryForgingTable.UID);
    registry.addRecipeCatalyst(new ItemStack(ModBlocks.FORGING_TABLE_IRONCLAD), JEIRecipeCategoryForgingTable.UID);
    registry.handleRecipes(ForgingTableRecipe.class, JEIRecipeWrapperForgingTable::new, JEIRecipeCategoryForgingTable.UID);
    registry.addRecipes(this.getForgingTableRecipes(), JEIRecipeCategoryForgingTable.UID);
  }

  private List<LoomRecipe> getLoomRecipes() {

    if (ModRecipes.LOOM_RECIPES == null) {
      ModRecipes.initRegistry();
    }

    if (ModRecipes.LOOM_RECIPES == null) {
      return Collections.emptyList();
    }

    return new ArrayList<>(ModRecipes.LOOM_RECIPES.getValuesCollection());
  }

  private List<ForgingTableRecipe> getForgingTableRecipes() {

    if (ModRecipes.FORGING_TABLE_RECIPES == null) {
      ModRecipes.initRegistry();
    }

    if (ModRecipes.FORGING_TABLE_RECIPES == null) {
      return Collections.emptyList();
    }

    return new ArrayList<>(ModRecipes.FORGING_TABLE_RECIPES.getValuesCollection());
  }
}
