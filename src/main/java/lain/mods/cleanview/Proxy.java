package lain.mods.cleanview;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

enum Proxy {

    INSTANCE;

    KeyMapping keyToggle = new KeyMapping("key.togglecleanview", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc");
    boolean lastState = false;
    boolean enabled = true;
    EntityDataAccessor<List<ParticleOptions>> effectParticles = null;
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
                    List<ParticleOptions> particles = ((LivingEntity) prevEnt).getActiveEffectsMap().values().stream().filter(MobEffectInstance::isVisible).map(MobEffectInstance::getParticleOptions).toList();
                    if (!particles.isEmpty())
                        prevEnt.getEntityData().set(effectParticles, particles);
                }
                lastCam = new WeakReference<>(ent);
            }

            if (ent instanceof LivingEntity)
                ent.getEntityData().set(effectParticles, List.of());
        }
    }

    void init() {
        setup();

        NeoForge.EVENT_BUS.addListener(this::handleClientTickEvent);
    }

    @SuppressWarnings("unchecked")
    void setup() {
        Throwable t = null;
        Field f;
        try {
            f = LivingEntity.class.getDeclaredField("DATA_EFFECT_PARTICLES");
        } catch (Throwable t1) {
            t = t1;
            try {
                f = LivingEntity.class.getDeclaredField("bL");
            } catch (Throwable t2) {
                t.addSuppressed(t2);
                f = null;
            }
        }
        try {
            if (f != null) {
                f.setAccessible(true);
                effectParticles = (EntityDataAccessor<List<ParticleOptions>>) f.get(null);
            }
        } catch (Throwable t3) {
            if (t == null)
                t = t3;
            else
                t.addSuppressed(t3);
        }
        if (effectParticles == null)
            throw new IllegalStateException("[CleanView] Failed to acquire specific field for the mod.", t);
    }

    void registerKeyMappings(Consumer<KeyMapping> register) {
        register.accept(keyToggle);
    }

}
