package com.canoestudios.pyrotechcomplement;

import com.canoestudios.pyrotechcomplement.init.ModBlocks;
import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.canoestudios.pyrotechcomplement.init.ModSounds;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION,
    dependencies = "required-after:pyrotech;after:crafttweaker"
)
@EventBusSubscriber(modid = Tags.MOD_ID)
public class PyrotechComplement {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.registerTileEntities();
        if (Loader.isModLoaded("crafttweaker")) {
            registerCraftTweakerPlugin();
            LOGGER.info("CraftTweaker detected; loom ZenScript API is available as mods.pyrotechcomplement.Loom");
        }
    }

    private static void registerCraftTweakerPlugin() {

        try {
            Class<?> craftTweakerApi = Class.forName("crafttweaker.CraftTweakerAPI");
            Class<?> zenLoom = Class.forName("com.canoestudios.pyrotechcomplement.plugin.crafttweaker.ZenLoom");
            craftTweakerApi.getMethod("registerClass", Class.class).invoke(null, zenLoom);
        } catch (ReflectiveOperationException e) {
            LOGGER.error("Unable to register CraftTweaker loom integration", e);
        }
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        new RegistryBuilder<LoomRecipe>()
            .setName(new ResourceLocation(Tags.MOD_ID, "loom_recipe"))
            .setType(LoomRecipe.class)
            .allowModification()
            .create();

        ModRecipes.initRegistry();
    }

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        ModBlocks.registerBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        ModBlocks.registerItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
        ModRecipes.registerDefaults();
    }

    @SubscribeEvent
    public static void onRegisterSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event) {
        ModSounds.register(event.getRegistry());
    }
}
