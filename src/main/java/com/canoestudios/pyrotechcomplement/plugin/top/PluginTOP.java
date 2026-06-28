package com.canoestudios.pyrotechcomplement.plugin.top;

import com.canoestudios.pyrotechcomplement.plugin.top.provider.ForgingTableProvider;
import com.canoestudios.pyrotechcomplement.plugin.top.provider.LoomProvider;
import com.canoestudios.pyrotechcomplement.plugin.top.provider.PrimitiveBloomeryProvider;
import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

@SuppressWarnings("unused")
public class PluginTOP {

  public static class Callback
      implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe top) {

      top.registerProvider(new LoomProvider());
      top.registerProvider(new ForgingTableProvider());
      top.registerProvider(new PrimitiveBloomeryProvider());
      return null;
    }
  }
}
