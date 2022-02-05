package dev.hcr.hcf.databases;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import dev.hcr.hcf.HCF;
import dev.hcr.hcf.factions.types.PlayerFaction;
import dev.hcr.hcf.factions.types.SafeZoneFaction;
import dev.hcr.hcf.factions.types.WarzoneFaction;
import dev.hcr.hcf.factions.types.WildernessFaction;
import dev.hcr.hcf.utils.backend.ConfigFile;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.UUID;

public class MongoImplementation {
    private final MongoClient client;
    private final MongoDatabase database;
    private final MongoCollection<Document> factions;
    private final HCF plugin;

    public MongoImplementation(HCF plugin) {
        this.plugin = plugin;
        ConfigFile config = plugin.getConfiguration("database");
        if (config.getString("mongo.password").isEmpty()) {
            this.client = new MongoClient(new ServerAddress(config.getString("mongo.host"), config.getInt("mongo.port")));
        } else {
            System.out.println("Attempting to start with password!");
            this.client = new MongoClient(new ServerAddress(config.getString("mongo.host"), config.getInt("mongo.port")), MongoCredential.createCredential(
                    config.getString("mong.user"), config.getString("mongo.auth-db"), config.getString("mongo.password").toCharArray()
            ), new MongoClientOptions.Builder().build());
        }
        this.database = client.getDatabase(config.getString("mongo.database"));
        this.factions = database.getCollection("factions");
    }

    public Document findFactionEntry(String factionName) {
        return factions.find(Filters.eq("name", factionName)).first();
    }

    public Document findFactionEntry(UUID uuid) {
        return factions.find(Filters.eq("uuid", uuid.toString())).first();
    }

    public void appendFactionData(Document document) {
        if (document == null) {
            System.out.println("Document returning null!");
            return;
        }
        // Update the found entry
        // BasicDBObject query = new BasicDBObject("uuid", document.getString("uuid"));
        // System.out.println("Updating faction: " + document.getString("name"));
        // factions.updateOne(query, document, new UpdateOptions().upsert(true));

        Bson filter = Filters.eq("uuid", document.getString("uuid"));
        Bson update = new Document("$set", document);
        UpdateOptions options = new UpdateOptions().upsert(true);
        factions.updateOne(filter, update, options);
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
        }
    }
}
