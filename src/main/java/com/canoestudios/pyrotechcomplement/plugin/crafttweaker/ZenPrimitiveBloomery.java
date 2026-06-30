package com.canoestudios.pyrotechcomplement.plugin.crafttweaker;

import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.canoestudios.pyrotechcomplement.recipe.PrimitiveBloomeryRecipe;
import com.codetaylor.mc.athenaeum.integration.crafttweaker.mtlib.helpers.CTLogHelper;
import com.codetaylor.mc.pyrotech.modules.tech.basic.recipe.AnvilRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.ModuleTechBloomery;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipe;
import com.codetaylor.mc.pyrotech.modules.tech.bloomery.recipe.BloomeryRecipeBase;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.CraftTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreIngredient;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ZenClass("mods.pyrotechcomplement.PrimitiveBloomery")
public class ZenPrimitiveBloomery {

  @ZenMethod
  public static void addRecipe(String name, IItemStack output, IIngredient input, int inputCount, IIngredient fuel, int fuelCount, int burnTimeTicks) {

    CraftTweaker.LATE_ACTIONS.add(new AddSimpleRecipe(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input),
        inputCount,
        CraftTweakerMC.getIngredient(fuel),
        fuelCount,
        burnTimeTicks
    ));
  }

  @ZenMethod
  public static void addBloomRecipe(String name, IIngredient input, int inputCount, IIngredient fuel, int fuelCount, int burnTimeTicks, int bloomYieldMin, int bloomYieldMax, float experience, String bloomRecipeId, String bloomLangKey) {

    CraftTweaker.LATE_ACTIONS.add(new AddLegacyBloomRecipe(
        name,
        CraftTweakerMC.getIngredient(input),
        inputCount,
        CraftTweakerMC.getIngredient(fuel),
        fuelCount,
        burnTimeTicks,
        bloomYieldMin,
        bloomYieldMax,
        experience,
        bloomRecipeId,
        bloomLangKey
    ));
  }

  @ZenMethod
  public static ZenPrimitiveBloomery createBloomeryBuilder(String name, IItemStack output, IIngredient input) {

    return new ZenPrimitiveBloomery(
        name,
        CraftTweakerMC.getItemStack(output),
        CraftTweakerMC.getIngredient(input)
    );
  }

  @ZenMethod
  public static void removeRecipes(IIngredient output) {

    CraftTweaker.LATE_ACTIONS.add(new RemoveRecipe(CraftTweakerMC.getIngredient(output)));
  }

  @ZenMethod
  public static void removeAllRecipes() {

    CraftTweaker.LATE_ACTIONS.add(new RemoveAllRecipes());
  }

  private final String name;
  private final ItemStack output;
  private final Ingredient input;
  private int inputCount;
  private Ingredient fuel;
  private int fuelCount;
  private int burnTimeTicks;
  private float experience;
  private float failureChance;
  private int bloomYieldMin;
  private int bloomYieldMax;
  private int slagCount;
  private ItemStack slagItem;
  private final List<BloomeryRecipeBase.FailureItem> failureItems;
  private AnvilRecipe.EnumTier[] anvilTiers;
  @Nullable
  private String langKey;

  private ZenPrimitiveBloomery(String name, ItemStack output, Ingredient input) {

    this.name = name;
    this.output = output;
    this.input = input;
    this.inputCount = 1;
    this.fuel = new OreIngredient("coal");
    this.fuelCount = 1;
    this.burnTimeTicks = 24 * 60 * 20;
    this.experience = 0.25f;
    this.failureChance = 0.25f;
    this.bloomYieldMin = 12;
    this.bloomYieldMax = 15;
    this.slagCount = 4;
    this.slagItem = ModuleTechBloomery.Items.SLAG == null ? ItemStack.EMPTY : new ItemStack(ModuleTechBloomery.Items.SLAG);
    this.failureItems = new ArrayList<>();
    this.anvilTiers = AnvilRecipe.EnumTier.values();
  }

  @ZenMethod
  public ZenPrimitiveBloomery setInputCount(int inputCount) {

    this.inputCount = inputCount;
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery setFuel(IIngredient fuel, @Optional int fuelCount) {

    this.fuel = CraftTweakerMC.getIngredient(fuel);
    this.fuelCount = Math.max(1, fuelCount);
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery setBurnTimeTicks(int burnTimeTicks) {

    this.burnTimeTicks = burnTimeTicks;
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery setExperience(float experience) {

    this.experience = experience;
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery setFailureChance(float failureChance) {

    this.failureChance = failureChance;
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery setBloomYield(int min, int max) {

    this.bloomYieldMin = min;
    this.bloomYieldMax = max;
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery setSlagItem(IItemStack slagItem, int slagCount) {

    this.slagItem = CraftTweakerMC.getItemStack(slagItem);
    this.slagCount = slagCount;
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery addFailureItem(IItemStack itemStack, int weight) {

    this.failureItems.add(new BloomeryRecipeBase.FailureItem(CraftTweakerMC.getItemStack(itemStack), weight));
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery setLangKey(String langKey) {

    this.langKey = langKey;
    return this;
  }

  @ZenMethod
  public ZenPrimitiveBloomery setAnvilTiers(String[] tiers) {

    AnvilRecipe.EnumTier[] parsed = new AnvilRecipe.EnumTier[tiers.length];

    for (int i = 0; i < tiers.length; i++) {
      parsed[i] = AnvilRecipe.EnumTier.valueOf(tiers[i].toUpperCase());
    }

    this.anvilTiers = parsed;
    return this;
  }

  @ZenMethod
  public void register() {

    CraftTweaker.LATE_ACTIONS.add(new AddBloomRecipe(this.createRecipe()));
  }

  private PrimitiveBloomeryRecipe createRecipe() {

    PrimitiveBloomeryRecipe recipe = new PrimitiveBloomeryRecipe(
        this.output,
        this.input,
        this.inputCount,
        this.fuel,
        this.fuelCount,
        this.burnTimeTicks,
        this.bloomYieldMin,
        this.bloomYieldMax,
        this.experience,
        this.failureChance,
        this.slagCount,
        this.slagItem,
        this.failureItems.toArray(new BloomeryRecipeBase.FailureItem[0]),
        Arrays.copyOf(this.anvilTiers, this.anvilTiers.length),
        this.langKey
    );

    recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name));
    return recipe;
  }

  public static class AddSimpleRecipe
      implements IAction {

    private final String name;
    private final ItemStack output;
    private final Ingredient input;
    private final int inputCount;
    private final Ingredient fuel;
    private final int fuelCount;
    private final int burnTimeTicks;

    public AddSimpleRecipe(String name, ItemStack output, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks) {

      this.name = name;
      this.output = output;
      this.input = input;
      this.inputCount = inputCount;
      this.fuel = fuel;
      this.fuelCount = fuelCount;
      this.burnTimeTicks = burnTimeTicks;
    }

    @Override
    public void apply() {

      PrimitiveBloomeryRecipe recipe = new PrimitiveBloomeryRecipe(this.output, this.input, this.inputCount, this.fuel, this.fuelCount, this.burnTimeTicks);
      ModRecipes.PRIMITIVE_BLOOMERY_RECIPES.register(recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name)));
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement primitive bloomery recipe for " + this.output;
    }
  }

  public static class AddLegacyBloomRecipe
      implements IAction {

    private final String name;
    private final Ingredient input;
    private final int inputCount;
    private final Ingredient fuel;
    private final int fuelCount;
    private final int burnTimeTicks;
    private final int bloomYieldMin;
    private final int bloomYieldMax;
    private final float experience;
    private final String bloomRecipeId;
    @Nullable
    private final String bloomLangKey;

    public AddLegacyBloomRecipe(String name, Ingredient input, int inputCount, Ingredient fuel, int fuelCount, int burnTimeTicks, int bloomYieldMin, int bloomYieldMax, float experience, String bloomRecipeId, @Nullable String bloomLangKey) {

      this.name = name;
      this.input = input;
      this.inputCount = inputCount;
      this.fuel = fuel;
      this.fuelCount = fuelCount;
      this.burnTimeTicks = burnTimeTicks;
      this.bloomYieldMin = bloomYieldMin;
      this.bloomYieldMax = bloomYieldMax;
      this.experience = experience;
      this.bloomRecipeId = bloomRecipeId;
      this.bloomLangKey = bloomLangKey;
    }

    @Override
    public void apply() {

      ItemStack output = ItemStack.EMPTY;
      BloomeryRecipe pyrotechRecipe = ModuleTechBloomery.Registries.BLOOMERY_RECIPE.getValue(new ResourceLocation(this.bloomRecipeId));

      if (pyrotechRecipe != null) {
        output = pyrotechRecipe.getOutput();
      } else {
        CTLogHelper.logError("Unable to find Pyrotech bloomery recipe for id: " + this.bloomRecipeId);
        return;
      }

      PrimitiveBloomeryRecipe recipe = new PrimitiveBloomeryRecipe(
          output,
          this.input,
          this.inputCount,
          this.fuel,
          this.fuelCount,
          this.burnTimeTicks,
          this.bloomYieldMin,
          this.bloomYieldMax,
          this.experience,
          pyrotechRecipe.getFailureChance(),
          pyrotechRecipe.getSlagCount(),
          pyrotechRecipe.getSlagItemStack(),
          pyrotechRecipe.getFailureItems(),
          Arrays.copyOf(pyrotechRecipe.getAnvilTiers(), pyrotechRecipe.getAnvilTiers().length),
          this.bloomLangKey != null ? this.bloomLangKey : pyrotechRecipe.getLangKey()
      );
      recipe.setRegistryName(new ResourceLocation("crafttweaker", this.name));
      ModRecipes.registerPrimitiveBloomeryRecipe(recipe);
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement primitive bloomery bloom recipe " + this.name;
    }
  }

  public static class AddBloomRecipe
      implements IAction {

    private final PrimitiveBloomeryRecipe recipe;

    public AddBloomRecipe(PrimitiveBloomeryRecipe recipe) {

      this.recipe = recipe;
    }

    @Override
    public void apply() {

      ModRecipes.registerPrimitiveBloomeryRecipe(this.recipe);
    }

    @Override
    public String describe() {

      return "Adding pyrotech complement primitive bloomery bloom recipe " + this.recipe.getRegistryName();
    }
  }

  public static class RemoveRecipe
      implements IAction {

    private final Ingredient output;

    public RemoveRecipe(Ingredient output) {

      this.output = output;
    }

    @Override
    public void apply() {

      PrimitiveBloomeryRecipe.removeRecipes(this.output);
    }

    @Override
    public String describe() {

      return "Removing pyrotech complement primitive bloomery recipes for " + this.output;
    }
  }

  public static class RemoveAllRecipes
      implements IAction {

    @Override
    public void apply() {

      ModRecipes.removeAllPrimitiveBloomeryRecipes();
    }

    @Override
    public String describe() {

      return "Removing all pyrotech complement primitive bloomery recipes";
    }
  }
}
