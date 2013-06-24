package husacct.define.domain.services.stateservice.state.softwareunit;

import husacct.define.domain.module.ModuleStrategy;
import husacct.define.domain.services.SoftwareUnitDefinitionDomainService;
import husacct.define.domain.services.UndoRedoService;
import husacct.define.domain.services.stateservice.StateService;
import husacct.define.domain.services.stateservice.interfaces.Istate;
import husacct.define.domain.softwareunit.SoftwareUnitDefinition;
import husacct.define.domain.softwareunit.SoftwareUnitDefinition.Type;
import husacct.define.task.DefinitionController;
import husacct.define.task.SoftwareUnitController;
import husacct.define.task.components.AnalyzedModuleComponent;

import java.util.ArrayList;

import antlr.collections.List;

public class SoftwareUnitAddCommand implements Istate {
	private ModuleStrategy module;
	private ArrayList<AnalyzedModuleComponent> units;
	
	
	public SoftwareUnitAddCommand(ModuleStrategy module,
			ArrayList<AnalyzedModuleComponent> unitTobeRemoved) {
		this.module=module;
		this.units=unitTobeRemoved;
	}

	@Override
	public void undo() {
	
	
	ArrayList<SoftwareUnitDefinition> uni = new ArrayList<SoftwareUnitDefinition>();
	for (AnalyzedModuleComponent u : units) {
		uni.add(new SoftwareUnitDefinition(u.getUniqueName(), Type.valueOf(u.getType())));
	}
	UndoRedoService.getInstance().removeSeperatedSoftwareUnit(uni, module.getId());
		
	}

	@Override
	public void redo() {

		ArrayList<SoftwareUnitDefinition> uni = new ArrayList<SoftwareUnitDefinition>();
		for (AnalyzedModuleComponent u : units) {
			uni.add(new SoftwareUnitDefinition(u.getUniqueName(), Type.valueOf(u.getType())));
		}
		UndoRedoService.getInstance().addSeperatedSoftwareUnit(uni, module.getId());
	}

}
