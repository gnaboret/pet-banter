package com.petbanter;

import java.util.Locale;

public enum PetPersonality
{
    GIANT_SQUIRREL("Giant Squirrel"),
    DARK_ACORN("Dark Acorn"),
    GENERIC("Pet");

    private final String displayName;

    PetPersonality(String displayName)
    {
        this.displayName = displayName;
    }

    public static PetPersonality fromName(String name)
    {
        if (name == null)
        {
            return GENERIC;
        }
        String normalized = name.toLowerCase(Locale.ROOT);
        if (normalized.contains("giant squirrel"))
        {
            return GIANT_SQUIRREL;
        }
        if (normalized.contains("dark acorn") || normalized.contains("dark squirrel"))
        {
            return DARK_ACORN;
        }
        return GENERIC;
    }

    public String getDisplayName()
    {
        return displayName;
    }
}
