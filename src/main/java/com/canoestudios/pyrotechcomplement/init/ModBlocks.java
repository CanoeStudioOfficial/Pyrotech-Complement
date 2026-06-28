package com.canoestudios.pyrotechcomplement.init;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.block.BlockForgingTable;
import com.canoestudios.pyrotechcomplement.block.BlockLoom;
import com.canoestudios.pyrotechcomplement.block.BlockPrimitiveBloomery;
import com.canoestudios.pyrotechcomplement.block.BlockPrimitiveBloomeryMolten;
import com.canoestudios.pyrotechcomplement.tile.TileForgingTable;
import com.canoestudios.pyrotechcomplement.tile.TileLoom;
import com.canoestudios.pyrotechcomplement.tile.TilePrimitiveBloomery;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModBlocks {

  public static final BlockLoom CRUDE_LOOM = new BlockLoom("crude_loom", BlockLoom.Tier.CRUDE);
  public static final BlockLoom LOOM = new BlockLoom("loom", BlockLoom.Tier.NORMAL);
  public static final BlockForgingTable FORGING_TABLE_GRANITE = new BlockForgingTable("forging_table_granite", BlockForgingTable.Style.GRANITE);
  public static final BlockForgingTable FORGING_TABLE_OBSIDIAN = new BlockForgingTable("forging_table_obsidian", BlockForgingTable.Style.OBSIDIAN);
  public static final BlockForgingTable FORGING_TABLE_IRONCLAD = new BlockForgingTable("forging_table_ironclad", BlockForgingTable.Style.IRONCLAD);
  public static final BlockPrimitiveBloomery PRIMITIVE_BLOOMERY = new BlockPrimitiveBloomery("primitive_bloomery");
  public static final BlockPrimitiveBloomeryMolten PRIMITIVE_BLOOMERY_MOLTEN = new BlockPrimitiveBloomeryMolten("primitive_bloomery_molten");

  public static void registerBlocks(IForgeRegistry<Block> registry) {

    registry.register(CRUDE_LOOM);
    registry.register(LOOM);
    registry.register(FORGING_TABLE_GRANITE);
    registry.register(FORGING_TABLE_OBSIDIAN);
    registry.register(FORGING_TABLE_IRONCLAD);
    registry.register(PRIMITIVE_BLOOMERY);
    registry.register(PRIMITIVE_BLOOMERY_MOLTEN);
  }

  public static void registerItems(IForgeRegistry<Item> registry) {

    registerItemBlock(registry, CRUDE_LOOM);
    registerItemBlock(registry, LOOM);
    registerItemBlock(registry, FORGING_TABLE_GRANITE);
    registerItemBlock(registry, FORGING_TABLE_OBSIDIAN);
    registerItemBlock(registry, FORGING_TABLE_IRONCLAD);
    registerItemBlock(registry, PRIMITIVE_BLOOMERY);
  }

  private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {

    ItemBlock itemBlock = new ItemBlock(block);
    itemBlock.setRegistryName(block.getRegistryName());
    itemBlock.setCreativeTab(ModCreativeTabs.PYROTECH_COMPLEMENT);
    registry.register(itemBlock);
  }

  public static void registerTileEntities() {

    GameRegistry.registerTileEntity(TileLoom.class, new ResourceLocation(Tags.MOD_ID, "tile.loom"));
    GameRegistry.registerTileEntity(TileForgingTable.class, new ResourceLocation(Tags.MOD_ID, "tile.forging_table"));
    GameRegistry.registerTileEntity(TilePrimitiveBloomery.class, new ResourceLocation(Tags.MOD_ID, "tile.primitive_bloomery"));
  }

  @SideOnly(Side.CLIENT)
  public static void registerModels() {

    registerModel(Item.getItemFromBlock(CRUDE_LOOM), "crude_loom");
    registerModel(Item.getItemFromBlock(LOOM), "loom");
    registerModel(Item.getItemFromBlock(FORGING_TABLE_GRANITE), "forging_table_granite");
    registerModel(Item.getItemFromBlock(FORGING_TABLE_OBSIDIAN), "forging_table_obsidian");
    registerModel(Item.getItemFromBlock(FORGING_TABLE_IRONCLAD), "forging_table_ironclad");
    registerModel(Item.getItemFromBlock(PRIMITIVE_BLOOMERY), "primitive_bloomery");
  }

  @SideOnly(Side.CLIENT)
  private static void registerModel(Item item, String name) {

    ModelLoader.setCustomModelResourceLocation(
        item,
        0,
        new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, name), "inventory")
    );
  }

  private ModBlocks() {
    //
  }
}
