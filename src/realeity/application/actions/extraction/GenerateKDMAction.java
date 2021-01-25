package realeity.application.actions.extraction;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.gmt.modisco.omg.kdm.kdm.Segment;
import org.eclipse.modisco.jee.jsp.Model;
import org.xml.sax.SAXException;

import anas.test.TestKDMRead;
import realeity.application.actions.AbstractMicroAction;
import realeity.application.data.GeneratedData;
import realeity.core.layering.algorithms.settings.AlgorithmContext;
import realeity.technique.extractor.SegmentExtractor;
import realeity.technique.extractor.ServletToUrl;
import realeity.technique.extractor.structuremodel.JSPModelsGeneration;
import realeity.technique.extractor.structuremodel.KDMModelsGeneration;
import uqam.dejee.element.serverpage.JspPage;
import uqam.dejee.technique.extractor.jsp.JspKdmModifier;
import uqam.dejee.technique.extractor.jsp.JspTagExtractor;
import uqam.dejee.technique.extractor.jsp.taglib.CustomTagLibrary;
import uqam.dejee.technique.extractor.xml.parser.TldXmlParser;
import uqam.dejee.technique.extractor.xml.parser.WebXmlParser;

/**
 * License GPL
 * 
 * @date 2014-06-11
 * @author Boaye Belle Alvine
 **/
public class GenerateKDMAction extends AbstractMicroAction {

	public static final String KDM_STRING = "_kdm";
	public static final String JSP_STRING = "_jsp";

	/**
	 * Default constructor.
	 * 
	 * @param actionName
	 * @param tooltipText
	 * @param actionIconPath
	 */
	public GenerateKDMAction(String actionName) {
		super(actionName);
	}

	@Override
	public void run() {

		// generating Jsp model and store it in the GeneratedData class
		generateJspModel();

		// Identify servlet to URL patterns mapping and store it in the
		// GeneratedData class
		identifyServletToURLMapping();

		// parse tablib descriptors and save their info in CustomTagLibrary
		// instance.
		parseTldXmlFiles();

		// explore the generated JSP model to identify a set of interested JSP
		// tags that hold dependencies and store it in the GeneratedData class
		identifyInterstingJspTags();

		// generating KDM model and store it in the GeneratedData class
		generateKDMModel();

		// update KDM model based on JSP data
		updateKdmModel();
		runNextAction = true;
	}

	public void parseTldXmlFiles() {
		try {
			TldXmlParser.getInstance().parseTldXmlFile(GeneratedData.getInstance().getSelectedProject());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.err.println(CustomTagLibrary.getInstance().toSting());

		// GeneratedData.getInstance().setServletToUrlsMapp(WebXmlParser.getInstance().getServletToUrlsMapp());

	}

	/**
	 * It is used identify Servelt to URL-patters mapping.
	 * 
	 */
	public void identifyServletToURLMapping() {
		try {
			WebXmlParser.getInstance().parseXml(GeneratedData.getInstance().getSelectedProject());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GeneratedData.getInstance().setServletToUrlsMapp(WebXmlParser.getInstance().getServletToUrlsMapp());
		List<ServletToUrl> servletToUrlsMapp = WebXmlParser.getInstance().getServletToUrlsMapp();
		for (ServletToUrl svlt : servletToUrlsMapp) {
			System.err.println(svlt.toString());
		}

	}

	/**
	 * It is used generate (new) or identify (existing) KDM Model.
	 * 
	 */
	public void generateKDMModel() {
		// get the KDM segment that is inside the project
		Segment kdmSegment = SegmentExtractor.getInstance()
				.findKDMSegment(GeneratedData.getInstance().getSelectedProject());

		if (kdmSegment == null) {
			// Use MoDisco to generate the KDM file from the java code source
			// the project.
			System.out.println(
					"Extraction phase : the KDM segment does not exist. " + "It will now be generated using Modisco");
			AlgorithmContext.kdmFilePath = GeneratedData.getInstance().getSelectedProject().getAbsolutePath()
					+ KDM_STRING + kdmExtension;
			String projectName = GeneratedData.getInstance().getSelectedProject().getName();
			try {
				SegmentExtractor.getInstance().setResourceModel(
						KDMModelsGeneration.getInstance().discoverKDMModel(projectName, AlgorithmContext.kdmFilePath));
			} catch (java.lang.ClassCastException e) {

			}

			// get the KDM segment from the resource
			kdmSegment = (Segment) SegmentExtractor.getInstance().getResourceModel().getContents().get(0);
		} else {
			System.out.println("Extraction phase : the KDM segment already exist.");
		}
		// TestKDMRead.modifyKdmPackages(kdmSegment);
		// TestKDMRead.printKdmSegment(kdmSegment);
		// TestKDMRead.identifyJspKdmServlets(kdmSegment);
		GeneratedData.getInstance().setProjectMainSegment(kdmSegment);
	}

	public void updateKdmModel() {
		Segment kdmSegment = GeneratedData.getInstance().getProjectMainSegment();
		Vector<JspPage> jspPages = GeneratedData.getInstance().getJspPages();

		JspKdmModifier.getInstance().updateJspKdmJsp(kdmSegment, jspPages);

		// JspKdmModifier.getInstance().identifyJspKdmServlets(kdmSegment);

	}

	/**
	 * It is used generate (new) or identify (existing) JSP Model.
	 * 
	 */
	public void generateJspModel() {
		// get the JSP model that is inside the project
		Model jspModel = SegmentExtractor.getInstance().findJspModel(GeneratedData.getInstance().getSelectedProject());
		if (jspModel == null) {
			// Use MoDisco to generate the JSP model.
			System.out.println(
					"Extraction phase : the JSP model does not exist. " + "It will now be generated using Modisco");
			AlgorithmContext.jspModelFilePath = GeneratedData.getInstance().getSelectedProject().getAbsolutePath()
					+ JSP_STRING + jspExtension;
			String projectName = GeneratedData.getInstance().getSelectedProject().getName();
			try {
				SegmentExtractor.getInstance().setJSPResourceModel(JSPModelsGeneration.getInstance()
						.discoverJSPModel(projectName, AlgorithmContext.jspModelFilePath));
			} catch (java.lang.ClassCastException e) {
			}
			// get the JSP segment from the resource
			jspModel = (Model) SegmentExtractor.getInstance().getJSPResourceModel().getContents().get(0);
		} else {
			System.out.println("Extraction phase : the JSP model already exist.");
		}
		GeneratedData.getInstance().setProjectJspModel(jspModel);
	}

	public void identifyInterstingJspTags() {
		Model jspModel = GeneratedData.getInstance().getProjectJspModel();
		GeneratedData.getInstance().setJspPages(JspTagExtractor.getInstance().identufyInterestedTags(jspModel));
		Vector<JspPage> jspPages = GeneratedData.getInstance().getJspPages();
		for (JspPage jPage : jspPages) {
			// System.err.println(jPage.toString());
			jPage.identifyTargetServlets();
			// jPage.printTargetServelts();
			jPage.mapURLtoServlet(GeneratedData.getInstance().getServletToUrlsMapp());
			// jPage.printTargetServeltJsp();
			jPage.identifyTextContainTaglibPrefex();
		}
		GeneratedData.getInstance().setJspPages(jspPages);
		for (JspPage jPage : GeneratedData.getInstance().getJspPages()) {
			System.err.println(jPage.toString());
			jPage.printTargetServelts();
			jPage.printTargetServeltJsp();
			jPage.printUsedTagLibs();
		}

	}

	@Override
	public void monitorExecution() {
		// TODO Auto-generated method stub

	}
}
