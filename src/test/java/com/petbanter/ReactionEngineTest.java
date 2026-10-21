package com.petbanter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ReactionEngineTest
{
    @Test
    public void recognizesSupportedPetsByNameWithoutIds()
    {
        assertEquals(PetPersonality.GIANT_SQUIRREL, PetPersonality.fromName("Giant Squirrel"));
        assertEquals(PetPersonality.DARK_ACORN, PetPersonality.fromName("Dark Acorn"));
        assertEquals(PetPersonality.DARK_ACORN, PetPersonality.fromName("Dark Squirrel"));
        assertEquals(PetPersonality.GENERIC, PetPersonality.fromName("Rocky"));
    }

    @Test
    public void newFollowerReactionIsContextual()
    {
        String reaction = new ReactionEngine().reaction(
            PetPersonality.DARK_ACORN, ReactionContext.NEW_FOLLOWER, 0);
        assertEquals("Ready when you are.", reaction);
    }

    @Test
    public void reactionIndexWrapsAround()
    {
        String reaction = new ReactionEngine().reaction(PetPersonality.GIANT_SQUIRREL,
            ReactionContext.IDLE, 3);
        assertTrue(reaction.length() > 0);
    }

    @Test
    public void rosterIncludesPetMetamorphs()
    {
        assertTrue(PetRoster.isKnownPet("Dark Squirrel"));
        assertTrue(PetRoster.isKnownPet("Giant Squirrel"));
        assertTrue(PetRoster.isKnownPet("Youngllef"));
        assertTrue(PetRoster.isKnownPet("Rift Guardian (blood)"));
        assertTrue(PetRoster.isKnownPet("Puppadile"));
        assertTrue(PetRoster.isKnownPet("Pet Penance Queen"));
        assertTrue(PetRoster.isKnownPet("Penance Queen"));
        assertTrue(PetRoster.isKnownPet("Pet Snakeling"));
        assertTrue(PetRoster.isKnownPet("General Graardor Jr."));
        assertTrue(PetRoster.isKnownPet("Kree'arra Jr."));
        assertTrue(PetRoster.isKnownPet("Chaos Elemental Jr."));
        assertTrue(PetRoster.isKnownPet("Smoke Devil"));
        assertTrue(PetRoster.isKnownPet("Pet General Graardor"));
        assertTrue(PetRoster.isKnownPet("Pet K'ril Tsutsaroth"));
        assertTrue(PetRoster.isKnownPet("Pet Kree'arra"));
        assertTrue(PetRoster.isKnownPet("Pet Zilyana"));
        assertTrue(PetRoster.isKnownPet("Dagannoth Prime Jr."));
        assertTrue(PetRoster.isKnownPet("Dagannoth Rex Jr."));
        assertTrue(PetRoster.isKnownPet("Dagannoth Supreme Jr."));
        assertTrue(!PetRoster.isKnownPet("Baby Blue Dragon"));
        assertTrue(!PetRoster.isKnownPet("Pet Baby Blue Dragon"));
        assertTrue(!PetRoster.isKnownPet("General Graardor"));
    }
}
