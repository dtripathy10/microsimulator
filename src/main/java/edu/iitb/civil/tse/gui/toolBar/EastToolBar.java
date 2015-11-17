package edu.iitb.civil.tse.gui.toolBar;

import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.controller.UserAction;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.Border;

public class EastToolBar extends JPanel implements ActionListener {

	JToolBar leftToolBar;
	Border raisedbevel, blackline;
	Insets margins;
	ToolBarButton newFile, openFile, saveFile, zoom, refresh, help, node, link, nodeModification, edgeModification,
			run;
	private final ToolBarButton help1;
	private final ToolBarButton help2;

	public EastToolBar() {

		blackline = BorderFactory.createLineBorder(Color.GRAY);
		this.setBorder(blackline);
		BorderLayout bl = new BorderLayout(2, 0);
		this.setLayout(bl);
		leftToolBar = new JToolBar(JToolBar.VERTICAL);
		leftToolBar.setFloatable(false);
		leftToolBar.setRollover(true);
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		// leftToolBar.setBorder(raisedbevel);
		margins = new Insets(0, 0, 0, 0);
		newFile = new ToolBarButton("ico/main/" + "generator.png");
		newFile.setToolTipText("Create Generator");
		newFile.setMargin(margins);
		newFile.setActionCommand("generator");
		newFile.addActionListener(this);
		leftToolBar.add(newFile);
		leftToolBar.addSeparator();

		openFile = new ToolBarButton("ico/main/" + "link.png");
		openFile.setToolTipText("Create Link");
		openFile.setMargin(margins);
		openFile.setActionCommand("link");
		openFile.addActionListener(this);
		leftToolBar.add(openFile);
		leftToolBar.addSeparator();

		saveFile = new ToolBarButton("ico/main/" + "unsignalised.png");
		saveFile.setToolTipText("Create Unsignalised");
		saveFile.setMargin(margins);
		saveFile.setActionCommand("unsignalised");
		saveFile.addActionListener(this);
		leftToolBar.add(saveFile);
		leftToolBar.addSeparator();

		zoom = new ToolBarButton("ico/main/" + "modifygenerator.png");
		zoom.setToolTipText("Modify Generator");
		zoom.addActionListener(this);
		zoom.setActionCommand("modifygenerator");
		zoom.setMargin(margins);
		leftToolBar.add(zoom);
		leftToolBar.addSeparator();

		help2 = new ToolBarButton("ico/main/" + "modifylink.png");
		help2.setToolTipText("Modify Link");
		help2.setActionCommand("modifylink");
		help2.addActionListener(this);
		help2.setMargin(margins);
		leftToolBar.add(help2);

		help1 = new ToolBarButton("ico/main/" + "modifyunsignalised.png");
		help1.setToolTipText("Modify Unsignalised");
		help1.setActionCommand("modifyunsignalised");
		help1.setMargin(margins);
		help1.addActionListener(this);
		leftToolBar.add(help1);
		this.add(leftToolBar, BorderLayout.WEST);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String x = e.getActionCommand();
		switch (x) {
		case "generator":
			Global.getViewerController().setCurrentAction(UserAction.CREATEGENERATOR);
			setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
			break;
		case "link":
			Global.getViewerController().setCurrentAction(UserAction.CREATELINK);
			setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
			break;
		case "unsignalised":
			Global.getViewerController().setCurrentAction(UserAction.CREATEUNSIGNALISED);
			setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
			break;
		}
	}

	private void setActionMessage(String string) {
		// TODO Auto-generated method stub

	}
}