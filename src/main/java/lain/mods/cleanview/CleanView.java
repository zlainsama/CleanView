package lain.mods.cleanview;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@Mod("cleanview")
public class CleanView {

    public CleanView(IEventBus bus) {
        bus.addListener(this::setupClient);
        if (FMLEnvironment.dist.isClient()) {
            bus.addListener(this::setupKeyMappings);
        }
    }

    private void setupClient(FMLClientSetupEvent event) {
        Proxy.INSTANCE.init();
    }

    private void setupKeyMappings(RegisterKeyMappingsEvent event) {
        Proxy.INSTANCE.registerKeyMappings(event::register);
    }

}
