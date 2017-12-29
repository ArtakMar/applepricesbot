package ru.proshik.applepriceparcer.service;

import ru.proshik.applepriceparcer.command.Command;
import ru.proshik.applepriceparcer.exception.NotFoundCommandException;

import java.util.List;

public class CommandExtractor {

    private CommandProvider commandProvider;

    public CommandExtractor(CommandProvider commandProvider) {
        this.commandProvider = commandProvider;
    }

    public Command extract(String commandTitle) throws NotFoundCommandException {
        if (commandTitle == null) {
            throw new IllegalArgumentException("Command title not must be empty");
        }

        List<Command> commands = commandProvider.commandList();

        for (Command c : commands) {
            if (c.getTitle().equals(commandTitle)) {
                return c;
            }
        }


        throw new NotFoundCommandException("Command with title " + commandTitle + " not found\n");
    }

}


