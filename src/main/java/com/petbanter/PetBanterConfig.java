package com.petbanter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("petbanter")
public interface PetBanterConfig extends Config
{
    @ConfigItem(
        keyName = "enabled",
        name = "Enable Pet Banter",
        description = "Allow your pet to react to gameplay events"
    )
    default boolean enabled()
    {
        return true;
    }

    @Range(min = 5, max = 120)
    @ConfigItem(
        keyName = "reactionCooldown",
        name = "Reaction Cooldown",
        description = "Minimum seconds between pet reactions"
    )
    default int reactionCooldown()
    {
        return 30;
    }

    @ConfigItem(
        keyName = "showThoughts",
        name = "Show Thought Bubbles",
        description = "Show thoughts above non-speaking pets"
    )
    default boolean showThoughts()
    {
        return true;
    }
}
