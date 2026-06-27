package com.canoestudios.pyrotechcomplement.plugin.top;

import com.canoestudios.pyrotechcomplement.plugin.top.provider.LoomProvider;
import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

@SuppressWarnings("unused")
public class PluginTOP {

  public static class Callback
      implements Function<ITheOneProbe, Void> {

    @Override
    public Void apply(ITheOneProbe top) {

      top.registerProvider(new LoomProvider());
      return null;
    }
  }
}
