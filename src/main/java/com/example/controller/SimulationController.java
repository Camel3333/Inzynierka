package com.example.controller;

import com.example.algorithm.AlgorithmType;
import com.example.algorithm.operations.Operation;
import com.example.algorithm.report.OperationsBatch;
import com.example.algorithm.report.StepReport;
import com.example.settings.*;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@FxmlView("/view/simulationOptionsView.fxml")
public class SimulationController {
    @FXML
    public Label depthLabel;
    @FXML
    public Label phaseLabel;
    @FXML
    public Label qLabel;
    @FXML
    public Label timeLabel;
    @FXML
    private VBox parent;
    @FXML
    private IntegerSettingTextField depth;
    @FXML
    private IntegerSettingTextField phase;
    @FXML
    private IntegerSettingTextField q;
    @FXML
    private IntegerSettingTextField time;

    @FXML
    private ComboBox<AlgorithmType> algorithmsBox;
    @FXML
    private Slider animationSpeedSlider;

    private final AlgorithmSettings algorithmSettings = new AlgorithmSettings();

    private final Map<AlgorithmType, List<Node>> options = new HashMap<>();

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

    @Setter
    private Simulation simulation;

    @Autowired
    private StatisticsController statisticsController;

    @Autowired
    private LoggerController loggerController;

    @Autowired
    private GraphController graphController;

    private BooleanProperty paused = new SimpleBooleanProperty(true);
    private BooleanProperty started = new SimpleBooleanProperty(false);
    private BooleanProperty idle = new SimpleBooleanProperty(true);
    private BooleanProperty isFinished = new SimpleBooleanProperty(false);

    private Stage documentationStage = null;

    public void show() {
        parent.setVisible(true);
        parent.setManaged(true);
    }

    public void hide() {
        parent.setVisible(false);
        parent.setManaged(false);
    }

    private void setDefaultSettings() {
        algorithmSettings.getSettings().put("depth",
                new AlgorithmSetting<>("depth", 1, Integer.class, (value) -> value >= 0));
        algorithmSettings.getSettings().put("phase",
                new AlgorithmSetting<>("phase", 1, Integer.class, (value) -> value >= 0));
        algorithmSettings.getSettings().put("q",
                new AlgorithmSetting<>("q", 1, Integer.class, (value) -> value >= 0));
        algorithmSettings.getSettings().put("time",
                new AlgorithmSetting<>("time", 1, Integer.class, (value) -> value >= 0));
    }

    @FXML
    public void initialize() {
        setDefaultSettings();
        options.put(AlgorithmType.LAMPORT, new ArrayList<>(List.of(depth, depthLabel)));
        options.put(AlgorithmType.KING, new ArrayList<>(List.of(phase, phaseLabel)));
        options.put(AlgorithmType.QVOTER, new ArrayList<>(List.of(q, qLabel, time, timeLabel)));
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
                        showAlgorithmSettings(newValue);
                    }
                }));

        depth.setContainedSetting((Setting<Integer>) algorithmSettings.getSettings().get("depth"));
        phase.setContainedSetting((Setting<Integer>) algorithmSettings.getSettings().get("phase"));
        q.setContainedSetting((Setting<Integer>) algorithmSettings.getSettings().get("q"));
        time.setContainedSetting((Setting<Integer>) algorithmSettings.getSettings().get("time"));


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

        nextStepDisabledProperty.bind(Bindings.createBooleanBinding(() -> {
            return !(idle.get() && started.get() && !isFinished.get());
        }, dependencies));

        liveDisabledProperty.bind(Bindings.createBooleanBinding(() -> {
            return !(idle.get() && started.get() && !isFinished.get());
        }, dependencies));

        instantFinishDisabledProperty.bind(Bindings.createBooleanBinding(() -> {
            return !(idle.get() && started.get() && !isFinished.get());
        }, dependencies));

        pauseDisabledProperty.bind(Bindings.createBooleanBinding(() -> {
            return !(!paused.get() && started.get() && !isFinished.get());
        }, dependencies));
    }

    private void setSimulationFlagsToNotStartedState() {
        paused.set(true);
        started.set(false);
        idle.set(true);
        isFinished.set(false);
    }

    private void showAlgorithmSettings(AlgorithmType algorithmType) {
        hideAlgorithmSettings();
        options.get(algorithmType).forEach(node -> {
            node.setVisible(true);
            node.setManaged(true);
        });
    }

    private void hideAlgorithmSettings() {
        options.values().stream()
                .flatMap(Collection::stream)
                .forEach(node -> {
                    node.setVisible(false);
                    node.setManaged(false);
                });
    }

    public void setAvailableAlgorithms(ObservableList<AlgorithmType> algorithmTypes) {
        algorithmsBox.setItems(algorithmTypes);
        algorithmsBox.getSelectionModel().select(0);

        List<Observable> inputDependencies = options.values()
                .stream()
                .flatMap(List::stream)
                .filter(node -> node instanceof SettingNode<?>)
                .map(node -> (SettingNode<?>) node)
                .map(settingNode -> (BooleanProperty) (settingNode.getIsValidProperty()))
                .collect(Collectors.toList());

        inputDependencies.add(algorithmsBox.getSelectionModel().selectedItemProperty());
        inputDependencies.add(started);
        inputDependencies.add(isFinished);
        inputDependencies.add(idle);

        Observable[] dependencies = inputDependencies.toArray(new Observable[0]);

        List<Node> algorithmNodes = options.get(algorithmsBox.getSelectionModel().selectedItemProperty().get());

        startDisabledProperty.unbind();
        startDisabledProperty.bind(Bindings.createBooleanBinding(() -> {
                    if (simulation.isGraphEmpty()) {
                        return true;
                    }
                    if (isFinished.get() && idle.get()) {
                        return false;
                    }
                    if (started.get()) {
                        return true;
                    }
                    List<SettingNode<?>> settingNodes = (algorithmNodes
                            .stream()
                            .filter(node -> node instanceof SettingNode<?>)
                            .map(node -> (SettingNode<?>) node)
                            .collect(Collectors.toList()));
                    for (SettingNode<?> settingNode : settingNodes) {
                        if (!settingNode.getIsValidProperty().get()) {
                            return true;
                        }
                    }
                    return false;
                }, dependencies
        ));

        animationSpeedSlider.valueProperty().addListener(observable -> simulation.setAnimationsSpeed(animationSpeedSlider.getValue()));
    }

    public void initSimulation() {
        statisticsController.clear();
        simulation.allowAnimations(true);
        AlgorithmType selectedAlgorithm = algorithmsBox.getValue();
        simulation.setEnvironment(selectedAlgorithm.getAlgorithm(), algorithmSettings);
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
    }

    public void openResultDialog() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(getClass().getResource("/view/simulationResultView.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    ((SimulationResultController)fxmlLoader.getController()).setMessage(graphController.getGraph().checkConsensus());
                    Stage stage = new Stage();
                    stage.setTitle("Result");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public void doStepTask() {
        if (!isFinished.get()) {
            processStep();

            if (isFinished.get()) {
                loggerController.addItem("[Finished] Simulation finished");
                System.out.println("Finished");
                openResultDialog();
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
        loggerController.addItem("[Finished] Simulation finished");
        System.out.println("Finished");
        openResultDialog();
    }

    private void instantFinishTask() {
        simulation.allowAnimations(false);
        while (!isFinished.get()) {
            processStep();
        }
        loggerController.addItem("[Finished] Simulation finished");
        System.out.println("Finished");
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
        if (documentationStage == null) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/documentationView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setTitle("Documentation");
            stage.setScene(scene);
            stage.show();
            documentationStage = stage;
        }
        else {
            if (!documentationStage.isShowing()) documentationStage.show();
            if (documentationStage.isIconified()) documentationStage.setIconified(false);
            documentationStage.toFront();
        }


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
