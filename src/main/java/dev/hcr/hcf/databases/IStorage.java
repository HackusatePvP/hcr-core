package dev.hcr.hcf.databases;

import dev.hcr.hcf.deathbans.DeathBan;
import dev.hcr.hcf.timers.types.PauseTimer;
import org.bson.Document;

import java.util.Map;
import java.util.UUID;

public interface IStorage {

    void saveUsers();

    void appendFactionData(Map<String, Object> map);

    void findFactionAndDelete(UUID uuid);

    void loadUserAsync(UUID uuid, String name);

    void loadUserAsync(UUID uuid);

    void appendUserDataSync(Map<String, Object> map);

    void appendUserDataAsync(Document document);

    void appendTimerDataAsync(Map<String, Object> map);

    void removeTimer(PauseTimer timer);

    boolean userExists(UUID uuid);

    void saveDeathBan(DeathBan deathBan);

    void removeDeathBan(UUID uuid);

    void loadTimers(UUID uuid);
}
