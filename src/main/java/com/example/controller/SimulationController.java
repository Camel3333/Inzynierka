package com.example.controller;

import com.example.algorithm.AlgorithmType;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.report.OperationsBatch;
import com.example.algorithm.ProbabilityType;
import com.example.algorithm.report.StepReport;
import com.example.model.MyGraph;
import com.example.controller.settings.AlgorithmSettingsController;
import com.example.controller.settings.KingSettingsController;
import com.example.controller.settings.LamportSettingsController;
import com.example.controller.settings.QVoterSettingsController;
import com.example.simulation.SimpleSimulation;
import com.example.simulation.Simulation;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import lombok.Getter;
import net.rgielen.fxweaver.core.FxControllerAndView;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@FxmlView("/view/simulationOptionsView.fxml")
public class SimulationController {
    @FXML
    public TextFlow warning;
    @FXML
    private VBox parent;
    @FXML
    private LamportSettingsController lamportSettingsController;
    @FXML
    private KingSettingsController kingSettingsController;
    @FXML
    private QVoterSettingsController qVoterSettingsController;
    @FXML
    private ComboBox<AlgorithmType> algorithmsBox;
    @FXML
    private Slider animationSpeedSlider;

    @Getter
    private final BooleanProperty startDisabledProperty = new SimpleBooleanProperty();
    @Getter
    private final BooleanProperty nextStepDisabledProperty = new SimpleBooleanProperty();
    @Getter
    private final BooleanProperty liveDisabledProperty = new SimpleBooleanProperty();
    @Getter
    private final BooleanProperty instantFinishDisabledProperty = new SimpleBooleanProperty();
    @Getter
    private final BooleanProperty pauseDisabledProperty = new SimpleBooleanProperty();

    private Service<?> activeService;
    private Simulation simulation;

    @Autowired
    private StatisticsController statisticsController;

    @Autowired
    private DocumentationController documentationController;

    @Autowired
    private LoggerController loggerController;

    @Autowired
    private GraphController graphController;

    @Autowired
    private FxWeaver fxWeaver;

    private final BooleanProperty paused = new SimpleBooleanProperty(true);
    private final BooleanProperty started = new SimpleBooleanProperty(false);
    private final BooleanProperty idle = new SimpleBooleanProperty(true);
    private final BooleanProperty isFinished = new SimpleBooleanProperty(false);
    private final BooleanProperty areAlgorithmSettingsValid = new SimpleBooleanProperty(true);

    public void show() {
        parent.setVisible(true);
        parent.setManaged(true);
        initWarning();
    }

    public void hide() {
        parent.setVisible(false);
        parent.setManaged(false);
    }

    public void setSettingsValidation(GraphController graphController) {
        lamportSettingsController.adjustSettingsConditions(graphController.getGraph());
        kingSettingsController.adjustSettingsConditions(graphController.getGraph());
        qVoterSettingsController.adjustSettingsConditions(graphController.getGraph());
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        animationSpeedSlider.valueProperty().addListener(
                observable -> Platform.runLater(
                        () -> simulation.setAnimationsSpeed(animationSpeedSlider.getValue()
                        )
                )
        );
    }

    private void initWarning() {
        MyGraph<Integer, Integer> graph = graphController.getGraph();
        if (graph != null && algorithmsBox.getValue() != AlgorithmType.QVOTER) warning.setVisible(
                ! graph.isComplete()
        );
        else if (algorithmsBox.getValue() == AlgorithmType.QVOTER) warning.setVisible(false);
    }

    @FXML
    public void initialize() {
        hideAlgorithmSettings();
        algorithmsBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(AlgorithmType algorithmType, boolean empty) {
                super.updateItem(algorithmType, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(algorithmType.toString());
                }
            }
        });

        algorithmsBox.getSelectionModel().selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        hideAlgorithmSettings();
                        showAlgorithmSettings(newValue);
                        bindStartButtonWithAlgorithmSettings(newValue);
                    }
                }));

        algorithmsBox.getSelectionModel().selectedIndexProperty().addListener(
                (index) -> initWarning()
        );

        nextStepDisabledProperty.setValue(true);
        liveDisabledProperty.setValue(true);
        instantFinishDisabledProperty.setValue(true);
        pauseDisabledProperty.setValue(true);

        List<Observable> dependenciesList = new ArrayList<>();
        dependenciesList.add(paused);
        dependenciesList.add(started);
        dependenciesList.add(idle);
        dependenciesList.add(isFinished);
        Observable[] dependencies = dependenciesList.toArray(new Observable[0]);

        nextStepDisabledProperty.bind(Bindings.createBooleanBinding(() ->
                !(idle.get() && started.get() && !isFinished.get()), dependencies));

        liveDisabledProperty.bind(Bindings.createBooleanBinding(() ->
                !(idle.get() && started.get() && !isFinished.get()), dependencies));

        instantFinishDisabledProperty.bind(Bindings.createBooleanBinding(() ->
                !(idle.get() && started.get() && !isFinished.get()), dependencies));

        pauseDisabledProperty.bind(Bindings.createBooleanBinding(() ->
                !(!paused.get() && started.get() && !isFinished.get()), dependencies));
    }

    private AlgorithmSettingsController getAlgorithmController(AlgorithmType algorithmType) {
        switch (algorithmType) {
            case LAMPORT -> {
                return lamportSettingsController;
            }
            case KING -> {
                return kingSettingsController;
            }
            case QVOTER -> {
                return qVoterSettingsController;
            }
        }
        return null;
    }

    private void bindStartButtonWithAlgorithmSettings(AlgorithmType algorithmType) {
        areAlgorithmSettingsValid.bind(getAlgorithmController(algorithmType).getAreSettingsValidProperty());
    }

    private void setSimulationFlagsToNotStartedState() {
        paused.unbind();
        started.unbind();
        idle.unbind();
        isFinished.unbind();
        paused.set(true);
        started.set(false);
        idle.set(true);
        isFinished.set(false);
    }

    private void showAlgorithmSettings(AlgorithmType algorithmType) {
        setSettingsManagedAndVisible(algorithmType, true);
    }

    private void hideAlgorithmSettings() {
        hideAlgorithmSettings(AlgorithmType.LAMPORT);
        hideAlgorithmSettings(AlgorithmType.KING);
        hideAlgorithmSettings(AlgorithmType.QVOTER);
    }

    private void hideAlgorithmSettings(AlgorithmType algorithmType) {
        setSettingsManagedAndVisible(algorithmType, false);
    }

    private void setSettingsManagedAndVisible(AlgorithmType algorithmType, boolean enable) {
        AlgorithmSettingsController algorithmSettingsController = getAlgorithmController(algorithmType);
        Node settingsParent = algorithmSettingsController.getParent();
        settingsParent.setManaged(enable);
        settingsParent.setVisible(enable);
    }

    public void setAvailableAlgorithms(ObservableList<AlgorithmType> algorithmTypes) {
        algorithmsBox.setItems(algorithmTypes);
        algorithmsBox.getSelectionModel().select(0);

        List<Observable> inputDependencies = List.of(started, isFinished, idle, areAlgorithmSettingsValid);
        Observable[] dependencies = inputDependencies.toArray(new Observable[0]);

        startDisabledProperty.unbind();
        startDisabledProperty.bind(Bindings.createBooleanBinding(() -> {
                    if (simulation.isGraphEmpty() || started.get() || !areAlgorithmSettingsValid.get() || !idle.get()) {
                        return true;
                    }
                    return false;
                }, dependencies
        ));
    }

    public void initSimulation() {
        statisticsController.clear();
        simulation.clearData();
        simulation.allowAnimations(true);
        AlgorithmType selectedAlgorithm = algorithmsBox.getValue();
        simulation.setEnvironment(selectedAlgorithm.getAlgorithm(), getAlgorithmController(selectedAlgorithm).getAlgorithmSettings());
        ((SimpleSimulation) simulation).loadEnvironment();
        isFinished.bind(((SimpleSimulation) simulation).getIsFinishedProperty());
        loggerController.addItem("[Start] Simulation started with " + selectedAlgorithm + " algorithm.");
        started.set(true);
    }

    private void processStep() {
        StepReport report = simulation.step();
        for (OperationsBatch operationBatch : report.getOperationsBatches()) {
            loggerController.addItem("");
            for (Operation operation : operationBatch.getOperations()) loggerController.addItem("[Event] " + operation.getDescription());
        }
        statisticsController.addStats(report.getNumSupporting(), report.getNumNotSupporting());
        graphController.updateVerticesTooltips();
    }

    public void openResultDialog() {
        Platform.runLater(() -> {
            Dialog<ButtonType> simulationResultDialog = new Dialog<>();
            FxControllerAndView<SimulationResultController, DialogPane> controllerAndView = fxWeaver.load(SimulationResultController.class);
            controllerAndView.getController().setMessage(graphController.getGraph().checkConsensus());
            simulationResultDialog.setDialogPane(controllerAndView.getView().orElseThrow(() -> new RuntimeException("Can't load dialog view, when there is no present")));
            simulationResultDialog.showAndWait();
        });
    }

    public void doStepTask() {
        if (!isFinished.get()) {
            processStep();

            if (isFinished.get()) {
                onFinish();
            }
        }
    }

    private void liveTask() {
        while (!isFinished.get()) {
            processStep();

            if (paused.get()) {
                return;
            }
        }
        onFinish();
    }

    private void instantFinishTask() {
        simulation.allowAnimations(false);
        while (!isFinished.get()) {
            processStep();
        }
        onFinish();
    }

    public void onFinish() {
        loggerController.addItem("");
        loggerController.addItem("[Finished] Simulation finished");
        setSimulationFlagsToNotStartedState();
        openResultDialog();
    }

    public void pause() {
        paused.set(true);
    }

    public void live() {
        runService(new SimulationLiveService());
    }

    public void instantFinish() {
        runService(new SimulationInstantFinishService());
    }

    public void doStep() {
        runService(new SimulationStepService());
    }

    private void runService(Service<?> service) {
        activeService = service;
        service.start();
    }

    public void stop() {
        pause();
        if (activeService != null && activeService.isRunning()) {
            activeService.cancel();
        }
        isFinished.unbind();
        if (simulation != null)
            simulation.stop();
        setSimulationFlagsToNotStartedState();
    }

    public void openDocumentation(ActionEvent actionEvent) throws IOException {
        documentationController.openDocumentation(
                algorithmsBox.getValue().ordinal() + 1
        );
    }

    public class SimulationLiveService extends Service<Boolean> {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    idle.set(false);
                    paused.set(false);
                    liveTask();
                    idle.set(true);
                    return true;
                }
            };
        }
    }

    public class SimulationStepService extends Service<Boolean> {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    idle.set(false);
                    doStepTask();
                    idle.set(true);
                    return true;
                }
            };
        }
    }

    public class SimulationInstantFinishService extends Service<Boolean> {
        @Override
        protected Task<Boolean> createTask() {
            return new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    idle.set(false);
                    instantFinishTask();
                    idle.set(true);
                    return true;
                }
            };
        }
    }
}
