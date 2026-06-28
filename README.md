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

The forging tables use Pyrotech hammer support. Pyrotech hammers, Pyrotech's hammer config list, tool class `hammer`, and ore dictionary hammers such as `toolHammer` are accepted.

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

## JEI and TOP

JEI recipe categories are available for the loom and forging table. The forging table JEI category shows the primary input, optional secondary input, accepted hammers, hammer-hit count, and output.

The One Probe displays active block progress and output previews for the loom and forging table.

## Build

```shell
./gradlew build
```

On Windows:

```shell
.\gradlew.bat --no-daemon build
```
