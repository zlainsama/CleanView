package lain.mods.cleanview;

import java.lang.ref.WeakReference;
import java.util.Collection;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

enum Proxy
{

    INSTANCE;

    private static final String TAG = "0256d9da-9c1b-46ea-a83c-01ae6981a2c8";

    @SuppressWarnings("unchecked")
    private static <T> T getRenderViewEntity()
    {
        try
        {
            return (T) FMLClientHandler.instance().getClient().getRenderViewEntity();
        }
        catch (Throwable t)
        {
            return null;
        }
    }

    WeakReference<EntityLivingBase> ref;
    boolean enabled = true;
    KeyBinding keyToggle;

    @SubscribeEvent
    public void handleEvent(InputEvent.KeyInputEvent event)
    {
        if (keyToggle.isPressed())
            enabled = !enabled;
    }

    @SubscribeEvent
    public void handleEvent(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            EntityLivingBase ent = enabled ? getRenderViewEntity() : null;

            EntityLivingBase prevEnt = (ref != null) ? ref.get() : null;
            if (prevEnt != ent)
            {
                if (prevEnt != null && prevEnt.getEntityData().getBoolean(TAG))
                {
                    Collection<PotionEffect> effects = prevEnt.getActivePotionEffects();
                    if (!effects.isEmpty())
                        prevEnt.getDataWatcher().updateObject(7, PotionHelper.calcPotionLiquidColor(effects));
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

    void registerEvents()
    {
        keyToggle = new KeyBinding("key.toggleCleanView", 0, "key.categories.misc");
        ClientRegistry.registerKeyBinding(keyToggle);

        MinecraftForge.EVENT_BUS.register(this);
    }

}
