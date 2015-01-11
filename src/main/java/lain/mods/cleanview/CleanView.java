package lain.mods.cleanview;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(modid = "CleanView", useMetadata = true)
public class CleanView
{

    @Mod.EventHandler
    public void handleEvent(FMLInitializationEvent event)
    {
        if (event.getSide().isClient())
            Proxy.setup();
        else
            System.err.println("This mod is client-only, please remove this from your server");
    }

}
