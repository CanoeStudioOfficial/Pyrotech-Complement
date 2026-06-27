package com.canoestudios.pyrotechcomplement.init;

import com.canoestudios.pyrotechcomplement.Tags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

public final class ModSounds {

  public static final SoundEvent LOOM_WEAVE = create("block.loom.weave");

  public static void register(IForgeRegistry<SoundEvent> registry) {

    registry.register(LOOM_WEAVE);
  }

  private static SoundEvent create(String name) {

    ResourceLocation id = new ResourceLocation(Tags.MOD_ID, name);
    SoundEvent soundEvent = new SoundEvent(id);
    soundEvent.setRegistryName(id);
    return soundEvent;
  }

  private ModSounds() {
    //
  }
}
