package husacct.define.domain.warningmessages;

import husacct.define.domain.module.ModuleStrategy;

public class ImplementationLevelWarning  extends WarningMessage{

	private ModuleStrategy module;

	public ModuleStrategy getModule() {
		return module;
	}

	public void setModule(ModuleStrategy module) {
		this.module = module;
	}

	public ImplementationLevelWarning(ModuleStrategy module)
	{
		this.module=module;
		generateMessage();
	}

	@Override
	public void generateMessage() {
		this.description="A module must be mapped to an implementation unit";
		this.resource="Module name: "+module.getName();
		this.type="Implentation Level";
		this.location="";
	}



}
