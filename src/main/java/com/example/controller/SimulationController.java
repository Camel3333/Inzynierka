package com.example.controller;

import com.example.algorithm.*;
import com.example.algorithm.report.StepReport;
import com.example.settings.*;
import com.example.simulation.SimpleSimulation;
import com.example.simulation.Simulation;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lombok.Setter;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

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
    private VBox parent;
    @FXML
    private IntegerSettingTextField depth;
    @FXML
    private IntegerSettingTextField phase;
    @FXML
    private Button startButton;
    @FXML
    private Button nextStepButton;
    @FXML
    private Button liveButton;
    @FXML
    private Button instantFinishButton;
    @FXML
    private Button pauseButton;
    @FXML
    private ComboBox<AlgorithmType> algorithmsBox;

    private AlgorithmSettings algorithmSettings = new AlgorithmSettings();

    private final Map<AlgorithmType, List<Node>> options = new HashMap<>();

    private ObservableList<AlgorithmType> availableAlgorithms = FXCollections.emptyObservableList();

    @Setter
    private Simulation simulation;

    private BooleanProperty paused =  new SimpleBooleanProperty(true);
    private BooleanProperty started = new SimpleBooleanProperty(false);
    private BooleanProperty idle = new SimpleBooleanProperty(true);
    private BooleanProperty isFinished;

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
    }

    @FXML
    public void initialize() {
        setDefaultSettings();
        options.put(AlgorithmType.LAMPORT, new ArrayList<>(List.of(depth, depthLabel)));
        options.put(AlgorithmType.KING, new ArrayList<>(List.of(phase, phaseLabel)));
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

        nextStepButton.setDisable(true);
        liveButton.setDisable(true);
        instantFinishButton.setDisable(true);
        pauseButton.setDisable(true);

        List<Observable> dependenciesList = new ArrayList<>();
        dependenciesList.add(paused);
        dependenciesList.add(started);
        dependenciesList.add(idle);
        Observable[] dependencies = dependenciesList.toArray(new Observable[0]);

        nextStepButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return !(idle.get() && started.get() && !isFinished.get());
        }, dependencies));

        liveButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return !(idle.get() && started.get() && !isFinished.get());
        }, dependencies));

        instantFinishButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return !(idle.get() && started.get() && !isFinished.get());
        }, dependencies));

        pauseButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return !(!paused.get() && started.get() && !isFinished.get());
        }, dependencies));
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
        availableAlgorithms = algorithmTypes;
        algorithmsBox.setItems(availableAlgorithms);
        algorithmsBox.getSelectionModel().select(0);

        List<Observable> inputDependencies = options.values()
                .stream()
                .flatMap(List::stream)
                .filter(node -> node instanceof SettingNode<?>)
                .map(node -> (SettingNode<?>)node)
                .map(settingNode -> (BooleanProperty) (settingNode.getIsValidProperty()))
                .collect(Collectors.toList());

        inputDependencies.add(algorithmsBox.getSelectionModel().selectedItemProperty());
        inputDependencies.add(started);

        Observable[] dependencies = inputDependencies.toArray(new Observable[0]);

        List<Node> algorithmNodes = options.get(algorithmsBox.getSelectionModel().selectedItemProperty().get());

        startButton.disableProperty().unbind();
        startButton.disableProperty().bind(Bindings.createBooleanBinding(()->{
            if (started.get()) {
                return true;
            }
            List<SettingNode<?>> settingNodes = (algorithmNodes
                    .stream()
                    .filter(node -> node instanceof SettingNode<?>)
                    .map(node -> (SettingNode<?>)node)
                    .collect(Collectors.toList()));
            for (SettingNode<?> settingNode : settingNodes){
                if (!settingNode.getIsValidProperty().get()){
                    return true;
                }
            }
            return false;
            }, dependencies
        ));
    }

    private boolean verifySettings(AlgorithmType algorithmType){
        List<SettingNode<?>> settingNodes = (options.get(algorithmType)
                .stream()
                .filter(node -> node instanceof SettingNode<?>)
                .map(node -> (SettingNode<?>)node)
                .collect(Collectors.toList()));
        for (SettingNode<?> settingNode : settingNodes){
            if (!settingNode.getIsValidProperty().get()){
                return false;
            }
        }
        return true;
    }

    public void initSimulation() {
        AlgorithmType selectedAlgorithm = algorithmsBox.getValue();
        simulation.setEnvironment(selectedAlgorithm.getAlgorithm(), algorithmSettings);
        ((SimpleSimulation)simulation).loadEnvironment();
        isFinished = ((SimpleSimulation) simulation).getIsFinishedProperty();
        started.set(true);
    }

    public void doStepTask() {
        if (!isFinished.get()) {
            StepReport report = ((SimpleSimulation)simulation).step();

            if(isFinished.get()) {
                System.out.println("Finished");
            }
        }
    }

    private void liveTask() {
        while(!isFinished.get()) {
            StepReport report = ((SimpleSimulation)simulation).step();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            if (paused.get()) {
                return;
            }
        }
        System.out.println("Finished");
    }

    private void instantFinishTask() {
        simulation.allowAnimations(false);
        while(!isFinished.get()) {
            StepReport report = ((SimpleSimulation)simulation).step();
        }
        System.out.println("Finished");
    }

    public void pause() {
        paused.set(true);
    }

    public void live() {
        new SimulationLiveService().start();
    }

    public void instantFinish() {
        new SimulationInstantFinishService().start();
    }

    public void doStep() {
        new SimulationStepService().start();
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
