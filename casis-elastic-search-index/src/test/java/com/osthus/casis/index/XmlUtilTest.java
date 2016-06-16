package com.osthus.casis.index;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.DocumentException;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class XmlUtilTest {

	///casis-elastic-search-index/src/test/resources/2016-06-23XmlUtilTest.xml test file
	XmlUtil xmlUtil= new XmlUtil();
	private String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n" + 
			"<Document>\r\n" + 
			"<CASIS.DOCNO>DGL1235365</CASIS.DOCNO>\r\n" + 
			"<CASIS.UPD>\r\n" + 
			"<PublicationDate CCYYMMDD=\"20011128\">28 Nov 2001</PublicationDate>\r\n" + 
			"</CASIS.UPD>\r\n" + 
			"<LaunchDetails>\r\n" + 
			"<LaunchDateComment/>\r\n" + 
			"<NewChemicalEntity/>\r\n" + 
			"<Ingredients>\r\n" + 
			"<Ingredient>glyceryl trinitrate</Ingredient>\r\n" + 
			"</Ingredients>\r\n" + 
			"<CASIS.USE>\r\n" + 
			"<CASIS.ACT>\r\n" + 
			"<Class>\r\n" + 
			"<ClassCode>C1E</ClassCode>\r\n" + 
			"<ClassDescription>Nitrates and Nitrites</ClassDescription>\r\n" + 
			"</Class>\r\n" + 
			"</CASIS.ACT>\r\n" + 
			"<CASIS.IND>\r\n" + 
			"<Indication>Prevention and treatment of angina pectoris</Indication>\r\n" + 
			"</CASIS.IND>\r\n" + 
			"</CASIS.USE>\r\n" + 
			"<CASIS.CN>\r\n" + 
			"<BrandName>ANGIOCARD</BrandName>\r\n" + 
			"</CASIS.CN>\r\n" + 
			"<CASIS.CO>\r\n" + 
			"<CASIS.NORMALIZED.CO>Manufacturer: Biogenics</CASIS.NORMALIZED.CO>\r\n" + 
			"<CASIS.NORMALIZED.CO>BIOGENICS</CASIS.NORMALIZED.CO>\r\n" + 
			"<Manufacturer>Biogenics</Manufacturer>\r\n" + 
			"<Corporation>BIOGENICS</Corporation>\r\n" + 
			"</CASIS.CO>\r\n" + 
			"<CASIS.DSTA>\r\n" + 
			"<CASIS.NORMALIZED.DSTA>PK: Launched 20010601</CASIS.NORMALIZED.DSTA>\r\n" + 
			"<Country>Pakistan</Country>\r\n" + 
			"<LaunchDate CCYYMM=\"20010601\">01 Jun 2001</LaunchDate>\r\n" + 
			"</CASIS.DSTA>\r\n" + 
			"<CASIS.TX>\r\n" + 
			"<Biotech>No</Biotech>\r\n" + 
			"<Unbranded>No</Unbranded>\r\n" + 
			"</CASIS.TX>\r\n" + 
			"<CASIS.RN>\r\n" + 
			"<CASInfo>\r\n" + 
			"<CASItem/>\r\n" + 
			"</CASInfo>\r\n" + 
			"</CASIS.RN>\r\n" + 
			"</LaunchDetails>\r\n" + 
			"<PackInfo>\r\n" + 
			"<ExcipientInfo>\r\n" + 
			"<Excipient/>\r\n" + 
			"</ExcipientInfo>\r\n" + 
			"<PriceInfo>\r\n" + 
			"<Price>caps retard a 30: PKR 84.150 (RPP)</Price>\r\n" + 
			"<Price>caps retard b 30: PKR 109.650 (RPP)</Price>\r\n" + 
			"</PriceInfo>\r\n" + 
			"<DoseFormInfo>\r\n" + 
			"<DoseForm>caps retard</DoseForm>\r\n" + 
			"</DoseFormInfo>\r\n" + 
			"<NumberOfIngredients>1</NumberOfIngredients>\r\n" + 
			"<CASIS.TX>\r\n" + 
			"<CompositionInfo>\r\n" + 
			"<Composition>caps retard a: glyceryl trinitrate, 2.5 mg</Composition>\r\n" + 
			"<Composition>caps retard b: glyceryl trinitrate, 6.5 mg</Composition>\r\n" + 
			"</CompositionInfo>\r\n" + 
			"</CASIS.TX>\r\n" + 
			"</PackInfo>\r\n" + 
			"<LaunchStatus>\r\n" + 
			"<RecordStatus/>\r\n" + 
			"</LaunchStatus>\r\n" + 
			"<CASIS.MDNUMBER>MD000001</CASIS.MDNUMBER>\r\n" + 
			"</Document>\r\n" + 
			"";
	@Test
	public void convertStringToXmlDocumnetTest() throws Exception {
		
		Document convertStringToXmlDocumnet = xmlUtil.convertStringToXmlDocumnet(xmlString);
		String expectValue = prettyPrint(convertStringToXmlDocumnet);
		Assert.assertTrue(expectValue.toString().contains(xmlString));
	}
	
	@Test
	public void renameXmlTagsTest() throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, ParserConfigurationException, SAXException, IOException {
		
		String result = xmlUtil.renameTagsDotToMinus(xmlString);
	
		String expectResult = "<Document><CASIS-DOCNO>DGL1235365</CASIS-DOCNO><CASIS-UPD><PublicationDate CCYYMMDD=\"20011128\">28 Nov 2001</PublicationDate></CASIS-UPD><LaunchDetails><LaunchDateComment/><NewChemicalEntity/><Ingredients><Ingredient>glyceryl trinitrate</Ingredient></Ingredients><CASIS-USE><CASIS-ACT><Class><ClassCode>C1E</ClassCode><ClassDescription>Nitrates and Nitrites</ClassDescription></Class></CASIS-ACT><CASIS-IND><Indication>Prevention and treatment of angina pectoris</Indication></CASIS-IND></CASIS-USE><CASIS-CN><BrandName>ANGIOCARD</BrandName></CASIS-CN><CASIS-CO><CASIS-NORMALIZED-CO>Manufacturer: Biogenics</CASIS-NORMALIZED-CO><CASIS-NORMALIZED-CO>BIOGENICS</CASIS-NORMALIZED-CO><Manufacturer>Biogenics</Manufacturer><Corporation>BIOGENICS</Corporation></CASIS-CO><CASIS-DSTA><CASIS-NORMALIZED-DSTA>PK: Launched 20010601</CASIS-NORMALIZED-DSTA><Country>Pakistan</Country><LaunchDate CCYYMM=\"20010601\">01 Jun 2001</LaunchDate></CASIS-DSTA><CASIS-TX><Biotech>No</Biotech><Unbranded>No</Unbranded></CASIS-TX><CASIS-RN><CASInfo><CASItem/></CASInfo></CASIS-RN></LaunchDetails><PackInfo><ExcipientInfo><Excipient/></ExcipientInfo><PriceInfo><Price>caps retard a 30: PKR 84.150 (RPP)</Price><Price>caps retard b 30: PKR 109.650 (RPP)</Price></PriceInfo><DoseFormInfo><DoseForm>caps retard</DoseForm></DoseFormInfo><NumberOfIngredients>1</NumberOfIngredients><CASIS-TX><CompositionInfo><Composition>caps retard a: glyceryl trinitrate, 2.5 mg</Composition><Composition>caps retard b: glyceryl trinitrate, 6.5 mg</Composition></CompositionInfo></CASIS-TX></PackInfo><LaunchStatus><RecordStatus/></LaunchStatus><CASIS-MDNUMBER>MD000001</CASIS-MDNUMBER></Document>";
		
		Assert.assertTrue(result.equals(expectResult));
		
	}
	
	@Test
	public void getXmlKeyValuesPairsTest() throws ParserConfigurationException, SAXException, IOException, TransformerException, DocumentException {
		HashMap<String, ArrayList<String>> xmlKeyValuesPairs = xmlUtil.getXmlKeyValuesPairs(xmlString);
		String result= xmlKeyValuesPairs.toString();
		String expectResult = "{Document_CASIS.UPD_PublicationDate_CCYYMMDD=[20011128], Document_CASIS.UPD_PublicationDate=[28 Nov 2001], Document_LaunchDetails_CASIS.USE_CASIS.ACT_Class_ClassDescription=[Nitrates and Nitrites], Document_PackInfo_CASIS.TX_CompositionInfo_Composition=[caps retard a: glyceryl trinitrate, 2.5 mg, caps retard b: glyceryl trinitrate, 6.5 mg], Document_LaunchDetails_CASIS.CO_Corporation=[BIOGENICS], Document_PackInfo_ExcipientInfo_Excipient=[], Document_LaunchDetails_CASIS.DSTA_LaunchDate=[01 Jun 2001], Document_LaunchDetails_CASIS.CO_Manufacturer=[Biogenics], Document_PackInfo_DoseFormInfo_DoseForm=[caps retard], Document_LaunchDetails_CASIS.CN_BrandName=[ANGIOCARD], Document_LaunchDetails_LaunchDateComment=[], Document_LaunchDetails_CASIS.RN_CASInfo_CASItem=[], Document_CASIS.DOCNO=[DGL1235365], Document_PackInfo_PriceInfo_Price=[caps retard a 30: PKR 84.150 (RPP), caps retard b 30: PKR 109.650 (RPP)], Document_LaunchDetails_CASIS.DSTA_Country=[Pakistan], Document_LaunchDetails_CASIS.TX_Unbranded=[No], Document_LaunchDetails_CASIS.USE_CASIS.IND_Indication=[Prevention and treatment of angina pectoris], Document_LaunchDetails_CASIS.USE_CASIS.ACT_Class_ClassCode=[C1E], Document_LaunchDetails_CASIS.DSTA_LaunchDate_CCYYMM=[20010601], Document_PackInfo_NumberOfIngredients=[1], Document_LaunchDetails_Ingredients_Ingredient=[glyceryl trinitrate], Document_LaunchStatus_RecordStatus=[], Document_LaunchDetails_CASIS.CO_CASIS.NORMALIZED.CO=[Manufacturer: Biogenics, BIOGENICS], Document_LaunchDetails_CASIS.TX_Biotech=[No], Document_LaunchDetails_NewChemicalEntity=[], Document_LaunchDetails_CASIS.DSTA_CASIS.NORMALIZED.DSTA=[PK: Launched 20010601], Document_CASIS.MDNUMBER=[MD000001]}";
		Assert.assertTrue(result.equals(expectResult));
	}
	@Test
	public void X2Test() {
//		fail("Not yet implemented");
//		xmlUtil.renameXmlTags(null);
	}
	
	private  final String prettyPrint(Document xml) throws Exception {

		Transformer tf = TransformerFactory.newInstance().newTransformer();

		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

		tf.setOutputProperty(OutputKeys.INDENT, "yes");

		Writer out = new StringWriter();

		tf.transform(new DOMSource(xml), new StreamResult(out));

		return out.toString();

	}


}
