package lain.mods.cleanview;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("cleanview")
public class CleanView {

    public CleanView(FMLJavaModLoadingContext context) {
        context.getModEventBus().addListener(this::setupClient);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            context.getModEventBus().addListener(this::setupKeyMappings);
        });
    }

    private void setupClient(FMLClientSetupEvent event) {
        Proxy.INSTANCE.init();
    }

    private void setupKeyMappings(RegisterKeyMappingsEvent event) {
        Proxy.INSTANCE.registerKeyMappings(event::register);
    }

}
