package com.example.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;

public class ContentZoomAndMoveHelper {
    private final DoubleProperty scaleFactorProperty = new ReadOnlyDoubleWrapper(1.0D);
    private final Node content;

    public ContentZoomAndMoveHelper(Node content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null.");
        } else {
            this.content = content;
            content.toFront();
            this.enablePanAndZoom();
        }
    }

    public void setContentPivot(double x, double y) {
        this.content.setTranslateX(this.content.getTranslateX() - x);
        this.content.setTranslateY(this.content.getTranslateY() - y);
    }

    public static double boundValue(double value, double min, double max) {
        if (Double.compare(value, min) < 0) {
            return min;
        } else {
            return Double.compare(value, max) > 0 ? max : value;
        }
    }

    private void enablePanAndZoom() {
        content.setOnScroll((event) -> {
            double direction = event.getDeltaY() >= 0.0D ? 1.0D : -1.0D;
            double currentScale = this.scaleFactorProperty.getValue();
            double computedScale = currentScale + direction * 0.25D;
            computedScale = boundValue(computedScale, 1.0D, 5.0D);
            if (currentScale != computedScale) {
                this.content.setScaleX(computedScale);
                this.content.setScaleY(computedScale);
                if (computedScale == 1.0D) {
                    this.content.setTranslateX(-content.getTranslateX());
                    this.content.setTranslateY(-content.getTranslateY());
                } else {
                    this.scaleFactorProperty.setValue(computedScale);
                    Bounds bounds = this.content.localToScene(this.content.getBoundsInLocal());
                    double f = computedScale / currentScale - 1.0D;
                    double dx = event.getX() - (bounds.getWidth() / 2.0D + bounds.getMinX());
                    double dy = event.getY() - (bounds.getHeight() / 2.0D + bounds.getMinY());
                    this.setContentPivot(f * dx, f * dy);
                }
            }

            event.consume();
        });
        DragContext sceneDragContext = new DragContext();
        content.setOnMousePressed((event) -> {
            if (event.isSecondaryButtonDown()) {
                content.getScene().setCursor(Cursor.MOVE);
                sceneDragContext.mouseAnchorX = event.getX();
                sceneDragContext.mouseAnchorY = event.getY();
                sceneDragContext.translateAnchorX = this.content.getTranslateX();
                sceneDragContext.translateAnchorY = this.content.getTranslateY();
            }

        });
        content.setOnMouseReleased((event) -> {
            content.getScene().setCursor(Cursor.DEFAULT);
        });
        content.setOnMouseDragged((event) -> {
            if (event.isSecondaryButtonDown()) {
                this.content.setTranslateX(sceneDragContext.translateAnchorX + event.getX() - sceneDragContext.mouseAnchorX);
                this.content.setTranslateY(sceneDragContext.translateAnchorY + event.getY() - sceneDragContext.mouseAnchorY);
            }

        });
    }

    public DoubleProperty scaleFactorProperty() {
        return this.scaleFactorProperty;
    }

    class DragContext {
        double mouseAnchorX;
        double mouseAnchorY;
        double translateAnchorX;
        double translateAnchorY;

        DragContext() {
        }
    }
}
