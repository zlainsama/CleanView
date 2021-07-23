package lain.mods.cleanview;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Collection;

enum Proxy {

    INSTANCE;

    KeyMapping keyToggle = new KeyMapping("key.togglecleanview", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc");
    boolean lastState = false;
    boolean enabled = true;
    EntityDataAccessor<Integer> potionEffects = null;
    WeakReference<Entity> lastCam = new WeakReference<>(null);

    void handleClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            boolean state = keyToggle.isDown();
            if (state != lastState) {
                if (state) // onPressed
                    enabled = !enabled;
                lastState = state;
            }

            Entity ent = Minecraft.getInstance().getCameraEntity();
            Entity prevEnt = lastCam.get();

            if (!enabled)
                ent = null;

            if (prevEnt != ent) {
                if (prevEnt instanceof LivingEntity) {
                    Collection<MobEffectInstance> effects = ((LivingEntity) prevEnt).getActiveEffects();
                    if (!effects.isEmpty())
                        prevEnt.getEntityData().set(potionEffects, PotionUtils.getColor(effects));
                }
                lastCam = new WeakReference<>(ent);
            }

            if (ent instanceof LivingEntity)
                ent.getEntityData().set(potionEffects, 0);
        }
    }

    void init() {
        setup();

        ClientRegistry.registerKeyBinding(keyToggle);
        MinecraftForge.EVENT_BUS.addListener(this::handleClientTickEvent);
    }

    @SuppressWarnings("unchecked")
    void setup() {
        Throwable t = null;
        Field f;
        try {
            f = LivingEntity.class.getDeclaredField("f_20962_");
        } catch (Throwable t1) {
            t = t1;
            try {
                f = LivingEntity.class.getDeclaredField("DATA_EFFECT_COLOR_ID");
            } catch (Throwable t2) {
                t.addSuppressed(t2);
                f = null;
            }
        }
        try {
            if (f != null) {
                f.setAccessible(true);
                potionEffects = (EntityDataAccessor<Integer>) f.get(null);
            }
        } catch (Throwable t3) {
            if (t == null)
                t = t3;
            else
                t.addSuppressed(t3);
        }
        if (potionEffects == null)
            throw new IllegalStateException("[CleanView] Failed to acquire specific field for the mod.", t);
    }

}
