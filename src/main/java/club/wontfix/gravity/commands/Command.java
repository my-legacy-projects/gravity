package club.wontfix.gravity.commands;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Command {

    private final String[] commands;

    private final String description;

    private final String usage;

    public abstract boolean execute(String label, String[] args);

}
