package com.example.controller;

import com.example.draw.CreationHelper;
import com.example.draw.DrawMode;
import com.example.draw.TraitorsGenerator;
import com.example.model.MyGraph;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.callback.ConfirmationCallback;
import java.util.Optional;

@Component
@FxmlView("/view/drawMenuView.fxml")
public class DrawMenuController {
    @FXML
    private MenuItem vertexItem;
    @FXML
    private MenuItem edgeItem;
    @FXML
    private MenuItem deleteItem;
    @FXML
    private MenuItem noneItem;
    @FXML
    private MenuItem undoItem;
    @FXML
    private MenuItem redoItem;
    @FXML
    private MenuItem generateTraitorsItem;
    @FXML
    private MenuItem generateGraphItem;

    @Autowired
    private FxWeaver fxWeaver;

    private CreationHelper drawHelper;

    public void selectMode(DrawMode mode){
        drawHelper.setDrawMode(mode);
        System.out.println("Selected mode: "+mode);
    }

    @FXML
    public void initialize(){
        vertexItem.setOnAction(e -> selectMode(DrawMode.VERTEX));
        edgeItem.setOnAction(e -> selectMode(DrawMode.EDGE));
        deleteItem.setOnAction(e -> selectMode(DrawMode.DELETE));
        noneItem.setOnAction(e -> selectMode(DrawMode.NONE));
        undoItem.setOnAction(e -> undo());
        redoItem.setOnAction(e -> redo());
        generateTraitorsItem.setOnAction(e -> openGenerateTraitorsDialog());
        generateGraphItem.setOnAction(e -> openGenerateGraphDialog());
    }

    public void setEnabled(boolean enabled) {
        vertexItem.setDisable(!enabled);
        edgeItem.setDisable(!enabled);
        deleteItem.setDisable(!enabled);
        noneItem.setDisable(!enabled);
        generateTraitorsItem.setDisable(!enabled);
    }

    public void undo() {
        drawHelper.getCommandRegistry().undo();
    }

    public void redo() {
        drawHelper.getCommandRegistry().redo();
    }

    private void openGenerateTraitorsDialog(){
        Dialog<ButtonType> generateTraitorsDialog = new Dialog<>();
        // load dialog pane
        FxControllerAndView<GenerateTraitorsController, DialogPane> controllerAndView = fxWeaver.load(GenerateTraitorsController.class);
        generateTraitorsDialog.setDialogPane(controllerAndView.getView().orElseThrow(() -> new RuntimeException("Can't load dialog view, when there is no present")));

        Optional<ButtonType> result = generateTraitorsDialog.showAndWait();
        if (result.get() == ButtonType.OK){
            controllerAndView.getController().generateTraitors((MyGraph<Integer, Integer>) drawHelper.getGraphController().getGraph());
        }
    }

    private void openGenerateGraphDialog(){
        Dialog<ButtonType> generateGraphDialog = new Dialog<>();
        // load dialog pane
        FxControllerAndView<GenerateGraphController, DialogPane> controllerAndView = fxWeaver.load(GenerateGraphController.class);
        generateGraphDialog.setDialogPane(controllerAndView.getView().orElseThrow(() -> new RuntimeException("Can't load dialog view, when there is no present")));

        Optional<ButtonType> result = generateGraphDialog.showAndWait();
        if (result.get() == ButtonType.OK){
            controllerAndView.getController().generateGraph(drawHelper.getGraphController());
        }
    }

    public void setDrawHelper(CreationHelper drawHelper) {
        undoItem.disableProperty().bind(Bindings.size(drawHelper.getCommandRegistry().getCommandStack()).isEqualTo(0));
        redoItem.disableProperty().bind(Bindings.size(drawHelper.getCommandRegistry().getAbortedCommandStack()).isEqualTo(0));
        this.drawHelper = drawHelper;
    }

    public BooleanProperty undoItemDisableProperty() {
        return undoItem.disableProperty();
    }

    public BooleanProperty redoItemDisableProperty() {
        return redoItem.disableProperty();
    }
}
