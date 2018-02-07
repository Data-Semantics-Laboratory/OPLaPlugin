package edu.wright.dase.swing.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphActions;
import com.mxgraph.util.mxPoint;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import edu.wright.dase.swing.editor.EditorActions.BackgroundAction;
import edu.wright.dase.swing.editor.EditorActions.BackgroundImageAction;
import edu.wright.dase.swing.editor.EditorActions.ExitAction;
import edu.wright.dase.swing.editor.EditorActions.GridColorAction;
import edu.wright.dase.swing.editor.EditorActions.GridStyleAction;
import edu.wright.dase.swing.editor.EditorActions.HistoryAction;
import edu.wright.dase.swing.editor.EditorActions.NewAction;
import edu.wright.dase.swing.editor.EditorActions.OpenAction;
import edu.wright.dase.swing.editor.EditorActions.PageBackgroundAction;
import edu.wright.dase.swing.editor.EditorActions.PromptPropertyAction;
import edu.wright.dase.swing.editor.EditorActions.SaveAction;
import edu.wright.dase.swing.editor.EditorActions.ScaleAction;
import edu.wright.dase.swing.editor.EditorActions.SelectShortestPathAction;
import edu.wright.dase.swing.editor.EditorActions.SelectSpanningTreeAction;
import edu.wright.dase.swing.editor.EditorActions.StylesheetAction;
import edu.wright.dase.swing.editor.EditorActions.ToggleDirtyAction;
import edu.wright.dase.swing.editor.EditorActions.ToggleGridItem;
import edu.wright.dase.swing.editor.EditorActions.ToggleOutlineItem;
import edu.wright.dase.swing.editor.EditorActions.TogglePropertyItem;
import edu.wright.dase.swing.editor.EditorActions.ToggleRulersItem;
import edu.wright.dase.swing.editor.EditorActions.WarningAction;
import edu.wright.dase.swing.editor.EditorActions.ZoomPolicyAction;

public class SchemaEditorMenuBar extends JMenuBar
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6776304509649205465L;

	@SuppressWarnings("serial")
	public SchemaEditorMenuBar(final BasicGraphEditor editor)
	{
		final mxGraphComponent graphComponent = editor.getGraphComponent();
		final mxGraph graph = graphComponent.getGraph();
		JMenu menu = null;
		JMenu submenu = null;

		// Creates the file menu
		menu = add(new JMenu(mxResources.get("file")));

		menu.add(editor.bind(mxResources.get("new"), new NewAction(),
				"/images/new.gif"));
		menu.add(editor.bind(mxResources.get("openFile"), new OpenAction(),
				"/images/open.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("save"), new SaveAction(false),
				"/images/save.gif"));
		menu.add(editor.bind(mxResources.get("saveAs"), new SaveAction(true),
				"/images/saveas.gif"));

		menu.addSeparator();

//		menu.add(editor.bind(mxResources.get("pageSetup"),
//				new PageSetupAction(),
//				"/images/pagesetup.gif"));
//		menu.add(editor.bind(mxResources.get("print"), new PrintAction(),
//				"/images/print.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("exit"), new ExitAction()));

		// Creates the edit menu
		menu = add(new JMenu(mxResources.get("edit")));

		menu.add(editor.bind(mxResources.get("undo"), new HistoryAction(true),
				"/images/undo.gif"));
		menu.add(editor.bind(mxResources.get("redo"), new HistoryAction(false),
				"/images/redo.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("cut"), TransferHandler
				.getCutAction(), "/images/cut.gif"));
		menu.add(editor
				.bind(mxResources.get("copy"), TransferHandler.getCopyAction(),
						"/images/copy.gif"));
		menu.add(editor.bind(mxResources.get("paste"), TransferHandler
				.getPasteAction(),
				"/images/paste.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("delete"), mxGraphActions
				.getDeleteAction(),
				"/images/delete.gif"));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("selectAll"), mxGraphActions
				.getSelectAllAction()));
		menu.add(editor.bind(mxResources.get("selectNone"), mxGraphActions
				.getSelectNoneAction()));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("warning"), new WarningAction()));
		menu.add(editor.bind(mxResources.get("edit"), mxGraphActions
				.getEditAction()));

		// Creates the view menu
		menu = add(new JMenu(mxResources.get("view")));

		JMenuItem item = menu.add(new TogglePropertyItem(graphComponent,
				mxResources.get("pageLayout"), "PageVisible", true,
				new ActionListener()
				{
					/**
					 * 
					 */
					public void actionPerformed(ActionEvent e)
					{
						if (graphComponent.isPageVisible()
								&& graphComponent.isCenterPage())
						{
							graphComponent.zoomAndCenter();
						}
					}
				}));

		item.addActionListener(new ActionListener()
		{
			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() instanceof TogglePropertyItem)
				{
					final mxGraphComponent graphComponent = editor
							.getGraphComponent();
					TogglePropertyItem toggleItem = (TogglePropertyItem) e
							.getSource();

					if (toggleItem.isSelected())
					{
						// Scrolls the view to the center
						SwingUtilities.invokeLater(new Runnable()
						{
							/*
							 * (non-Javadoc)
							 * @see java.lang.Runnable#run()
							 */
							public void run()
							{
								graphComponent.scrollToCenter(true);
								graphComponent.scrollToCenter(false);
							}
						});
					}
					else
					{
						// Resets the translation of the view
						mxPoint tr = graphComponent.getGraph().getView()
								.getTranslate();

						if (tr.getX() != 0 || tr.getY() != 0)
						{
							graphComponent.getGraph().getView().setTranslate(
									new mxPoint());
						}
					}
				}
			}
		});

		menu.add(new TogglePropertyItem(graphComponent, mxResources
				.get("antialias"), "AntiAlias", true));

		menu.addSeparator();

		menu.add(new ToggleGridItem(editor, mxResources.get("grid")));
		menu.add(new ToggleRulersItem(editor, mxResources.get("rulers")));

		menu.addSeparator();

		submenu = (JMenu) menu.add(new JMenu(mxResources.get("zoom")));

		submenu.add(editor.bind("400%", new ScaleAction(4)));
		submenu.add(editor.bind("200%", new ScaleAction(2)));
		submenu.add(editor.bind("150%", new ScaleAction(1.5)));
		submenu.add(editor.bind("100%", new ScaleAction(1)));
		submenu.add(editor.bind("75%", new ScaleAction(0.75)));
		submenu.add(editor.bind("50%", new ScaleAction(0.5)));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("custom"), new ScaleAction(0)));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("zoomIn"), mxGraphActions
				.getZoomInAction()));
		menu.add(editor.bind(mxResources.get("zoomOut"), mxGraphActions
				.getZoomOutAction()));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("page"), new ZoomPolicyAction(
				mxGraphComponent.ZOOM_POLICY_PAGE)));
		menu.add(editor.bind(mxResources.get("width"), new ZoomPolicyAction(
				mxGraphComponent.ZOOM_POLICY_WIDTH)));

		menu.addSeparator();

		menu.add(editor.bind(mxResources.get("actualSize"), mxGraphActions
				.getZoomActualAction()));

		// Creates the diagram menu
		menu = add(new JMenu(mxResources.get("diagram")));

		menu.add(new ToggleOutlineItem(editor, mxResources.get("outline")));

		menu.addSeparator();

		submenu = (JMenu) menu.add(new JMenu(mxResources.get("background")));

		submenu.add(editor.bind(mxResources.get("backgroundColor"),
				new BackgroundAction()));
		submenu.add(editor.bind(mxResources.get("backgroundImage"),
				new BackgroundImageAction()));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("pageBackground"),
				new PageBackgroundAction()));

		submenu = (JMenu) menu.add(new JMenu(mxResources.get("grid")));

		submenu.add(editor.bind(mxResources.get("gridSize"),
				new PromptPropertyAction(graph, "Grid Size", "GridSize")));
		submenu.add(editor.bind(mxResources.get("gridColor"),
				new GridColorAction()));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("dashed"), new GridStyleAction(
				mxGraphComponent.GRID_STYLE_DASHED)));
		submenu.add(editor.bind(mxResources.get("dot"), new GridStyleAction(
				mxGraphComponent.GRID_STYLE_DOT)));
		submenu.add(editor.bind(mxResources.get("line"), new GridStyleAction(
				mxGraphComponent.GRID_STYLE_LINE)));
		submenu.add(editor.bind(mxResources.get("cross"), new GridStyleAction(
				mxGraphComponent.GRID_STYLE_CROSS)));

		menu.addSeparator();

		submenu = (JMenu) menu.add(new JMenu(mxResources.get("layout")));

		submenu.add(editor.graphLayout("verticalHierarchical", true));
		submenu.add(editor.graphLayout("horizontalHierarchical", true));

		submenu.addSeparator();

		submenu.add(editor.graphLayout("verticalPartition", false));
		submenu.add(editor.graphLayout("horizontalPartition", false));

		submenu.addSeparator();

		submenu.add(editor.graphLayout("verticalStack", false));
		submenu.add(editor.graphLayout("horizontalStack", false));

		submenu.addSeparator();

		submenu.add(editor.graphLayout("verticalTree", true));
		submenu.add(editor.graphLayout("horizontalTree", true));

		submenu.addSeparator();

		submenu.add(editor.graphLayout("parallelEdges", false));

		submenu.addSeparator();

		submenu.add(editor.graphLayout("organicLayout", true));

		submenu = (JMenu) menu.add(new JMenu(mxResources.get("selection")));

		submenu.add(editor.bind(mxResources.get("selectPath"),
				new SelectShortestPathAction(false)));
		submenu.add(editor.bind(mxResources.get("selectDirectedPath"),
				new SelectShortestPathAction(true)));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("selectTree"),
				new SelectSpanningTreeAction(false)));
		submenu.add(editor.bind(mxResources.get("selectDirectedTree"),
				new SelectSpanningTreeAction(true)));

		menu.addSeparator();

		submenu = (JMenu) menu.add(new JMenu(mxResources.get("stylesheet")));

		submenu
				.add(editor
						.bind(
								mxResources.get("basicStyle"),
								new StylesheetAction(
										"/resources/basic-style.xml")));
		submenu
				.add(editor
						.bind(
								mxResources.get("defaultStyle"),
								new StylesheetAction(
										"/resources/default-style.xml")));

		// Creates the options menu
		menu = add(new JMenu(mxResources.get("options")));

		submenu = (JMenu) menu.add(new JMenu(mxResources.get("display")));
		submenu.add(new TogglePropertyItem(graphComponent, mxResources
				.get("buffering"), "TripleBuffered", true));
		submenu.add(editor.bind(mxResources.get("dirty"),
				new ToggleDirtyAction()));

		submenu.addSeparator();

		item = submenu.add(new TogglePropertyItem(graphComponent, mxResources
				.get("centerPage"), "CenterPage", true, new ActionListener()
		{
			/**
			 * 
			 */
			public void actionPerformed(ActionEvent e)
			{
				if (graphComponent.isPageVisible()
						&& graphComponent.isCenterPage())
				{
					graphComponent.zoomAndCenter();
				}
			}
		}));

		submenu.add(new TogglePropertyItem(graphComponent, mxResources
				.get("centerZoom"), "CenterZoom", true));
		submenu.add(new TogglePropertyItem(graphComponent, mxResources
				.get("zoomToSelection"), "KeepSelectionVisibleOnZoom", true));

		submenu.addSeparator();

		submenu.add(new TogglePropertyItem(graphComponent, mxResources
				.get("preferPagesize"), "PreferPageSize", true));

		// This feature is not yet implemented
		//submenu.add(new TogglePropertyItem(graphComponent, mxResources
		//		.get("pageBreaks"), "PageBreaksVisible", true));

		submenu.addSeparator();

		submenu.add(editor.bind(mxResources.get("tolerance"),
				new PromptPropertyAction(graph, "Tolerance")));

		// Creates the window menu
		menu = add(new JMenu(mxResources.get("window")));

		UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();

		for (int i = 0; i < lafs.length; i++)
		{
			final String clazz = lafs[i].getClassName();
			menu.add(new AbstractAction(lafs[i].getName())
			{
				public void actionPerformed(ActionEvent e)
				{
					editor.setLookAndFeel(clazz);
				}
			});
		}

		// Creates the help menu
		menu = add(new JMenu(mxResources.get("help")));

		item = menu.add(new JMenuItem(mxResources.get("aboutGraphEditor")));
		item.addActionListener(new ActionListener()
		{
			/*
			 * (non-Javadoc)
			 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
			 */
			public void actionPerformed(ActionEvent e)
			{
				editor.about();
			}
		});
	}

}
