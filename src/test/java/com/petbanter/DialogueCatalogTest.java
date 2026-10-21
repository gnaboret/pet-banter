package com.petbanter;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;

public class DialogueCatalogTest
{
    @Test
    public void loadsNamedProfilesAndSharedContexts()
    {
        DialogueCatalog catalog = new DialogueCatalog(new Gson());
        assertTrue(catalog.linesFor("Phoenix").size() >= 100);
        assertEquals(catalog.linesFor("Phoenix").size(),
            new HashSet<>(catalog.linesFor("Phoenix")).size());
        assertTrue(catalog.linesFor("Dark Squirrel").contains("The shadows are listening."));
        assertTrue(catalog.linesFor("Dark Squirrel").size() >= 100);
        assertTrue(catalog.sharedLines("fallback").size() >= 100);
        assertTrue(catalog.sharedLine("travel", 0).length() > 0);
        assertEquals("Race you to the next tree.",
            catalog.profileInteraction("Giant Squirrel", 1));
        ReactionEngine engine = new ReactionEngine(catalog);
        assertTrue(engine.nearbyInteraction("Giant Squirrel", "Dark Squirrel", 0).length() > 0);
        assertTrue(catalog.linesFor("Puppadile").size() >= 100);
        assertTrue(catalog.linesFor("Greatish Guardian").size() >= 100);
        assertTrue(catalog.linesFor("Elidinis' Guardian").size() >= 100);
        assertTrue(catalog.linesFor("Callisto Cub (retro)").size() >= 100);
        assertTrue(catalog.linesFor("Zebo").size() >= 100);
        assertTrue(catalog.linesFor("Tumeken's Damaged Guardian").size() >= 100);
        assertTrue(catalog.linesFor("Vanguard").size() >= 100);
        assertEquals("olmlet", catalog.transmogParent("Vanguard"));
        assertEquals("giant squirrel", catalog.transmogParent("Dark Squirrel"));
        assertEquals("pet dark core", catalog.transmogParent("Corporeal Critter"));
        assertTrue(catalog.linesFor("Penance Queen").contains("The next wave will test everything."));
        assertEquals("Good. Something finally understands the dark.",
            catalog.contextLine("Dark Squirrel", "danger", 0));
        assertEquals("Excellent. A crisis worthy of my entrance.",
            catalog.contextLine("Phoenix", "danger", 0));
        assertTrue(catalog.linesFor("Baby Blue Dragon").isEmpty());
        assertTrue(engine.reaction("A newly added transmog", ReactionContext.IDLE, 1).contains("newly added transmog"));
        for (String petName : PetRoster.names())
        {
            assertTrue("Too few authored lines for " + petName + " (" + catalog.authoredLineCount(petName) + ")",
                catalog.authoredLineCount(petName) >= 9);
            assertTrue("No dialogue for " + petName,
                engine.reaction(petName, ReactionContext.IDLE, 0).length() > 0);
        }
    }
}
