package lain.mods.cleanview;

import java.io.File;
import java.util.List;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.config.GuiConfig;

public class Config
{

    public static void doConfig()
    {
        if (config == null)
            return;
        try
        {
            config.load();
            ENABLED = config.getBoolean("Enabled", Configuration.CATEGORY_GENERAL, ENABLED, null);
        }
        finally
        {
            if (config.hasChanged())
                config.save();
        }
    }

    public static void doConfig(File fileConfig)
    {
        config = new Configuration(fileConfig);
        doConfig();
    }

    @SuppressWarnings("rawtypes")
    public static List getElements()
    {
        return new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();
    }

    public static String getPath()
    {
        return GuiConfig.getAbridgedConfigPath(config.toString());
    }

    public static boolean ENABLED = true;

    private static Configuration config;

}
