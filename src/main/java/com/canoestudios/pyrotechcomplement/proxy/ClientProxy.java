package com.canoestudios.pyrotechcomplement.proxy;

import com.canoestudios.pyrotechcomplement.client.TileForgingTableRenderer;
import com.canoestudios.pyrotechcomplement.client.TileSluiceRenderer;
import com.canoestudios.pyrotechcomplement.init.ModBlocks;
import com.canoestudios.pyrotechcomplement.tile.TileForgingTable;
import com.canoestudios.pyrotechcomplement.tile.TileSluice;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy
    extends CommonProxy {

  @Override
  public void registerModels() {

    ModBlocks.registerModels();
    ClientRegistry.bindTileEntitySpecialRenderer(TileForgingTable.class, new TileForgingTableRenderer());
    ClientRegistry.bindTileEntitySpecialRenderer(TileSluice.class, new TileSluiceRenderer());
  }
}
