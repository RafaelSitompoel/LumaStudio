package com.example.lumastudio.models;

public class FrameModel {
    private final String id;
    private final String name;
    private final String category;
    private final int slotCount;
    private final String aspectRatio;
    private final int frameColor;
    private final int backgroundColor;
    private final String patternName;
    private final String description;

    public FrameModel(String id, String name, String category, int slotCount, String aspectRatio, int frameColor, int backgroundColor, String patternName, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.slotCount = slotCount;
        this.aspectRatio = aspectRatio;
        this.frameColor = frameColor;
        this.backgroundColor = backgroundColor;
        this.patternName = patternName;
        this.description = description;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getSlotCount() { return slotCount; }
    public String getAspectRatio() { return aspectRatio; }
    public int getFrameColor() { return frameColor; }
    public int getBackgroundColor() { return backgroundColor; }
    public String getPatternName() { return patternName; }
    public String getDescription() { return description; }
}