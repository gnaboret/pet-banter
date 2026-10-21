package com.petbanter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DialogueCatalog
{
    private static final String RESOURCE = "/pet-banter-dialogue.json";
    private final Map<String, List<String>> shared;
    private final Map<String, DialogueProfile> profiles;
    private final Map<String, String> transmogParents;

    @Inject
    public DialogueCatalog(Gson gson)
    {
        try (Reader reader = new InputStreamReader(
            DialogueCatalog.class.getResourceAsStream(RESOURCE), StandardCharsets.UTF_8))
        {
            CatalogFile file = gson.fromJson(reader, CatalogFile.class);
            shared = file.shared == null ? Collections.emptyMap() : file.shared;
            profiles = file.profiles == null ? Collections.emptyMap() : file.profiles;
            transmogParents = file.transmogParents == null ? Collections.emptyMap() : file.transmogParents;
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Unable to load Pet Banter dialogue catalog", ex);
        }
    }

    public List<String> linesFor(String petName)
    {
        String normalized = petName == null ? "" : petName.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, DialogueProfile> entry : profiles.entrySet())
        {
            if (entry.getValue().transmogs != null)
            {
                for (Map.Entry<String, DialogueProfile> transmog : entry.getValue().transmogs.entrySet())
                {
                    if (matches(normalized, transmog.getKey()))
                    {
                        return combinedLines(transmog.getValue().lines);
                    }
                }
            }
            if (matches(normalized, entry.getKey()))
            {
                return combinedLines(entry.getValue().lines);
            }
        }
        return Collections.emptyList();
    }

    public int authoredLineCount(String petName)
    {
        String normalized = petName == null ? "" : petName.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, DialogueProfile> entry : profiles.entrySet())
        {
            if (entry.getValue().transmogs != null)
            {
                for (Map.Entry<String, DialogueProfile> transmog : entry.getValue().transmogs.entrySet())
                {
                    if (matches(normalized, transmog.getKey()))
                    {
                        return transmog.getValue().lines == null ? 0 : transmog.getValue().lines.size();
                    }
                }
            }
            if (matches(normalized, entry.getKey()))
            {
                return entry.getValue().lines == null ? 0 : entry.getValue().lines.size();
            }
        }
        return 0;
    }

    private List<String> combinedLines(List<String> profileLines)
    {
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        if (profileLines != null)
        {
            unique.addAll(profileLines);
        }
        unique.addAll(shared.getOrDefault("fallback", Collections.emptyList()));
        return new ArrayList<>(unique);
    }

    public String sharedLine(String context, int index)
    {
        List<String> lines = shared.getOrDefault(context, Collections.emptyList());
        return lines.isEmpty() ? null : lines.get(Math.floorMod(index, lines.size()));
    }

    public String contextLine(String petName, String context, int index)
    {
        String normalized = petName == null ? "" : petName.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, DialogueProfile> entry : profiles.entrySet())
        {
            if (entry.getValue().transmogs != null)
            {
                for (Map.Entry<String, DialogueProfile> transmog : entry.getValue().transmogs.entrySet())
                {
                    if (normalized.contains(transmog.getKey().toLowerCase(Locale.ROOT)))
                    {
                        String line = lineFromContext(transmog.getValue(), context, index);
                        if (line != null)
                        {
                            return line;
                        }
                    }
                }
            }
            if (normalized.contains(entry.getKey().toLowerCase(Locale.ROOT)))
            {
                String line = lineFromContext(entry.getValue(), context, index);
                if (line != null)
                {
                    return line;
                }
            }
        }
        String parent = transmogParent(petName);
        if (parent != null && !parent.equalsIgnoreCase(petName))
        {
            return contextLine(parent, context, index);
        }
        return null;
    }

    private String lineFromContext(DialogueProfile profile, String context, int index)
    {
        if (profile.contexts == null)
        {
            return null;
        }
        List<String> lines = profile.contexts.get(context);
        return lines == null || lines.isEmpty() ? null : lines.get(Math.floorMod(index, lines.size()));
    }

    private boolean matches(String normalizedName, String profileName)
    {
        String normalizedProfile = profileName.toLowerCase(Locale.ROOT);
        String withoutPetPrefix = normalizedProfile.startsWith("pet ")
            ? normalizedProfile.substring(4) : normalizedProfile;
        String compactName = normalizedName.replace("'", "");
        String compactProfile = normalizedProfile.replace("'", "");
        String compactWithoutPet = withoutPetPrefix.replace("'", "");
        return normalizedName.equals(normalizedProfile)
            || normalizedName.equals(withoutPetPrefix)
            || normalizedName.equals("pet " + withoutPetPrefix)
            || normalizedName.startsWith(normalizedProfile + " jr")
            || normalizedName.contains(normalizedProfile)
            || compactName.equals(compactProfile)
            || compactName.equals(compactWithoutPet)
            || compactName.startsWith(compactProfile + " jr")
            || compactName.startsWith(compactWithoutPet + " jr")
            || compactName.contains(compactProfile);
    }

    public List<String> sharedLines(String context)
    {
        return shared.getOrDefault(context, Collections.emptyList());
    }

    public String transmogParent(String variantName)
    {
        if (variantName == null)
        {
            return null;
        }
        return transmogParents.get(variantName.toLowerCase(Locale.ROOT));
    }

    public String profileInteraction(String petName, int index)
    {
        String normalized = petName == null ? "" : petName.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, DialogueProfile> entry : profiles.entrySet())
        {
            if (entry.getValue().transmogs != null)
            {
                for (Map.Entry<String, DialogueProfile> transmog : entry.getValue().transmogs.entrySet())
                {
                    if (matches(normalized, transmog.getKey())
                        && transmog.getValue().interactions != null
                        && !transmog.getValue().interactions.isEmpty())
                    {
                        List<String> lines = transmog.getValue().interactions;
                        return lines.get(Math.floorMod(index, lines.size()));
                    }
                }
            }
            if (matches(normalized, entry.getKey())
                && entry.getValue().interactions != null && !entry.getValue().interactions.isEmpty())
            {
                List<String> lines = entry.getValue().interactions;
                return lines.get(Math.floorMod(index, lines.size()));
            }
        }
        return null;
    }

    private static class CatalogFile
    {
        Map<String, List<String>> shared;
        Map<String, String> transmogParents;
        Map<String, DialogueProfile> profiles;
    }

    private static class DialogueProfile
    {
        String personality;
        List<String> lines;
        List<String> interactions;
        Map<String, List<String>> contexts;
        Map<String, DialogueProfile> transmogs;
    }
}
