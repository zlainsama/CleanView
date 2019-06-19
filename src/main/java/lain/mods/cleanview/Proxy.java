package lain.mods.cleanview;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.gameevent.TickEvent;

enum Proxy
{

    INSTANCE;

    KeyBinding keyToggle = new KeyBinding("key.togglecleanview", InputMappings.INPUT_INVALID.getKeyCode(), "key.categories.misc");
    boolean lastState = false;
    DataParameter<Integer> potionEffects = null;
    WeakReference<Entity> lastCam = new WeakReference<>(null);

    boolean enabled = true;

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

            Entity ent = Minecraft.getInstance().getRenderViewEntity();
            Entity prevEnt = lastCam.get();

            if (!enabled)
                ent = null;

            if (prevEnt != ent)
            {
                if (prevEnt instanceof LivingEntity)
                {
                    Collection<EffectInstance> effects = ((LivingEntity) prevEnt).getActivePotionEffects();
                    if (!effects.isEmpty())
                        prevEnt.getDataManager().set(potionEffects, PotionUtils.getPotionColorFromEffectList(effects));
                }
                lastCam = new WeakReference<>(ent);
            }

            if (ent != null)
                ent.getDataManager().set(potionEffects, 0);
        }
    }

    void init()
    {
        setup();

        ClientRegistry.registerKeyBinding(keyToggle);
        MinecraftForge.EVENT_BUS.addListener(this::handleClientTickEvent);
    }

    @SuppressWarnings("unchecked")
    void setup()
    {
        Throwable t = null;
        Field f;
        try
        {
            f = LivingEntity.class.getDeclaredField("field_184633_f");
        }
        catch (Throwable t1)
        {
            t = t1;
            try
            {
                f = LivingEntity.class.getDeclaredField("POTION_EFFECTS");
            }
            catch (Throwable t2)
            {
                t.addSuppressed(t2);
                f = null;
            }
        }
        try
        {
            if (f != null)
            {
                f.setAccessible(true);
                potionEffects = (DataParameter<Integer>) f.get(null);
            }
        }
        catch (Throwable t3)
        {
            if (t == null)
                t = t3;
            else
                t.addSuppressed(t3);
        }
        if (potionEffects == null)
            throw new IllegalStateException("[CleanView] Failed to acquire specific field for the mod.", t);
    }

}
