package com.canoestudios.pyrotechcomplement.init;

import com.codetaylor.mc.athenaeum.util.OreDictHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public final class ModOreDict {

  private static boolean registered;

  public static void register() {

    if (registered) {
      return;
    }

    OreDictHelper.register("coal", new ItemStack(Items.COAL, 1, 0));
    registered = true;
  }

  private ModOreDict() {
    //
  }
}
