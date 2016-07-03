package com.osthus.casis.index;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.dom4j.DocumentException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.google.common.collect.Multimap;
import com.osthus.casis.index.ioc.ElasticSearchIocModule;

import de.osthus.ambeth.ioc.annotation.Autowired;
import de.osthus.ambeth.testutil.AbstractIocTest;
import de.osthus.ambeth.testutil.TestFrameworkModule;

@TestFrameworkModule(value = ElasticSearchIocModule.class)
public class JsonUtilTest extends AbstractIocTest {

	private Connection conn;

	@Autowired
	protected JsonUtil jsonUtilService;

	@Before
	public void before() {
		conn = DBManager.getConn();
	}

	@Test
	public void getOracleInValuesTest() {
		// prepare data
		JSONArray resultSetToJson = new JSONArray();
		JSONObject jsonDocument1 = new JSONObject("{\"DOCNO\":\"1\"}");
		JSONObject jsonDocument2 = new JSONObject("{\"DOCNO\":\"2\"}");
		resultSetToJson.put(jsonDocument1);
		resultSetToJson.put(jsonDocument2);
		String expectString = "'1','2'";
		// run app
		String resultString = jsonUtilService.getOracleInValues(resultSetToJson);

		// check result
		Assert.assertEquals(expectString, resultString);

	}

	@Test
	public void resultSetToJsonDocumentTest()
			throws SQLException, TransformerConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, ParserConfigurationException, SAXException, IOException, DocumentException {

		// prepare data
		String sql = "SELECT * FROM CASIS_DOCUMENT where DOCNO in ('DGL1319896','DGL1525654')";
		JSONArray resultString = null;

		try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rset = stmt.executeQuery();) {
			// run app
			resultString = jsonUtilService.resultSetToJsonDocument(rset);
		}

		// check result
		String expectString = "[{\"PART\":\"8\",\"UPD\":\"20060528\",\"VERSION\":\"1\",\"DOCUMENT\":{\"Document_CASIS-DOCNO\":[\"DGL1319896\"],\"Document_CASIS-UPD_PublicationDate\":[\"28 May 2006\"],\"Document_LaunchDetails_CASIS-CO_CASIS-NORMALIZED-CO\":[\"Manufacturer: Arrow Farmaceutica\",\"ARROW FARMACEUTICA\"],\"Document_LaunchDetails_CASIS-TX_Biotech\":[\"No\"],\"Document_PackInfo_ExcipientInfo_Excipient\":[\"\"],\"Document_LaunchDetails_CASIS-USE_CASIS-ACT_Class_ClassDescription\":[\"Topical Corticosteroid Combinations\"],\"Document_LaunchDetails_CASIS-CN_BrandName\":[\"AC TR+S.N+GR+NI.MG\"],\"Document_PackInfo_DoseFormInfo_DoseForm\":[\"ointment topical\",\"cream topical\"],\"Document_PackInfo_CASIS-TX_CompositionInfo_Composition\":[\"cream topical: gramicidin; neomycin; nystatin; triamcinolone acetonide\",\"ointment topical: gramicidin; neomycin; nystatin; triamcinolone acetonide\"],\"Document_LaunchDetails_CASIS-USE_CASIS-ACT_Class_ClassCode\":[\"D7B\"],\"Document_LaunchDetails_LaunchDateComment\":[\"\"],\"Document_PackInfo_PriceInfo_Price\":[\"cream topical 30 g 1: BRL 11.230 (RPP)\",\"ointment topical 30 g 1: BRL 11.230 (RPP)\"],\"Document_LaunchDetails_CASIS-CO_Manufacturer\":[\"Arrow Farmaceutica\"],\"Document_LaunchDetails_CASIS-DSTA_LaunchDate_CCYYMM\":[\"20060101\"],\"Document_LaunchDetails_CASIS-DSTA_LaunchDate\":[\"01 Jan 2006\"],\"Document_LaunchDetails_CASIS-CO_Corporation\":[\"ARROW FARMACEUTICA\"],\"Document_LaunchDetails_CASIS-RN_CASInfo_CASItem\":[\"\"],\"Document_LaunchDetails_CASIS-USE_CASIS-IND_Indication\":[\"Inflammation and pruriginous dermatoses associated with bacterial infections.\"],\"Document_LaunchDetails_CASIS-DSTA_Country\":[\"Brazil\"],\"Document_LaunchDetails_CASIS-DSTA_CASIS-NORMALIZED-DSTA\":[\"BR: Launched 20060101\"],\"Document_PackInfo_NumberOfIngredients\":[\"4\"],\"Document_LaunchDetails_CASIS-TX_Unbranded\":[\"Yes\"],\"Document_CASIS-UPD_PublicationDate_CCYYMMDD\":[\"20060528\"],\"Document_LaunchDetails_Ingredients_Ingredient\":[\"gramicidin\",\"neomycin\",\"nystatin\",\"triamcinolone acetonide\"],\"Document_LaunchStatus_RecordStatus\":[\"\"],\"Document_CASIS-MDNUMBER\":[\"MD000001\"],\"DOCUMENT_COMPLETETEXT\":[\"<Document><CASIS-DOCNO>DGL1319896<\\/CASIS-DOCNO><CASIS-UPD><PublicationDate CCYYMMDD=\\\"20060528\\\">28 May 2006<\\/PublicationDate><\\/CASIS-UPD><LaunchDetails><LaunchDateComment/><NewChemicalEntity/><Ingredients><Ingredient>gramicidin<\\/Ingredient><Ingredient>neomycin<\\/Ingredient><Ingredient>nystatin<\\/Ingredient><Ingredient>triamcinolone acetonide<\\/Ingredient><\\/Ingredients><CASIS-USE><CASIS-ACT><Class><ClassCode>D7B<\\/ClassCode><ClassDescription>Topical Corticosteroid Combinations<\\/ClassDescription><\\/Class><\\/CASIS-ACT><CASIS-IND><Indication>Inflammation and pruriginous dermatoses associated with bacterial infections.<\\/Indication><\\/CASIS-IND><\\/CASIS-USE><CASIS-CN><BrandName>AC TR+S.N+GR+NI.MG<\\/BrandName><\\/CASIS-CN><CASIS-CO><CASIS-NORMALIZED-CO>Manufacturer: Arrow Farmaceutica<\\/CASIS-NORMALIZED-CO><CASIS-NORMALIZED-CO>ARROW FARMACEUTICA<\\/CASIS-NORMALIZED-CO><Manufacturer>Arrow Farmaceutica<\\/Manufacturer><Corporation>ARROW FARMACEUTICA<\\/Corporation><\\/CASIS-CO><CASIS-DSTA><CASIS-NORMALIZED-DSTA>BR: Launched 20060101<\\/CASIS-NORMALIZED-DSTA><Country>Brazil<\\/Country><LaunchDate CCYYMM=\\\"20060101\\\">01 Jan 2006<\\/LaunchDate><\\/CASIS-DSTA><CASIS-TX><Biotech>No<\\/Biotech><Unbranded>Yes<\\/Unbranded><\\/CASIS-TX><CASIS-RN><CASInfo><CASItem/><\\/CASInfo><\\/CASIS-RN><\\/LaunchDetails><PackInfo><ExcipientInfo><Excipient/><\\/ExcipientInfo><PriceInfo><Price>cream topical 30 g 1: BRL 11.230 (RPP)<\\/Price><Price>ointment topical 30 g 1: BRL 11.230 (RPP)<\\/Price><\\/PriceInfo><DoseFormInfo><DoseForm>ointment topical<\\/DoseForm><DoseForm>cream topical<\\/DoseForm><\\/DoseFormInfo><NumberOfIngredients>4<\\/NumberOfIngredients><CASIS-TX><CompositionInfo><Composition>cream topical: gramicidin; neomycin; nystatin; triamcinolone acetonide<\\/Composition><Composition>ointment topical: gramicidin; neomycin; nystatin; triamcinolone acetonide<\\/Composition><\\/CompositionInfo><\\/CASIS-TX><\\/PackInfo><LaunchStatus><RecordStatus/><\\/LaunchStatus><CASIS-MDNUMBER>MD000001<\\/CASIS-MDNUMBER><\\/Document>\"],\"Document_LaunchDetails_NewChemicalEntity\":[\"\"]},\"DOCNO\":\"DGL1319896\",\"DATEINSERTED\":\"2016-03-03 09:20:22\",\"ID\":\"2541215\",\"SRC_DB\":\"DGL\",\"UPDATE_TIMESTAMP\":\"2016-06-16 12:29:10\"},{\"PART\":\"8\",\"UPD\":\"20150528\",\"VERSION\":\"1\",\"DOCUMENT\":{\"Document_CASIS-DOCNO\":[\"DGL1525654\"],\"Document_CASIS-UPD_PublicationDate\":[\"28 May 2015\"],\"Document_LaunchDetails_CASIS-CO_CASIS-NORMALIZED-CO\":[\"Manufacturer: LUNDBECK\",\"LUNDBECK\"],\"Document_LaunchDetails_CASIS-TX_Biotech\":[\"No\"],\"Document_PackInfo_ExcipientInfo_Excipient\":[\"\"],\"Document_LaunchDetails_CASIS-USE_CASIS-ACT_Class_ClassDescription\":[\"Anti-depressants And Mood Stabilisers\"],\"Document_LaunchDetails_CASIS-CN_BrandName\":[\"BRINTELLIX\"],\"Document_PackInfo_DoseFormInfo_DoseForm\":[\"tabs film-coated\"],\"Document_PackInfo_CASIS-TX_CompositionInfo_Composition\":[\"tabs film-coated a: vortioxetine hydrobromide, 10 mg\",\"tabs film-coated b: vortioxetine hydrobromide, 10 mg\",\"tabs film-coated c: vortioxetine hydrobromide, 20 mg\",\"tabs film-coated d: vortioxetine hydrobromide, 20 mg\"],\"Document_LaunchDetails_CASIS-USE_CASIS-ACT_Class_ClassCode\":[\"N6A\"],\"Document_LaunchDetails_LaunchDateComment\":[\"\"],\"Document_PackInfo_PriceInfo_Price\":[\"tabs film-coated a 28: EUR 32.630 (RPP)\",\"tabs film-coated b 98: EUR 102.900 (RPP)\",\"tabs film-coated c 28: EUR 52.870 (RPP)\",\"tabs film-coated d 98: EUR 166.700 (RPP)\"],\"Document_LaunchDetails_CASIS-CO_Manufacturer\":[\"LUNDBECK\"],\"Document_LaunchDetails_CASIS-DSTA_LaunchDate_CCYYMM\":[\"20150301\"],\"Document_LaunchDetails_CASIS-DSTA_LaunchDate\":[\"01 Mar 2015\"],\"Document_LaunchDetails_CASIS-CO_Corporation\":[\"LUNDBECK\"],\"Document_LaunchDetails_CASIS-RN_CASInfo_CASItem\":[\"\"],\"Document_LaunchDetails_CASIS-USE_CASIS-IND_Indication\":[\"Depression.\"],\"Document_LaunchDetails_CASIS-DSTA_Country\":[\"Finland\"],\"Document_LaunchDetails_CASIS-DSTA_CASIS-NORMALIZED-DSTA\":[\"SF: Launched 20150301\"],\"Document_PackInfo_NumberOfIngredients\":[\"1\"],\"Document_LaunchDetails_CASIS-TX_Unbranded\":[\"No\"],\"Document_CASIS-UPD_PublicationDate_CCYYMMDD\":[\"20150528\"],\"Document_LaunchDetails_Ingredients_Ingredient\":[\"vortioxetine\"],\"Document_LaunchStatus_RecordStatus\":[\"\"],\"Document_CASIS-MDNUMBER\":[\"MD000001\"],\"DOCUMENT_COMPLETETEXT\":[\"<Document><CASIS-DOCNO>DGL1525654<\\/CASIS-DOCNO><CASIS-UPD><PublicationDate CCYYMMDD=\\\"20150528\\\">28 May 2015<\\/PublicationDate><\\/CASIS-UPD><LaunchDetails><LaunchDateComment/><NewChemicalEntity/><Ingredients><Ingredient>vortioxetine<\\/Ingredient><\\/Ingredients><CASIS-USE><CASIS-ACT><Class><ClassCode>N6A<\\/ClassCode><ClassDescription>Anti-depressants And Mood Stabilisers<\\/ClassDescription><\\/Class><\\/CASIS-ACT><CASIS-IND><Indication>Depression.<\\/Indication><\\/CASIS-IND><\\/CASIS-USE><CASIS-CN><BrandName>BRINTELLIX<\\/BrandName><\\/CASIS-CN><CASIS-CO><CASIS-NORMALIZED-CO>Manufacturer: LUNDBECK<\\/CASIS-NORMALIZED-CO><CASIS-NORMALIZED-CO>LUNDBECK<\\/CASIS-NORMALIZED-CO><Manufacturer>LUNDBECK<\\/Manufacturer><Corporation>LUNDBECK<\\/Corporation><\\/CASIS-CO><CASIS-DSTA><CASIS-NORMALIZED-DSTA>SF: Launched 20150301<\\/CASIS-NORMALIZED-DSTA><Country>Finland<\\/Country><LaunchDate CCYYMM=\\\"20150301\\\">01 Mar 2015<\\/LaunchDate><\\/CASIS-DSTA><CASIS-TX><Biotech>No<\\/Biotech><Unbranded>No<\\/Unbranded><\\/CASIS-TX><CASIS-RN><CASInfo><CASItem/><\\/CASInfo><\\/CASIS-RN><\\/LaunchDetails><PackInfo><ExcipientInfo><Excipient/><\\/ExcipientInfo><PriceInfo><Price>tabs film-coated a 28: EUR 32.630 (RPP)<\\/Price><Price>tabs film-coated b 98: EUR 102.900 (RPP)<\\/Price><Price>tabs film-coated c 28: EUR 52.870 (RPP)<\\/Price><Price>tabs film-coated d 98: EUR 166.700 (RPP)<\\/Price><\\/PriceInfo><DoseFormInfo><DoseForm>tabs film-coated<\\/DoseForm><\\/DoseFormInfo><NumberOfIngredients>1<\\/NumberOfIngredients><CASIS-TX><CompositionInfo><Composition>tabs film-coated a: vortioxetine hydrobromide, 10 mg<\\/Composition><Composition>tabs film-coated b: vortioxetine hydrobromide, 10 mg<\\/Composition><Composition>tabs film-coated c: vortioxetine hydrobromide, 20 mg<\\/Composition><Composition>tabs film-coated d: vortioxetine hydrobromide, 20 mg<\\/Composition><\\/CompositionInfo><\\/CASIS-TX><\\/PackInfo><LaunchStatus><RecordStatus/><\\/LaunchStatus><CASIS-MDNUMBER>MD000001<\\/CASIS-MDNUMBER><\\/Document>\"],\"Document_LaunchDetails_NewChemicalEntity\":[\"\"]},\"DOCNO\":\"DGL1525654\",\"DATEINSERTED\":\"2016-03-03 09:22:21\",\"ID\":\"2971151\",\"SRC_DB\":\"DGL\",\"UPDATE_TIMESTAMP\":\"2016-06-16 12:29:10\"}]";
		Assert.assertEquals(expectString, resultString.toString());
	}

	@Test
	public void resultTOMapTest()
			throws SQLException, TransformerConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, ParserConfigurationException, SAXException, IOException, DocumentException {
		String nos = "'PD022697','DGL1525654'";

		String sqlCompany = "select src_db,co, COUNTRY,STATUS, DOCNO from CASIS_COMPANY where DOCNO in(" + nos + ")";
		Map<String, JSONArray> multiMap = null;

		try (PreparedStatement ps = conn.prepareStatement(sqlCompany); ResultSet resultSet = ps.executeQuery();) {
			multiMap = jsonUtilService.resultTOMap(resultSet);
		}

		String expectString = "{DGL1525654=[{\"STATUS\":\"CORPORATION\",\"COUNTRY\":\"\",\"SRC_DB\":\"DGL\",\"CO\":\"LUNDBECK\"},{\"STATUS\":\"MANUFACTURER\",\"COUNTRY\":\"\",\"SRC_DB\":\"DGL\",\"CO\":\"LUNDBECK\"}], PD022697=[{\"STATUS\":\"Originator\",\"COUNTRY\":\"\",\"SRC_DB\":\"PHP\",\"CO\":\"Sanofi\"},{\"STATUS\":\"Licensee\",\"COUNTRY\":\"\",\"SRC_DB\":\"PHP\",\"CO\":\"PPD\"}]}";
		Assert.assertEquals(expectString, multiMap.toString()); // oricle no sequence 

	}

	@Test
	public void resultTOMapLinksTest()
			throws SQLException, TransformerConfigurationException, TransformerFactoryConfigurationError,
			TransformerException, ParserConfigurationException, SAXException, IOException, DocumentException {
		String nos = "'PD022697','DGL1525654'";
		String sqlDslCrm = "SELECT dsl.src_db,CRM.SRC_DB, dsl.MDNUMBER,dsl.DOCNO ,CRM.CDBREGNO,CRM.MOLWEIGHT,CRM.MOLDATE,CRM.MOLNAME,CRM.MOLFORMULA,dsl.CASNO "
				+ "from DOC_STRUC_LINK dsl " + "LEFT OUTER JOIN CASIS_RCG_MOLTABLE CRM "
				+ "ON CRM.MDNUMBER = dsl.MDNUMBER " + "where DOCNO in(" + nos + ")";
		Map<String, JSONArray> multiMap = null;
		try (PreparedStatement ps = conn.prepareStatement(sqlDslCrm); ResultSet resultSet = ps.executeQuery();) {
			multiMap = jsonUtilService.resultTOMap(resultSet);
		}
		String expectString = "{DGL1525654=[{\"MOLWEIGHT\":\"0\",\"MOLDATE\":\"2004-06-14 10:11:00\",\"MOLFORMULA\":\"\",\"MOLNAME\":\"\",\"CASNO\":\"\",\"SRC_DB\":\"DGL\",\"MDNUMBER\":\"MD000001    \",\"CDBREGNO\":\"1\"}], PD022697=[{\"MOLWEIGHT\":\"0\",\"MOLDATE\":\"2004-06-14 10:11:00\",\"MOLFORMULA\":\"\",\"MOLNAME\":\"\",\"CASNO\":\"\",\"SRC_DB\":\"PHP\",\"MDNUMBER\":\"MD000001    \",\"CDBREGNO\":\"1\"}]}";
		Assert.assertEquals(expectString, multiMap.toString());

	}

	// PD022697

	@After
	public void after() {
		DBManager.closeConn(conn);
	}
}
