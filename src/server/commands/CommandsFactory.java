package server.commands;

import data.Privilege;

/**
 * @author Curcudel Ioan-Razvan
 */

public class CommandsFactory {

	public static CommandManager getCommand(Privilege privilege) {
		{
			switch (privilege) {
				case ROOT: // TODO root commands
					// return new CommandManager(new RoodCommands());
				case ADMIN:
					return new CommandManager(new AdminCommands());
				case USER:
					return new CommandManager(new UserCommands());
				default: // TODO guests commands
					// return new CommandManager(new GuestCommands());
					return null;
			}
		}

	}
}
