package lain.mods.cleanview;

import net.minecraft.client.Minecraft;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod(modid = "CleanView", useMetadata = true)
public class CleanView
{

    @Mod.EventHandler
    public void handleEvent(FMLInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Subscribe
    public void handleEvent(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.renderViewEntity != null)
                mc.renderViewEntity.getDataWatcher().updateObject(7, 0);
        }
    }

}
