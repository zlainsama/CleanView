package lain.mods.cleanview;

import java.lang.ref.WeakReference;
import java.util.Collection;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Proxy
{

    public static void setup()
    {
        if (INSTANCE == null)
            throw new RuntimeException();
    }

    private static final String TAG = "0256d9da-9c1b-46ea-a83c-01ae6981a2c8";
    private static final Proxy INSTANCE = new Proxy();

    WeakReference<EntityLivingBase> ref = new WeakReference<EntityLivingBase>(null);
    boolean enabled = true;
    KeyBinding keyToggle;

    private Proxy()
    {
        MinecraftForge.EVENT_BUS.register(this);

        keyToggle = new KeyBinding("key.toggleCleanView", 0, "key.categories.misc");
        ClientRegistry.registerKeyBinding(keyToggle);
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> T getRenderViewEntity()
    {
        try
        {
            return (T) FMLClientHandler.instance().getClient().getRenderViewEntity();
        }
        catch (Throwable ignored)
        {
            return null;
        }
    }

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
            EntityLivingBase ent = getRenderViewEntity();
            EntityLivingBase prevEnt = ref.get();

            if (!enabled)
                ent = null;

            if (prevEnt != ent)
            {
                if (prevEnt != null && prevEnt.getEntityData().getBoolean(TAG))
                {
                    Collection<PotionEffect> effects = prevEnt.getActivePotionEffects();
                    if (!effects.isEmpty())
                        prevEnt.getDataManager().set(EntityLivingBase.POTION_EFFECTS, PotionUtils.getPotionColorFromEffectList(effects));
                    prevEnt.getEntityData().removeTag(TAG);
                }
                ref = new WeakReference<EntityLivingBase>(ent);
            }

            if (ent != null)
            {
                ent.getDataManager().set(EntityLivingBase.POTION_EFFECTS, 0);
                if (!ent.getEntityData().getBoolean(TAG))
                    ent.getEntityData().setBoolean(TAG, true);
            }
        }
    }

}
