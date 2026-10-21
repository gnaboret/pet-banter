package com.petbanter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.Collections;

/** Names used for nearby-pet recognition; follower detection itself remains Client#getFollower(). */
public final class PetRoster
{
    private static final Set<String> PET_NAMES = new LinkedHashSet<>(Arrays.asList(
        "abyssal orphan", "abyssal protector", "baby chinchompa", "baby mole", "beaver", "bloodhound", "fox", "penance queen", "pet penance queen",
        "callisto cub", "callisto cub (retro)", "chompy chick", "chaos elemental jr", "chaos elemental jr.", "corporeal critter", "dark acorn", "dark squirrel",
        "giant squirrel", "hellpuppy", "herbi", "kalphite princess", "little nightmare",
        "lil' zik", "olmlet", "penance pet", "pet chaos elemental",
        "pet dagannoth prime", "dagannoth prime jr", "dagannoth prime jr.", "pet dagannoth rex", "dagannoth rex jr", "dagannoth rex jr.", "pet dagannoth supreme", "dagannoth supreme jr", "dagannoth supreme jr.", "pet dark core",
        "pet kraken", "smoke devil", "pet smoke devil", "phoenix", "rock golem", "rocky", "scorpia's offspring",
        "skotos", "snakeling", "pet snakeling", "sraracha", "heron", "nexling", "muphin", "baron", "butch", "huberte", "moxi", "bran", "yami", "lil'viathan", "wisp", "lil' creator", "tzrek-jad", "ikkle hydra", "quetzin", "nid", "smol heredit", "soup", "gull", "dom", "aggy", "beef", "maggot marquess", "mr mcgroot", "tangleroot", "tiny tempor",
        "tumeken's guardian", "venenatis spiderling", "vet'ion jr.", "vet'ion jr", "vorki",
        "youngllef", "general graardor jr", "general graardor jr.", "pet general graardor", "kree'arra jr", "kree'arra jr.", "pet kree'arra", "k'ril tsutsaroth jr", "k'ril tsutsaroth jr.", "pet k'ril tsutsaroth", "zilyana jr", "zilyana jr.", "pet zilyana", "zilyana's servant", "zulrah's spawn", "cat",
        "kitten", "hellcat", "pet rock", "fish", "pet fish", "toy cat", "overgrown cat", "wily cat", "lazy cat", "broav", "pheasant",
        "rift guardian", "rift guardian (fire)", "rift guardian (ice)", "rift guardian (air)",
        "rift guardian (earth)", "rift guardian (water)", "rift guardian (chaos)", "rift guardian (law)",
        "rift guardian (death)", "rift guardian (nature)", "rift guardian (cosmic)", "rift guardian (mind)", "greatish guardian",
        "rift guardian (body)", "rift guardian (soul)", "rift guardian (astral)", "rift guardian (blood)",
        "rift guardian (mist)", "rift guardian (smoke)", "rift guardian (lava)", "rift guardian (steam)",
        "puppadile", "tektiny", "enraged tektiny", "vespina", "flying vespina", "mini vanguard", "vanguard", "vasa minirio", "elidinis' guardian", "akkhito", "babi", "kephriti", "zebo", "tumeken's damaged guardian", "elidinis' damaged guardian", "lil' z"
    ));

    private PetRoster()
    {
    }

    public static boolean isKnownPet(String name)
    {
        if (name == null)
        {
            return false;
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        return PET_NAMES.contains(normalized);
    }

    public static Set<String> names()
    {
        return Collections.unmodifiableSet(PET_NAMES);
    }
}
