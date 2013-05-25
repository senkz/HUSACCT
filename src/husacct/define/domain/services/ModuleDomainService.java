package husacct.define.domain.services;

import husacct.ServiceProvider;
import husacct.define.domain.SoftwareArchitecture;
import husacct.define.domain.SoftwareUnitDefinition;
import husacct.define.domain.module.ModuleComparator;
import husacct.define.domain.module.ModuleFactory;
import husacct.define.domain.module.ModuleStrategy;
import husacct.define.task.JtreeController;

import java.util.ArrayList;
import java.util.Collections;

public class ModuleDomainService {

	public long addModuleToRoot(ModuleStrategy module){
		long moduleId = SoftwareArchitecture.getInstance().addModule(module);

		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
		return moduleId;
	}

	public long addModuleToParent(long parentModuleId, ModuleStrategy module){
		ModuleStrategy parentModule = SoftwareArchitecture.getInstance().getModuleById(parentModuleId);
		parentModule.addSubModule(module);
		DefaultRuleDomainService service = new DefaultRuleDomainService();

		long moduleId = module.getId();
		service.addDefaultRules(module);

		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
		return moduleId;
	}

	public String addNewModuleToParent(long parentModuleId, ModuleStrategy module){
		ModuleStrategy parentModule = SoftwareArchitecture.getInstance().getModuleById(parentModuleId);
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
		return parentModule.addSubModule(module);
	}

	public void updateModule(long moduleId, String moduleName, String moduleDescription) {
		ModuleStrategy module = SoftwareArchitecture.getInstance().getModuleById(moduleId);
		module.setName(moduleName);
		module.setDescription(moduleDescription);
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
	}

	public void removeAllModules() {
		SoftwareArchitecture.getInstance().removeAllModules();
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
	}

	public void removeModuleById(long moduleId) {
		ModuleStrategy module = SoftwareArchitecture.getInstance().getModuleById(moduleId);

		SoftwareArchitecture.getInstance().removeModule(module);
		// TODO: this is a quick fix
		try{
			JtreeController.instance().registerTreeRemoval(module);
		}
		catch(Exception e){
			//TODO: Add an exception scenario
		}
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
	}

	public String getModuleNameById(long moduleId) {
		String moduleName = new String();
		if (moduleId != -1){
			moduleName = SoftwareArchitecture.getInstance().getModuleById(moduleId).getName();
		}
		return moduleName;
	}

	public ModuleStrategy getModuleById(long moduleId) {
		return SoftwareArchitecture.getInstance().getModuleById(moduleId);
	}

	public ModuleStrategy getRootModule(){
		return SoftwareArchitecture.getInstance().getRootModule();
	}

	public ModuleStrategy[] getRootModules(){
		ArrayList<ModuleStrategy> moduleList = SoftwareArchitecture.getInstance().getModules();
		ModuleStrategy[] modules = new ModuleStrategy[moduleList.size()]; 
		moduleList.toArray(modules);
		return modules;
	}

	public ArrayList<Long> getRootModulesIds(){
		ArrayList<ModuleStrategy> moduleList = SoftwareArchitecture.getInstance().getModules();
		ArrayList<Long> moduleIdList = new ArrayList<Long>();
		for (ModuleStrategy module : moduleList) {
			moduleIdList.add(module.getId());
		}
		return moduleIdList;
	}

	public ArrayList<Long> getSiblingModuleIds(long moduleId) {
		ArrayList<Long> childModuleIdList = new ArrayList<Long>();
		if (moduleId != -1) {
			long parentModuleId = SoftwareArchitecture.getInstance().getParentModuleIdByChildId(moduleId);
			childModuleIdList = getSubModuleIds(parentModuleId);

			ModuleStrategy module = SoftwareArchitecture.getInstance().getModuleById(moduleId);
			childModuleIdList.remove(module.getId());
		}
		return childModuleIdList; 
	}

	public ArrayList<Long> getSubModuleIds(Long parentModuleId) {
		ArrayList<Long> childModuleIdList = new ArrayList<Long>();

		if (parentModuleId != -1) {
			ModuleStrategy parentModule = SoftwareArchitecture.getInstance().getModuleById(parentModuleId);

			for (ModuleStrategy module : parentModule.getSubModules()) {
				childModuleIdList.add(module.getId());

				ArrayList<Long> subModuleIdList = getSubModuleIds(module.getId());
				for (Long l : subModuleIdList){
					childModuleIdList.add(l);
				}
			}
		}else {
			childModuleIdList = getRootModulesIds();
		}
		return childModuleIdList;
	}


	public ArrayList<ModuleStrategy> getSortedModules() {
		ArrayList<ModuleStrategy> modules = SoftwareArchitecture.getInstance().getModules();
		Collections.sort(modules, new ModuleComparator());
		for(ModuleStrategy module : modules) {
			this.sortModuleChildren(module);
		}
		return modules;
	}

	public void sortModuleChildren(ModuleStrategy module) {
		ArrayList<ModuleStrategy> children = module.getSubModules();
		Collections.sort(children, new ModuleComparator());
		for(ModuleStrategy child : children) {
			this.sortModuleChildren(child);
		}
	}

	public ModuleStrategy getModuleByLogicalPath(String logicalPath){
		return SoftwareArchitecture.getInstance().getModuleByLogicalPath(logicalPath);
	}

	public void moveLayerUp(long layerId){
		SoftwareArchitecture.getInstance().moveLayerUp(layerId);
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
	}

	public void moveLayerDown(long layerId){
		SoftwareArchitecture.getInstance().moveLayerDown(layerId);
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
	}

	public ModuleStrategy getModuleIdBySoftwareUnit(SoftwareUnitDefinition su) {
		return SoftwareArchitecture.getInstance().getModuleBySoftwareUnit(su.getName());
	}

	public Long getParentModuleIdByChildId(Long moduleId) {
		return SoftwareArchitecture.getInstance().getParentModuleIdByChildId(moduleId);
	}	

	public ModuleStrategy getParentModule(ModuleStrategy module){	
		return recursiveSearch(SoftwareArchitecture.getInstance().getRootModule(),module);
	}	

	private ModuleStrategy recursiveSearch(ModuleStrategy currentModule, ModuleStrategy comparisonModule)
	{
		if (currentModule.equals(comparisonModule)){
			return currentModule;
		}
		if (currentModule.hasSubModules()){
			for (ModuleStrategy subModule : currentModule.getSubModules()){
				return recursiveSearch(subModule, comparisonModule);
			}
		}
		ModuleFactory moduleFactory = new ModuleFactory();
		ModuleStrategy newModule = moduleFactory.createModule(currentModule.getType());
		return newModule;
	}

	public void updateModule(long moduleId, String moduleName,
			String moduleDescription, String newType) {

		ModuleStrategy module = SoftwareArchitecture.getInstance().getModuleById(moduleId);
		DefaultRuleDomainService service = new DefaultRuleDomainService();
		service.removeDefaultRules(module);
		ModuleStrategy updatedModule=SoftwareArchitecture.getInstance().updateModuleType(module,newType);
		service.addDefaultRules(updatedModule);
		service.updateModuleRules(updatedModule);
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();

	}
}
