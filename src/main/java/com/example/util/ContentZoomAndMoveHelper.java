package com.example.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ContentZoomAndMoveHelper {
    private final DoubleProperty scaleFactorProperty = new ReadOnlyDoubleWrapper(0.0D);
    private final Node content;
    private final Pane container;

    public ContentZoomAndMoveHelper(Node content, Pane container) {
        if (content == null || container == null) {
            throw new IllegalArgumentException("Content and container cannot be null.");
        } else {
            this.content = content;
            this.container = container;
            content.toFront();
            this.enablePanAndZoom();
        }
    }

    public void setContentPivot(double x, double y) {
        this.content.setTranslateX(x);
        this.content.setTranslateY(y);
    }

    public static double boundValue(double value, double min, double max) {
        if (Double.compare(value, min) < 0) {
            return min;
        } else {
            return Double.compare(value, max) > 0 ? max : value;
        }
    }

    private double mapToNormalScale(double scale) {
        return scale >= 0 ? scale + 1 : 1 + scale * 0.1;
    }

    private void enablePanAndZoom() {
        container.setOnScroll((event) -> {
            if (event.getDeltaY() == 0.0) {
                return;
            }
            double direction = event.getDeltaY() > 0.0D ? 1.0D : -1.0D;
            double currentScale = this.scaleFactorProperty.getValue();
            double computedScale = currentScale + direction;
            computedScale = boundValue(computedScale, -5.0D, 5.0D);
            if (currentScale != computedScale) {
                double calculatedScale = mapToNormalScale(computedScale);
                this.content.setScaleX(calculatedScale);
                this.content.setScaleY(calculatedScale);
                this.scaleFactorProperty.setValue(computedScale);

                if (calculatedScale == 1.0D) {
                    this.content.setTranslateX(-container.getTranslateX());
                    this.content.setTranslateY(-container.getTranslateY());
                } else {
//                    Bounds bounds = this.content.localToScene(this.content.getBoundsInLocal());
                    Bounds bounds = content.getLayoutBounds();
                    double f = 1 - (calculatedScale);
                    double dx = event.getX() - bounds.getWidth() / 2.0D;
                    double dy = event.getY() - bounds.getHeight() / 2.0D;
                    this.setContentPivot(f * dx, f * dy);
                }
            }

            event.consume();
        });
        DragContext sceneDragContext = new DragContext();
        container.setOnMousePressed((event) -> {
            if (event.isSecondaryButtonDown()) {
                container.getScene().setCursor(Cursor.MOVE);
                sceneDragContext.mouseAnchorX = event.getX();
                sceneDragContext.mouseAnchorY = event.getY();
                sceneDragContext.translateAnchorX = this.content.getTranslateX();
                sceneDragContext.translateAnchorY = this.content.getTranslateY();
            }

        });
        container.setOnMouseReleased((event) -> {
            container.getScene().setCursor(Cursor.DEFAULT);
        });
        container.setOnMouseDragged((event) -> {
            if (event.isSecondaryButtonDown()) {
                this.content.setTranslateX(sceneDragContext.translateAnchorX + event.getX() - sceneDragContext.mouseAnchorX);
                this.content.setTranslateY(sceneDragContext.translateAnchorY + event.getY() - sceneDragContext.mouseAnchorY);
            }

        });
    }

    public DoubleProperty scaleFactorProperty() {
        return this.scaleFactorProperty;
    }

    public void resetLayout() {
        scaleFactorProperty.setValue(0.0);
        this.content.setScaleX(1.0);
        this.content.setScaleY(1.0);
        content.setTranslateX(0);
        content.setTranslateY(0);
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
