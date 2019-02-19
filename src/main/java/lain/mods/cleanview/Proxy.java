package lain.mods.cleanview;

import java.lang.ref.WeakReference;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;

enum Proxy
{

    INSTANCE;

    KeyBinding keyToggle = new KeyBinding("key.togglecleanview", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
    boolean lastState = false;
    WeakReference<EntityLivingBase> ref = new WeakReference<>(null);
    boolean enabled = true;

    @SuppressWarnings("unchecked")
    <T extends Entity> T getRenderViewEntity()
    {
        try
        {
            return (T) Minecraft.getInstance().getRenderViewEntity();
        }
        catch (Throwable ignored)
        {
            return null;
        }
    }

    void handleClientTickEvent(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            boolean state = keyToggle.isKeyDown();
            if (state != lastState)
            {
                if (lastState) // onRelease
                    enabled = !enabled;
                lastState = state;
            }

            EntityLivingBase ent = getRenderViewEntity();
            EntityLivingBase prevEnt = ref.get();

            if (!enabled)
                ent = null;

            if (prevEnt != ent)
            {
                if (prevEnt != null)
                {
                    Collection<PotionEffect> effects = prevEnt.getActivePotionEffects();
                    if (!effects.isEmpty())
                        prevEnt.getDataManager().set(EntityLivingBase.POTION_EFFECTS, PotionUtils.getPotionColorFromEffectList(effects));
                }
                ref = new WeakReference<>(ent);
            }

            if (ent != null)
                ent.getDataManager().set(EntityLivingBase.POTION_EFFECTS, 0);
        }
    }

    void init()
    {
        ClientRegistry.registerKeyBinding(keyToggle);
        MinecraftForge.EVENT_BUS.addListener(this::handleClientTickEvent);
    }

}
