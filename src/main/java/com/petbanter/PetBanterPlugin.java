package com.petbanter;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@PluginDescriptor(
    name = "Pet Banter",
    description = "Brings OSRS pets to life with contextual reactions and thoughts",
    tags = {"pet", "follower", "companion"}
)
public class PetBanterPlugin extends Plugin
{
    private static final int OVERHEAD_SPEECH_CYCLES = 150;
    private static final long MIN_REACTION_DELAY_MILLIS = 28_000L;
    private static final long MAX_REACTION_DELAY_MILLIS = 148_000L;
    @Inject
    private PetBanterConfig config;

    @Inject
    private PetTracker petTracker;

    @Inject
    private Client client;

    @Inject
    private ReactionEngine reactionEngine;

    private int lastFollowerId = -1;
    private String lastFollowerName;
    private long nextReactionAtMillis;
    private int reactionIndex;
    private boolean firstReaction;
    private NPC activeFollower;
    private ReactionContext pendingContext = ReactionContext.IDLE;
    private long pendingContextUntilMillis;

    @Override
    protected void startUp()
    {
        log.info("Pet Banter started!");
    }

    @Override
    protected void shutDown()
    {
        log.info("Pet Banter stopped!");
        clearOverheadText();
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (!config.enabled())
        {
            clearOverheadText();
            pendingContext = ReactionContext.IDLE;
            pendingContextUntilMillis = 0;
            return;
        }
        int followerId = petTracker.getFollowerId();
        String followerName = petTracker.getFollowerName();
        String previousFollowerName = lastFollowerName;
        boolean followerChanged = followerId != lastFollowerId;
        boolean nameChanged = !java.util.Objects.equals(followerName, lastFollowerName);
        if (followerChanged || nameChanged)
        {
            log.debug("Follower changed: {}", followerName);
            lastFollowerId = followerId;
            lastFollowerName = followerName;
            nextReactionAtMillis = 0;
            firstReaction = previousFollowerName == null || "None".equals(previousFollowerName);
        }
        if (followerId == -1 || !config.showThoughts())
        {
            clearOverheadText();
            return;
        }
        if (System.currentTimeMillis() >= nextReactionAtMillis)
        {
            PetPersonality personality = PetPersonality.fromName(petTracker.getFollowerName());
            ReactionContext context;
            if (firstReaction)
            {
                context = ReactionContext.NEW_FOLLOWER;
            }
            else if (nameChanged && previousFollowerName != null && !"None".equals(previousFollowerName))
            {
                context = ReactionContext.METAMORPHOSIS;
            }
            else if (client.getLocalPlayer() != null
                && client.getLocalPlayer().getHealthRatio() >= 0
                && client.getLocalPlayer().getHealthRatio() < 30)
            {
                context = ReactionContext.DANGER;
            }
            else if (pendingContext == ReactionContext.LOOT
                && System.currentTimeMillis() <= pendingContextUntilMillis)
            {
                context = ReactionContext.LOOT;
            }
            else
            {
                context = ReactionContext.IDLE;
            }
            firstReaction = false;
            reactionIndex++;
            String reaction = reactionEngine.reaction(followerName, context, reactionIndex);
            activeFollower = petTracker.getFollower();
            if (activeFollower == null)
            {
                nextReactionAtMillis = System.currentTimeMillis() + MIN_REACTION_DELAY_MILLIS;
                return;
            }
            activeFollower.setOverheadText(reaction);
            activeFollower.setOverheadCycle(client.getGameCycle() + OVERHEAD_SPEECH_CYCLES);
            if (config.showChatReactions())
            {
                addPetChatMessage(followerName, reaction);
            }
            java.util.List<NPC> nearbyPets = config.showPetInteractions()
                ? petTracker.getNearbyKnownPets(config.interactionRange()) : java.util.Collections.emptyList();
            int interactionsShown = 0;
            for (NPC nearbyPet : nearbyPets)
            {
                if (config.showChatReactions())
                {
                    String opening = reactionEngine.nearbyInteraction(followerName,
                        nearbyPet.getName(), reactionIndex);
                    String reply = reactionEngine.nearbyInteraction(nearbyPet.getName(),
                        followerName, reactionIndex + 1);
                    addPetChatMessage(followerName, opening);
                    addPetChatMessage(nearbyPet.getName(), reply);
                }
                interactionsShown++;
                if (interactionsShown >= 3)
                {
                    break;
                }
            }
            nextReactionAtMillis = System.currentTimeMillis() + ThreadLocalRandom.current()
                .nextLong(MIN_REACTION_DELAY_MILLIS, MAX_REACTION_DELAY_MILLIS + 1);
            pendingContext = ReactionContext.IDLE;
            pendingContextUntilMillis = 0;
        }
    }

    private void addPetChatMessage(String petName, String message)
    {
        if (petName == null || petName.trim().isEmpty() || message == null || message.trim().isEmpty())
        {
            return;
        }
        client.addChatMessage(ChatMessageType.PUBLICCHAT, petName, message, null);
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        String message = event.getMessage();
        if (message == null)
        {
            return;
        }
        String normalized = message.toLowerCase(java.util.Locale.ROOT);
        if (normalized.contains("you receive") || normalized.contains("you found")
            || normalized.contains("you get some"))
        {
            pendingContext = ReactionContext.LOOT;
            pendingContextUntilMillis = System.currentTimeMillis() + 10_000L;
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        clearOverheadText();
        lastFollowerId = -1;
        lastFollowerName = null;
        nextReactionAtMillis = 0;
        firstReaction = true;
        pendingContext = ReactionContext.IDLE;
        pendingContextUntilMillis = 0;
    }

    private void clearOverheadText()
    {
        if (activeFollower != null)
        {
            activeFollower.setOverheadText(null);
            activeFollower = null;
        }
    }

    @Provides
    PetBanterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PetBanterConfig.class);
    }
}
