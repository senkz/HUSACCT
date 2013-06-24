package husacct.define.presentation.moduletree;

import husacct.define.task.components.AnalyzedModuleComponent;

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

<<<<<<< HEAD
}

	


=======
	

	
}
>>>>>>> 728ed6ea96ae32da46002d13adc0c058a06e0fd5
