package com.example.command;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommandRegistry {
    private ObservableList<Command> commandStack = FXCollections.observableArrayList();
    private List<Command> abortedCommandStack = new ArrayList<>();

    public void executeCommand(Command command) {
        command.execute();
        commandStack.add(command);
        abortedCommandStack.clear();
    }

    public void redo() {
        if(abortedCommandStack.isEmpty()){
            return;
        }
        Command command = abortedCommandStack.remove(abortedCommandStack.size() - 1);
        command.redo();
        commandStack.add(command);
    }

    public void undo() {
        if(commandStack.isEmpty()){
            return;
        }
        Command command = commandStack.remove(commandStack.size() - 1);
        command.undo();
        abortedCommandStack.add(command);
    }

    public ObservableList<Command> getCommandStack() {
        return commandStack;
    }
}
