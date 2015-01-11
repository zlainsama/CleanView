package lain.mods.cleanview;

import java.lang.ref.WeakReference;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod(modid = "CleanView", useMetadata = true, guiFactory = "lain.mods.cleanview.GuiFactory", canBeDeactivated = false)
public class CleanView
{

    private static final String TAG = "0256d9da-9c1b-46ea-a83c-01ae6981a2c8";

    WeakReference<EntityLivingBase> ref;

    @SubscribeEvent
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

    @SubscribeEvent
    public void handleEvent(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            EntityLivingBase ent = Config.ENABLED ? FMLClientHandler.instance().getClient().renderViewEntity : null;

            EntityLivingBase prevEnt = (ref != null) ? ref.get() : null;
            if (prevEnt != ent)
            {
                if (prevEnt != null && prevEnt.getEntityData().getBoolean(TAG))
                {
                    prevEnt.removePotionEffect(0);
                    prevEnt.getEntityData().removeTag(TAG);
                }
                ref = (ent != null) ? new WeakReference<EntityLivingBase>(ent) : null;
            }

            if (ent != null)
            {
                ent.getDataWatcher().updateObject(7, 0);
                if (!ent.getEntityData().getBoolean(TAG))
                    ent.getEntityData().setBoolean(TAG, true);
            }
        }
    }

}
