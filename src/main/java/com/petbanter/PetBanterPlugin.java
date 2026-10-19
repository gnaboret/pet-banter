package com.petbanter;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
    name = "Pet Banter",
    description = "Brings OSRS pets to life with contextual reactions and thoughts",
    tags = {"pet", "follower", "companion"}
)
public class PetBanterPlugin extends Plugin
{
    @Inject
    private PetBanterConfig config;

    @Override
    protected void startUp()
    {
        log.info("Pet Banter started!");
    }

    @Override
    protected void shutDown()
    {
        log.info("Pet Banter stopped!");
    }

    @Provides
    PetBanterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PetBanterConfig.class);
    }
}
