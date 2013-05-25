package husacct.define.persistency;

import husacct.define.domain.Application;
import husacct.define.domain.Project;
import husacct.define.domain.SoftwareArchitecture;
import husacct.define.domain.SoftwareUnitDefinition;
import husacct.define.domain.SoftwareUnitDefinition.Type;
import husacct.define.domain.appliedrules.AppliedRuleFactory;
import husacct.define.domain.appliedrules.AppliedRuleStrategy;
import husacct.define.domain.module.ModuleFactory;
import husacct.define.domain.module.ModuleStrategy;
import husacct.define.domain.module.modules.Component;
import husacct.define.domain.module.modules.ExternalLibrary;
import husacct.define.domain.module.modules.Layer;
import husacct.define.domain.module.modules.SubSystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Element;

import com.sun.xml.internal.ws.api.server.Module;


public class XMLDomain {

	private Element workspace;
	private ModuleFactory moduleFactory = new ModuleFactory();

	public XMLDomain(Element workspaceData) {
		this.workspace = workspaceData;
	}

	public Element getWorkspaceData() {
		return this.workspace;
	}

	private List<Element> getWorkspaceChildren() {
		return this.workspace.getChildren();
	}

	public Application getApplication() {
		List<Element> applicationProperties = this.getWorkspaceChildren();

	    Element ApName = (Element)applicationProperties.get(0);
	    Element ApVersion = (Element)applicationProperties.get(1);
	    Element ApProjects = (Element)applicationProperties.get(2);
	    ArrayList<Project> applicationProjects = getProjects(ApProjects);
	    
	    Application XMLAp = new Application(ApName.getText(), applicationProjects, ApVersion.getText());
	    XMLAp.setArchitecture( this.getArchitecture() );
    
	    return XMLAp;
	}
	
	private ArrayList<Project> getProjects(Element projectsElement) {
		ArrayList<Project> projects = new ArrayList<Project>();
		List<Element> projectElements = projectsElement.getChildren("Project");
    	for(Element project : projectElements) {
    		projects.add(getProject(project));
    	}
    	return projects;
	}
	
	private Project getProject(Element projectElement) {
		Project project = new Project();
		project.setName(projectElement.getChild("name").getText());
		project.setProgrammingLanguage(projectElement.getChild("programmingLanguage").getText());
		project.setVersion(projectElement.getChild("version").getText());
		project.setDescription(projectElement.getChild("description").getText());
		
		ArrayList<String> projectPaths = new ArrayList<String>(); 
		List<Element> pathElements = projectElement.getChild("paths").getChildren("path");
		for(Element path : pathElements) {
			projectPaths.add(path.getText());
		}
		project.setPaths(projectPaths);
		
		return project;
	}
	
	public SoftwareArchitecture getArchitecture() {
		List<Element> applicationProperties = this.getWorkspaceChildren();
		Element ApArchitecture = (Element)applicationProperties.get(3);
    	Element ArchitectureName = (Element)ApArchitecture.getChild("name");
    	Element ArchitectureDescription = (Element)ApArchitecture.getChild("description");   	
    	SoftwareArchitecture XMLArchitecture = new SoftwareArchitecture(ArchitectureName.getText(), ArchitectureDescription.getText());    	
    	Element ArchitectureModuleRoot = ApArchitecture.getChild("modules");
    	if (ArchitectureModuleRoot != null) {
	    	List<Element> ArchitectureModules = ArchitectureModuleRoot.getChildren("Module");
	    	if (ArchitectureModules.size() > 0) {
	    		@SuppressWarnings("rawtypes")
				Iterator moduleIterator = ArchitectureModules.iterator();
	    		while (moduleIterator.hasNext()){ 
	    			Object o = moduleIterator.next();

	    			if (o instanceof Element) {
	    				XMLArchitecture.addModule(this.getModuleFromXML((Element) o));
	    			}
	    		}
	    	}
    	
	    	XMLArchitecture.setModules(this.getModules(ArchitectureModules));
    	}
    	
    	XMLArchitecture.setAppliedRules(this.getAppliedRules(ApArchitecture));
    	return XMLArchitecture;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<AppliedRuleStrategy> getAppliedRules(Element ApplicationArchitecture) {
		Element AppliedRulesRoot = ApplicationArchitecture.getChild("rules");
		ArrayList<AppliedRuleStrategy> ruleList = new ArrayList<AppliedRuleStrategy>();
		
		if (AppliedRulesRoot != null) {
    		List<Element> AppliedRules = AppliedRulesRoot.getChildren("AppliedRule");
    		if (AppliedRules.size() > 0) {
    			Iterator appliedIterator = AppliedRules.iterator();
    			while (appliedIterator.hasNext()) {
    				Object o = appliedIterator.next();
    				if (o instanceof Element) {
    					ruleList.add(this.getAppliedRuleFromXML((Element) o));
    				}
    			}
    		}
    	}
		
		return ruleList;
	}

	public ArrayList<ModuleStrategy> getModules(List<Element> modules) {		
		ArrayList<ModuleStrategy> returnList = new ArrayList<ModuleStrategy>();

		for (int i = 0; i < modules.size(); i++) {
			Element theModule = (Element)modules.get(i);
			returnList.add(this.getModuleFromXML(theModule));
		}

		return returnList;
	}

	@SuppressWarnings("rawtypes")
	public AppliedRuleStrategy getAppliedRuleFromXML(Element e) {
		Element ruleDescription = e.getChild("description");
		Element ruleRegex = e.getChild("regex");
		Element ruleId = e.getChild("id");
		Element ruleType = e.getChild("type");
		Element ruleModuleFrom = e.getChild("moduleFrom").getChild("Module");
		ModuleStrategy moduleFrom = ruleModuleFrom == null ? moduleFactory.createDummy("Component") : this.getModuleFromXML(ruleModuleFrom);
		Element ruleModuleTo = e.getChild("moduleTo").getChild("Module");
		ModuleStrategy moduleTo = ruleModuleTo == null ? moduleFrom : this.getModuleFromXML(ruleModuleTo);
		Element ruleExceptions = e.getChild("exceptions");
		Element ruleEnabled = e.getChild("enabled");
		Element ruleDependencies = e.getChild("dependencies");

		ArrayList<String> dependencies = new ArrayList<String>();
		List<Element> ruleDependencyList = ruleDependencies.getChildren("dependency");
		Iterator DependencyIterator = ruleDependencyList.iterator();
		if (ruleDependencyList.size() > 0) {
			while (DependencyIterator.hasNext()) {
				Object o = DependencyIterator.next();
				if (o instanceof Element) {
					dependencies.add(((Element) o).getValue());
				}
			}
		}
		boolean enabled = Boolean.parseBoolean(ruleEnabled.getValue());
		
		AppliedRuleFactory factory = new AppliedRuleFactory();
		AppliedRuleStrategy AppliedXMLRule = factory.createRule(ruleType.getValue());
		AppliedXMLRule.setAppliedRule(ruleDescription.getValue(), dependencies.toArray(new String[dependencies.size()]), ruleRegex.getValue(), moduleFrom, moduleTo, enabled);
		AppliedXMLRule.setId(Integer.parseInt(ruleId.getValue()));
		
		if (ruleExceptions != null) {
			List<Element> ExceptionList = ruleExceptions.getChildren("AppliedRule");
			Iterator ExceptionIterator = ExceptionList.iterator();
			if (ExceptionList.size() > 0) {
				while (ExceptionIterator.hasNext()) {
					Object o = ExceptionIterator.next();
					if (o instanceof Element) {
						AppliedXMLRule.addException( this.getAppliedRuleFromXML((Element) o));
					}
				}
			}
		}
		
		AppliedXMLRule.setId(Integer.parseInt(ruleId.getValue()));

		return AppliedXMLRule;
	}

	@SuppressWarnings("rawtypes")
	public ModuleStrategy getModuleFromXML(Element e) {
		Element ModuleType = e.getChild("type");
		String ModuleTypeText = ModuleType.getText();
		ModuleStrategy xmlModule;

		String moduleName = e.getChild("name").getValue();
		String moduleDescription = e.getChild("description").getValue();
		
		String moduleId = e.getChild("id").getValue();

		xmlModule = moduleFactory.createModule(ModuleTypeText);
		xmlModule.set(moduleName, moduleDescription);
		xmlModule.setId(Long.parseLong(moduleId));
		if(ModuleTypeText.equals("Layer")){
			((Layer)xmlModule).setHierarchicalLevel(Integer.parseInt(e.getChild("HierarchicalLevel").getValue()));
		}		

		Element SoftwareUnitDefinitions = e.getChild("SoftwareUnitDefinitions");
		if (SoftwareUnitDefinitions != null) {
			List<Element> SoftwareUnitDefinitionsList = SoftwareUnitDefinitions.getChildren("SoftwareUnitDefinition");
			Iterator SUDIterator = SoftwareUnitDefinitionsList.iterator();
    		while (SUDIterator.hasNext()){ 
    			Object o = SUDIterator.next();
    			
    			if (o instanceof Element) {
    				xmlModule.addSUDefinition(this.getSoftwareUnitDefinitionFromXML((Element) o));
    			}
    		}
		}
		
		Element SubModules = e.getChild("SubModules");
		if (SubModules != null) {
			List<Element> SubModulesList = SubModules.getChildren("Module");
			Iterator ModuleIterator = SubModulesList.iterator();
			while (ModuleIterator.hasNext()) {
				Object o = ModuleIterator.next();
				
				if (o instanceof Element) {
					xmlModule.addSubModule( this.getModuleFromXML((Element) o ));
				}
			}
			
		}

		return xmlModule;
	}

	public SoftwareUnitDefinition getSoftwareUnitDefinitionFromXML(Element e) {
		Element SUDName = e.getChild("name");
		Element SUDType = e.getChild("type");
		Type SoftwareUnitDefinitionType;

		if (SUDType.getValue().toUpperCase().equals("CLASS")) {
			SoftwareUnitDefinitionType = Type.CLASS;
		} else if (SUDType.getValue().toUpperCase().equals("INTERFACE")) {
			SoftwareUnitDefinitionType = Type.INTERFACE;
		} else {
			SoftwareUnitDefinitionType = Type.PACKAGE;
		}

		return new SoftwareUnitDefinition(SUDName.getText(), SoftwareUnitDefinitionType);
	}
}