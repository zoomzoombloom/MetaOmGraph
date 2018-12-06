package edu.iastate.metnet.metaomgraph.chart;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import edu.iastate.metnet.metaomgraph.AnimatedSwingWorker;
import edu.iastate.metnet.metaomgraph.MetaOmGraph;

import resource.rscripts.*;

public class MakeChartWithR {

	private String pathtoRscripts = MetaOmGraph.getpathtoRscrips();
	private String pathtoR = MetaOmGraph.getRPath();

	public MakeChartWithR() {

	}

	/**
	 * save file and return full path of saved file
	 * 
	 * @param dataRows
	 * @param rowNames
	 * @param colNames
	 * @param fname
	 * @return
	 * @throws IOException
	 */
	public String saveDatatoFile(List<double[]> dataRows, String[] rowNames, String[] colNames, String fname)
			throws IOException {
		// directory same as where the project source files are
		String directory = MetaOmGraph.getActiveProject().getSourceFile().getParent();
		// JOptionPane.showMessageDialog(null, "this dir:" + directory);
		String tempFilename = fname;
		tempFilename += "_chartDataHM.txt";
		final File file = new File(directory + System.getProperty("file.separator") + tempFilename);
		new AnimatedSwingWorker("Working...", true) {
			@Override
			public Object construct() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {

						// save to file as tab delimited and
						FileWriter fw;
						try {
							fw = new FileWriter(file);

							String header = "Name" + "\t" + String.join("\t", colNames);
							fw.write(header);
							for (int i = 0; i < dataRows.size(); i++) {
								String thisLine = "\n" + rowNames[i];
								double[] thisData = dataRows.get(i);
								for (int j = 0; j < thisData.length; j++) {
									thisLine += "\t" + thisData[j];

								}
								fw.write(thisLine);
							}
							fw.close();
							JOptionPane.showMessageDialog(null, "File saved:" + file.getAbsolutePath(), "File saved",
									JOptionPane.INFORMATION_MESSAGE);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				return null;
			}
		}.start();

		return file.getAbsolutePath();
	}

	/**
	 * Make heat map using rscript.
	 * 
	 * @param datafilepath
	 *            data to plot
	 * @param chartSavename
	 *            filename to save as .png
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void makeHeatmap(String datafilepath, String chartSavename) throws IOException, InterruptedException {

		// JOptionPane.showMessageDialog(null, "This OS:"+MetaOmGraph.getOsName());
		String fileToSave = MetaOmGraph.getActiveProject().getSourceFile().getParent()
				+ System.getProperty("file.separator") + chartSavename + ".png";
		if (pathtoR == "") {
			JOptionPane.showMessageDialog(null, "Please set the path to \"Rscript\" in the project properties panel",
					"Rscript not found", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (pathtoRscripts == "") {
			JOptionPane.showMessageDialog(null,
					"Please set the path to the folder containin the R scripts to create the plot, in the project properties panel",
					"R files not found", JOptionPane.WARNING_MESSAGE);
			return;
		}
		// call rscript

		new AnimatedSwingWorker("Working...", true) {
			@Override
			public Object construct() {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						Process pr = null;
						try {
							// copy scripts outside
							// JOptionPane.showMessageDialog(null, "rsc:" +
							// getClass().getResource("/resource/rscripts/makeHeatmap.R").toString().split("file:/")[1]);
							// JOptionPane.showMessageDialog(null, "rsc:"
							// +getClass().getResource("/resource/MetaOmicon.png").toString());

							pr = Runtime.getRuntime().exec(new String[] { pathtoR, pathtoRscripts + "/makeHeatmap.R",
									datafilepath, fileToSave });
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (java.lang.NullPointerException npe) {
							JOptionPane.showMessageDialog(null, "1 Error while executing Rscript", "Error",
									JOptionPane.ERROR_MESSAGE);
							JOptionPane.showMessageDialog(null,
									"Please check the paths to R and R files in the project properties panel",
									"R files not found", JOptionPane.WARNING_MESSAGE);
						}
						int code = 0;
						try {
							code = pr.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							JOptionPane.showMessageDialog(null, "InterruptedException");
							e.printStackTrace();
						}
						switch (code) {
						case 0:
							// normal termination
							JOptionPane.showMessageDialog(null, "File saved:" + fileToSave, "File saved",
									JOptionPane.INFORMATION_MESSAGE);
							break;
						case 1:
							// error
							JOptionPane.showMessageDialog(null, "1 Error while executing Rscript", "Error",
									JOptionPane.ERROR_MESSAGE);
							JOptionPane.showMessageDialog(null,
									"Please check the paths to R and R files in the project properties panel",
									"R files not found", JOptionPane.WARNING_MESSAGE);
							return;
						default:
							JOptionPane.showMessageDialog(null, "Error while executing Rscript", "Error",
									JOptionPane.ERROR_MESSAGE);
							JOptionPane.showMessageDialog(null,
									"Please check the paths to R and R files in the project properties panel",
									"R files not found", JOptionPane.WARNING_MESSAGE);
							return;
						}
					}
				});
				return null;
			}
		}.start();

		return;
	}

}