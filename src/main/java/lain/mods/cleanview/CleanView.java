package lain.mods.cleanview;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "CleanView", useMetadata = true)
public class CleanView
{

    @Mod.EventHandler
    public void handleEvent(FMLInitializationEvent event)
    {
        if (event.getSide().isClient())
            Proxy.INSTANCE.registerEvents();
    }

}
