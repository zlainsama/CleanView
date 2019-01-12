package lain.mods.cleanview;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.events.client.ClientTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;

public class CleanView implements ClientModInitializer
{

    private FabricKeyBinding keyToggle;
    private boolean lastState;
    private boolean disabled;
    private TrackedData<Integer> colors;
    private WeakReference<Entity> lastCam;

    private void onClientTick(MinecraftClient client)
    {
        Entity ent = client.getCameraEntity();
        Entity prevEnt = lastCam.get();

        if (disabled)
            ent = null;

        if (prevEnt != ent)
        {
            if (prevEnt instanceof LivingEntity)
            {
                Collection<StatusEffectInstance> effects = ((LivingEntity) prevEnt).getPotionEffects();
                if (!effects.isEmpty())
                    prevEnt.getDataTracker().set(colors, PotionUtil.getColor(effects));
            }
            lastCam = new WeakReference<Entity>(ent);
        }

        if (ent != null)
            ent.getDataTracker().set(colors, 0);
    }

    @Override
    public void onInitializeClient()
    {
        setupKeyBindings();
        setupEvents();
    }

    private void onKeyBindingActivate(FabricKeyBinding keybinding)
    {
        if (keybinding == keyToggle)
            disabled = !disabled;
    }

    @SuppressWarnings("unchecked")
    private void setupEvents()
    {
        try
        {
            Field f = null;
            try
            {
                f = LivingEntity.class.getDeclaredField("field_6240");
            }
            catch (NoSuchFieldException e)
            {
                f = LivingEntity.class.getDeclaredField("POTION_SWIRLS_COLOR");
            }
            f.setAccessible(true);
            colors = (TrackedData<Integer>) f.get(null);
        }
        catch (Throwable t)
        {
            colors = null;
        }
        finally
        {
            if (colors == null)
                throw new IllegalStateException("[CleanView] Failed to acquire specific field for the mod.");
        }

        lastCam = new WeakReference<Entity>(null);
        ClientTickEvent.CLIENT.register(client -> {
            tickKeyBindings();
            onClientTick(client);
        });
    }

    private void setupKeyBindings()
    {
        keyToggle = FabricKeyBinding.Builder.create(Identifier.create("cleanview:togglecleanview"), InputUtil.Type.KEY_KEYBOARD, InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "key.categories.misc").build();
        if (!KeyBindingRegistry.INSTANCE.register(keyToggle))
            keyToggle = null;
    }

    private void tickKeyBindings()
    {
        if (keyToggle != null)
        {
            try
            {
                boolean state = keyToggle.method_1434();
                if (lastState != state)
                {
                    if (state)
                        onKeyBindingActivate(keyToggle);
                    lastState = state;
                }
            }
            catch (Throwable t)
            {
                keyToggle = null;
                System.err.println("[CleanView] Failed to check KeyBinding state.");
            }
        }
    }

}
