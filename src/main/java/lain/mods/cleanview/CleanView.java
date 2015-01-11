package lain.mods.cleanview;

import net.minecraft.client.entity.EntityClientPlayerMP;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod(modid = "CleanView", useMetadata = true, guiFactory = "lain.mods.cleanview.GuiFactory")
public class CleanView
{

    private static final String TAG = "0256d9da-9c1b-46ea-a83c-01ae6981a2c8";

    @Subscribe
    public void handleEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID == "CleanView")
            Config.doConfig();
    }

    @Mod.EventHandler
    public void handleEvent(FMLInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Mod.EventHandler
    public void handleEvent(FMLPreInitializationEvent event)
    {
        Config.doConfig(event.getSuggestedConfigurationFile());
    }

    @Subscribe
    public void handleEvent(PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && event.player instanceof EntityClientPlayerMP)
        {
            if (event.player != null)
            {
                if (Config.ENABLED)
                {
                    event.player.getDataWatcher().updateObject(7, 0);
                    if (!event.player.getEntityData().getBoolean(TAG))
                        event.player.getEntityData().setBoolean(TAG, true);
                }
                else if (event.player.getEntityData().getBoolean(TAG))
                {
                    event.player.removePotionEffect(0);
                    event.player.getEntityData().removeTag(TAG);
                }
            }
        }
    }

}
