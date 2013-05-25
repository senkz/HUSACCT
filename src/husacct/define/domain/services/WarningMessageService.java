package husacct.define.domain.services;

import husacct.define.domain.module.ModuleStrategy;
import husacct.define.domain.warningmessages.CodeLevelWarning;
import husacct.define.domain.warningmessages.ImplementationLevelWarning;
import husacct.define.domain.warningmessages.WarningMessage;
import husacct.define.task.JtreeStateEngine;
import husacct.define.task.components.AnalyzedModuleComponent;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class WarningMessageService extends Observable implements Observer {
	private  ArrayList<WarningMessage> warnings= new ArrayList<WarningMessage>();
	private ArrayList<Observer> observers = new ArrayList<Observer>();
	private static WarningMessageService instance;
	public enum removalType{partialRemoval,fullRemoval};

	public static WarningMessageService getInstance()
	{
		return (instance==null) ? instance = new WarningMessageService() : instance;
	}

	public void addWarning(WarningMessage warning ){
		warnings.add(warning);
		notifyAllObservers(this,"warning added");
	}

	public void removeWarning(WarningMessage warning){
		int index = warnings.indexOf(warning);
		warnings.remove(index);
		notifyAllObservers(this,"warning removed");
	}

	public ArrayList<WarningMessage> getWarningMessages(){
		return warnings;
	}

	public boolean hasWarnings(){
		return (warnings.size()>0);
	}

	public int warningsCount(){
		return warnings.size();
	}

	// TODO: This is a very bad function, needs revising
	public boolean hasCodeLevelWarning(AnalyzedModuleComponent analyzedModuleToChek,removalType removaltype)
	{
		ArrayList<WarningMessage> messagesTobeRemoved = new ArrayList<WarningMessage>();
		boolean haswarning=false;

		for (WarningMessage message : warnings) {
			if (message instanceof CodeLevelWarning) {
				AnalyzedModuleComponent analyzedModule = ((CodeLevelWarning) message).getNotCodeLevelModule();

				String leftUniqName= analyzedModule.getUniqueName().toLowerCase();
				String rightUniqName = analyzedModuleToChek.getUniqueName().toLowerCase();
				if(leftUniqName.equals(rightUniqName)){
					if (removalType.partialRemoval==removaltype) {
						JtreeStateEngine.instance().registerCodeRenewal(analyzedModuleToChek);
					} 
					haswarning=true;
					messagesTobeRemoved.add(message);
				}
			}
		}
		for (WarningMessage warningMessage : messagesTobeRemoved) {
			removeWarning(warningMessage);
		}
		return haswarning;
	}

	public boolean isCodeLevelWarning(AnalyzedModuleComponent analyzedModuleToChek){
		boolean haswarning = false;
		for (WarningMessage message : warnings) {
			if (message instanceof CodeLevelWarning) {
				AnalyzedModuleComponent analyzedModule = ((CodeLevelWarning) message).getNotCodeLevelModule();
				String leftUniqName= analyzedModule.getUniqueName().toLowerCase();
				String rightUniqName = analyzedModuleToChek.getUniqueName().toLowerCase();
				if(leftUniqName.equals(rightUniqName)){
					haswarning=true;
					break;
				}
			}
		}
		return haswarning;
	}

	public void processModule(ModuleStrategy module){
		if (module.isMapped()){
			chekIfImplementationWarningExist(module);
		} else {
			createModuleWarning(module);
		}
	}


	private void chekIfImplementationWarningExist(ModuleStrategy module) {
		for (WarningMessage warning : warnings) {
			if(warning instanceof ImplementationLevelWarning){
				Long idOfWarningModule = ((ImplementationLevelWarning)warning).getModule().getId();
				if(module.getId()==idOfWarningModule){
					int index= warnings.indexOf(warning);
					warnings.remove(index);
					break;
				}
			}
		}
		notifyAllObservers(this, "removed Module");
	}


	private void createModuleWarning(ModuleStrategy module) {
		ImplementationLevelWarning warning = new ImplementationLevelWarning(module);
		warnings.add(warning);
		notifyAllObservers(this,"create Module");
	}

	public void addObserver(Observer o){
		observers.add(o);
	}

	public void notifyAllObservers(Observable o, Object arg) {
		for (Observer observer : observers) {
			observer.update(this, arg);
		}
	}


	public boolean isCodeLevelWarning(String uniqname) {
		boolean haswarning = false;
		for (WarningMessage message : warnings) {
			if (message instanceof CodeLevelWarning) {
				AnalyzedModuleComponent analyzedModule = ((CodeLevelWarning) message).getNotCodeLevelModule();
				String leftUniqName= analyzedModule.getUniqueName().toLowerCase();
				String rightUniqName =uniqname ;
				if(leftUniqName.equals(rightUniqName)){
					haswarning=true;
					break;
				}
			}
		}
		return haswarning;
	}


	public void addCodeLevelWarning(Long key, AnalyzedModuleComponent unitTobeRestored) {
		CodeLevelWarning codeLevelWarning = new CodeLevelWarning(key, unitTobeRestored);
		warnings.add(codeLevelWarning);
	}

	public void removeCodeLevelWarning(AnalyzedModuleComponent unitTobeRemoved) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
	}

}
