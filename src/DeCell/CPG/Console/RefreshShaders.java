package DeCell.CPG.Console;

import DeCell.CPG.CustomizablePhaseGlow;
import org.lazywizard.console.BaseCommand;

public class RefreshShaders implements BaseCommand {
    @Override
    public CommandResult runCommand(String args, CommandContext context) {
        CustomizablePhaseGlow.UpdateShaders();
        return CommandResult.SUCCESS;
    }
}
