package husacct.control.presentation;
import husacct.control.presentation.menubar.DefineMenu;
import husacct.control.presentation.menubar.ExportMenu;
import husacct.control.presentation.menubar.FileMenu;
import husacct.control.presentation.menubar.HelpMenu;
import husacct.control.presentation.menubar.LanguageMenu;
import husacct.control.presentation.menubar.MenuBar;
import husacct.control.presentation.menubar.ValidateMenu;
import husacct.control.presentation.menubar.ViewMenu;
import husacct.control.task.MainController;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class MainGui extends JFrame {

	private static final long serialVersionUID = 140205650372010347L;

	private MainController controller;
	private JMenuBar menuBar;

	public MainGui(MainController controller) {
		this.controller = controller;
		setupFrame();
		createMenuBar();
		this.setContentPane(new JDesktopPane());
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.setVisible(true);	
	}

	private void createMenuBar() {
		this.menuBar = new MenuBar();
		
		FileMenu fileMenu = new FileMenu(controller);
		ViewMenu viewMenu = new ViewMenu(controller.getViewController());
		DefineMenu defineMenu = new DefineMenu(controller);
		ValidateMenu validateMenu = new ValidateMenu(controller);
		ExportMenu exportMenu = new ExportMenu(controller.getStateController());
		LanguageMenu languageMenu = new LanguageMenu(controller.getLocaleController());
		HelpMenu helpMenu = new HelpMenu();

		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(defineMenu);
		menuBar.add(validateMenu);
		menuBar.add(exportMenu);
		menuBar.add(languageMenu);
		menuBar.add(helpMenu);
		
		setJMenuBar(menuBar);
		
	}

	private void setupFrame(){
		this.setTitle("HUSACCT");
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setBounds(100, 100, 783, 535);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				controller.exit();		
			}
		});
	}

}
