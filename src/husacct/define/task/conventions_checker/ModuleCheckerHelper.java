package husacct.define.task.conventions_checker;


import husacct.ServiceProvider;
import husacct.define.domain.appliedrules.AppliedRuleStrategy;
import husacct.define.domain.module.ModuleStrategy;
import husacct.define.domain.services.AppliedRuleDomainService;

import java.util.ArrayList;

public class ModuleCheckerHelper {
	
	private AppliedRuleDomainService appliedRuleService;
	private String errorMessage;
	
	public ModuleCheckerHelper() {
		this.setErrorMessage("");
		this.appliedRuleService = new AppliedRuleDomainService();
	}
	
	public boolean checkRuleTypeAlreadySet(String ruleTypeKey, ModuleStrategy moduleFrom) {
		for(AppliedRuleStrategy appliedRule : this.getFromModuleAppliedRules(moduleFrom)) {
			if(appliedRule.getRuleType().equals(ruleTypeKey)) {
				setErrorMessage("'"+ ServiceProvider.getInstance().getLocaleService().getTranslatedString(ruleTypeKey) + "'");
				return false;
			}
		}
		return true;
	}
	
	public boolean checkRuleTypeAlreadyFromThisToSelected(String ruleType, ModuleStrategy fromModule, ModuleStrategy toModule) {
		for(AppliedRuleStrategy appliedRule : this.getFromModuleAppliedRules(fromModule)) {
			if(appliedRule.getRuleType().equals(ruleType) &&
			   appliedRule.getModuleFrom().getId() == fromModule.getId() &&
			   appliedRule.getModuleTo().getId() == toModule.getId()) {
				setErrorMessage("'" + appliedRule.getModuleFrom().getName() + "' " + ServiceProvider.getInstance().getLocaleService().getTranslatedString(ruleType) + " '" + appliedRule.getModuleTo().getName() + "'");
				return false;
			}
		}
		for(ModuleStrategy fromModuleChild : fromModule.getSubModules()) {
			if(!this.checkRuleTypeAlreadyFromThisToSelected(ruleType, fromModuleChild, toModule)) {
				return false;
			}
		}
		for(ModuleStrategy toModuleChild : toModule.getSubModules()) {
			if(!this.checkRuleTypeAlreadyFromThisToSelected(ruleType, fromModule, toModuleChild)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean checkRuleTypeAlreadyFromThisToOther(String ruleType, ModuleStrategy fromModule, ModuleStrategy toModule) {
		for(AppliedRuleStrategy appliedRule : this.getFromModuleAppliedRules(fromModule)) {
			if(appliedRule.getRuleType().equals(ruleType) &&
				appliedRule.getModuleFrom().getId() == fromModule.getId() &&
				appliedRule.getModuleTo().getId() != toModule.getId()) {
				setErrorMessage("'" + appliedRule.getModuleFrom().getName() + "' " + ServiceProvider.getInstance().getLocaleService().getTranslatedString(ruleType) + " '" + appliedRule.getModuleTo().getName() + "'");
				return false;
			}
		}
		for(ModuleStrategy fromModuleChild : fromModule.getSubModules()) {
			if(!this.checkRuleTypeAlreadyFromThisToOther(ruleType, fromModuleChild, toModule)) {
				return false;
			}
		}
		for(ModuleStrategy toModuleChild : toModule.getSubModules()) {
			if(!this.checkRuleTypeAlreadyFromThisToOther(ruleType, fromModule, toModuleChild)) {
				return false;
			}
		}
		return true;
	}
		
	private ArrayList<AppliedRuleStrategy> getFromModuleAppliedRules(ModuleStrategy fromModule) {
		ArrayList<Long> appliedRuleIds = appliedRuleService.getAppliedRulesIdsByModuleFromId(fromModule.getId());
		ArrayList<AppliedRuleStrategy> appliedRules = new ArrayList<AppliedRuleStrategy>();
		for(Long appliedRuleId : appliedRuleIds) {
			appliedRules.add(appliedRuleService.getAppliedRuleById(appliedRuleId));
		}
		return appliedRules;
	}
	
	public boolean checkRuleTypeAlreadyFromOtherToSelected(String ruleType, ModuleStrategy fromModule, ModuleStrategy toModule) {
		for(AppliedRuleStrategy appliedRule : getToModuleAppliedRules(toModule)) {
			if(appliedRule.getRuleType().equals(ruleType) &&
				checkRuleTypeAlreadyFromOtherToSelectedFromModuleId(appliedRule.getModuleFrom(), fromModule) &&
				appliedRule.getModuleTo().getId() == toModule.getId()) {
				setErrorMessage("'" + appliedRule.getModuleFrom().getName() + "' " + ServiceProvider.getInstance().getLocaleService().getTranslatedString(ruleType) + " '" + appliedRule.getModuleTo().getName() + "'");
				return false;
			}
		}
		for(ModuleStrategy toModuleChild : toModule.getSubModules()) {
			if(!this.checkRuleTypeAlreadyFromOtherToSelected(ruleType, fromModule, toModuleChild)) {
				return false;
			}
		}
		return true;
	}
	
	private boolean checkRuleTypeAlreadyFromOtherToSelectedFromModuleId(ModuleStrategy appliedRuleModule, ModuleStrategy fromModule) {
		if(appliedRuleModule.getId() == fromModule.getId()) {
			return false;
		} else {
			for(ModuleStrategy fromModuleChild : fromModule.getSubModules()) {
				if(!checkRuleTypeAlreadyFromOtherToSelectedFromModuleId(appliedRuleModule, fromModuleChild)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private ArrayList<AppliedRuleStrategy> getToModuleAppliedRules(ModuleStrategy toModule) {
		ArrayList<Long> appliedRuleIds = appliedRuleService.getAppliedRulesIdsByModuleToId(toModule.getId());
		ArrayList<AppliedRuleStrategy> appliedRules = new ArrayList<AppliedRuleStrategy>();
		for(Long appliedRuleId : appliedRuleIds) {
			appliedRules.add(appliedRuleService.getAppliedRuleById(appliedRuleId));
		}
		return appliedRules;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String message) {
		if(message != "") {
			this.errorMessage = ServiceProvider.getInstance().getLocaleService().getTranslatedString("NotAllowedBecauseDefined") + ":\n\n " + message;
		} else {
			this.errorMessage = "";
		}
	}
}
