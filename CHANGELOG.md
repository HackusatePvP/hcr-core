# Changelog 2022.03.a6 (3/22/2022)
- Greatly improved the pvp class system
  - Added the ability to hook iHCF events instead of using the class task detection system.
  - Added archer abilities; Speed 5 and Resistance 5.
  - Created task to handle default class effects.
  - Added archer tags (untested).
    - Added [ArcherTagPlayerEvent](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/pvpclass/events/archer/ArcherTagPlayerEvent.java)
  - Added [MinerClass]() (untested).
  - Added [BardClass]()
    - Bard class can only give out positive effects to teammates, negative effects will be added in the next update.
  - Greatly improved applying class effects to the class holder and teammate effects.
    - Passive effects will no longer override any clickable effects.
    - Fixed infinite looping bug when a clickable effect had expired.
  - Added [PassiveEffectApplyTask](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/pvpclass/tasks/PassiveEffectApplyTask.), this is only used if you are not using iHCF events.
- Added improvements to [Timers](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/Timer.java)
  - Improved timer designs and messages.
  - Added new timers for pvp classes.
    - Added [ArcherTagTimer](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/timers/types/player/ArcherTagTimer.java)
    - Added [ClassWarmupTimer](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/timers/types/player/ClassWarmupTimer.java)
    - Added [ArcherResistanceTimer](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/timers/types/player/effects/ArcherResistanceTimer.java)
    - Added [ArcherSpeedTimer](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/timers/types/player/effects/ArcherSpeedTimer.java)
    - Added [FactionHomeTimer](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/timers/types/player/faction/FactionHomeTimer.java)
  - Timers now work with system time to increase timer accuracy.
- Added improvements to factions.
  - Fixed bug which caused duplicate road saving.
  - Each territory can define whether deathbans are enabled.   
  - Improved [FactionShowCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/member/FactionShowCommand.java) messages.
    - Converts player uuids to string names.
    - Added role sorting.
    - Added different coloring to online and offline players.
  - Added [FactionHomeCommand](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/commands/member/FactionHomeCommand.java)  
  - Improved faction chat design.
  - Added more faction staff commands.
    - [FactionForceJoinCommand](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/commands/staff/FactionForceJoinCommand,java)
    - [FactionForceKickCommand](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/commands/staff/FactionForceKickCommand.java) (not finished).
    - [FactionForceLeaderCommand](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/commands/staff/FactionForceLeaderCommand.java)
    - [FactionForceSetHomeCommand](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/commands/staff/FactionForceSetHomeCommand.java)
  - Improved the faction show messages and design.
- Started implementation of [DeathBans](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/deathbans/DeathBan.java)
  - Deathbans will be using a different saving method by serialization for simplicity.
  - Deathbans can be configured in the [config.yml](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/resources/config.yml), please note you will need a proper hook setup.
- User improvements:
  - Started implementation of [UserEvents](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/users/UserEvent.java)
- Added scoreboard which will require [Assemble](https://github.com/ThatKawaiiSam/Assemble).
- Territory improvements:
  - Improved territory protection, this not fully finished yet.
  - Added illegal interaction messages.
- Re-packaged listeners for better organization.  
- Existing Bugs:
  - Temporarily removed saving faction invites to the database. This will be fixed by the next update.
  - Claims that are created with the [FactionClaimForCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/staff/FactionClaimForCommand.java) will not load properly from the database. This will be fixed in the next update.
  - There maybe some issues with bard effects if you are not using the iHCF events. This will be addressed in the next update.

# Changelog 2022.02.a5 (2/17/2022)
- Started implementation of [timers](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/Timer.java).
  - Added [SOTWTimer](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/types/server/SOTWTimer.java)
  - Added [CombatTimer](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/types/player/CombatTimer.java)
  - Added [EnderPearlTimer](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/types/player/EnderPearlTimer.java)
  - Added custom events which can be used to modify timers.
    - [TimerEvent](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/structure/TimerEvent.java)
    - [TimerExpireEvent](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/structure/TimerExpireEvent.java)
    - [TimerStartEvent](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/structure/TimerStartEvent.java)
    - [TimerStopEvent](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/timers/structure/TimerStopEvent.java)
- Added more faction functionality.
  - Improved territory protection.
    - Added build ranges to warzone. Configurations will be stored in the [faction.properties](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/resources/factions/faction.properties) file.
    - Added [FactionBypassCommand](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/commands/staff/FactionBypassCommand.java) to bypass territory protection listeners.
  - Created all [RoadFaction](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/types/roads/RoadFaction.java) factions.
  - Added [FactionSetHomeCommand](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/commands/captain/FactionSetHomeCommand.java), not completed.
  - Slightly improved faction designs and messages.
  - Slightly fixed faction map pillars not disappearing.
- Fixed duplication of claims when saving to mongodb.
- Added more functionality to user.
  - All timer functions will be stored in the [User](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/users/User.java) class. Such as adding, removing, and retrieving active timers.
- Added the ability to hook other cores into the plugin. Currently, supports [Aqua](https://www.mc-market.org/resources/11118/) and Cove (not public).  
- Started [PvPClass](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/pvpclass/PvPClass.java) implementations.
  - Added [ArcherClass](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/pvpclass/types/ArcherClass.java), not finished.
  - Added custom events to better handle class equipping and un-equipping.
    - Added [PvPClassEvent](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/pvpclass/events/PvPClassEvent.java)
    - Added [ClassEquippedEvent](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/pvpclass/events/ClassEquippedEvent.java)
    - Added [ClassUnequippedEvent](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/pvpclass/events/ClassUnequippedEvent.java)
  - Classes do not need a special spigot which can handle armor equip and unequipped events.
  - All classes are handled by the [KitDetectionClass](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/pvpclass/KitDetectionTask.java) and ran asynchronously up until potion effects are given.

# Changelog 2022.02.a4 (2/10/2022)
- Added packet handlers and implemented [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) packet library (version 4.6).
  - Started [PacketController](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/packets/PacketController.java) api implementation.
- Added more faction functionality.
  - Added client side pillars when claiming.
  - Added [FactionMapCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/member/FactionMapCommand.java)
  - Started [FactionClaimForCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/staff/FactionClaimForCommand.java)
  - Added several new faction events. Each event is fired asynchronously.
    - [PlayerPacketEvent](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/events/packets/PlayerPacketEvent.java)
    - [RemoveClaimingPillarPacketsEvent](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/events/packets/RemoveClaimingPillarPacketsEvent.java)
    - [SendClaimingPillarPacketsEvent](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/events/packets/SendClaimingPillarPacketsEvent.java)
    - [SendFactionMapPacketsEvent](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/factions/events/packets/SendFactionMapPacketsEvent.java)
  - Fully implemented faction colors.
- Improved faction design.
  - Changed RegenStatus coloring and Unicode's.
  - Changed [Role](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/users/faction/Role.java) astrix design to unicode stars.
  - Fixed improper relation display.
- Added more functionality to users.
  - Added reclaim variable (Used to check if the user has reclaimed for the map).
  - Added factionMap variable (Used to check if the user has faction map enabled).
- Added logic to retrieve specific corners of a cuboid.
- Started to add logic to claim for different factions.
- Implemented precise calculations for warzone radius. 
- Added unique donor reclaim system [ClaimBonusChest](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/commands/donor/ClaimBonusChestCommand.java).

# Changelog 2022.02.a3 (2/6/2022)
- Fixed several database issues.
  - Properly integrated MongoDB upsert operations.
  - Properly called faction super loading functions for loading.
- Added basic faction design.
  - Started implementation of faction colors.
  - Added spigots ChatComponentAPI.
- Started implementation of user database.  
  - Added [PvPStatistics](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/users/statistics/types/PvPStatistics.java) (Not finished).
  - Added [OreStatistics](https://github.com/HackusatePvP/hcr-core/tree/main/src/main/java/dev/hcr/hcf/users/statistics/types/OreStatistics.java) (Not finished).
- Fixed command permission logic.  
- Fixed minor bugs.
  - You can no longer invite a player to your faction whose already in your faction.
  - You can no longer join multiple factions.

# Changelog 2022.02.a2 (2/2/2022)
- Added more faction functionality.
  - Added more system factions; [Warzone](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/types/WarzoneFaction.java), [Wilderness](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/types/WildernessFaction.java)
  - Added more events to handle territory protection.
  - Added logic to get faction by a location.
  - Claims have been implemented (Not finished).
- Added more commands.
  - Factions
    - [FactionClaimCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/coleader/FactionClaimCommand.java)
    - [FactionPromoteCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/coleader/FactionPromoteCommand.java)
- Implemented databases (Not finished).
  - Added MongoDB support.
  - All current faction data now saves to the database.
- Added logic to load users' factions.
- Started adding saving implementations (Not finished).
- Added more utils.
  - Added [Cuboid](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/claims/cuboid/Cuboid.java) utility. All credit goes to the original [creator](https://www.spigotmc.org/threads/region-cuboid.329859/).
  - Added [TaskUtils](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/utils/TaskUtils.java).
  - Added [LocationUtils](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/utils/LocationUtils.java).
- Properties configuration will now create a file with preset values.

# Changelog 2022.01.a1 (1/30/2022)
- Improved faction command framework; Aliases are now mapped separately from the main command map.
- Added more commands.
  - Core
    - [BalanceCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/commands/players/BalanceCommand.java)
    - [Economy](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/commands/admin/EconomyCommand.java)
  - Factions
    - [FactionDepositCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/member/FactionDepositCommand.java)
    - [FactionLeaveCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/member/FactionLeaveCommand.java)
    - [FactionWithdrawCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/captain/FactionWithdrawCommand.java)
- Improved Faction functionality.
  - Added regen task and status.
  - Added faction balance.
  - Added faction relations.
  - Added faction events to improve api and implementations.
    - [PlayerFactionJoinEvent](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/events/members/PlayerJoinFactionEvent.java)
    - [PlayerFactionLeaveEvent](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/events/members/PlayerFactionLeaveEvent.java)
- Improved User functionality.
  - Added user balance.
  - Added user [ChatChannel](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/users/User.java)
- Improved Faction Design.
  - Cleaned up [FactionShowCommand](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/java/dev/hcr/hcf/factions/commands/member/FactionShowCommand.java)
  - Added messages to faction commands.
- Added more configurations.
  - Added properties' configuration file for factions [faction.properties](https://github.com/HackusatePvP/hcr-core/blob/main/src/main/resources/factions/faction.properties)
- Improved and fixed tab completers for various commands.    
