package com.petbanter;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Locale;
import javax.inject.Inject;

public class ReactionEngine
{
    private final Map<PetPersonality, String[]> reactions = new EnumMap<>(PetPersonality.class);
    private final Map<String, String[]> namedReactions = new LinkedHashMap<>();
    private final DialogueCatalog catalog;

    @Inject
    public ReactionEngine(DialogueCatalog catalog)
    {
        this.catalog = catalog;
        initializeFallbacks();
    }

    public ReactionEngine()
    {
        this.catalog = null;
        initializeFallbacks();
    }

    private void initializeFallbacks()
    {
        reactions.put(PetPersonality.GIANT_SQUIRREL, new String[] {
            "I could climb that tree faster than you.", "Any acorns nearby?", "Keep up!"
        });
        reactions.put(PetPersonality.DARK_ACORN, new String[] {
            "The shadows are listening.", "An acorn… touched by darkness.", "Do not trust the trees."
        });
        reactions.put(PetPersonality.GENERIC, new String[] {
            "I have important pet business.", "I was promised snacks.", "This adventure needs more me."
        });
        namedReactions.put("bloodhound", new String[] {"I smell treasure.", "Someone has been hiding something.", "Follow my nose."});
        namedReactions.put("beaver", new String[] {"This place needs a dam.", "I can fix that with enough logs.", "You call that construction?"});
        namedReactions.put("rock golem", new String[] {"I am carrying the team.", "Mining sounds good.", "I have a rock-solid plan."});
        namedReactions.put("rocky", new String[] {"I have a rock-solid plan.", "Do not take me for granite.", "That joke was excellent."});
        namedReactions.put("phoenix", new String[] {"I have risen for this.", "Try not to get burned.", "I will be back."});
        namedReactions.put("olmlet", new String[] {"The maze remembers you.", "Bring more supplies next time.", "I saw that mistake."});
        namedReactions.put("youngllef", new String[] {"The gauntlet awaits.", "Do not waste the prep time.", "I prefer the red prison."});
        namedReactions.put("tangleroot", new String[] {"The garden is judging you.", "Water the herbs.", "I have roots here."});
        namedReactions.put("hellpuppy", new String[] {"Fetch? I fetch souls.", "The underworld has better treats.", "Good. You brought chaos."});
        namedReactions.put("snakeling", new String[] {"Sssomething is nearby.", "I shed my old opinions.", "Do not make me hiss."});
        namedReactions.put("kraken", new String[] {"The sea remembers.", "Tentacles are excellent for multitasking.", "I demand wetter terrain."});
        namedReactions.put("cat", new String[] {"You may continue.", "Pet me properly.", "I meant to do that."});
        namedReactions.put("kitten", new String[] {"Everything is bite-sized.", "I found a very important string.", "Carry me."});
        namedReactions.put("hellcat", new String[] {"I am not a normal cat.", "The scratching post fears me.", "Infernal service, coming right up."});
        namedReactions.put("pet rock", new String[] {"...", "I remain unmoved.", "That was a very moving story."});
        namedReactions.put("fish", new String[] {"Blub.", "I am out of my depth.", "Please find me some water."});
        namedReactions.put("penguin", new String[] {"Waddle we do next?", "The operation is proceeding smoothly.", "Never trust a penguin in disguise."});
        namedReactions.put("chompy", new String[] {"Chomp.", "I am peckish.", "That looks edible."});
    }

    public String reaction(PetPersonality personality, ReactionContext context, int index)
    {
        String[] options = reactions.get(personality);
        if (context == ReactionContext.NEW_FOLLOWER)
        {
            return "Ready when you are.";
        }
        return options[Math.floorMod(index, options.length)];
    }

    public String reaction(String petName, ReactionContext context, int index)
    {
        if (context == ReactionContext.NEW_FOLLOWER)
        {
            return "Ready when you are.";
        }
        if (context == ReactionContext.METAMORPHOSIS && catalog != null)
        {
            String line = catalog.contextLine(petName, "metamorphosis", index);
            if (line == null)
            {
                line = catalog.sharedLine("metamorphosis", index);
            }
            if (line != null)
            {
                return speakerLine(line, petName);
            }
        }
        if (context == ReactionContext.DANGER && catalog != null)
        {
            String line = catalog.contextLine(petName, "danger", index);
            if (line == null)
            {
                line = catalog.sharedLine("danger", index);
            }
            if (line != null)
            {
                return speakerLine(line, petName);
            }
        }
        if (context == ReactionContext.LOOT && catalog != null)
        {
            String line = catalog.contextLine(petName, "loot", index);
            if (line == null)
            {
                line = catalog.sharedLine("loot", index);
            }
            if (line != null)
            {
                return speakerLine(line, petName);
            }
        }
        if (context == ReactionContext.IDLE && catalog != null && index % 5 == 0)
        {
            String line = catalog.contextLine(petName, "travel", index);
            if (line == null)
            {
                line = catalog.sharedLine("travel", index);
            }
            if (line != null)
            {
                return speakerLine(line, petName);
            }
        }
        String normalized = petName == null ? "" : petName.toLowerCase(Locale.ROOT);
        if (catalog != null)
        {
            String displayName = petName == null || petName.trim().isEmpty() ? "Pet" : petName.trim();
            java.util.List<String> catalogLines = catalog.linesFor(petName);
            if (!catalogLines.isEmpty())
            {
                return speakerLine(catalogLines.get(Math.floorMod(index, catalogLines.size())), displayName);
            }
        }
        for (Map.Entry<String, String[]> entry : namedReactions.entrySet())
        {
            if (normalized.contains(entry.getKey()))
            {
                String[] lines = entry.getValue();
                return lines[Math.floorMod(index, lines.length)];
            }
        }
        if (catalog != null)
        {
            java.util.List<String> sharedLines = catalog.sharedLines("fallback");
            if (!sharedLines.isEmpty())
            {
                String displayName = petName == null || petName.trim().isEmpty() ? "Pet" : petName.trim();
                return speakerLine(sharedLines.get(Math.floorMod(index, sharedLines.size())), displayName);
            }
        }
        String displayName = petName == null || petName.trim().isEmpty() ? "Pet" : petName.trim();
        String[] fallback = {
            displayName + " is on patrol.",
            displayName + " approves this adventure.",
            "I would like a snack.",
            "I have a thought about that.",
            "I am keeping an eye on things."
        };
        return fallback[Math.floorMod(index, fallback.length)];
    }

    public String interaction(PetPersonality speaker, PetPersonality listener, int index)
    {
        if (speaker == PetPersonality.DARK_ACORN || listener == PetPersonality.DARK_ACORN)
        {
            return "The shadows have company.";
        }
        return index % 2 == 0 ? "Race you to the next tree!" : "Try not to lose your acorns.";
    }

    public String nearbyInteraction(String petName, int index)
    {
        if (catalog != null)
        {
            String profileLine = catalog.profileInteraction(petName, index);
            if (profileLine != null)
            {
                return profileLine;
            }
            String line = catalog.sharedLine("nearbyPet", index);
            if (line != null)
            {
                return speakerLine(line, petName);
            }
        }
        return interaction(PetPersonality.fromName(petName), PetPersonality.GENERIC, index);
    }

    public String nearbyInteraction(String speakerName, String listenerName, int index)
    {
        if (catalog != null)
        {
            String speakerLine = catalog.profileInteraction(speakerName, index);
            if (speakerLine != null)
            {
                return speakerLine;
            }
            String listenerLine = catalog.profileInteraction(listenerName, index + 1);
            if (listenerLine != null)
            {
                return listenerLine;
            }
        }
        return nearbyInteraction(listenerName, index);
    }

    private String speakerLine(String line, String petName)
    {
        if (line == null)
        {
            return null;
        }
        String displayName = petName == null || petName.trim().isEmpty() ? "Pet" : petName.trim();
        if (line.startsWith("{pet}"))
        {
            return line.replaceFirst("^\\{pet\\}", "I");
        }
        return line.replace("{pet}", displayName);
    }
}
