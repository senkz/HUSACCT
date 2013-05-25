package husacct.define.task;

import husacct.ServiceProvider;
import husacct.define.domain.appliedrules.AppliedRuleStrategy;
import husacct.define.domain.module.ModuleFactory;
import husacct.define.domain.module.ModuleStrategy;
import husacct.define.domain.services.AppliedRuleDomainService;
import husacct.define.domain.services.DefaultRuleDomainService;
import husacct.define.domain.services.ModuleDomainService;
import husacct.define.domain.services.SoftwareUnitDefinitionDomainService;
import husacct.define.domain.services.WarningMessageService;
import husacct.define.presentation.jpanel.DefinitionJPanel;
import husacct.define.presentation.utils.JPanelStatus;
import husacct.define.presentation.utils.UiDialogs;
import husacct.define.task.components.AbstractDefineComponent;
import husacct.define.task.components.DefineComponentFactory;
import husacct.define.task.components.SoftwareArchitectureComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
//import husacct.define.domain.module.ExternalSystem;

public class DefinitionController extends Observable implements Observer {

	private DefinitionJPanel definitionJPanel;
	private static DefinitionController instance;
	private List<Observer> observers;
	private Logger logger;
	private long selectedModuleId = -1;

	private ModuleDomainService moduleService;
	private AppliedRuleDomainService appliedRuleService;
	private DefaultRuleDomainService defaultRuleService;
	private SoftwareUnitDefinitionDomainService softwareUnitDefinitionDomainService;

	public static DefinitionController getInstance() {
		return instance == null ? (instance = new DefinitionController()) : instance;
	}

	public static void setInstance(DefinitionController dC){
		instance = dC;
	}

	public DefinitionController() {
		this.observers = new ArrayList<Observer>();
		this.logger = Logger.getLogger(DefinitionController.class);
		this.moduleService = new ModuleDomainService();
		this.appliedRuleService = new AppliedRuleDomainService();
		this.softwareUnitDefinitionDomainService = new SoftwareUnitDefinitionDomainService();
		this.defaultRuleService = new DefaultRuleDomainService();
	}

	public void initSettings() {
		this.observers.clear();
		this.definitionJPanel = new DefinitionJPanel();
	}

	/**
	 * Init the user interface for creating/editting the definition.
	 * 
	 * @return JPanel The jpanel
	 */
	public JPanel initUi() {
		return definitionJPanel;
	}

	public void setSelectedModuleId(long moduleId) {
		this.selectedModuleId = moduleId;
		notifyObservers(moduleId);
	}

	public long getSelectedModuleId() {
		return selectedModuleId;
	}
	
	public boolean addModule(long selectedModuleId, String name, String description, String moduleType){
		ModuleFactory moduleFactory = new ModuleFactory();
		logger.info("Adding "+moduleType+" "+name);
		try{
			JPanelStatus.getInstance("Adding "+moduleType).start();
			ModuleStrategy newModule = moduleFactory.createModule(moduleType);
			newModule.set(name, description);
			newModule.setId(selectedModuleId);
			passModuleToService(selectedModuleId, newModule);
			return true;
		} catch(Exception e){
			logger.error("add"+moduleType+"("+ name + ") - exception: "+e.getMessage());
			return false;
		}finally{
			JPanelStatus.getInstance().stop();
		}
	}
	
	private void passModuleToService(long selectedModuleId, ModuleStrategy module) {
		String ExceptionMessage = "";
		if(selectedModuleId == -1) {
			this.moduleService.addModuleToRoot(module);
		} else {
			logger.debug("Adding child");
			ExceptionMessage = this.moduleService.addNewModuleToParent(selectedModuleId, module);
		}
		this.notifyObservers();

		if(!ExceptionMessage.isEmpty()) {
			UiDialogs.errorDialog(definitionJPanel, ExceptionMessage);
		}
	}

	/**
	 * Remove a module by Id
	 */
	public void removeModuleById(long moduleId) {
		logger.info("Removing module by Id " + moduleId);
		try {
			JPanelStatus.getInstance("Removing Module").start();
			this.moduleService.removeModuleById(moduleId);
			this.setSelectedModuleId(-1);
			this.notifyObservers();
		} catch (Exception e) {
			logger.error("removeModuleById(" + moduleId + ") - exception: " + e.getMessage());
			UiDialogs.errorDialog(definitionJPanel, e.getMessage());
			System.out.println(e.getStackTrace());
		} finally {
			JPanelStatus.getInstance().stop();
		}
	}

	public void moveLayerUp(long layerId) {
		logger.info("Moving layer up");
		try {
			if (layerId != -1) {
				JPanelStatus.getInstance("Moving layer up").start();
				this.moduleService.moveLayerUp(layerId);
				this.notifyObservers();
			}
		} catch (Exception e) {
			logger.error("moveLayerUp() - exception: " + e.getMessage());
			UiDialogs.errorDialog(definitionJPanel, e.getMessage());
		} finally {
			JPanelStatus.getInstance().stop();
		}
	}

	public void moveLayerDown(long layerId) {
		logger.info("Moving layer down");
		try {
			if (layerId != -1) {
				JPanelStatus.getInstance("Moving layer down").start();
				this.moduleService.moveLayerDown(layerId);
				this.notifyObservers();
			}
		} catch (Exception e) {
			logger.error("moveLayerDown() - exception: " + e.getMessage());
			UiDialogs.errorDialog(definitionJPanel, e.getMessage());
		} finally {
			JPanelStatus.getInstance().stop();
		}
	}

	/**
	 * Remove the selected software unit
	 */
	public void removeSoftwareUnits(List<String> softwareUnitNames, List<String> types) {
		try {
			long moduleId = getSelectedModuleId();
			int location = 0;
			boolean confirm = UiDialogs.confirmDialog(definitionJPanel, ServiceProvider.getInstance().getLocaleService().getTranslatedString("ConfirmRemoveSoftwareUnit"), "Remove?");

			for(String softwareUnit : softwareUnitNames) {
				String type = types.get(location);
				logger.info("Removing software unit " + softwareUnit);
				if (moduleId != -1 && softwareUnit != null && !softwareUnit.equals("")) {
					if (confirm) {
						logger.info("getting type:" + type);

						JPanelStatus.getInstance("Removing software unit").start();
						if(type.toUpperCase().equals("REGEX")) {
							this.softwareUnitDefinitionDomainService.removeRegExSoftwareUnit(moduleId, softwareUnit);
							this.notifyObservers();
						}else{
							boolean chekHasCodelevelWarning=WarningMessageService.getInstance().isCodeLevelWarning(softwareUnit);
							if(chekHasCodelevelWarning){
								boolean confirm2 = UiDialogs.confirmDialog(definitionJPanel,"Your about to remove an software unit that does exist at code level", "Remove?");
								if(confirm2)
								{
									this.softwareUnitDefinitionDomainService.removeSoftwareUnit(moduleId, softwareUnit);
								}
							}else{
								this.softwareUnitDefinitionDomainService.removeSoftwareUnit(moduleId, softwareUnit);
							}
							this.notifyObservers();
						}
					}
				}
				location++;
			}
		} catch (Exception e) {
			logger.error("removeSoftwareUnit() - exception: " + e.getMessage());
			e.printStackTrace();
			UiDialogs.errorDialog(definitionJPanel, e.getMessage());
		} finally {
			JPanelStatus.getInstance().stop();
		}
	}

	public void removeRules(List<Long> appliedRuleIds) {
		boolean mandatory = false;
		try {
			if(getSelectedModuleId() != -1L && !appliedRuleIds.isEmpty()){
				for(long appliedRuleID : appliedRuleIds){
					AppliedRuleStrategy rule = appliedRuleService.getAppliedRuleById(appliedRuleID);
					if (defaultRuleService.isMandatoryRule(rule)){
						mandatory = true;
						UiDialogs.errorDialog(definitionJPanel, ServiceProvider.getInstance().getLocaleService().getTranslatedString("DefaultRule") + "\n- " +rule.getRuleType());
						break;
					}
				}
				if(!mandatory){
					boolean confirm = UiDialogs.confirmDialog(definitionJPanel, ServiceProvider.getInstance().getLocaleService().getTranslatedString("ConfirmRemoveAppliedRule"), "Remove?");
					if(confirm){
						for(long appliedRuleID : appliedRuleIds){
							logger.info("Removing rule " + appliedRuleID);
							JPanelStatus.getInstance("Removing applied rule").start();
							this.appliedRuleService.removeAppliedRule(appliedRuleID);
						}
						this.notifyObservers();
					}
				}
			}
		}catch (Exception e) {
			logger.error("removeRule() - exception: " + e.getMessage());
			UiDialogs.errorDialog(definitionJPanel, e.getMessage());
		} finally {
			JPanelStatus.getInstance().stop();
		}
	}

	/**
	 * Function which will save the name and description changes to the module
	 */
	public void updateModule(String moduleName, String moduleDescription) {
		logger.info("Updating module " + moduleName);
		try {
			JPanelStatus.getInstance("Updating module").start();
			long moduleId = getSelectedModuleId();
			if (moduleId != -1) {
				this.moduleService.updateModule(moduleId, moduleName, moduleDescription);
			}
			this.notifyObservers();
		} catch (Exception e) {
			logger.error("updateModule() - exception: " + e.getMessage());
			UiDialogs.errorDialog(definitionJPanel, e.getMessage());
		} finally {
			JPanelStatus.getInstance().stop();
		}
	}

	public AbstractDefineComponent getModuleTreeComponents() {
		JPanelStatus.getInstance("Updating Modules").start();

		SoftwareArchitectureComponent rootComponent = new SoftwareArchitectureComponent();
		ArrayList<ModuleStrategy> modules = this.moduleService.getSortedModules();
		for (ModuleStrategy module : modules) {

			this.addChildComponents(rootComponent, module);
		}

		JPanelStatus.getInstance().stop();
		return rootComponent;
	}

	private void addChildComponents(AbstractDefineComponent parentComponent, ModuleStrategy module) {
		AbstractDefineComponent childComponent = DefineComponentFactory.getDefineComponent(module);
		for(ModuleStrategy subModule : module.getSubModules()) {
			logger.debug(module.getName()+"  ]"+module.getType());
			this.addChildComponents(childComponent, subModule);
		}
		parentComponent.addChild(childComponent);
	}


	public String getModuleName(long moduleId){
		String moduleName = "Root";
		if (this.getSelectedModuleId() != -1){
			moduleName = this.moduleService.getModuleNameById(this.getSelectedModuleId());
		}
		return moduleName;
	}

	/**
	 * This function will return a hash map with the details of the requested module.
	 */
	public HashMap<String, Object> getModuleDetails(long moduleId) {
		HashMap<String, Object> moduleDetails = new HashMap<String, Object>();

		if (moduleId != -1) {
			try {
				ModuleStrategy module = this.moduleService.getModuleById(moduleId);
				moduleDetails.put("id", module.getId());
				moduleDetails.put("name", module.getName());
				moduleDetails.put("description", module.getDescription());
				moduleDetails.put("type", module.getType());

			} catch (Exception e) {
				logger.error("getModuleDetails() - exception: " + e.getMessage());
				UiDialogs.errorDialog(definitionJPanel, e.getMessage());
			}
		}
		return moduleDetails;
	}

	public void update(Observable o, Object arg) {
		logger.info("update(" + o + ", " + arg + ")");
		long moduleId = getSelectedModuleId();
		notifyObservers(moduleId);
	}

	@Override
	public void notifyObservers(){
		long moduleId = getSelectedModuleId();
		for (Observer o : this.observers){
			o.update(this, moduleId);
		}
	}

	/**
	 * This function will load notify all to update their data
	 */
	public void notifyObservers(long moduleId){
		for (Observer o : this.observers){
			o.update(this, moduleId);
		}
	}

	public void addObserver(Observer o){
		if (!this.observers.contains(o)){
			this.observers.add(o);
		}
	}

	public void removeObserver(Observer o){
		if (this.observers.contains(o)){
			this.observers.remove(o);
		}
	}

	public ArrayList<Long> getAppliedRuleIdsBySelectedModule() {
		return this.appliedRuleService.getAppliedRulesIdsByModuleFromId(getSelectedModuleId());
	}

	public HashMap<String, Object> getRuleDetailsByAppliedRuleId(long appliedRuleId){
		AppliedRuleStrategy rule = this.appliedRuleService.getAppliedRuleById(appliedRuleId);
		HashMap<String, Object> ruleDetails = new HashMap<String, Object>();
		ruleDetails.put("id", rule.getId());
		ruleDetails.put("description", rule.getDescription());
		ruleDetails.put("dependencies", rule.getDependencies());
		ruleDetails.put("moduleFromName", rule.getModuleFrom().getName());
		ruleDetails.put("moduleToName", rule.getModuleTo().getName());
		ruleDetails.put("enabled", rule.isEnabled());
		ruleDetails.put("regex", rule.getRegex());
		ruleDetails.put("ruleTypeKey", rule.getRuleType());
		ruleDetails.put("numberofexceptions", rule.getExceptions().size());
		return ruleDetails;
	}

	public ArrayList<String> getSoftwareUnitNamesBySelectedModule() {
		return this.softwareUnitDefinitionDomainService.getSoftwareUnitNames(getSelectedModuleId());
	}

	public ArrayList<String> getRegExSoftwareUnitNamesBySelectedModule() {
		return this.softwareUnitDefinitionDomainService.getRegExSoftwareUnitNames(getSelectedModuleId());
	}

	public String getSoftwareUnitTypeBySoftwareUnitName(String softwareUnitName){
		return this.softwareUnitDefinitionDomainService.getSoftwareUnitType(softwareUnitName);
	}

	public boolean isAnalysed(){
		return ServiceProvider.getInstance().getAnalyseService().isAnalysed();
	}

	public void updateModule(String moduleName, String moduleDescription,
			String type) {
		this.moduleService.updateModule(getSelectedModuleId(), moduleName, moduleDescription,type);
		this.notifyObservers();
	}
}
