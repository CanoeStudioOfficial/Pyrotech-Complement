package com.canoestudios.pyrotechcomplement.proxy;

import com.canoestudios.pyrotechcomplement.PyrotechComplement;
import com.canoestudios.pyrotechcomplement.init.ModBlocks;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

  public void construction(FMLConstructionEvent event) {

    this.registerJEIPlugin();
  }

  public void preInit(FMLPreInitializationEvent event) {

    ModBlocks.registerTileEntities();
    this.registerCraftTweakerPlugin();
    this.registerTOPPlugin();
  }

  public void registerModels() {

    //
  }

  private void registerCraftTweakerPlugin() {

    if (!Loader.isModLoaded("crafttweaker")) {
      return;
    }

    try {
      Class<?> craftTweakerApi = Class.forName("crafttweaker.CraftTweakerAPI");
      Class<?> zenLoom = Class.forName("com.canoestudios.pyrotechcomplement.plugin.crafttweaker.ZenLoom");
      Class<?> zenForgingTable = Class.forName("com.canoestudios.pyrotechcomplement.plugin.crafttweaker.ZenForgingTable");
      craftTweakerApi.getMethod("registerClass", Class.class).invoke(null, zenLoom);
      craftTweakerApi.getMethod("registerClass", Class.class).invoke(null, zenForgingTable);
      PyrotechComplement.LOGGER.info("CraftTweaker detected; ZenScript APIs are available for Pyrotech Complement");
    } catch (ReflectiveOperationException e) {
      PyrotechComplement.LOGGER.error("Unable to register CraftTweaker integrations", e);
    }
  }

  private void registerTOPPlugin() {

    if (!Loader.isModLoaded("theoneprobe")) {
      return;
    }

    FMLInterModComms.sendFunctionMessage(
        "theoneprobe",
        "getTheOneProbe",
        "com.canoestudios.pyrotechcomplement.plugin.top.PluginTOP$Callback"
    );
  }

  private void registerJEIPlugin() {

    if (!Loader.isModLoaded("jei")) {
      return;
    }

    try {
      Class<?> pluginDelegate = Class.forName("com.codetaylor.mc.athenaeum.integration.jei.PluginDelegate");
      Class<?> pluginJEI = Class.forName("com.canoestudios.pyrotechcomplement.plugin.jei.PluginJEI");
      pluginDelegate.getMethod("registerPlugin", Class.forName("mezz.jei.api.IModPlugin")).invoke(null, pluginJEI.newInstance());
    } catch (ReflectiveOperationException e) {
      PyrotechComplement.LOGGER.error("Unable to register JEI loom integration", e);
    }
  }
}
