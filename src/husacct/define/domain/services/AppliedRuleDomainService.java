package husacct.define.domain.services;

import husacct.ServiceProvider;
import husacct.define.domain.SoftwareArchitecture;
import husacct.define.domain.appliedrules.AppliedRuleFactory;
import husacct.define.domain.appliedrules.AppliedRuleStrategy;
import husacct.define.domain.module.ModuleStrategy;

import java.util.ArrayList;

public class AppliedRuleDomainService {
	
	private AppliedRuleFactory ruleFactory = new AppliedRuleFactory();

	public AppliedRuleStrategy[] getAppliedRules(boolean enabledRulesOnly) { 
		ArrayList<AppliedRuleStrategy> ruleList;
		if (enabledRulesOnly) {
			ruleList = SoftwareArchitecture.getInstance().getEnabledAppliedRules();
		} else {
			ruleList = SoftwareArchitecture.getInstance().getAppliedRules();
		}
		AppliedRuleStrategy[] rules = new AppliedRuleStrategy[ruleList.size()]; 
		ruleList.toArray(rules);

		return rules;
	}

	public AppliedRuleStrategy[] getAppliedRules() {
		return getAppliedRules(false);
	}  
	
	public long addAppliedRule(String ruleTypeKey, String description, String[] dependencies,
			String regex, long moduleFromId, long moduleToId, boolean enabled) {
		ModuleStrategy moduleFrom = SoftwareArchitecture.getInstance().getModuleById(moduleFromId);
		ModuleStrategy moduleTo = SoftwareArchitecture.getInstance().getModuleById(moduleToId);
		
		return addAppliedRule(ruleTypeKey,description,dependencies,regex,moduleFrom , moduleTo, enabled);
	} 
	
	public long addAppliedRule(String ruleTypeKey, String description, String[] dependencies,
			String regex, ModuleStrategy moduleFrom, ModuleStrategy moduleTo, boolean enabled) {
		
		AppliedRuleStrategy rule = ruleFactory.createRule(ruleTypeKey);
		rule.setAppliedRule(description, dependencies, regex, moduleFrom, moduleTo, enabled);
		if(isDuplicate(rule)){
			ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
			return -1;
		}
		SoftwareArchitecture.getInstance().addAppliedRule(rule);
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
		return rule.getId();
	}
	
	public void updateAppliedRule(long appliedRuleId, Boolean isGenerated, String ruleTypeKey,String description, String[] dependencies, 
			String regex,long moduleFromId, long moduleToId, boolean enabled) {

		ModuleStrategy moduleFrom = SoftwareArchitecture.getInstance().getModuleById(moduleFromId);
		ModuleStrategy moduleTo = SoftwareArchitecture.getInstance().getModuleById(moduleToId);
		updateAppliedRule(appliedRuleId, ruleTypeKey, description, dependencies, 
				regex, moduleFrom, moduleTo, enabled);
	}
	
	public long updateAppliedRule(long appliedRuleId, String ruleTypeKey,String description, String[] dependencies, 
			String regex, ModuleStrategy moduleFrom, ModuleStrategy moduleTo, boolean enabled) {
		
		AppliedRuleStrategy dummyRule = ruleFactory.createDummyRule(ruleTypeKey);
		dummyRule.setAppliedRule(description, dependencies, regex, moduleFrom, moduleTo, enabled);
		if(isDuplicate(dummyRule)){
			return -1;
		}
		
		AppliedRuleStrategy rule = SoftwareArchitecture.getInstance().getAppliedRuleById(appliedRuleId);
		rule.setAppliedRule(dummyRule);
		
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
		return rule.getId();
	}

	public void removeAppliedRules() {
		SoftwareArchitecture.getInstance().removeAppliedRules();
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
	}

	public void removeAppliedRule(long appliedrule_id) {
		SoftwareArchitecture.getInstance().removeAppliedRule(appliedrule_id);
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
	}

	public String getRuleTypeByAppliedRule(long appliedruleId) {
		return SoftwareArchitecture.getInstance().getAppliedRuleById(appliedruleId).getRuleType();
	}

	public void setAppliedRuleIsEnabled(long appliedRuleId, boolean enabled) {
		SoftwareArchitecture.getInstance().getAppliedRuleById(appliedRuleId).setEnabled(enabled);
		ServiceProvider.getInstance().getDefineService().notifyServiceListeners();
	}

	public ArrayList<Long> getAppliedRulesIdsByModuleFromId(long moduleId) {
		return SoftwareArchitecture.getInstance().getAppliedRulesIdsByModuleFromId(moduleId);
	}

	public ArrayList<Long> getAppliedRulesIdsByModuleToId(long moduleId) {
		return SoftwareArchitecture.getInstance().getAppliedRulesIdsByModuleToId(moduleId);
	}

	public long getModuleToIdOfAppliedRule(long appliedRuleId) {
		return SoftwareArchitecture.getInstance().getAppliedRuleById(appliedRuleId).getModuleTo().getId();
	}

	public boolean getAppliedRuleIsEnabled(long appliedRuleId) {
		return SoftwareArchitecture.getInstance().getAppliedRuleById(appliedRuleId).isEnabled();
	}

	public AppliedRuleStrategy getAppliedRuleById(long appliedRuleId) {
		return SoftwareArchitecture.getInstance().getAppliedRuleById(appliedRuleId);
	}
	
	public String[][] getCategories(){
		return ruleFactory.getCategories();
	}


	/** 
	 * Domain checks
	 */
	public boolean checkConventions(AppliedRuleStrategy appliedRule){
		return appliedRule.checkConvention();
	}
	
	public boolean isDuplicate(AppliedRuleStrategy rule){
		AppliedRuleStrategy[] appliedRules = getAppliedRules(false);
		for(AppliedRuleStrategy appliedRule : appliedRules){
			if(rule.equals(appliedRule)){
				return true;
			}
		}
		return false;
	}

}
