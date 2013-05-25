package husacct.define.task.conventions_checker;

import husacct.define.domain.module.ModuleStrategy;
import husacct.define.domain.module.modules.Layer;

import java.util.ArrayList;

public class RuleConventionsChecker {
	private ModuleStrategy moduleFrom;
	private ModuleStrategy moduleTo;
	private String ruleTypeKey;
	private boolean alreadyExists;
	
	private ModuleCheckerHelper moduleCheckerHelper;
	private LayerCheckerHelper layerCheckerHelper;
	
	public RuleConventionsChecker(ModuleStrategy moduleFrom, ModuleStrategy moduleTo, String ruleTypeKey, boolean alreadyExists) {
		this.setModuleFrom(moduleFrom);
		this.setModuleTo(moduleTo);
		this.setRuleTypeKey(ruleTypeKey);
		this.moduleCheckerHelper = new ModuleCheckerHelper();
		this.layerCheckerHelper = new LayerCheckerHelper(moduleTo);
		this.alreadyExists = alreadyExists;
	}
	
	
	public boolean checkRuleConventions() {
		boolean conventionCheckSucces = true;
		if(alreadyExists){
			return conventionCheckSucces;
		}else{
			conventionCheckSucces = moduleCheckerHelper.checkRuleTypeAlreadySet(ruleTypeKey, moduleFrom);
			return conventionCheckSucces;
		}	
//		if(ruleTypeKey.equals("SubClassConvention")) {
//			conventionCheckSucces = checkSubClassConvention();
//		} else if(ruleTypeKey.equals("InterfaceConvention")) {
//			conventionCheckSucces = checkInterfaceConvention();
//		} else if(ruleTypeKey.equals("IsNotAllowedToUse")) {
//			conventionCheckSucces = checkIsNotAllowedToUse();
//		} else if(ruleTypeKey.equals("IsOnlyAllowedToUse")) {
//			conventionCheckSucces = checkIsOnlyAllowedToUse();
//		} else if(ruleTypeKey.equals("IsOnlyModuleAllowedToUse")) {
//			conventionCheckSucces = checkIsOnlyModuleAllowedToUse();
//		} else if(ruleTypeKey.equals("IsAllowedToUse")) {
//			conventionCheckSucces = checkIsAllowedToUse();
//		} else if(ruleTypeKey.equals("MustUse")) {
//			conventionCheckSucces = checkMustUse();
//		} else if(ruleTypeKey.equals("IsNotAllowedToMakeSkipCall")) {
//			conventionCheckSucces = checkSkipCall();
//		} else if(ruleTypeKey.equals("IsNotAllowedToMakeBackCall")) {
//			conventionCheckSucces = checkBackCall();
//		}
	}
	
	
	
	/**
	 * checks are the same as MustUse
	 */
	private boolean checkSubClassConvention() {
		return checkMustUse();
	}
	
	/**
	 * checks are the same as MustUse
	 */
	private boolean checkInterfaceConvention() {
		return checkMustUse();
	}
	
	private boolean checkIsNotAllowedToUse() {
		boolean isNotAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("IsOnlyAllowedToUse", moduleFrom, moduleTo);
		if(isNotAllowedToUseSucces) {
			isNotAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("IsOnlyModuleAllowedToUse", moduleFrom, moduleTo);
		}
		if(isNotAllowedToUseSucces) {
			isNotAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("IsAllowedToUse", moduleFrom, moduleTo);
		}
		if(isNotAllowedToUseSucces) {
			isNotAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("MustUse", moduleFrom, moduleTo);
		}
		if(isNotAllowedToUseSucces && layerCheckerHelper.checkTypeIsLayer(moduleFrom) && layerCheckerHelper.checkTypeIsLayer(moduleTo)){
			ArrayList<Layer> backCallLayers = layerCheckerHelper.getBackCallLayers(moduleFrom.getId());
			ArrayList<Layer> skipCallLayers = layerCheckerHelper.getSkipCallLayers(moduleFrom.getId());
			for(Layer skipCallLayer : skipCallLayers) {
				if(skipCallLayer.equals(moduleTo)){
					isNotAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadySet("IsNotAllowedToMakeSkipCall", moduleFrom);
				}
			}
			for(Layer backCallLayer : backCallLayers) {
				if(backCallLayer.equals(moduleTo)){
					isNotAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadySet("IsNotAllowedToMakeBackCall", moduleFrom);
				}
			}
		}
		return isNotAllowedToUseSucces;
	}
	
	private boolean checkIsOnlyAllowedToUse() {
		boolean isOnlyAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("IsNotAllowedToUse", moduleFrom, moduleTo);
		if(isOnlyAllowedToUseSucces) {
			isOnlyAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("IsOnlyAllowedToUse", moduleFrom, moduleTo);
		}
		if(isOnlyAllowedToUseSucces) {
			isOnlyAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToOther("IsOnlyAllowedToUse", moduleFrom, moduleTo);
		}
		if(isOnlyAllowedToUseSucces) {
			isOnlyAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromOtherToSelected("IsOnlyModuleAllowedToUse", moduleFrom, moduleTo);
		}
		if(isOnlyAllowedToUseSucces) {
			isOnlyAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToOther("IsAllowedToUse", moduleFrom, moduleTo);
		}
		if(isOnlyAllowedToUseSucces) {
			isOnlyAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToOther("MustUse", moduleFrom, moduleTo);
		}
		if(isOnlyAllowedToUseSucces) {
			if(!moduleCheckerHelper.checkRuleTypeAlreadySet("IsNotAllowedToMakeSkipCall", moduleFrom)) {
				ArrayList<Layer> skipCallLayers = layerCheckerHelper.getSkipCallLayers(moduleFrom.getId());
				for(Layer skipCallLayer : skipCallLayers) {
					if(skipCallLayer == moduleTo) {
						isOnlyAllowedToUseSucces = false;
						break;
					}
				}
			}
		}
		if(isOnlyAllowedToUseSucces) {
			if(!moduleCheckerHelper.checkRuleTypeAlreadySet("IsNotAllowedToMakeBackCall", moduleFrom)) {
				ArrayList<Layer> skipCallLayers = layerCheckerHelper.getBackCallLayers(moduleFrom.getId());
				for(Layer skipCallLayer : skipCallLayers) {
					if(skipCallLayer == moduleTo) {
						isOnlyAllowedToUseSucces = false;
						break;
					}
				}
			}
		}
		return isOnlyAllowedToUseSucces;
	}
	
	private boolean checkIsOnlyModuleAllowedToUse() {
		boolean isOnlyModuleAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("IsNotAllowedToUse", moduleFrom, moduleTo);
		if(isOnlyModuleAllowedToUseSucces) {
			isOnlyModuleAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToOther("IsOnlyAllowedToUse", moduleFrom, moduleTo);
		}
		if(isOnlyModuleAllowedToUseSucces) {
			isOnlyModuleAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromOtherToSelected("IsOnlyModuleAllowedToUse", moduleFrom, moduleTo);
		}
		if(isOnlyModuleAllowedToUseSucces) {
			isOnlyModuleAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromOtherToSelected("IsAllowedToUse", moduleFrom, moduleTo);
		}
		if(isOnlyModuleAllowedToUseSucces) {
			isOnlyModuleAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromOtherToSelected("MustUse", moduleFrom, moduleTo);
		}
		if(isOnlyModuleAllowedToUseSucces) {
			if(!moduleCheckerHelper.checkRuleTypeAlreadySet("IsNotAllowedToMakeSkipCall", moduleFrom)) {
				ArrayList<Layer> skipCallLayers = layerCheckerHelper.getSkipCallLayers(moduleFrom.getId());
				for(Layer skipCallLayer : skipCallLayers) {
					if(skipCallLayer == moduleTo) {
						isOnlyModuleAllowedToUseSucces = false;
						break;
					}
				}
			}
		}
		if(isOnlyModuleAllowedToUseSucces) {
			if(!moduleCheckerHelper.checkRuleTypeAlreadySet("IsNotAllowedToMakeBackCall", moduleFrom)) {
				ArrayList<Layer> skipCallLayers = layerCheckerHelper.getBackCallLayers(moduleFrom.getId());
				for(Layer skipCallLayer : skipCallLayers) {
					if(skipCallLayer == moduleTo) {
						isOnlyModuleAllowedToUseSucces = false;
						break;
					}
				}
			}
		}
		return isOnlyModuleAllowedToUseSucces;
	}
	
	private boolean checkIsAllowedToUse() {
		boolean isAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("IsNotAllowedToUse", moduleFrom, moduleTo);
		if(isAllowedToUseSucces) {
			isAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToOther("IsOnlyAllowedToUse", moduleFrom, moduleTo);
		}
		if(isAllowedToUseSucces) {
			isAllowedToUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromOtherToSelected("IsOnlyModuleAllowedToUse", moduleFrom, moduleTo);
		}
		return isAllowedToUseSucces;
	}
	
	private boolean checkMustUse() {
		boolean mustUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToSelected("IsNotAllowedToUse", moduleFrom, moduleTo);
		if(mustUseSucces) {
			mustUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromThisToOther("IsOnlyAllowedToUse", moduleFrom, moduleTo);
		}
		if(mustUseSucces) {
			mustUseSucces = moduleCheckerHelper.checkRuleTypeAlreadyFromOtherToSelected("IsOnlyModuleAllowedToUse", moduleFrom, moduleTo);
		}
		if(mustUseSucces) {
			if(!moduleCheckerHelper.checkRuleTypeAlreadySet("IsNotAllowedToMakeSkipCall", moduleFrom)) {
				ArrayList<Layer> skipCallLayers = layerCheckerHelper.getSkipCallLayers(moduleFrom.getId());
				for(Layer skipCallLayer : skipCallLayers) {
					if(skipCallLayer == moduleTo) {
						mustUseSucces = false;
						break;
					}
				}
			}
		}
		if(mustUseSucces) {
			if(!moduleCheckerHelper.checkRuleTypeAlreadySet("IsNotAllowedToMakeBackCall", moduleFrom)) {
				ArrayList<Layer> skipCallLayers = layerCheckerHelper.getBackCallLayers(moduleFrom.getId());
				for(Layer skipCallLayer : skipCallLayers) {
					if(skipCallLayer == moduleTo) {
						mustUseSucces = false;
						break;
					}
				}
			}
		}
		return mustUseSucces;
	}
	
	private boolean checkSkipCall() {
		boolean skipCallSucces = moduleCheckerHelper.checkRuleTypeAlreadySet(ruleTypeKey, moduleFrom);
		if(skipCallSucces) {
			skipCallSucces = layerCheckerHelper.checkTypeIsLayer(moduleFrom);
		}
		if(skipCallSucces) {
			ArrayList<Layer> skipCallLayers = layerCheckerHelper.getSkipCallLayers(moduleFrom.getId());
			for(Layer skipCallLayer : skipCallLayers) {
				this.moduleTo = skipCallLayer;
				if(!this.checkIsNotAllowedToUse()) {
					skipCallSucces = false;
					break;
				}
			}
		}
		return skipCallSucces;
	}
	
	private boolean checkBackCall() {
		boolean backCallSucces = moduleCheckerHelper.checkRuleTypeAlreadySet(ruleTypeKey, moduleFrom);
		if(backCallSucces) {
			backCallSucces = layerCheckerHelper.checkTypeIsLayer(moduleFrom);
		}
		if(backCallSucces) {
			ArrayList<Layer> backCallLayers = layerCheckerHelper.getBackCallLayers(moduleFrom.getId());
			for(Layer backCallLayer : backCallLayers) {
				this.moduleTo = backCallLayer;
				if(!this.checkIsNotAllowedToUse()) {
					backCallSucces = false;
					break;
				}
			}
		}
		return backCallSucces;
	}
	
	public ModuleStrategy getModuleTo() {
		return moduleTo;
	}

	public void setModuleTo(ModuleStrategy moduleTo) {
		this.moduleTo = moduleTo;
	}

	public ModuleStrategy getModuleFrom() {
		return moduleFrom;
	}

	public void setModuleFrom(ModuleStrategy moduleFrom) {
		this.moduleFrom = moduleFrom;
	}

	public String getRuleTypeKey() {
		return ruleTypeKey;
	}

	public void setRuleTypeKey(String ruleTypeKey) {
		this.ruleTypeKey = ruleTypeKey;
	}
	
	public String getErrorMessage() {
		String errorMessage = moduleCheckerHelper.getErrorMessage();
		if(errorMessage == "") {
			errorMessage = layerCheckerHelper.getErrorMessage();
		}
		return errorMessage;
	}
}