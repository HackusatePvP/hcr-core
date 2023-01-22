package dev.hcr.hcf.databases.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import dev.hcr.hcf.HCF;
import dev.hcr.hcf.databases.IStorage;
import dev.hcr.hcf.deathbans.DeathBan;
import dev.hcr.hcf.factions.types.*;
import dev.hcr.hcf.factions.types.roads.EastRoad;
import dev.hcr.hcf.factions.types.roads.NorthRoad;
import dev.hcr.hcf.factions.types.roads.SouthRoad;
import dev.hcr.hcf.factions.types.roads.WestRoad;
import dev.hcr.hcf.timers.Timer;
import dev.hcr.hcf.timers.types.PauseTimer;
import dev.hcr.hcf.users.User;
import dev.hcr.hcf.utils.TaskUtils;
import dev.hcr.hcf.utils.backend.types.PropertiesConfiguration;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MongoStorage implements IStorage {
    private final MongoCollection<Document> factions;
    private final MongoCollection<Document> users;
    private final MongoCollection<Document> timerCooldown;
    private final MongoCollection<Document> deathbans;
    private final MongoCollection<Document> lives;

    private final Gson gson = new Gson();
    private final Type type = new TypeToken<HashMap>(){}.getType();
    private final HCF plugin = HCF.getPlugin();

    public MongoStorage() {
        PropertiesConfiguration configuration = PropertiesConfiguration.getPropertiesConfiguration("mongo.properties");
        MongoClient mongoClient;
        if (configuration.getBoolean("db-auth")) {
            // Please note the difference between database and authentication database. The authentication database is a database within mongo that stores user and user permissions.
            // Which is why you have to specify the auth database. By default windows installation it is "admin"
            mongoClient = new MongoClient(new ServerAddress(configuration.getString("host"), configuration.getInteger("port")), MongoCredential.createCredential(
                    configuration.getString("db-auth-user"),
                    configuration.getString("db-auth-db"),
                    configuration.getString("db-auth-password").toCharArray()
            ), MongoClientOptions.builder().build());
        } else {
            mongoClient = new MongoClient(new ServerAddress(configuration.getString("host"), configuration.getInteger("port")));
        }
        MongoDatabase database = mongoClient.getDatabase(configuration.getString("database"));
        this.factions = database.getCollection("factions");
        this.users = database.getCollection("users");
        this.timerCooldown = database.getCollection("timers");
        this.deathbans = database.getCollection("deathbans");
        this.lives = database.getCollection("lives");

        // Last
        loadFactions();
        loadDeathBans();
      //  loadTimers();
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

    public void appendFactionData(Map<String, Object> map) {

        String gsonString = gson.toJson(map, type);
        Document document = Document.parse(gsonString);

        Bson filter = Filters.eq("uuid", document.getString("uuid"));
        Bson update = new Document("$set", document);
        UpdateOptions options = new UpdateOptions().upsert(true);
        factions.updateOne(filter, update, options);
    }

    public void appendFactionDataAsync(Map<String, Object> map) {
        TaskUtils.runAsync(() -> {
            appendFactionData(map);
            User user = User.getUser(UUID.fromString((String) map.get("uuid")));
            appendUserLives(user);
        });
    }

    public void appendUserDataSync(Map<String, Object> map) {
        appendUserData(map);
        User user = User.getUser(UUID.fromString((String) map.get("uuid")));
        appendUserLives(user);
    }

    private void appendUserData(Map<String, Object> map) {
        String gsonString = gson.toJson(map, type);
        Document document = Document.parse(gsonString);

        if (document == null) return;
        Bson filter = Filters.eq("uuid", document.getString("uuid"));
        Bson update = new Document("$set", document);
        UpdateOptions options = new UpdateOptions().upsert(true);
        users.updateOne(filter, update, options);
    }

    public void appendUserDataAsync(Document document) {
        TaskUtils.runAsync(() -> {
            appendUserData(document);
        });
    }

    @Override
    public boolean userExists(UUID uuid) {
        return users.find(Filters.eq("uuid", uuid.toString())).first() != null;
    }

    public void loadUserAsync(UUID uuid, String name) {
        TaskUtils.runAsync(() -> {
            if (User.getUser(uuid) != null) {
                return; // Don't keep loading players if they are already cached.
            }
            // Create a new instance of user.
            User user = new User(uuid, name);
            Document document = users.find(Filters.eq("uuid", uuid.toString())).first();
            if (document == null) {
                return;
            }
            Map<String, Object> map = new HashMap<>();
            document.forEach(map::put);
            user.load(map);
            loadUserLives(uuid);
            loadTimers(uuid);
        });
    }

    public void loadUserAsync(UUID uuid) {
        Document document = users.find(Filters.eq("uuid", uuid.toString())).first();
        if (document == null) {
            return;
        }
        String name = document.getString("name");
        User user = new User(uuid, name);
        user.load(document);
        loadUserLives(uuid);
        loadTimers(uuid);
    }

    private void loadUserLives(UUID uuid) {
        Document document = lives.find(Filters.eq("uuid", uuid.toString())).first();
        if (document == null) return;
        User user = User.getUser(uuid);
        user.setLives(document.getInteger("lives"));
    }

    private void appendUserLives(User user) {
        Bson filter = Filters.eq("uuid", user.getUniqueID().toString());
        Bson update = new Document("$set", new BasicDBObject("uuid", user.getUniqueID()).append("lives", user.getLives()));
        UpdateOptions options = new UpdateOptions().upsert(true);
        lives.updateOne(filter, update, options);
    }

    public void appendTimerData(Map<String, Object> map) {
        String gsonString = gson.toJson(map, type);
        Document document = Document.parse(gsonString);

        Bson uuidFilter = Filters.eq("uuid", document.getString("uuid"));
        Bson typeFilter = Filters.eq("type", document.getString("type"));
        Bson update = new Document("$set", document);
        UpdateOptions options = new UpdateOptions().upsert(true);
        timerCooldown.updateOne(Filters.and(uuidFilter, typeFilter), update, options);
        System.out.println("Saved Timer data...");
    }

    public void appendTimerDataSync(Map<String, Object> map) {
        TaskUtils.runSync(() -> {
            appendTimerData(map);
        });
    }

    public void appendTimerDataAsync(Map<String, Object> map) {
        TaskUtils.runAsync(() -> {
            appendTimerData(map);
        });
    }

    @Override
    public void removeTimer(PauseTimer timer) {
        Bson uuidFilter;
        if (timer.getPlayer() != null) {
            uuidFilter = Filters.eq("uuid", timer.getPlayer().getUniqueId().toString());
        } else {
            uuidFilter = Filters.eq("uuid", timer.getUuid().toString());
        }
        Bson typeFilter = Filters.eq("type", timer.getName());
        Document document = timerCooldown.find(Filters.and(uuidFilter, typeFilter)).first();
        if (document == null) {
            System.out.println("Timer not found.");
            return;
        }
        timerCooldown.deleteOne(document);
    }

    public void loadTimers(UUID uuid) {
        for (Document document : timerCooldown.find(Filters.eq("uuid", uuid.toString()))) {
            Map<String, Object> map = new HashMap<>();
            document.forEach(map::put);
            Timer.createTimer(map);
        }
    }

    public void loadFactions() {
        for (Document document : factions.find()) {
            if (document == null) continue;
            HCF.getPlugin().getLogger().info("Loading faction: " + document.getString("name"));
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
            if (name.toLowerCase().contains("glowstone")) {
                new GlowStoneMountainFaction(document);
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
            System.out.println(document.entrySet());
            Map<String, Object> map = new HashMap<>();
            map.put("uuid", uuid);
            map.put("executionTime", executionTime);
            map.put("duration", duration);
            map.put("expiredTime", expiredTime);
            new DeathBan(map);
        }
    }

    public void saveDeathBan(DeathBan deathBan) {
        Bson filter = Filters.eq("uuid", deathBan.getUniqueID().toString());
        Bson update = new BasicDBObject("$set", new Document(deathBan.serialize()));
        UpdateOptions options = new UpdateOptions().upsert(true);
        deathbans.updateOne(filter, update, options);
    }

    public void removeDeathBan(UUID uuid) {
        Bson filter = Filters.eq("uuid", uuid);
        deathbans.deleteOne(filter);
    }

    public MongoCollection<Document> getUsers() {
        return users;
    }

    @Override
    public void saveUsers() {
        for (User user : User.getUsers()) {

        }
    }

}
