package husacct.define.domain.services.stateservice.state;

import husacct.define.domain.services.stateservice.interfaces.Istate;
import husacct.define.task.DefinitionController;

import java.util.ArrayList;

public class StateDefineController {

	private ArrayList<Istate> states = new ArrayList<Istate>();
	private StateKeeper keeper = new StateKeeper(states);

	public boolean undo() {
		DefinitionController.getInstance().setSelectedModuleId(0);
		keeper.undo();
		DefinitionController.getInstance().notifyObservers();

		return true;

	}

	public boolean redo() {
		DefinitionController.getInstance().setSelectedModuleId(0);
		keeper.redo();
		DefinitionController.getInstance().notifyObservers();
		return true;
	}

	public void insertCommand(Istate sate) {

		keeper.insertCommand(sate);

	}

	public boolean[] getStatesStatus() {

		return keeper.getStates();

	}

}
