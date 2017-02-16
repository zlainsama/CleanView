package lain.mods.cleanview;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "cleanview", useMetadata = true, acceptedMinecraftVersions = "[1.10,)")
public class CleanView
{

    @Mod.EventHandler
    public void handleEvent(FMLInitializationEvent event)
    {
        if (event.getSide().isClient())
            Proxy.setup();
        else
            System.err.println("This mod is client-only, please remove it from your server");
    }

}
