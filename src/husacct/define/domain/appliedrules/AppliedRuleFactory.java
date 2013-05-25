package husacct.define.domain.appliedrules;

import husacct.define.domain.appliedrules.propertyrules.FacadeConventionRule;
import husacct.define.domain.appliedrules.propertyrules.InterfaceConventionRule;
import husacct.define.domain.appliedrules.propertyrules.NamingConventionExceptionRule;
import husacct.define.domain.appliedrules.propertyrules.NamingConventionRule;
import husacct.define.domain.appliedrules.propertyrules.SubClassConventionRule;
import husacct.define.domain.appliedrules.propertyrules.VisibilityConventionExceptionRule;
import husacct.define.domain.appliedrules.propertyrules.VisibilityConventionRule;
import husacct.define.domain.appliedrules.relationrules.IsAllowedToUseRule;
import husacct.define.domain.appliedrules.relationrules.IsNotAllowedToMakeBackCallRule;
import husacct.define.domain.appliedrules.relationrules.IsNotAllowedToMakeSkipCallRule;
import husacct.define.domain.appliedrules.relationrules.IsNotAllowedToUseRule;
import husacct.define.domain.appliedrules.relationrules.IsOnlyAllowedToUseRule;
import husacct.define.domain.appliedrules.relationrules.IsOnlyModuleAllowedToUseRule;
import husacct.define.domain.appliedrules.relationrules.MustUseRule;

import org.apache.log4j.Logger;


public class AppliedRuleFactory {

	private static Logger logger = Logger.getLogger(AppliedRuleFactory.class);

	private static final String[] ruleTypes = new String[]{
		"IsNotAllowedToUse",
		"IsNotAllowedToMakeBackCall",
		"IsNotAllowedToMakeSkipCall",
		"IsAllowedToUse",
		"IsOnlyAllowedToUse",
		"IsOnlyModuleAllowedToUse",
		"MustUse",
		"NamingConvention",
		"NamingConventionException",
		"VisibilityConvention",
		"VisibilityConventionException",
		"InterfaceConvention",
		"SubClassConvention",
		"FacadeConvention"
	};

	private static final Class<?>[] ruleClasses = new Class[]{
		IsNotAllowedToUseRule.class,
		IsNotAllowedToMakeBackCallRule.class,
		IsNotAllowedToMakeSkipCallRule.class,
		IsAllowedToUseRule.class,
		IsOnlyAllowedToUseRule.class,
		IsOnlyModuleAllowedToUseRule.class,
		MustUseRule.class,
		NamingConventionRule.class,
		NamingConventionExceptionRule.class,
		VisibilityConventionRule.class,
		VisibilityConventionExceptionRule.class,
		InterfaceConventionRule.class,
		SubClassConventionRule.class,
		FacadeConventionRule.class
	};
	
	private static final String[][] categories = new String[][]{
		new String[]{ 
				"IsNotAllowedToUse",
				"IsNotAllowedToMakeBackCall",
				"IsNotAllowedToMakeSkipCall",
				"IsAllowedToUse",
				"IsOnlyAllowedToUse",
				"IsOnlyModuleAllowedToUse",
				"MustUse"
				},
		new String[]{ 
				"NamingConvention",
				"VisibilityConvention",
				"InterfaceConvention",
				"SubClassConvention",
				"FacadeConvention"
		}
	};
	
	public String[][] getCategories(){
		return categories;
	}

	public String[] getRuletypeOptions(){
		return ruleTypes;
	}

	public AppliedRuleStrategy createRule(String choice){
		for(int i = 0; i < ruleTypes.length; i++){
			if(ruleTypes[i].equals(choice)) try{
				AppliedRuleStrategy newRule = (AppliedRuleStrategy)ruleClasses[i].newInstance();
				newRule.setRuleType(choice);
				return newRule;
			}catch (InstantiationException ex) {
				logger.error("Instantiation Error in RuleFactory: " + ex.toString());
			} catch (IllegalAccessException ex) {
				logger.error("Instantiation Error in RuleFactory: " + ex.toString());
			}
		}
		logger.error("Error in AppliedRuleFactory: Illegal choice: ");
		throw new IllegalArgumentException("Illegal choice");
	}
	public AppliedRuleStrategy createDummyRule(String choice){
		for(int i = 0; i < ruleTypes.length; i++){
			if(ruleTypes[i].equals(choice)) try{
				AppliedRuleStrategy newRule = (AppliedRuleStrategy)ruleClasses[i].newInstance();
				newRule.setRuleType(choice);
				newRule.setId(-1);
				return newRule;
			}catch (InstantiationException ex) {
				logger.error("Instantiation Error in RuleFactory: " + ex.toString());
			} catch (IllegalAccessException ex) {
				logger.error("Instantiation Error in RuleFactory: " + ex.toString());
			}
		}
		logger.error("Error in AppliedRuleFactory: Illegal choice: ");
		throw new IllegalArgumentException("Illegal choice");
	}
}
