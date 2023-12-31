package com.hyuchiha.Annihilation.Database.Databases;

import com.hyuchiha.Annihilation.Database.Base.Account;
import com.hyuchiha.Annihilation.Database.Base.Database;
import com.hyuchiha.Annihilation.Database.StatType;
import com.hyuchiha.Annihilation.Game.Kit;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;


public class MongoDB extends Database {
  private static final String ACCOUNTS_COLLECTION = "accounts";

  private final Plugin plugin;
  private MongoClient mongoClient;

  public MongoDB(Plugin plugin) {
    super(plugin);

    this.plugin = plugin;
  }


  public boolean init() {
    super.init();

    ConfigurationSection section = getConfigSection();

    MongoCredential credential = MongoCredential.createScramSha1Credential(
        section.getString("user"),
        section.getString("name"),
        section.getString("pass").toCharArray()
    );

    MongoClientOptions.Builder options = new MongoClientOptions.Builder();
    //options.sslEnabled(true);
    //options.sslInvalidHostNameAllowed(true);
    options.connectTimeout(10000);

    this.mongoClient = new MongoClient(
        new ServerAddress(
            getConfigSection().getString("host"),
            getConfigSection().getInt("port")
        ), credential, options.build());

    return getDatabase() != null;
  }


  public MongoDatabase getDatabase() {
    return this.mongoClient.getDatabase(getConfigSection().getString("name"));
  }


  protected List<Account> loadTopAccountsByStatType(StatType type, int size) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    MongoIterable<Document> findIterable = collection.find().sort(Sorts.descending(type.name().toLowerCase())).limit(size);

    List<Account> accounts = new ArrayList<>();

    for (Document document : findIterable) {
      accounts.add(getAccountFromDocument(document));
    }

    return accounts;
  }


  protected void createAccountAndAddToDatabase(Account account) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    collection.insertOne(getDocument(account));

    this.cachedAccounts.put(account.getUUID(), account);
  }


  protected Account loadAccount(String uuid) {
    MongoDatabase database = getDatabase();

    Document document = database.getCollection(ACCOUNTS_COLLECTION).find(Filters.eq("uuid", uuid)).first();

    if (document != null) {
      Account account = getAccountFromDocument(document);

      this.cachedAccounts.put(uuid, account);

      return account;
    }
    return null;
  }


  public void saveAccount(Account account) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);

    Document dbAccount = collection.find(Filters.eq("uuid", account.getUUID())).first();

    if (dbAccount != null) {
      collection.replaceOne(
          Filters.eq("_id", dbAccount.get("_id")),
          getDocument(account));
    } else {

      collection.insertOne(getDocument(account));
    }
  }

  @Override
  public void addUnlockedKit(String uuid, String kit) {
    MongoDatabase database = getDatabase();

    MongoCollection<Document> collection = database.getCollection(ACCOUNTS_COLLECTION);
    Document document = collection.find(eq("uuid", uuid)).first();

    if (document != null) {
      Account account = getAccountFromDocument(document);

      account.getKits().add(Kit.valueOf(kit.toUpperCase()));

      collection.replaceOne(
          eq("_id", document.get("_id")),
          getDocument(account)
      );

      cachedAccounts.put(uuid, account);
    }
  }


  private Document getDocument(Account account) {
    Document document = new Document("uuid", account.getUUID())
        .append("username", account.getName())
        .append("kills", account.getKills())
        .append("deaths", account.getDeaths())
        .append("wins", account.getWins())
        .append("losses", account.getLosses())
        .append("nexus_damage", account.getNexus_damage());

    List<String> kits = new ArrayList<>();

    for (Kit kit : account.getKits()) {
      kits.add(kit.name());
    }

    document.append("kits", kits);

    return document;
  }


  private Account getAccountFromDocument(Document document) {
    Account account = new Account(
        document.getString("uuid"),
        document.getString("username"),
        document.getInteger("kills"),
        document.getInteger("deaths"),
        document.getInteger("wins"),
        document.getInteger("losses"),
        document.getInteger("nexus_damage")
    );

    ArrayList<String> kitsDB = (ArrayList<String>) document.get("kits");

    List<Kit> kits = new ArrayList<>();

    for (String kitToFind : kitsDB) {
      Kit loadedKit = Kit.valueOf(kitToFind);
      kits.add(loadedKit);
    }

    account.setKits(kits);

    return account;
  }


  private ConfigurationSection getConfigSection() {
    return this.plugin.getConfig().getConfigurationSection("Database");
  }
}
