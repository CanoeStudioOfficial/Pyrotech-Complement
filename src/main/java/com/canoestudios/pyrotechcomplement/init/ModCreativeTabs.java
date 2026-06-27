package com.canoestudios.pyrotechcomplement.init;

import com.canoestudios.pyrotechcomplement.Tags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class ModCreativeTabs {

  public static final CreativeTabs PYROTECH_COMPLEMENT = new CreativeTabs(Tags.MOD_ID) {
    @Nonnull
    @Override
    public ItemStack createIcon() {

      return new ItemStack(ModBlocks.LOOM);
    }
  };

  private ModCreativeTabs() {

    //
  }
}
