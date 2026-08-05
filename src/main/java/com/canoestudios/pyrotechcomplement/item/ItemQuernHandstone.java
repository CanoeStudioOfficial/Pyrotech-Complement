package com.canoestudios.pyrotechcomplement.item;

import com.canoestudios.pyrotechcomplement.Tags;
import com.canoestudios.pyrotechcomplement.init.ModCreativeTabs;
import net.minecraft.item.Item;

public class ItemQuernHandstone extends Item {

  public ItemQuernHandstone(String name) {

    this.setRegistryName(Tags.MOD_ID, name);
    this.setTranslationKey(Tags.MOD_ID + "." + name);
    this.setCreativeTab(ModCreativeTabs.PYROTECH_COMPLEMENT);
    this.setMaxStackSize(1);
    this.setMaxDamage(128);
  }
}
