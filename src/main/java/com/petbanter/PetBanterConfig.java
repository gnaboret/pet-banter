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

    @ConfigItem(
        keyName = "showThoughts",
        name = "Show Thought Bubbles",
        description = "Show thoughts above non-speaking pets"
    )
    default boolean showThoughts()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showChatReactions",
        name = "Show Chat Reactions",
        description = "Show pet reactions in the local chat box"
    )
    default boolean showChatReactions()
    {
        return true;
    }

    @ConfigItem(
        keyName = "showPetInteractions",
        name = "Show Pet Interactions",
        description = "Allow nearby recognized pets to exchange dialogue"
    )
    default boolean showPetInteractions()
    {
        return true;
    }

    @Range(min = 1, max = 15)
    @ConfigItem(
        keyName = "interactionRange",
        name = "Nearby Pet Range",
        description = "Maximum same-plane distance for pet-to-pet interactions"
    )
    default int interactionRange()
    {
        return 5;
    }

}
