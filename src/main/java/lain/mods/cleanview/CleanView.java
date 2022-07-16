package lain.mods.cleanview;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("cleanview")
public class CleanView {

    public CleanView() {
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "client-only", (v, n) -> n));

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
    }

    private void setupClient(FMLClientSetupEvent event) {
        Proxy.INSTANCE.init();
    }

    private void setupKeyMappings(RegisterKeyMappingsEvent event) {
        Proxy.INSTANCE.registerKeyMappings(event::register);
    }

}
