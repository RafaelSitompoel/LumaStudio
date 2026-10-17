package com.example.lumastudio.utils;

public class FramePreset {
    private String name;
    private int frameColor;
    private int backgroundColor;
    private int borderWidth;
    private int spacing;
    private boolean isVertical;
    private int slotCount;
    private String stylePattern; // "Solid", "Korean Pastel", "Coquette Hearts", "Vintage Retro"
    private int cornerStyle; // 0 = Rounded Rect, 1 = Heart, 2 = Circle, 3 = Squiggle

    public FramePreset(String name, int frameColor, int backgroundColor, int borderWidth, int spacing, boolean isVertical, int slotCount, String stylePattern, int cornerStyle) {
        this.name = name;
        this.frameColor = frameColor;
        this.backgroundColor = backgroundColor;
        this.borderWidth = borderWidth;
        this.spacing = spacing;
        this.isVertical = isVertical;
        this.slotCount = slotCount;
        this.stylePattern = stylePattern;
        this.cornerStyle = cornerStyle;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getFrameColor() { return frameColor; }
    public void setFrameColor(int frameColor) { this.frameColor = frameColor; }

    public int getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(int backgroundColor) { this.backgroundColor = backgroundColor; }

    public int getBorderWidth() { return borderWidth; }
    public void setBorderWidth(int borderWidth) { this.borderWidth = borderWidth; }

    public int getSpacing() { return spacing; }
    public void setSpacing(int spacing) { this.spacing = spacing; }

    public boolean isVertical() { return isVertical; }
    public void setVertical(boolean vertical) { isVertical = vertical; }

    public int getSlotCount() { return slotCount; }
    public void setSlotCount(int slotCount) { this.slotCount = slotCount; }

    public String getStylePattern() { return stylePattern; }
    public void setStylePattern(String stylePattern) { this.stylePattern = stylePattern; }

    public int getCornerStyle() { return cornerStyle; }
    public void setCornerStyle(int cornerStyle) { this.cornerStyle = cornerStyle; }
}