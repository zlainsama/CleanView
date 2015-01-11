package lain.mods.cleanview;

import java.lang.ref.WeakReference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class Proxy
{

    public static void setup()
    {
        if (INSTANCE == null)
            throw new RuntimeException();
    }

    private static final String TAG = "0256d9da-9c1b-46ea-a83c-01ae6981a2c8";
    private static final Proxy INSTANCE = new Proxy();

    WeakReference<EntityLivingBase> ref;
    boolean enabled = true;
    KeyBinding keyToggle;

    private Proxy()
    {
        FMLCommonHandler.instance().bus().register(this);

        keyToggle = new KeyBinding("Toggle CleanView", 0, "key.categories.misc");
        ClientRegistry.registerKeyBinding(keyToggle);
    }

    @SubscribeEvent
    public void handleEvent(InputEvent.KeyInputEvent event)
    {
        if (keyToggle.getIsKeyPressed())
            enabled = !enabled;
    }

    @SubscribeEvent
    public void handleEvent(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            EntityLivingBase ent = enabled ? FMLClientHandler.instance().getClient().renderViewEntity : null;

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
