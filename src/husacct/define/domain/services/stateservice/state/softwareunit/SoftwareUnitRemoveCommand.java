package husacct.define.domain.services.stateservice.state.softwareunit;

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
import java.util.List;

public class SoftwareUnitRemoveCommand implements Istate {

	private long moduleId;
    private	List<String> data;
	
	public SoftwareUnitRemoveCommand() {
		// TODO Auto-generated constructor stub
	}
	


	public SoftwareUnitRemoveCommand(long selectedModuleId,
			List<String> selectedModules) {
	this.moduleId=selectedModuleId;
	this.data=selectedModules;
	}

	@Override
	public void undo() {

	ArrayList<AnalyzedModuleComponent> units = StateService.instance().getAnalyzedSoftWareUnit(data);
	ArrayList<SoftwareUnitDefinition> uni = new ArrayList<SoftwareUnitDefinition>();
	for (AnalyzedModuleComponent u : units) {
		uni.add(new SoftwareUnitDefinition(u.getUniqueName(), Type.valueOf(u.getType())));
	}
	
	UndoRedoService.getInstance().addSeperatedSoftwareUnit(uni, moduleId);
	 
	

	}

	@Override
	public void redo() {
		 DefinitionController.getInstance().setSelectedModuleId(moduleId);
		 
		 ArrayList<AnalyzedModuleComponent> units = StateService.instance().getAnalyzedSoftWareUnit(data);
		 ArrayList<SoftwareUnitDefinition> uni = new ArrayList<SoftwareUnitDefinition>();
			for (AnalyzedModuleComponent u : units) {
				uni.add(new SoftwareUnitDefinition(u.getUniqueName(), Type.valueOf(u.getType())));
			}
		 
		 UndoRedoService.getInstance().removeSeperatedSoftwareUnit(uni, moduleId);


	}

}
