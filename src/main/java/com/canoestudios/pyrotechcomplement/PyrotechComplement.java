package com.canoestudios.pyrotechcomplement;

import com.canoestudios.pyrotechcomplement.init.ModBlocks;
import com.canoestudios.pyrotechcomplement.init.ModRecipes;
import com.canoestudios.pyrotechcomplement.init.ModSounds;
import com.canoestudios.pyrotechcomplement.proxy.CommonProxy;
import com.canoestudios.pyrotechcomplement.recipe.ForgingTableRecipe;
import com.canoestudios.pyrotechcomplement.recipe.LoomRecipe;
import com.canoestudios.pyrotechcomplement.recipe.PrimitiveBloomeryRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = Tags.MOD_ID,
    name = Tags.MOD_NAME,
    version = Tags.VERSION,
    dependencies = "required-after:athenaeum;required-after:pyrotech;after:crafttweaker;after:jei;after:theoneprobe"
)
@EventBusSubscriber(modid = Tags.MOD_ID)
public class PyrotechComplement {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SidedProxy(
        clientSide = "com.canoestudios.pyrotechcomplement.proxy.ClientProxy",
        serverSide = "com.canoestudios.pyrotechcomplement.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    /**
     * <a href="https://cleanroommc.com/wiki/forge-mod-development/event#overview">
     *     Take a look at how many FMLStateEvents you can listen to via the @Mod.EventHandler annotation here
     * </a>
     */
    @Mod.EventHandler
    public void construction(FMLConstructionEvent event) {
        proxy.construction(event);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
    }

    @SubscribeEvent
    public static void onNewRegistry(RegistryEvent.NewRegistry event) {
        new RegistryBuilder<LoomRecipe>()
            .setName(new ResourceLocation(Tags.MOD_ID, "loom_recipe"))
            .setType(LoomRecipe.class)
            .allowModification()
            .create();

        new RegistryBuilder<ForgingTableRecipe>()
            .setName(new ResourceLocation(Tags.MOD_ID, "forging_table_recipe"))
            .setType(ForgingTableRecipe.class)
            .allowModification()
            .create();

        new RegistryBuilder<PrimitiveBloomeryRecipe>()
            .setName(new ResourceLocation(Tags.MOD_ID, "primitive_bloomery_recipe"))
            .setType(PrimitiveBloomeryRecipe.class)
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
