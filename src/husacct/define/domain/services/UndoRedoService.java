package husacct.define.domain.services;

import java.util.ArrayList;
import java.util.List;

import husacct.define.domain.appliedrule.AppliedRuleStrategy;
import husacct.define.domain.module.ModuleStrategy;
import husacct.define.domain.seperatedinterfaces.IAppliedRuleSeperatedInterface;
import husacct.define.domain.seperatedinterfaces.IModuleSeperatedInterface;
import husacct.define.domain.seperatedinterfaces.ISofwareUnitSeperatedInterface;
import husacct.define.domain.seperatedinterfaces.IseparatedDefinition;
import husacct.define.domain.softwareunit.ExpressionUnitDefinition;
import husacct.define.domain.softwareunit.SoftwareUnitDefinition;

public class UndoRedoService  implements IModuleSeperatedInterface,ISofwareUnitSeperatedInterface,IAppliedRuleSeperatedInterface{
 private List<IseparatedDefinition> observers = new ArrayList<IseparatedDefinition>();
 private List<IAppliedRuleSeperatedInterface> applies = new ArrayList<IAppliedRuleSeperatedInterface>();
 private List<IModuleSeperatedInterface> modules = new ArrayList<IModuleSeperatedInterface>();
 private List<ISofwareUnitSeperatedInterface> sU= new ArrayList<ISofwareUnitSeperatedInterface>();
 private static UndoRedoService instance =null;
 public static UndoRedoService getInstance()
 {
	if (instance==null) {
	
		return instance= new UndoRedoService();
	} else {
	
		return instance;
	}
	 
 } 





	@Override
	public void addSeperatedModule(ModuleStrategy module) {
		for (IModuleSeperatedInterface observer : modules) {
			observer.addSeperatedModule(module);
		}
		
	}

	@Override
	public void removeSeperatedModule(ModuleStrategy module) {
		
		for (IModuleSeperatedInterface observer : modules) {
			
			observer.removeSeperatedModule(module);
		}
		
	}
	

	@Override
	public void layerUp(long moduleID) {
		for (IModuleSeperatedInterface observer : modules) {
			observer.layerUp(moduleID);
		}
		
	}





	@Override
	public void layerDown(long moduleID) {
		for (IModuleSeperatedInterface observer : modules) {
			observer.layerDown(moduleID);
		}
		
	}
	

	@Override
	public void addSeperatedSoftwareUnit(List<SoftwareUnitDefinition> units, long moduleId) {
		for (ISofwareUnitSeperatedInterface observer : sU) {
			observer.addSeperatedSoftwareUnit(units, moduleId);
		}
		
	}

	@Override
	public void removeSeperatedSoftwareUnit(List<SoftwareUnitDefinition> units, long moduleId) {
		for (ISofwareUnitSeperatedInterface observer : sU) {
		observer.removeSeperatedSoftwareUnit(units, moduleId);
		}
		
	}

	@Override
	public void addSeperatedAppliedRule(List<AppliedRuleStrategy> rules) {
		
		for (IAppliedRuleSeperatedInterface observer : applies) {
		
			observer.addSeperatedAppliedRule(rules);
		}
		
	}

	@Override
	public void removeSeperatedAppliedRule(List<AppliedRuleStrategy> rules) {
		for (IAppliedRuleSeperatedInterface observer : applies) {
			observer.removeSeperatedAppliedRule(rules);
		}
		
	}





	@Override
	public void addSeperatedExeptionRule(long parentRuleID,List<AppliedRuleStrategy> rules) {
		for (IAppliedRuleSeperatedInterface observer : applies) {
			
			((IAppliedRuleSeperatedInterface)observer).addSeperatedExeptionRule(parentRuleID,rules);
		}
		
	}





	@Override
	public void removeSeperatedExeptionRule(long parentRuleID,List<AppliedRuleStrategy> rules) {
		for (IAppliedRuleSeperatedInterface observer : applies) {
			((IAppliedRuleSeperatedInterface)observer).removeSeperatedExeptionRule(parentRuleID,rules);
		}
		
	}





	

	


	
	
	




	public void registerObserver(IseparatedDefinition observer) {

if (observer instanceof IAppliedRuleSeperatedInterface) {
		
	applies.add((IAppliedRuleSeperatedInterface) observer);
			}
if (observer instanceof IseparatedDefinition) {
			sU.add((ISofwareUnitSeperatedInterface) observer);	
			}
if (observer instanceof IModuleSeperatedInterface) {
	modules.add((IModuleSeperatedInterface) observer);
}
	
		
		
	}





	@Override
	public void addExpression(long moduleId, ExpressionUnitDefinition expression) {
		for (ISofwareUnitSeperatedInterface observer : sU) {
		observer.addExpression(moduleId, expression);
		}
		
	}





	@Override
	public void removeExpression(long moduleId,
			ExpressionUnitDefinition expression) {
		for (ISofwareUnitSeperatedInterface observer : sU) {
	         observer.removeExpression(moduleId, expression);
		}
		
	}





	@Override
	public void editExpression(long moduleId,
			ExpressionUnitDefinition oldExpresion, ExpressionUnitDefinition newExpression) {
		for (ISofwareUnitSeperatedInterface observer : sU) {
			observer.editExpression(moduleId, oldExpresion, newExpression);
		}
		
	}





	@Override
	public void switchSoftwareUnitLocation(long fromModule, long toModule,
			List<String> uniqNames) {
<<<<<<< HEAD
		for (ISofwareUnitSeperatedInterface observer : sU) {
		observer.switchSoftwareUnitLocation(fromModule, toModule, uniqNames);
=======
		for (Object observer : getSeperatedSofwareUnitInterfacess(ISofwareUnitSeperatedInterface.class)) {
			((ISofwareUnitSeperatedInterface)observer).switchSoftwareUnitLocation(fromModule, toModule, uniqNames);
>>>>>>> 728ed6ea96ae32da46002d13adc0c058a06e0fd5
		}
		
	}





	@Override
	public void editAppliedRule(long ruleid,
			Object[] newValues) {
<<<<<<< HEAD
		for (IAppliedRuleSeperatedInterface observer : applies) {
		observer.editAppliedRule(ruleid, newValues);
		}
		
		
	}





	@Override
	public void seperatedUpdateModuleType(ModuleStrategy oldmodule, ModuleStrategy newModule) {
		for (IModuleSeperatedInterface observer : modules) {
			observer.seperatedUpdateModuleType(oldmodule, newModule);
		}
=======
		for (Object observer : getSeperatedSofwareUnitInterfacess(IAppliedRuleSeperatedInterface.class)) {
			((IAppliedRuleSeperatedInterface)observer).editAppliedRule(ruleid, newValues);
		}
		
>>>>>>> 728ed6ea96ae32da46002d13adc0c058a06e0fd5
		
	}

}
