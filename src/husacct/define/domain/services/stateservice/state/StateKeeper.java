package husacct.define.domain.services.stateservice.state;

import husacct.define.domain.services.stateservice.interfaces.Istate;

import java.util.ArrayList;
import java.util.Collection;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class StateKeeper implements Istate {

	private ArrayList<Istate> states;
	private int currentIndex = -1;
	private int _currentIndex = 0;
	private int sizeoflist = 0;

	private enum State {
		inAction, outAction
	};

	private State myState = State.outAction;

	public StateKeeper(ArrayList<Istate> listOfStatesTokeep) {
		this.states = listOfStatesTokeep;
	}

	public void undo()
	{
		myState=State.inAction;
		upDate();
		Istate result=null;
		if (currentIndex==-1) {
			currentIndex=sizeoflist-1;
			_currentIndex=6;
	result=		states.get(currentIndex);
		}else if (_currentIndex==currentIndex) {
			result=		states.get(currentIndex);
			currentIndex--;
		}else  {
			_currentIndex=currentIndex;
			currentIndex--;
			result=		states.get(currentIndex);
			
		}
		
		
		
		
		
		
		result.undo();
	}
	public void redo()
	{
		myState=State.inAction;
		Istate result=null;	
		 if (_currentIndex==currentIndex) {
			result=		states.get(currentIndex);
			currentIndex++;
		}else  {
			result=		states.get(currentIndex);
			_currentIndex=currentIndex;
			currentIndex++;
			
			
		}
		
		 result.redo();
		
	}
	
	


	private void upDate() {
		sizeoflist=states.size();
		
		
	}
	
	
	
	public void insertCommand(Istate sate){
		
		if (myState==State.inAction) {
			
			removeStates();
			registerState(sate);
		} else {
			registerState(sate);

		}
		
		myState=State.outAction;

	}
	
	public void registerState(Istate sate) {
				
		
		
		
		if (states.size() == 5) {
			states.remove(0);

			states.add(sate);
		
		} else {
			
		

			states.add(sate);
		}
		
		upDate();
	}
	
	private void removeStates() {
      ArrayList<Istate> oldvalues = new ArrayList<>();
	
		for (int i = currentIndex-1; i > -1; i--) {
			oldvalues.add(states.get(i));
		}
		states= new ArrayList<Istate>();
		java.util.Collections.reverse(oldvalues);
		states.addAll(oldvalues);
		
		currentIndex=-1;
		
	}


	public boolean[] getStates()
	{
		upDate();
		boolean undo =false;
		boolean redo =false;
		if (myState==State.outAction) {
			if (sizeoflist>0) {
				undo=true;
				redo=false;
			}
		}else{
			if (sizeoflist>0) {
			undo=true;
			redo=true;
		}
	
	 if (currentIndex==states.size()) {
		undo=true;
		redo=false;
	}
	 
	 
	 if (currentIndex==0&&_currentIndex==1) {
		 undo=false;
			redo=true;
	}
	 
	 if (currentIndex==0&&states.size()==1) {
		 undo=false;
			redo=true;
	}
		
		
		
		
		}
		
		return new boolean[]{undo,redo};
	}

}


