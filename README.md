# Pet Banter

Pet Banter gives the player's current RuneLite follower small, local thought bubbles.

## Supported personalities

The catalog contains dedicated personalities and lines for the recognized OSRS follower pets and supported metamorphoses, with shared fallback pools for any newly encountered name. Giant Squirrel and Dark Squirrel also have bespoke interaction and context overrides. Detection uses the verified `Client.getFollower()` reference and runtime follower names; no NPC IDs are hardcoded.

## Settings

The plugin can be enabled or disabled, thought bubbles/chat reactions can be hidden, and nearby-pet interactions can be disabled independently. Follow-up reactions use a random 28-second to 2-minute-28-second delay. Reactions are local overlay text and do not send game input or network requests.

## Dialogue catalog

Dialogue lives in `src/main/resources/pet-banter-dialogue.json`. A profile can define a personality, ordinary lines, pet-to-pet `interactions`, nested `transmogs`, and optional `contexts` maps for `danger`, `loot`, `travel`, and `metamorphosis`. The top-level `transmogParents` map records each variant’s base pet. Profile context lines take precedence over shared pools; shared lines remain the fallback. Up to three recognized nearby pets on the same plane can join one exchange, within the configurable one-to-fifteen-tile range (five by default). Adding a new pet or transmog should normally require only a JSON edit plus a roster name when nearby recognition is needed.

## Development

Use Java 11 and the included wrapper:

```powershell
.\gradlew.bat test
.\gradlew.bat run
```

The `run` task starts a separate development RuneLite client; it is not the RuneLite instance launched by the installed Jagex Launcher. The source plugin will therefore appear in the Gradle client, but not automatically in the installed production client. The supported production-client route is to package and submit the plugin through RuneLite's normal external-plugin/plugin-hub review process, then enable the approved plugin in the installed RuneLite. Do not copy unsigned jars into the installed client, automate login, or bypass Jagex authentication. A clean build only verifies compilation; in-game behavior must be checked manually with a follower equipped.
