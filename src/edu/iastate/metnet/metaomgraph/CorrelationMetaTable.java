package edu.iastate.metnet.metaomgraph;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * This class is frame to display correlation from meta-analysis model
 * display genename, correlation, p value q value calculate conf interval etc.
 * the values are loaded from CorrelationMeta object 
 */

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JScrollBar;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import edu.iastate.metnet.metaomgraph.chart.MetaOmChartPanel;
import edu.iastate.metnet.metaomgraph.chart.ScatterPlotChart;
import edu.iastate.metnet.metaomgraph.utils.Utils;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import java.awt.Color;
import java.awt.Component;

import javax.swing.SwingConstants;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Point;

public class CorrelationMetaTable extends JInternalFrame {
	private JTable table;
	private HashMap<String, CorrelationMetaCollection> metaCorrRes;
	private JScrollPane scrollPane;
	private JComboBox comboBox;
	private JLabel lblCorrInfo;
	private JPanel plotButtonsPanel;
	private JButton btnPlot;
	private MetaOmProject myProject;
	private static double alpha = 0.05; // default value

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CorrelationMetaTable frame = new CorrelationMetaTable();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CorrelationMetaTable() {

		this(null);
		this.setSize(800, 500);
		setLocation(new Point(250, 0));
	}

	public CorrelationMetaTable(HashMap<String, CorrelationMetaCollection> metaCorrRes) {
		setLocation(new Point(250, 0));
		myProject = MetaOmGraph.activeProject;
		IconTheme theme = MetaOmGraph.getIconTheme();
		int width = MetaOmGraph.getMainWindow().getWidth();
		int height = MetaOmGraph.getMainWindow().getHeight();
		this.setSize(width - 200, height - 200);
		this.setLocation((width - this.getWidth()) / 2, (height - this.getHeight()) / 2);
		putClientProperty("JInternalFrame.frameType", "normal");
		// this.setSize(800,500);
		setTitle("MOG statistical infrence");
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);
		this.metaCorrRes = metaCorrRes;
		setBounds(100, 100, 450, 300);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblChooseCorrelationData = new JLabel("Choose correlation data");
		panel.add(lblChooseCorrelationData, BorderLayout.WEST);

		comboBox = new JComboBox();
		panel.add(comboBox, BorderLayout.CENTER);
		// change data in table with combobox selection
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				// Do Something
				loadDatainTable(comboBox.getSelectedItem().toString());
			}
		});

		JButton btnNewButton = new JButton("New button");
		// panel.add(btnNewButton, BorderLayout.EAST);
		// add info label
		lblCorrInfo = new JLabel("dasasas");
		lblCorrInfo.setToolTipText("Correlation information");
		lblCorrInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblCorrInfo.setBackground(Color.WHITE);
		lblCorrInfo.setFont(new Font("Garamond", Font.PLAIN, 18));
		panel.add(lblCorrInfo, BorderLayout.SOUTH);

		JPanel panel_1 = new JPanel();
		getContentPane().add(panel_1, BorderLayout.SOUTH);

		JButton btnNewButton_1 = new JButton("New buttonBottom");
		// panel_1.add(btnNewButton_1);

		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));

		scrollPane = new JScrollPane();
		panel_2.add(scrollPane, BorderLayout.CENTER);

		DefaultTableModel model = new DefaultTableModel();
		table = new JTable(model);
		table.setAutoCreateRowSorter(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		model.addColumn("Name");
		model.addColumn("r");
		model.addColumn("pval");
		model.addColumn("CI");
		model.addColumn("z");
		model.addColumn("Q");
		scrollPane.setViewportView(table);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmSaveTable = new JMenuItem("Save table");
		mntmSaveTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Utils.saveJTabletofile(table);
			}
		});
		mnFile.add(mntmSaveTable);

		JMenuItem mntmLoadTable = new JMenuItem("Load table");
		mnFile.add(mntmLoadTable);

		JMenu mnPlot = new JMenu("Plot");
		menuBar.add(mnPlot);

		loadJCombobox();
		loadDatainTable(comboBox.getSelectedItem().toString());

		JMenuItem mntmPlotLineChart = new JMenuItem("Plot line chart");
		mntmPlotLineChart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// get correct indices wrt the list
				int[] rowIndices = table.getSelectedRows();
				// JOptionPane.showMessageDialog(null, "sR:" + Arrays.toString(rowIndices));
				int j = 0;
				for (int i : rowIndices) {
					rowIndices[j++] = table.convertRowIndexToModel(i);
				}
				// JOptionPane.showMessageDialog(null, "sR corr:" +
				// Arrays.toString(rowIndices));
				new MetaOmChartPanel(rowIndices, myProject.getDefaultXAxis(), myProject.getDefaultYAxis(),
						myProject.getDefaultTitle(), myProject.getColor1(), myProject.getColor2(), myProject)
								.createInternalFrame();
			}
		});
		mnPlot.add(mntmPlotLineChart);

		JMenuItem mntmPlotScatterPlot = new JMenuItem("Plot scatter plot");
		mntmPlotScatterPlot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// get correct indices wrt the list
				int[] rowIndices = table.getSelectedRows();
				// JOptionPane.showMessageDialog(null, "sR:" + Arrays.toString(rowIndices));
				int j = 0;
				for (int i : rowIndices) {
					rowIndices[j++] = table.convertRowIndexToModel(i);
				}

				if (rowIndices.length < 1) {
					JOptionPane.showMessageDialog(null,
							"Please select two or more rows and try again to plot a scatterplot.",
							"Invalid number of rows selected", JOptionPane.ERROR_MESSAGE);
					return;
				}

				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {// get data for selected rows

							ScatterPlotChart f = new ScatterPlotChart(rowIndices, 0, myProject);
							MetaOmGraph.getDesktop().add(f);
							f.setDefaultCloseOperation(2);
							f.setClosable(true);
							f.setResizable(true);
							f.pack();
							f.setSize(1000, 700);
							f.setVisible(true);
							f.toFront();

						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "Error occured while reading data!!!", "Error",
									JOptionPane.ERROR_MESSAGE);

							e.printStackTrace();
							return;
						}
					}
				});

				return;
			}
		});
		mnPlot.add(mntmPlotScatterPlot);

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmAlphaForCi = new JMenuItem("alpha for CI");
		mntmAlphaForCi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// choose pval dialog
				boolean flag = false;
				while (flag == false) {
					double newalpha = 0;
					try {
						newalpha = Double.parseDouble(JOptionPane.showInputDialog("Please enter alpha"));
					} catch (java.lang.NullPointerException e) {

					}
					if (newalpha < 1 && newalpha > 0) {
						alpha = newalpha;
						flag = true;
						// update table
						loadDatainTable(comboBox.getSelectedItem().toString());
					} else {
						JOptionPane.showMessageDialog(null, "Please enter a valid alpha between 0 and 1");
						flag = false;
					}
				}
			}
		});
		mnEdit.add(mntmAlphaForCi);

		JMenuItem mntmRemoveCorrelation = new JMenuItem("Remove correlation");
		mntmRemoveCorrelation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// JOptionPane.showMessageDialog(null, "Remove correlation");

				JDialog dialog = new JDialog();
				dialog.setLocationRelativeTo(null);
				dialog.setTitle("Please choose...");

				// display list of correlations and let user choose which one to remove
				DefaultTableModel model = new DefaultTableModel() {
				};
				model.addColumn("Name");
				// add correlation name to the displayed table
				for (Object s : metaCorrRes.keySet().toArray()) {
					Vector row = new Vector();
					row.add(s);
					model.addRow(row);
				}
				JTable tabNames = new JTable(model);
				tabNames.setAutoCreateRowSorter(true);
				tabNames.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				tabNames.setFont(new Font("Times New Roman", Font.PLAIN, 13));
				tabNames.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

				JScrollPane spTable = new JScrollPane(tabNames);
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(spTable, BorderLayout.CENTER);
				JLabel lab1 = new JLabel("Please select the rows to remove and click remove");
				lab1.setFont(new Font("Times New Roman", Font.PLAIN, 13));
				panel.add(lab1, BorderLayout.NORTH);
				JButton removeButton = new JButton("Remove");
				// action performed
				removeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int[] selectedInd = tabNames.getSelectedRows();
						// JOptionPane.showMessageDialog(null, "remove:" +
						// Arrays.toString(selectedInd));
						for (int i = 0; i < selectedInd.length; i++) {
							String keyToRem = (String) tabNames
									.getValueAt(tabNames.convertRowIndexToModel(selectedInd[i]), 0);
							// JOptionPane.showMessageDialog(null, "removing:" + keyToRem);
							metaCorrRes.remove(keyToRem);
							/**
							 * TODO Remove correlation columns from main table
							 */
							String[] infocolnames = myProject.getInfoColumnNames();
							int colNum = 0;
							for (int j = 0; j < infocolnames.length; j++) {
								if (keyToRem.equals(infocolnames[j])) {
									myProject.deleteInfoColumn(j);
								}
							}

						}

						// if all values are removed
						if (metaCorrRes.isEmpty()) {
							JOptionPane.showMessageDialog(null, "All data deleted");
							dispose();
							dialog.dispose();
						} else {
							// reload checkbox and internal frame and finally dispose this dialog
							loadJCombobox();
							loadDatainTable(comboBox.getSelectedItem().toString());
							dialog.dispose();
						}
					}
				});
				panel.add(removeButton, BorderLayout.SOUTH);

				panel.setVisible(true);
				dialog.getContentPane().add(panel);

				dialog.pack();
				dialog.setVisible(true); // show the dialog on the screen
				// Do something here

			}
		});
		mnEdit.add(mntmRemoveCorrelation);

	}

	private void loadJCombobox() {
		if (metaCorrRes != null) {
			// populate jcombobox
			comboBox.setModel(new DefaultComboBoxModel(metaCorrRes.keySet().toArray()));

		} else {
			JOptionPane.showMessageDialog(null, "Error!!! Metacorrelation List can't be null");
		}
	}

	/**
	 * load data for correlation with name s
	 * 
	 * @param s
	 */
	private void loadDatainTable(String s) {

		CorrelationMetaCollection cmcObj = metaCorrRes.get(s);
		// check values of table depending on cmcObj and populate the table
		int corrTypeId = cmcObj.getCorrTypeId();
		// list of all correlation i.e. for each row
		List<CorrelationMeta> corrList = cmcObj.getCorrList();

		if (corrList != null) {
			if (corrTypeId == 0) {
				DefaultTableModel model = new DefaultTableModel() {
					@Override
					public Class getColumnClass(int column) {
						switch (column) {
						case 0:
							return String.class;
						case 1:
							return Double.class;
						case 2:
							return Double.class;
						default:
							return Object.class;
						}
					}
				};
				table = new JTable(model);
				model.addColumn("Name");
				model.addColumn("r");
				model.addColumn("pval");
				double cilevel = (1 - alpha) * 100;
				model.addColumn(cilevel + "% CI for r");
				model.addColumn("z");
				model.addColumn("Q");
				table.setAutoCreateRowSorter(true);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
				table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				table.getColumnModel().getColumn(1).setCellRenderer(new DecimalFormatRenderer());
				table.getColumnModel().getColumn(2).setCellRenderer(new DecimalFormatRenderer());
				table.getColumnModel().getColumn(4).setCellRenderer(new DecimalFormatRenderer());
				table.getColumnModel().getColumn(5).setCellRenderer(new DecimalFormatRenderer());

				for (int i = 0; i < corrList.size(); i++) {
					CorrelationMeta thisObj = corrList.get(i);
					Vector row = new Vector();
					row.add(thisObj.getName());
					row.add(thisObj.getrVal());
					row.add(thisObj.getpVal());
					row.add(thisObj.getrCI(alpha));
					row.add(thisObj.getzVal());
					row.add(thisObj.getqVal());

					model.addRow(row);
				}
				scrollPane.setViewportView(table);
			} else {
				DefaultTableModel model = new DefaultTableModel() {
					@Override
					public Class getColumnClass(int column) {
						switch (column) {
						case 0:
							return String.class;
						case 1:
							return Double.class;
						case 2:
							return Double.class;
						default:
							return Object.class;
						}
					}
				};
				table = new JTable(model);
				model.addColumn("Name");
				model.addColumn("r");
				model.addColumn("pval");
				table.setAutoCreateRowSorter(true);
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				table.setFont(new Font("Times New Roman", Font.PLAIN, 13));
				table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
				table.getColumnModel().getColumn(1).setCellRenderer(new DecimalFormatRenderer());
				table.getColumnModel().getColumn(2).setCellRenderer(new DecimalFormatRenderer());

				for (int i = 0; i < corrList.size(); i++) {
					CorrelationMeta thisObj = corrList.get(i);
					Vector row = new Vector();
					row.add(thisObj.getName());
					row.add(thisObj.getrVal());
					row.add(thisObj.getpVal());
					model.addRow(row);
				}
				scrollPane.setViewportView(table);
			}
		}
		// update corr info label
		// String corrInfo=getcorrInfo(cmcObj);
		String infoText = cmcObj.getcorrInfo();

		lblCorrInfo.setText(infoText);
	}

	/**
	 * return alpha value
	 * 
	 * @return
	 */
	public static double getAlpha() {
		return alpha;
	}

	/**
	 * Class to handle decimal format
	 * 
	 * @author urmi
	 *
	 */
	static class DecimalFormatRenderer extends DefaultTableCellRenderer {
		private static final DecimalFormat formatter = new DecimalFormat("#.0000");

		public Component getTableCellRendererComponent(JTable table, Object val, boolean isSelected, boolean hasFocus,
				int row, int column) {
			// format the cell value
			val = formatter.format((Number) val);
			return super.getTableCellRendererComponent(table, val, isSelected, hasFocus, row, column);
		}
	}

}