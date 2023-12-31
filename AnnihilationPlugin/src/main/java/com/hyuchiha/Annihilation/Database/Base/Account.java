package com.hyuchiha.Annihilation.Database.Base;

import com.hyuchiha.Annihilation.Game.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class Account {
  private String uuid;
  private String name;
  private int kills;
  private int deaths;
  private int wins;
  private int losses;
  private int nexus_damage;
  private double money;

  private List<Kit> kits = new ArrayList<>();

  public Account(String uuid, String name, int kills, int deaths, int wins, int losses, int nexus_damage) {
    this.money = 0.0D;

    this.uuid = uuid;
    this.name = name;
    this.kills = kills;
    this.deaths = deaths;
    this.wins = wins;
    this.losses = losses;
    this.nexus_damage = nexus_damage;
  }

  public Account(String uuid, String name) {
    this.money = 0.0D;
    this.uuid = uuid;
    this.name = name;
  }


  public String getUUID() {
    return this.uuid;
  }


  public void setUUID(String uuid) {
    this.uuid = uuid;
  }


  public String getName() {
    return this.name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public int getKills() {
    return this.kills;
  }


  public void setKills(int kills) {
    this.kills = kills;
  }


  public int getDeaths() {
    return this.deaths;
  }


  public void setDeaths(int deaths) {
    this.deaths = deaths;
  }


  public int getWins() {
    return this.wins;
  }


  public void setWins(int wins) {
    this.wins = wins;
  }


  public int getLosses() {
    return this.losses;
  }


  public void setLosses(int losses) {
    this.losses = losses;
  }


  public int getNexus_damage() {
    return this.nexus_damage;
  }


  public void setNexus_damage(int nexus_damage) {
    this.nexus_damage = nexus_damage;
  }


  public void increaseKills() {
    this.kills++;
  }


  public void increaseDeaths() {
    this.deaths++;
  }


  public void increaseWins() {
    this.wins++;
  }


  public void increateLosses() {
    this.losses++;
  }


  public void increaseNexusDamage() {
    this.nexus_damage++;
  }


  public Player getPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }

  public List<Kit> getKits() {
    return kits;
  }

  public void setKits(List<Kit> kits) {
    this.kits = kits;
  }

  public boolean hasKit(Kit kitToVerify) {
    for (Kit kit : kits) {
      if (kit == kitToVerify) {
        return true;
      }
    }

    return false;
  }
}
