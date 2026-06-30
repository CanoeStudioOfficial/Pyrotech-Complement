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
- 原始锻造炉

锻造台使用 Pyrotech 的锤子体系。支持 Pyrotech 锤子、Pyrotech 锤子配置列表、工具类 `hammer`，以及矿辞锤子，例如 `toolHammer`。

原始锻造炉是参考 TFC 锻造炉做的前期多方块。把锻造炉门贴在一个空的内部格旁边，内部格底部、四周和烟囱外圈用石质方块围起来，从烟囱上方丢入矿石和煤类燃料，再用打火石、火焰弹或 Pyrotech 点火器点燃。默认铁矿配方产出 Pyrotech 的铁坯。

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

### 原始锻造炉

ZenClass：

```zenscript
mods.pyrotechcomplement.PrimitiveBloomery
```

方法：

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

// 旧版兼容方法。bloomRecipeId 只用于读取 Pyrotech 配方的最终产物；
// 新生成的铁坯 NBT 会写入本模组的 CraftTweaker 配方 id。
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

铁坯配方示例：

```zenscript
// 1 个矿辞铁矿 + 1 个矿辞煤 -> Pyrotech 铁坯，需要 24 分钟。
// 铁坯 NBT 中的 recipeId 会是 "crafttweaker:bloom_from_iron_ore"。
// setLangKey 是可选项；省略时会从输入物品推导铁坯名称。
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

## JEI 和 TOP

JEI 已支持织机、锻造台和原始锻造炉配方分类。原始锻造炉 JEI 分类会显示矿石输入、燃料输入、燃烧时间和输出。

The One Probe 会显示织机、锻造台和原始锻造炉的当前进度、结构状态与输出预览。

## 构建

```shell
./gradlew build
```

Windows：

```shell
.\gradlew.bat --no-daemon build
```
