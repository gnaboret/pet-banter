package com.petbanter;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

@Singleton
public class PetTracker
{
    private final Client client;

    @Inject
    public PetTracker(Client client)
    {
        this.client = client;
    }

    public NPC getFollower()
    {
        return client.getFollower();
    }

    public int getFollowerId()
    {
        NPC follower = getFollower();
        return follower == null ? -1 : follower.getId();
    }

    public String getFollowerName()
    {
        NPC follower = getFollower();
        return follower == null ? "None" : follower.getName();
    }

    public List<NPC> getNearbyKnownPets()
    {
        return getNearbyKnownPets(5);
    }

    public List<NPC> getNearbyKnownPets(int range)
    {
        NPC follower = getFollower();
        if (follower == null || follower.getWorldLocation() == null)
        {
            return java.util.Collections.emptyList();
        }
        int followerPlane = follower.getWorldLocation().getPlane();
        int interactionRange = Math.max(1, Math.min(15, range));
        return client.getNpcs().stream()
            .filter(npc -> npc != follower)
            .filter(npc -> PetRoster.isKnownPet(npc.getName()))
            .filter(npc -> npc.getWorldLocation() != null)
            .filter(npc -> npc.getWorldLocation().getPlane() == followerPlane)
            .filter(npc -> follower.getWorldLocation().distanceTo(npc.getWorldLocation()) <= interactionRange)
            .sorted(Comparator
                .comparingInt((NPC npc) -> follower.getWorldLocation().distanceTo(npc.getWorldLocation()))
                .thenComparing(npc -> npc.getName() == null ? "" : npc.getName()))
            .collect(Collectors.toList());
    }
}
