package com.canoestudios.pyrotechcomplement.init;

import com.canoestudios.pyrotechcomplement.Tags;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
public final class ClientRegistration {

  @SubscribeEvent
  public static void onModelRegistry(ModelRegistryEvent event) {

    ModBlocks.registerModels();
  }

  private ClientRegistration() {
    //
  }
}
