package com.canoestudios.pyrotechcomplement.proxy;

import com.canoestudios.pyrotechcomplement.init.ModBlocks;

public class ClientProxy
    extends CommonProxy {

  @Override
  public void registerModels() {

    ModBlocks.registerModels();
  }
}
