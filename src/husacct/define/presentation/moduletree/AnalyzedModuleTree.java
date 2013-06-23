package husacct.define.presentation.moduletree;

import java.util.ArrayList;
import java.util.Collections;
import husacct.define.task.components.AbstractCombinedComponent;
import husacct.define.task.components.AnalyzedModuleComponent;
import husacct.define.task.components.RegexComponent;
import husacct.define.domain.SoftwareUnitRegExDefinition;
import husacct.define.domain.services.WarningMessageService;
import husacct.define.domain.warningmessages.CodeLevelWarning;
import javax.swing.JTree;
import javax.swing.tree.TreeSelectionModel;

public class AnalyzedModuleTree extends JTree {

	private static final long serialVersionUID = 3282591641481691737L;

	public AnalyzedModuleTree(AnalyzedModuleComponent rootComponent) {
		super(new CombinedModuleTreeModel(rootComponent));
		CombinedModuleCellRenderer moduleCellRenderer = new CombinedModuleCellRenderer();
		this.setCellRenderer(moduleCellRenderer);
		this.setDefaultSettings();
	}

	

	public void setDefaultSettings() {
		this.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	}

	public void restoreTreeItem(
			AnalyzedModuleComponent analyzedsoftwarecomponent) {

		analyzedsoftwarecomponent.unfreeze();
	}

	public void removeTreeItem(AnalyzedModuleComponent analyzedsoftwarecomponent) {

		analyzedsoftwarecomponent.freeze();

	}

	private CodeLevelWarning CodeLevelWarning(long moduleId,
			AnalyzedModuleComponent analyzedsoftwarecomponent) {

		return new CodeLevelWarning(analyzedsoftwarecomponent);
	}

	private ArrayList<Integer> getQueryofposition(
			AnalyzedModuleComponent analyzedsoftwarecomponent) {
		ArrayList<Integer> retrievedposition = new ArrayList<Integer>();
		AnalyzedModuleComponent temp = analyzedsoftwarecomponent;
		boolean stop = true;
		while (stop) {
			retrievedposition.add(temp.getAnalyzedModuleComponentPosition());
			if (temp.getParentofChild().getUniqueName().equals("root")) {
				stop = false;
			} else {
				temp = temp.getParentofChild();
				continue;
			}

		}
		Collections.reverse(retrievedposition);

		return retrievedposition;

	}


}
