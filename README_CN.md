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
- 简陋的洗矿槽
- 洗矿槽
- 手推磨
- 手推磨磨盘
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
    @Optional string renderType
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

// 选择内部渲染类型。植物纤维类型会在织机内部显示 Pyrotech 的植物纤维纹理。
mods.pyrotechcomplement.Loom.addRecipe(
    "twine_from_fiber",
    <pyrotech:material:14>,
    <pyrotech:material:12>,
    4,
    6,
    "plant_fiber"
);

// 可用的命名类型包括：
// "line" / "string" / "yarn" / "wool" -> 白色线/布渲染
// "plant" / "fiber" / "plant_fiber" -> 植物纤维渲染
// "cloth" / "fabric" -> 布料渲染
// "leather" / "leather_sheet" -> Pyrotech 皮革薄片渲染
// "crude" / "drying_rack" -> Pyrotech 简陋晾干架渲染
// 也可以直接填写 1.12 方块图集路径，例如：
// "minecraft:blocks/wool_colored_white"

mods.pyrotechcomplement.Loom.removeRecipes(<minecraft:wool>);
// mods.pyrotechcomplement.Loom.removeAllRecipes();
```

织机现在使用 TFC 风格的方块实体渲染。织造过程中，每个输入物品会在框架内部显示为独立的动态织物条；成品完成后会一直显示，直到玩家取走输出。`renderType` 会随配方保存，所以简陋织机和普通织机都会使用配方选择的同一种内部渲染。

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

### 洗矿槽

ZenClass：

```zenscript
mods.pyrotechcomplement.Sluice
```

方法：

```zenscript
mods.pyrotechcomplement.Sluice.addRecipe(
    string name,
    IItemStack output,
    IIngredient input
);

mods.pyrotechcomplement.Sluice.addRecipe(
    string name,
    IItemStack output,
    IIngredient input,
    float chance
);

mods.pyrotechcomplement.Sluice.removeRecipes(IIngredient input);
mods.pyrotechcomplement.Sluice.removeAllRecipes();
```

洗矿槽分为 Pyrotech 风格的 `crude_sluice`（简陋的洗矿槽）和 `sluice`（洗矿槽）两个等级。内置配方支持 `oreIron` 等矿辞输入，也支持砂砾、沙子和灵魂沙。自定义配方优先于内置的自动矿辞配方。三参数写法为固定产出；带 `chance` 的写法只设置当前输入的产出概率，范围为 `0.0` 到 `1.0`。

简陋的洗矿槽容量为 16 个输入，每个物品需要 200 tick 处理；内置自动矿石或矿石沉积物配方有 25% 的等级失败率。普通洗矿槽容量为 32 个输入，每个物品需要 100 tick 处理，并且没有这项等级失败。使用带 `chance` 的 CraftTweaker 配方后，会替换该输入的内置逻辑，并且简陋/普通两个等级都按该矿自己的概率判定。

示例：

```zenscript
// 洗矿铁矿时固定产出 1 个铁粒。
mods.pyrotechcomplement.Sluice.addRecipe(
    "iron_wash",
    <minecraft:iron_nugget>,
    <ore:oreIron>
);

// 每种矿石分别设置产出概率，失败时输入仍会被消耗。
mods.pyrotechcomplement.Sluice.addRecipe(
    "iron_wash_chance",
    <minecraft:iron_nugget>,
    <ore:oreIron>,
    0.60
);

mods.pyrotechcomplement.Sluice.addRecipe(
    "gold_wash_chance",
    <minecraft:gold_nugget>,
    <ore:oreGold>,
    0.20
);

// 禁用内置的金矿洗矿配方。
mods.pyrotechcomplement.Sluice.removeRecipes(<ore:oreGold>);

// 移除所有自定义和自动洗矿配方。
// mods.pyrotechcomplement.Sluice.removeAllRecipes();
```

### 手推磨

手推磨是接近 TFC 的手动研磨设备。先把手推磨磨盘安装到手推磨，再把输入物品放在中心，最后空手右键把手开始研磨。默认研磨时间为 90 tick，完成一次会消耗磨盘 1 点耐久。磨盘、输入物品和输出物品都会在方块内部渲染显示。

ZenClass：

```zenscript
mods.pyrotechcomplement.Quern
```

```zenscript
// 第四个参数可选，表示研磨时间 tick；省略时为 90。
mods.pyrotechcomplement.Quern.addRecipe(
    "wheat_to_bread",
    <minecraft:bread>,
    <minecraft:wheat>
);

mods.pyrotechcomplement.Quern.addRecipe(
    "custom_quern_recipe",
    <some_mod:result>,
    <some_mod:input>,
    120
);

mods.pyrotechcomplement.Quern.removeRecipes(<minecraft:wheat>);
// mods.pyrotechcomplement.Quern.removeAllRecipes();
```

### 石炉

Pyrotech 的 `StoneOvenRecipe` 底层本来支持每条配方单独设置烹饪时间，但原版 `mods.pyrotech.StoneOven.addRecipe(...)` 的 CraftTweaker 方法没有暴露这个参数。本模组补了一个带时间参数的入口。

ZenClass：

```zenscript
mods.pyrotechcomplement.StoneOven
```

方法：

```zenscript
mods.pyrotechcomplement.StoneOven.addRecipe(
    string name,
    IItemStack output,
    IIngredient input,
    int cookTimeTicks,
    @Optional boolean inherited
);
```

示例：

```zenscript
// 1 个苹果 -> 1 个烤苹果，需要 30 秒。
// inherited=true 时，会按 Pyrotech 原本的时长倍率同步继承到 Brick Oven。
mods.pyrotechcomplement.StoneOven.addRecipe(
    "baked_apple_slow",
    <pyrotech:apple_baked>,
    <minecraft:apple>,
    600,
    true
);
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

原始锻造炉的铁坯配方沿用 Pyrotech Bloomery builder 的语义。`createBloomeryBuilder(name, output, input)` 里的 `output` 不是原始锻造炉直接吐出的普通物品。原始锻造炉会产出 Pyrotech 铁坯，`output` 是这个铁坯被锤打时掉落的物品。生成的铁坯会在 NBT 里保存这条 CraftTweaker 配方 id，并且本模组会自动注册对应的 Pyrotech `BloomAnvilRecipe`。

`setBloomYield(min, max)` 控制这个铁坯总共大约能锤出多少次指定产物。

铁坯配方示例：

```zenscript
// 1 个矿辞铁矿 + 1 个矿辞煤 -> Pyrotech 铁坯，需要 24 分钟。
// 把这个铁坯放到支持的 Pyrotech 砧上锤打，会掉落铁粒。
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

自定义铁坯锤打产物示例：

```zenscript
// 1 个矿辞金矿 + 1 个矿辞煤 -> Pyrotech 铁坯。
// 把这个铁坯放到支持的 Pyrotech 砧上锤打，会掉落金粒。
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

锤打产物可以是任意具体物品：

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

// 把 <modid:bronze_ingot> 换成整合包里实际的青铜锭物品。
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

Pyrotech 自带的 Bloomery CraftTweaker builder 本来也是同一个 `output` 语义：

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

## JEI 和 TOP

JEI 已支持织机、锻造台、原始锻造炉、手推磨和洗矿槽配方分类。手推磨分类会显示输入、所需磨盘、输出和研磨时间；磨盘与手推磨的合成配方也会在 JEI 的普通合成分类中显示。

The One Probe 会显示织机、锻造台和原始锻造炉的当前进度、结构状态与输出预览。

## 构建

```shell
./gradlew build
```

Windows：

```shell
.\gradlew.bat --no-daemon build
```
