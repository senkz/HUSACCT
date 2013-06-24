package husacct.define.domain.services.stateservice.state.module;

import husacct.define.domain.module.ModuleStrategy;
import husacct.define.domain.services.ModuleDomainService;
import husacct.define.domain.services.UndoRedoService;
import husacct.define.domain.services.stateservice.interfaces.Istate;
import husacct.define.task.DefinitionController;

public class UpdateModuleTypeCommand implements Istate {
private ModuleStrategy oldModule;
private ModuleStrategy newModule;
	public UpdateModuleTypeCommand(ModuleStrategy oldModule,ModuleStrategy newModule) {
		this.oldModule=oldModule;
		this.newModule=newModule;
	}
	
	@Override
	public void undo() {
	UndoRedoService.getInstance().seperatedUpdateModuleType(newModule, oldModule);
    DefinitionController.getInstance().notifyObservers();
	}

	@Override
	public void redo() {
		UndoRedoService.getInstance().seperatedUpdateModuleType(oldModule, newModule);
		DefinitionController.getInstance().notifyObservers();
	}

}
