package lain.mods.cleanview;

import java.io.File;
import net.minecraftforge.common.config.Configuration;

public class Config
{

    public static void doConfig()
    {
        if (config == null)
            return;
        try
        {
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

    public static boolean ENABLED = true;

    private static Configuration config;

}
