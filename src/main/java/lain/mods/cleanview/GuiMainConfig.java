package lain.mods.cleanview;

import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.config.GuiConfig;

public class GuiMainConfig extends GuiConfig
{

    @SuppressWarnings("unchecked")
    public GuiMainConfig(GuiScreen parent)
    {
        super(parent, Config.getElements(), "cleanview", false, false, Config.getPath());
    }

}
