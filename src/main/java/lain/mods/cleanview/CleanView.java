package lain.mods.cleanview;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ParticleEffect;
import org.lwjgl.glfw.GLFW;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

public class CleanView implements ClientModInitializer {

    private KeyBinding keyToggle;
    private boolean lastState;
    private boolean disabled;
    private TrackedData<List<ParticleEffect>> colors;
    private WeakReference<Entity> lastCam;

    private void onClientTick(MinecraftClient client) {
        Entity ent = client.getCameraEntity();
        Entity prevEnt = lastCam.get();

        if (disabled)
            ent = null;

        if (prevEnt != ent) {
            if (prevEnt instanceof LivingEntity) {
                List<ParticleEffect> effects = (((LivingEntity) prevEnt).getActiveStatusEffects()).values().stream().filter(StatusEffectInstance::shouldShowParticles).map(StatusEffectInstance::createParticle).toList();
                if (!effects.isEmpty())
                    prevEnt.getDataTracker().set(colors, effects);
            }
            lastCam = new WeakReference<>(ent);
        }

        if (ent instanceof LivingEntity)
            ent.getDataTracker().set(colors, List.of());
    }

    @Override
    public void onInitializeClient() {
        setupKeyBindings();
        setupEvents();
    }

    private void onKeyBindingActivate(KeyBinding keybinding) {
        if (keybinding == keyToggle)
            disabled = !disabled;
    }

    @SuppressWarnings("unchecked")
    private void setupEvents() {
        try {
            Field f = LivingEntity.class.getDeclaredField(FabricLoader.getInstance().getMappingResolver().mapFieldName("intermediary", "net.minecraft.class_1309", "field_49792", "Lnet/minecraft/class_2940;"));
            f.setAccessible(true);
            colors = (TrackedData<List<ParticleEffect>>) f.get(null);
        } catch (Throwable t) {
            throw new IllegalStateException("[CleanView] Failed to acquire specific field for the mod.", t);
        }

        lastCam = new WeakReference<>(null);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickKeyBindings();
            onClientTick(client);
        });
    }

    private void setupKeyBindings() {
        keyToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.cleanview.togglecleanview", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "key.categories.misc"));
    }

    private void tickKeyBindings() {
        if (keyToggle != null) {
            try {
                boolean state = keyToggle.isPressed();
                if (lastState != state) {
                    if (state)
                        onKeyBindingActivate(keyToggle);
                    lastState = state;
                }
            } catch (Throwable t) {
                keyToggle = null;
                t.printStackTrace();
                System.err.println("[CleanView] Failed to check KeyBinding state, the toggle key will not work.");
            }
        }
    }

}
