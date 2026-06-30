# Pyrotech Complement

[中文文档](README_CN.md)

Pyrotech Complement is a Minecraft 1.12.2 addon for Pyrotech. It adds a small set of functional blocks inspired by later TerraFirmaCraft-style workflows while keeping the implementation close to Pyrotech's systems and assets.

## Requirements

- Minecraft 1.12.2
- Forge 14.23.5.2847
- Athenaeum
- Pyrotech

Optional integrations:

- CraftTweaker
- JEI
- The One Probe

## Content

- Crude Loom
- Loom
- Granite Forging Table
- Obsidian Forging Table
- Ironclad Forging Table
- Primitive Bloomery

The forging tables use Pyrotech hammer support. Pyrotech hammers, Pyrotech's hammer config list, tool class `hammer`, and ore dictionary hammers such as `toolHammer` are accepted.

The primitive bloomery is a loose, early-game multiblock inspired by TFC's bloomery. Place the bloomery door against an empty internal block, surround the internal block and chimney with stone-like blocks, drop ore and fuel into the chimney, then light the door. The default iron recipes produce Pyrotech blooms.

## CraftTweaker

CraftTweaker classes are registered during late recipe actions, following Pyrotech's recipe integration style.

### Loom

ZenClass:

```zenscript
mods.pyrotechcomplement.Loom
```

Methods:

```zenscript
mods.pyrotechcomplement.Loom.addRecipe(
    string name,
    IItemStack output,
    IIngredient input,
    int inputCount,
    int steps,
    @Optional string texture
);

mods.pyrotechcomplement.Loom.removeRecipes(IIngredient output);
mods.pyrotechcomplement.Loom.removeAllRecipes();
```

Example:

```zenscript
// 4 string -> 1 wool, 8 loom steps.
mods.pyrotechcomplement.Loom.addRecipe(
    "wool_from_string",
    <minecraft:wool>,
    <minecraft:string>,
    4,
    8,
    "minecraft:blocks/wool_colored_white"
);

// Texture is optional. If omitted, a Pyrotech drying rack texture is used.
mods.pyrotechcomplement.Loom.addRecipe(
    "twine_from_fiber",
    <pyrotech:material:14>,
    <pyrotech:material:12>,
    4,
    6
);

mods.pyrotechcomplement.Loom.removeRecipes(<minecraft:wool>);
// mods.pyrotechcomplement.Loom.removeAllRecipes();
```

### Forging Table

ZenClass:

```zenscript
mods.pyrotechcomplement.ForgingTable
```

Methods:

```zenscript
mods.pyrotechcomplement.ForgingTable.addRecipe(
    string name,
    IItemStack output,
    IIngredient input,
    int inputCount,
    int hits
);

mods.pyrotechcomplement.ForgingTable.addRecipe(
    string name,
    IItemStack output,
    IIngredient input,
    int inputCount,
    IIngredient secondaryInput,
    int secondaryInputCount,
    int hits
);

mods.pyrotechcomplement.ForgingTable.removeRecipes(IIngredient output);
mods.pyrotechcomplement.ForgingTable.removeAllRecipes();
```

Single-input example:

```zenscript
// 1 iron ingot -> 9 Pyrotech iron shards, 6 hammer hits.
mods.pyrotechcomplement.ForgingTable.addRecipe(
    "iron_shards_from_ingot",
    <pyrotech:material:19> * 9,
    <ore:ingotIron>,
    1,
    6
);
```

Two-input example:

```zenscript
// 1 stone stick + 1 flint shard -> 1 stone tool shaft, 4 hammer hits.
mods.pyrotechcomplement.ForgingTable.addRecipe(
    "stone_tool_shaft",
    <pyrotech:material:47>,
    <ore:stickStone>,
    1,
    <pyrotech:material:10>,
    1,
    4
);
```

Removal examples:

```zenscript
mods.pyrotechcomplement.ForgingTable.removeRecipes(<pyrotech:material:19>);
// mods.pyrotechcomplement.ForgingTable.removeAllRecipes();
```

### Primitive Bloomery

ZenClass:

```zenscript
mods.pyrotechcomplement.PrimitiveBloomery
```

Methods:

```zenscript
mods.pyrotechcomplement.PrimitiveBloomery.addRecipe(
    string name,
    IItemStack output,
    IIngredient input,
    int inputCount,
    IIngredient fuel,
    int fuelCount,
    int burnTimeTicks
);

mods.pyrotechcomplement.PrimitiveBloomery.createBloomeryBuilder(
    string name,
    IItemStack output,
    IIngredient input
);

builder.setInputCount(int inputCount);
builder.setFuel(IIngredient fuel, @Optional int fuelCount);
builder.setBurnTimeTicks(int burnTimeTicks);
builder.setExperience(float experience);
builder.setFailureChance(float failureChance);
builder.setBloomYield(int min, int max);
builder.setSlagItem(IItemStack slagItem, int slagCount);
builder.addFailureItem(IItemStack itemStack, int weight);
builder.setLangKey(@Optional string langKey);
builder.setAnvilTiers(string[] tiers);
builder.register();

// Legacy compatibility method. bloomRecipeId is only used to read the Pyrotech
// recipe output; new blooms store this mod's CraftTweaker recipe id in NBT.
mods.pyrotechcomplement.PrimitiveBloomery.addBloomRecipe(
    string name,
    IIngredient input,
    int inputCount,
    IIngredient fuel,
    int fuelCount,
    int burnTimeTicks,
    int bloomYieldMin,
    int bloomYieldMax,
    float experience,
    string bloomRecipeId,
    string bloomLangKey
);

mods.pyrotechcomplement.PrimitiveBloomery.removeRecipes(IIngredient output);
mods.pyrotechcomplement.PrimitiveBloomery.removeAllRecipes();
```

Primitive Bloomery bloom recipes follow Pyrotech's Bloomery builder semantics. The `output` parameter in `createBloomeryBuilder(name, output, input)` is not a direct Primitive Bloomery output item. The Primitive Bloomery produces a Pyrotech bloom, and `output` is the item dropped when that bloom is hammered. The generated bloom stores this CraftTweaker recipe id in NBT, and this mod automatically registers the matching Pyrotech `BloomAnvilRecipe`.

`setBloomYield(min, max)` controls how many times the bloom can produce that hammer output.

Example:

```zenscript
// 1 oreIron + 1 coal -> a Pyrotech bloom after 24 minutes.
// Hammering that bloom on a supported Pyrotech anvil drops iron nuggets.
// The bloom stores recipeId "crafttweaker:bloom_from_iron_ore" in NBT.
// setLangKey is optional; omit it to infer the bloom name from the input item.
mods.pyrotechcomplement.PrimitiveBloomery.createBloomeryBuilder(
        "bloom_from_iron_ore",
        <minecraft:iron_nugget>,
        <ore:oreIron>
    )
    .setFuel(<ore:coal>, 1)
    .setAnvilTiers(["granite", "ironclad"])
    .setBurnTimeTicks(28800)
    .setFailureChance(0.25)
    .setBloomYield(12, 15)
    .setSlagItem(<pyrotech:slag>, 4)
    .addFailureItem(<pyrotech:slag>, 2)
    .setLangKey("tile.oreIron")
    .register();
```

Custom bloom output example:

```zenscript
// 1 oreGold + 1 coal -> a Pyrotech bloom.
// Hammering that bloom drops gold nuggets instead of iron nuggets.
mods.pyrotechcomplement.PrimitiveBloomery.createBloomeryBuilder(
        "bloom_from_gold_ore",
        <minecraft:gold_nugget>,
        <ore:oreGold>
    )
    .setFuel(<ore:coal>, 1)
    .setAnvilTiers(["granite", "ironclad"])
    .setBurnTimeTicks(28800)
    .setFailureChance(0.25)
    .setBloomYield(12, 15)
    .setSlagItem(<pyrotech:generated_slag_iron>, 4)
    .addFailureItem(<pyrotech:slag>, 2)
    .setLangKey("tile.oreGold")
    .register();
```

The hammer output can be any concrete item stack:

```zenscript
mods.pyrotechcomplement.PrimitiveBloomery.createBloomeryBuilder(
        "diamond_bloom_from_ore",
        <minecraft:diamond>,
        <ore:oreDiamond>
    )
    .setFuel(<ore:coal>, 1)
    .setAnvilTiers(["granite", "ironclad"])
    .setBurnTimeTicks(28800)
    .setBloomYield(2, 4)
    .setLangKey("tile.oreDiamond")
    .register();

// Replace <modid:bronze_ingot> with the bronze item from your modpack.
mods.pyrotechcomplement.PrimitiveBloomery.createBloomeryBuilder(
        "bronze_bloom_from_copper_ore",
        <modid:bronze_ingot>,
        <ore:oreCopper>
    )
    .setFuel(<ore:coal>, 1)
    .setBurnTimeTicks(28800)
    .setBloomYield(8, 12)
    .setLangKey("tile.oreCopper")
    .register();
```

Pyrotech's own Bloomery CraftTweaker builder already uses the same `output` meaning:

```zenscript
mods.pyrotech.Bloomery.createBloomeryBuilder(
        "bloom_from_diamond_ore",
        <minecraft:diamond>,
        <ore:oreDiamond>
    )
    .setBloomYield(2, 4)
    .setLangKey("tile.oreDiamond")
    .register();
```

## JEI and TOP

JEI recipe categories are available for the loom, forging table, and primitive bloomery. The primitive bloomery category shows ore input, fuel input, burn time, and output.

The One Probe displays active block progress and output previews for the loom, forging table, and primitive bloomery.

## Build

```shell
./gradlew build
```

On Windows:

```shell
.\gradlew.bat --no-daemon build
```
