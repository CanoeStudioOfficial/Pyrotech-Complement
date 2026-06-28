# Pyrotech Complement 中文文档

[English README](README.md)

Pyrotech Complement 是一个 Minecraft 1.12.2 的 Pyrotech 附属模组。它添加了一些参考后续 TerraFirmaCraft 风格工作流程的功能性方块，同时尽量沿用 Pyrotech 的系统、注册方式和资源风格。

## 依赖

- Minecraft 1.12.2
- Forge 14.23.5.2847
- Athenaeum
- Pyrotech

可选联动：

- CraftTweaker
- JEI
- The One Probe

## 内容

- 简陋织机
- 织机
- 花岗岩锻造台
- 黑曜石锻造台
- 复合锻造台

锻造台使用 Pyrotech 的锤子体系。支持 Pyrotech 锤子、Pyrotech 锤子配置列表、工具类 `hammer`，以及矿辞锤子，例如 `toolHammer`。

## CraftTweaker

CraftTweaker 类会在 late recipe action 阶段注册，整体风格参考 Pyrotech 的 CraftTweaker 支持。

### 织机

ZenClass：

```zenscript
mods.pyrotechcomplement.Loom
```

方法：

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

示例：

```zenscript
// 4 个线 -> 1 个羊毛，需要 8 次织机步骤。
mods.pyrotechcomplement.Loom.addRecipe(
    "wool_from_string",
    <minecraft:wool>,
    <minecraft:string>,
    4,
    8,
    "minecraft:blocks/wool_colored_white"
);

// texture 是可选参数。不写时会使用默认的 Pyrotech 晾干架纹理。
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

### 锻造台

ZenClass：

```zenscript
mods.pyrotechcomplement.ForgingTable
```

方法：

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

单输入配方示例：

```zenscript
// 1 个铁锭 -> 9 个 Pyrotech 铁碎片，需要 6 次锤击。
mods.pyrotechcomplement.ForgingTable.addRecipe(
    "iron_shards_from_ingot",
    <pyrotech:material:19> * 9,
    <ore:ingotIron>,
    1,
    6
);
```

双输入配方示例：

```zenscript
// 1 个石棍 + 1 个燧石碎片 -> 1 个石质工具柄，需要 4 次锤击。
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

移除配方示例：

```zenscript
mods.pyrotechcomplement.ForgingTable.removeRecipes(<pyrotech:material:19>);
// mods.pyrotechcomplement.ForgingTable.removeAllRecipes();
```

## JEI 和 TOP

JEI 已支持织机和锻造台配方分类。锻造台 JEI 分类会显示主输入、可选副输入、可用锤子、锤击次数和输出。

The One Probe 会显示织机和锻造台的当前进度与输出预览。

## 构建

```shell
./gradlew build
```

Windows：

```shell
.\gradlew.bat --no-daemon build
```
