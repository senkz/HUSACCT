package husacct.define.domain.warningmessages;

import husacct.define.domain.SoftwareArchitecture;
import husacct.define.domain.services.WarningMessageService;
import husacct.define.domain.services.stateservice.StateService;

public class WarningMessageFactory {









	public WarningMessageContainer getsortedMessages()
	{
		
		WarningMessageService.getInstance().updateWarnings();
		WarningMessageContainer root = new WarningMessageContainer(new CustomWarningMessage("WARNINGS"));
		WarningMessageContainer codelevelContainer = new WarningMessageContainer(new CustomWarningMessage("Code Level "));
		((CustomWarningMessage)codelevelContainer.getvalue()).setDecription(codelevelContainer.getchildren().size());
		WarningMessageContainer implevelContainer = new WarningMessageContainer(new CustomWarningMessage("Implementation Level"));
		((CustomWarningMessage)implevelContainer.getvalue()).setDecription(WarningMessageService.getInstance().sizeOfnotImplemented());
		//in the future U can display custom messages 
		//WarningMessageContainer customContainer = new WarningMessageContainer(new CustomWarningMessage("Custom"));
	WarningMessageContainer notMapped=	getNotmapped();
	System.out.println("hheeyy hooooo");	
	addNotCodeLevel(codelevelContainer);
		addNotMappedModule(implevelContainer);
	
		
		root.addChild(implevelContainer);
		root.addChild(codelevelContainer);
		root.addChild(notMapped);
	
		
	return root;
	
	}

	

	private WarningMessageContainer getNotmapped() {
		
	return StateService.instance().getNotMappedUnits();
}
	
	private void addNotMappedModule(WarningMessageContainer implevelContainer) {
		SoftwareArchitecture.getInstance().updateWarnings();
		for (WarningMessage message : WarningMessageService.getInstance().getWarningMessages()) {
			
			 if (message instanceof ImplementationLevelWarning) {
				
				implevelContainer.addChild(new WarningMessageContainer(message));
			}
		
		}
		
	}
	
	private void addNotCodeLevel(WarningMessageContainer rootOfNotmapped)
	{
<<<<<<< HEAD

	
		for (CodeLevelWarning code: WarningMessageService.getInstance().getNotCodeLevelWarnings()){

	
=======
	
		for (CodeLevelWarning code: WarningMessageService.getInstance().getNotCodeLevelWarnings()){
>>>>>>> 728ed6ea96ae32da46002d13adc0c058a06e0fd5
			rootOfNotmapped.addChild(new WarningMessageContainer(code));
		}
		
	}




}
