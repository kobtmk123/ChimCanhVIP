package vn.been.chimcanh.data;

import vn.been.chimcanh.model.ParrotLevel;
import java.util.UUID;

public class ParrotData {
    private final UUID parrotId;
    private UUID ownerId;
    private ParrotLevel level;
    private int seedsEatenInLevel;
    private long lastBathTime;
    private String description;

    public ParrotData(UUID parrotId, UUID ownerId, ParrotLevel level) {
        this.parrotId = parrotId;
        this.ownerId = ownerId;
        this.level = level;
        this.seedsEatenInLevel = 0;
        this.lastBathTime = 0;
        this.description = "Một chú vẹt trung thành.";
    }

    // Thêm các getters và setters ở đây
    // ví dụ: public UUID getParrotId() { return parrotId; }
    // ví dụ: public void setLevel(ParrotLevel level) { this.level = level; }
}