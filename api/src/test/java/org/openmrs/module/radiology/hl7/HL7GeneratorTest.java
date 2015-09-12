/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonName;
import org.openmrs.module.radiology.Modality;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.RequestedProcedurePriority;
import org.openmrs.module.radiology.Study;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;

/**
 * Tests methods in the {@link HL7Generator}
 */
public class HL7GeneratorTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private Patient patient = null;
	
	private Study study = null;
	
	private RadiologyOrder radiologyOrder = null;
	
	@Before
	public void runBeforeEachTest() throws Exception {
		
		patient = new Patient();
		patient.setPatientId(1);
		
		PatientIdentifierType patientIdentifierType = new PatientIdentifierType();
		patientIdentifierType.setPatientIdentifierTypeId(1);
		patientIdentifierType.setName("Test Identifier Type");
		patientIdentifierType.setDescription("Test description");
		PatientIdentifier patientIdentifier = new PatientIdentifier();
		patientIdentifier.setIdentifierType(patientIdentifierType);
		patientIdentifier.setIdentifier("100");
		patientIdentifier.setPreferred(true);
		Set<PatientIdentifier> patientIdentifiers = new HashSet<PatientIdentifier>();
		patientIdentifiers.add(patientIdentifier);
		patient.addIdentifiers(patientIdentifiers);
		
		patient.setGender("M");
		
		Set<PersonName> personNames = new HashSet<PersonName>();
		PersonName personName = new PersonName();
		personName.setFamilyName("Doe");
		personName.setGivenName("John");
		personName.setMiddleName("Francis");
		personNames.add(personName);
		patient.setNames(personNames);
		
		Calendar cal = Calendar.getInstance();
		cal.set(1950, Calendar.APRIL, 1, 0, 0, 0);
		patient.setBirthdate(cal.getTime());
		
		radiologyOrder = new RadiologyOrder();
		radiologyOrder.setId(20);
		radiologyOrder.setPatient(patient);
		cal.set(2015, Calendar.FEBRUARY, 4, 14, 35, 0);
		radiologyOrder.setStartDate(cal.getTime());
		radiologyOrder.setInstructions("CT ABDOMEN PANCREAS WITH IV CONTRAST");
		
		study = new Study();
		study.setStudyId(1);
		study.setRadiologyOrder(radiologyOrder);
		study.setStudyInstanceUid("1.2.826.0.1.3680043.8.2186.1.1");
		study.setModality(Modality.CT);
		study.setPriority(RequestedProcedurePriority.STAT);
	}
	
	/**
	 * Tests HL7Generator.createEncodedRadiologyORMO01Message
	 * 
	 * @throws HL7Exception
	 * @see {@link HL7Generator#createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should return encoded hl7 ormo01 message given all params including new order control code", method = "createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)")
	public void createEncodedRadiologyORMO01Message_shouldReturnEncodedHL7ORMO01MessageGivenAllParamsIncludingNewOrderControlCode()
	        throws HL7Exception {
		
		String encodedOrmMessage = HL7Generator.createEncodedRadiologyORMO01Message(study, radiologyOrder,
		    CommonOrderOrderControl.NEW_ORDER, CommonOrderPriority.STAT);
		
		assertThat(encodedOrmMessage, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    encodedOrmMessage,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|NW|1|||||^^^20150204143500^^S\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
	}
	
	/**
	 * Test HL7Generator.createEncodedRadiologyORMO01Message
	 * 
	 * @throws HL7Exception
	 * @see {@link HL7Generator#createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should return encoded hl7 ormo01 message given all params including change order control code", method = "createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)")
	public void createEncodedRadiologyORMO01Message_shouldReturnEncodedHL7ORMO01MessageGivenAllParamsIncludingChangeOrderControlCode()
	        throws HL7Exception {
		
		String encodedOrmMessage = HL7Generator.createEncodedRadiologyORMO01Message(study, radiologyOrder,
		    CommonOrderOrderControl.CHANGE_ORDER, CommonOrderPriority.STAT);
		
		assertThat(encodedOrmMessage, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    encodedOrmMessage,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|XO|1|||||^^^20150204143500^^S\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
	}
	
	/**
	 * Test HL7Generator.createEncodedRadiologyORMO01Message
	 * 
	 * @throws HL7Exception
	 * @see {@link HL7Generator#createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should return encoded hl7 ormo01 message given all params including cancel order control code", method = "createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)")
	public void createEncodedRadiologyORMO01Message_shouldReturnEncodedHL7ORMO01MessageGivenAllParamsIncludingCancelOrderControlCode()
	        throws HL7Exception {
		
		String encodedOrmMessage = HL7Generator.createEncodedRadiologyORMO01Message(study, radiologyOrder,
		    CommonOrderOrderControl.CANCEL_ORDER, CommonOrderPriority.STAT);
		
		assertThat(encodedOrmMessage, startsWith("MSH|^~\\&|OpenMRSRadiologyModule|OpenMRS|||"));
		assertThat(
		    encodedOrmMessage,
		    endsWith("||ORM^O01||P|2.3.1\r"
		            + "PID|||100||Doe^John^Francis||19500401000000|M\r"
		            + "ORC|CA|1|||||^^^20150204143500^^S\r"
		            + "OBR||||^^^^CT ABDOMEN PANCREAS WITH IV CONTRAST|||||||||||||||1|1||||CT||||||||||||||||||||^CT ABDOMEN PANCREAS WITH IV CONTRAST\r"
		            + "ZDS|1.2.826.0.1.3680043.8.2186.1.1^^Application^DICOM\r"));
	}
	
	/**
	 * Test HL7Generator.createEncodedRadiologyORMO01Message
	 * 
	 * @throws HL7Exception
	 * @see {@link HL7Generator#createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should fail given null as study", method = "createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)")
	public void createEncodedRadiologyORMO01Message_shouldFailGivenNullAsStudy() throws HL7Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("study cannot be null."));
		HL7Generator.createEncodedRadiologyORMO01Message(null, radiologyOrder, CommonOrderOrderControl.NEW_ORDER,
		    CommonOrderPriority.STAT);
	}
	
	/**
	 * Test HL7Generator.createEncodedRadiologyORMO01Message
	 * 
	 * @throws HL7Exception
	 * @see {@link HL7Generator#createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should fail given null as order", method = "createEncodedRadiologyORMO01Message(Study, Order, CommonOrderOrderControl, CommonOrderPriority)")
	public void createEncodedRadiologyORMO01Message_shouldFailGivenNullAsOrder() throws HL7Exception {
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("order cannot be null."));
		HL7Generator.createEncodedRadiologyORMO01Message(study, null, CommonOrderOrderControl.NEW_ORDER,
		    CommonOrderPriority.STAT);
	}
}
