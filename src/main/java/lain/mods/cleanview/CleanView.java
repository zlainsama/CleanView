package lain.mods.cleanview;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod("cleanview")
public class CleanView {

    public CleanView() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        if (FMLEnvironment.dist.isClient()) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupKeyMappings);
        }
    }

    private void setupClient(FMLClientSetupEvent event) {
        Proxy.INSTANCE.init();
    }

    private void setupKeyMappings(RegisterKeyMappingsEvent event) {
        Proxy.INSTANCE.registerKeyMappings(event::register);
    }

}
