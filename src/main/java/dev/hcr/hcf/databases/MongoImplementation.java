package dev.hcr.hcf.databases;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import dev.hcr.hcf.HCF;
import dev.hcr.hcf.deathbans.DeathBan;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.factions.types.roads.EastRoad;
import dev.hcr.hcf.factions.types.roads.NorthRoad;
import dev.hcr.hcf.factions.types.roads.SouthRoad;
import dev.hcr.hcf.factions.types.roads.WestRoad;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import dev.hcr.hcf.utils.backend.ConfigFile;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MongoImplementation {
    private final MongoCollection<Document> factions;
    private final MongoCollection<Document> users;
    private final MongoCollection<Document> timers;
    private final MongoCollection<Document> deathbans;

    public MongoImplementation(HCF plugin) {
        ConfigFile config = plugin.getConfiguration("database");
        MongoClient client;
        if (config.getString("mongo.password").isEmpty()) {
            client = new MongoClient(new ServerAddress(config.getString("mongo.host"), config.getInt("mongo.port")));
        } else {
            System.out.println("Attempting to start with password!");
            client = new MongoClient(new ServerAddress(config.getString("mongo.host"), config.getInt("mongo.port")), MongoCredential.createCredential(
                    config.getString("mong.user"), config.getString("mongo.auth-db"), config.getString("mongo.password").toCharArray()
            ), new MongoClientOptions.Builder().build());
        }
        MongoDatabase database = client.getDatabase(config.getString("mongo.database"));
        this.factions = database.getCollection("factions");
        this.users = database.getCollection("users");
        this.timers = database.getCollection("timers");
        this.deathbans = database.getCollection("deathbans");
    }

    public Document findFactionEntry(String factionName) {
        return factions.find(Filters.eq("name", factionName)).first();
    }

    public Document findFactionEntry(UUID uuid) {
        return factions.find(Filters.eq("uuid", uuid.toString())).first();
    }

    public void findFactionAndDelete(UUID uuid) {
        Bson filter = Filters.eq("uuid", uuid.toString());
        factions.deleteOne(filter);
    }

    public void appendFactionDataSync(Document document) {
        TaskUtils.runSync(() -> {
           appendFactionData(document);
        });
    }

    public void appendFactionData(Document document) {
        Bson filter = Filters.eq("uuid", document.getString("uuid"));
        Bson update = new Document("$set", document);
        UpdateOptions options = new UpdateOptions().upsert(true);
        factions.updateOne(filter, update, options);
    }

    public void appendFactionDataAsync(Document document) {
        TaskUtils.runAsync(() -> {
            appendFactionData(document);
        });
    }

    public void appendUserDataSync(Document document) {
        appendData(document);
    }

    private void appendData(Document document) {
        if (document == null) return;
        Bson filter = Filters.eq("uuid", document.getString("uuid"));
        Bson update = new Document("$set", document);
        UpdateOptions options = new UpdateOptions().upsert(true);
        users.updateOne(filter, update, options);
    }

    public void appendUserDataAsync(Document document) {
        TaskUtils.runAsync(() -> {
            appendData(document);
        });
    }

    public void loadUserAsync(UUID uuid, String name) {
        TaskUtils.runAsync(() -> {
            // Create a new instance of user.
            User user = new User(uuid, name);
            Document document = users.find(Filters.eq("uuid", uuid.toString())).first();
            if (document == null) {
                return;
            }
            user.load(document);
        });
    }

    public void appendTimerData(User user, Timer timer) {
        Document timerDocument = new Document("uuid", user.getUuid().toString());
        timerDocument.append("name", timer.getName());
        timerDocument.append("delay", timer.getDelay());
        Bson filter = Filters.eq("uuid", user.getUuid());
        Bson update = new Document("$set", timerDocument);
        UpdateOptions options = new UpdateOptions().upsert(true);
        timers.updateOne(filter, update, options);
    }

    public void appendTimerDataSync(User user, Timer timer) {
        TaskUtils.runSync(() -> {
            appendTimerData(user, timer);
        });
    }

    public void appendTimerDataAsync(User user, Timer timer) {
        TaskUtils.runAsync(() -> {
            appendTimerData(user, timer);
        });
    }

    public void loadFactions() {
        for (Document document : factions.find()) {
            if (document == null) continue;
            System.out.println("Loading: " + document.getString("name"));
            if (document.containsKey("leader")) {
                new PlayerFaction(document);
                continue;
            }
            String name = document.getString("name");
            if (name.toLowerCase().contains("safe") || name.toLowerCase().contains("spawn")) {
                new SafeZoneFaction(document);
            }
            if (name.toLowerCase().contains("wilderness")) {
                new WildernessFaction(document);
            }
            if (name.toLowerCase().contains("warzone")) {
                new WarzoneFaction(document);
            }
            if (name.toLowerCase().contains("road")) {
                String[] split = name.toLowerCase().split("road");
                if (split[0].contains("_")) {
                    split[0] = split[0].replace("_", "");
                }
                loadRoadFaction(split[0], document);
            }
        }
    }

    private void loadRoadFaction(String road, Document document) {
        switch (road.toLowerCase()) {
            case "north":
                new NorthRoad(document);
            case "east":
                new EastRoad(document);
            case "south":
                new SouthRoad(document);
            case "west":
                new WestRoad(document);
        }
    }

    public void loadDeathBans() {
        for (Document document : deathbans.find()) {
          // UUID uuid = UUID.fromString(document.getString("uuid"));
            UUID uuid = (UUID) document.get("uuid");
            long executionTime = document.getLong("executionTime");
            long duration = document.getLong("duration");
            long expiredTime = document.getLong("expiredTime");
            System.out.println(document.entrySet().toString());
            Map<String, Object> map = new HashMap<>();
            map.put("uuid", uuid);
            map.put("executionTime", executionTime);
            map.put("duration", duration);
            map.put("expiredTime", expiredTime);
            new DeathBan(map);
        }
    }

    public void saveDeathBan(DeathBan deathBan) {
        Bson filter = Filters.eq("uuid", deathBan.getUniqueID());
        Bson update = new BasicDBObject("$set", new Document(deathBan.serialize()));
        UpdateOptions options = new UpdateOptions().upsert(true);
        deathbans.updateOne(filter, update, options);
    }

    public void removeDeathBan(UUID uuid) {
        Bson filter = Filters.eq("uuid", uuid);
        deathbans.deleteOne(filter);
    }
}
