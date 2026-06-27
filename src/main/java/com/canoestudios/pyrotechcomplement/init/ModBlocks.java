package com.canoestudios.pyrotechcomplement.init;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.block.BlockLoom;
import com.canoestudios.pyrotechcomplement.tile.TileLoom;
import com.codetaylor.mc.pyrotech.ModPyrotech;
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

  public static void registerBlocks(IForgeRegistry<Block> registry) {

    registry.register(CRUDE_LOOM);
    registry.register(LOOM);
  }

  public static void registerItems(IForgeRegistry<Item> registry) {

    registerItemBlock(registry, CRUDE_LOOM);
    registerItemBlock(registry, LOOM);
  }

  private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {

    ItemBlock itemBlock = new ItemBlock(block);
    itemBlock.setRegistryName(block.getRegistryName());
    itemBlock.setCreativeTab(ModPyrotech.CREATIVE_TAB);
    registry.register(itemBlock);
  }

  public static void registerTileEntities() {

    GameRegistry.registerTileEntity(TileLoom.class, new ResourceLocation(Tags.MOD_ID, "tile.loom"));
  }

  @SideOnly(Side.CLIENT)
  public static void registerModels() {

    registerModel(Item.getItemFromBlock(CRUDE_LOOM), "crude_loom");
    registerModel(Item.getItemFromBlock(LOOM), "loom");
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
