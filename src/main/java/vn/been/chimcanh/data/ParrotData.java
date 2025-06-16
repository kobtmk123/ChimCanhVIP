package vn.been.chimcanh.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Parrot;
import vn.been.chimcanh.model.ParrotLevel;

import java.util.UUID;

public class ParrotData {

    private final UUID parrotId;
    private UUID ownerId;
    private ParrotLevel level;
    private int seedsEatenInLevel;
    private long lastBathTime;
    private String description;
    private long tamedTimestamp;

    public ParrotData(UUID parrotId, UUID ownerId) {
        this.parrotId = parrotId;
        this.ownerId = ownerId;
        this.level = ParrotLevel.NON;
        this.seedsEatenInLevel = 0;
        this.lastBathTime = 0;
        this.description = "Một chú vẹt trung thành.";
        this.tamedTimestamp = System.currentTimeMillis();
    }

    private ParrotData(UUID parrotId, ConfigurationSection section) {
        this.parrotId = parrotId;
        this.ownerId = UUID.fromString(section.getString("owner"));
        this.level = ParrotLevel.valueOf(section.getString("level", "NON"));
        this.seedsEatenInLevel = section.getInt("seeds-eaten");
        this.lastBathTime = section.getLong("last-bath-time");
        this.description = section.getString("description", "Một chú vẹt trung thành.");
        this.tamedTimestamp = section.getLong("tamed-timestamp", System.currentTimeMillis());
    }

    public static ParrotData loadFromConfig(UUID parrotId, ConfigurationSection section) {
        return new ParrotData(parrotId, section);
    }

    public void saveToConfig(ConfigurationSection section) {
        section.set("owner", ownerId.toString());
        section.set("level", level.name());
        section.set("seeds-eaten", seedsEatenInLevel);
        section.set("last-bath-time", lastBathTime);
        section.set("description", description);
        section.set("tamed-timestamp", tamedTimestamp);
    }

    public boolean feed(int amount) {
        if (level.getNextLevel() == null) {
            return false;
        }
        this.seedsEatenInLevel += amount;
        if (this.seedsEatenInLevel >= level.getSeedsToUpgrade()) {
            levelUp();
            return true;
        }
        return false;
    }

    private void levelUp() {
        ParrotLevel nextLevel = level.getNextLevel();
        if (nextLevel != null) {
            this.level = nextLevel;
            this.seedsEatenInLevel = 0;
        }
    }

    public void updateNameTag(Parrot parrot) {
        if (parrot == null || !parrot.isValid()) return;
        parrot.setCustomName(level.getDisplayName());
        parrot.setCustomNameVisible(true);
    }

    // --- CÁC GETTERS & SETTERS ĐÃ ĐƯỢC THÊM VÀO ---
    public UUID getParrotId() {
        return parrotId;
    }
    public UUID getOwnerId() {
        return ownerId;
    }
    public ParrotLevel getLevel() {
        return level;
    }
    public String getDescription() {
        return description;
    }
    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    // ---------------------------------------------
}