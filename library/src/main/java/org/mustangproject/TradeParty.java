package org.mustangproject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mustangproject.ZUGFeRD.IZUGFeRDExportableContact;
import org.mustangproject.ZUGFeRD.IZUGFeRDExportableTradeParty;
import org.mustangproject.ZUGFeRD.IZUGFeRDLegalOrganisation;
import org.mustangproject.ZUGFeRD.IZUGFeRDTradeSettlement;
import org.mustangproject.ZUGFeRD.IZUGFeRDTradeSettlementDebit;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/***
 * A organisation, i.e. usually a company
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TradeParty implements IZUGFeRDExportableTradeParty {

	protected String name, zip, street, location, country;
	protected String taxID = null, vatID = null;
	protected String ID = null;
	protected String additionalAddress = null;
	protected String additionalAddressExtension = null;
	protected List<BankDetails> bankDetails = new ArrayList<>();
	protected List<IZUGFeRDTradeSettlementDebit> debitDetails = new ArrayList<>();
	protected Contact contact = null;
	protected LegalOrganisation legalOrg = null;
	protected SchemedID globalId=null;
	protected SchemedID uriUniversalCommunicationId=null; //e.g. the email address of the organization

	/**
	 * Default constructor.
	 * Probably a bad idea but might be needed by jackson or similar
	 */
	public TradeParty() {

	}


	/***
	 *
	 * @param name of the company
	 * @param street street and number (use setAdditionalAddress for more parts)
	 * @param zip postcode of the company
	 * @param location city of the company
	 * @param country two letter ISO code
	 */
	public TradeParty(String name, String street, String zip, String location, String country) {
		this.name = name;
		this.street = street;
		this.zip = zip;
		this.location = location;
		this.country = country;

	}

	protected void parseFromUBL(NodeList nodes) {
		if (nodes.getLength() > 0) {

			for (int nodeIndex = 0; nodeIndex < nodes.getLength(); nodeIndex++) {
				//nodes.item(i).getTextContent())) {
				Node currentItemNode = nodes.item(nodeIndex);
				NodeList itemChilds = currentItemNode.getChildNodes();
				for (int itemChildIndex = 0; itemChildIndex < itemChilds.getLength(); itemChildIndex++) {
					if (itemChilds.item(itemChildIndex).getLocalName() != null) {

						if (itemChilds.item(itemChildIndex).getLocalName().equals("Party")) {

							NodeList party = itemChilds.item(itemChildIndex).getChildNodes();
							for (int partyIndex = 0; partyIndex < party.getLength(); partyIndex++) {
								if (party.item(partyIndex).getLocalName() != null) {

									if (party.item(partyIndex).getLocalName().equals("PartyName")) {

										NodeList partyName = party.item(partyIndex).getChildNodes();
										for (int partyNameIndex = 0; partyNameIndex < partyName.getLength(); partyNameIndex++) {
											if (partyName.item(partyNameIndex).getLocalName() != null) {

												if (partyName.item(partyNameIndex).getLocalName().equals("Name")) {
													setName(partyName.item(partyNameIndex).getTextContent());
												}

											}
										}
									}
									if (party.item(partyIndex).getLocalName().equals("PostalAddress")) {

										NodeList postal = party.item(partyIndex).getChildNodes();
										for (int postalChildIndex = 0; postalChildIndex < postal.getLength(); postalChildIndex++) {
											if (postal.item(postalChildIndex).getLocalName() != null) {

												if (postal.item(postalChildIndex).getLocalName().equals("StreetName")) {
													setStreet(postal.item(postalChildIndex).getTextContent());
												}
												if (postal.item(postalChildIndex).getLocalName().equals("AdditionalStreetName")) {
													setAdditionalAddress(postal.item(postalChildIndex).getTextContent());
												}
												//unknow correspondence if (postal.item(postalChildIndex).getLocalName().equals("LineThree")) {


												if (postal.item(postalChildIndex).getLocalName().equals("CityName")) {
													setLocation(postal.item(postalChildIndex).getTextContent());
												}
												if (postal.item(postalChildIndex).getLocalName().equals("PostalZone")) {
													setZIP(postal.item(postalChildIndex).getTextContent());
												}
												if (postal.item(postalChildIndex).getLocalName().equals("Country")) {
													NodeList country = postal.item(postalChildIndex).getChildNodes();
													for (int countryIndex = 0; countryIndex < country.getLength(); countryIndex++) {
														if (country.item(countryIndex).getLocalName() != null) {

															if (country.item(countryIndex).getLocalName().equals("IdentificationCode")) {
																setCountry(country.item(countryIndex).getTextContent());
															}

														}
													}


												}

												if (postal.item(postalChildIndex).getLocalName().equals("AddressLine")) {
													NodeList AddressLine = postal.item(postalChildIndex).getChildNodes();
													for (int lineIndex = 0; lineIndex < AddressLine.getLength(); lineIndex++) {
														if (AddressLine.item(lineIndex).getLocalName() != null) {

															if (AddressLine.item(lineIndex).getLocalName().equals("Line")) {
																setAdditionalAddressExtension(AddressLine.item(lineIndex).getTextContent());
															}

														}
													}


												}
												if (postal.item(postalChildIndex).getLocalName().equals("Name")) {
													setName(postal.item(postalChildIndex).getTextContent());
												}

											}
										}
									}
								}
							}


						}

						if (itemChilds.item(itemChildIndex).getLocalName().equals("GlobalID")) {
							if (itemChilds.item(itemChildIndex).getAttributes().getNamedItem("schemeID") != null) {
								SchemedID gid=new SchemedID().setScheme(itemChilds.item(itemChildIndex).getAttributes().getNamedItem("schemeID").getNodeValue()).setId(itemChilds.item(itemChildIndex).getTextContent());
								addGlobalID(gid);
							}

						}
						if (itemChilds.item(itemChildIndex).getLocalName().equals("DefinedTradeContact")) {
							NodeList contact = itemChilds.item(itemChildIndex).getChildNodes();
							setContact(new Contact(contact));
						}

						if (itemChilds.item(itemChildIndex).getLocalName().equals("PostalTradeAddress")) {
							NodeList postal = itemChilds.item(itemChildIndex).getChildNodes();
							for (int postalChildIndex = 0; postalChildIndex < postal.getLength(); postalChildIndex++) {
								if (postal.item(postalChildIndex).getLocalName() != null) {
									if (postal.item(postalChildIndex).getLocalName().equals("LineOne")) {
										setStreet(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("LineTwo")) {
										setAdditionalAddress(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("LineThree")) {
										setAdditionalAddressExtension(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("CityName")) {
										setLocation(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("PostcodeCode")) {
										setZIP(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("CountryID")) {
										setCountry(postal.item(postalChildIndex).getTextContent());
									}

								}
							}

						}

						if (itemChilds.item(itemChildIndex).getLocalName().equals("SpecifiedTaxRegistration")) {
							NodeList taxChilds = itemChilds.item(itemChildIndex).getChildNodes();
							for (int taxChildIndex = 0; taxChildIndex < taxChilds.getLength(); taxChildIndex++) {
								if (taxChilds.item(taxChildIndex).getLocalName() != null) {
									if ((taxChilds.item(taxChildIndex).getLocalName().equals("ID"))) {
										if (taxChilds.item(taxChildIndex).getAttributes().getNamedItem("schemeID") != null) {
											Node firstChild = taxChilds.item(taxChildIndex).getFirstChild();
											if (firstChild != null)
											{
												if (taxChilds.item(taxChildIndex).getAttributes()
													.getNamedItem("schemeID").getNodeValue().equals("VA")) {
													setVATID(firstChild.getNodeValue());
												}
												if (taxChilds.item(taxChildIndex).getAttributes()
													.getNamedItem("schemeID").getNodeValue().equals("FC")) {
													setTaxID(firstChild.getNodeValue());
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

	}

	/***
	 * XML parsing constructor
	 * @param nodes the nodelist returned e.g. from xpath
	 */
	public TradeParty(NodeList nodes) {
/**
 * CII would be sth like
 * <ram:SellerTradeParty>
 *  <ram:ID>LIEF-987654321</ram:ID>
 *  <ram:Name>Max Mustermann IT GmbH</ram:Name>
 *  <ram:DefinedTradeContact>
 *  <ram:PersonName>Herr Treudiener</ram:PersonName>
 *  <ram:TelephoneUniversalCommunication>
 *  <ram:CompleteNumber>+49 1234-98765-12</ram:CompleteNumber>
 *  </ram:TelephoneUniversalCommunication>
 *  <ram:EmailURIUniversalCommunication>
 *  <ram:URIID>treudiener@max-mustermann-it.de</ram:URIID>
 *  </ram:EmailURIUniversalCommunication>
 *  </ram:DefinedTradeContact>
 *  <ram:PostalTradeAddress>
 *  <ram:PostcodeCode>12345</ram:PostcodeCode>
 *  <ram:LineOne>Musterstraße 1</ram:LineOne>
 *  <ram:CityName>Musterstadt</ram:CityName>
 *  <ram:CountryID>DE</ram:CountryID>
 *  </ram:PostalTradeAddress>
 *  <ram:SpecifiedTaxRegistration>
 *  <ram:ID schemeID="VA">DE123456789</ram:ID>
 *  </ram:SpecifiedTaxRegistration>
 *  </ram:SellerTradeParty>
 *  while UBL may be
 *  <cac:AccountingCustomerParty>
 *     <cac:Party>
 *       <cac:PartyName>
 *         <cbc:Name>Theodor Est</cbc:Name>
 *       </cac:PartyName>
 *       <cac:PostalAddress>
 *         <cbc:StreetName>Bahnstr. 42</cbc:StreetName>
 *         <cbc:AdditionalStreetName>Hinterhaus</cbc:AdditionalStreetName>
 *         <cbc:CityName>Spielkreis</cbc:CityName>
 *         <cbc:PostalZone>88802</cbc:PostalZone>
 *         <cac:AddressLine>
 *           <cbc:Line>Zweiter Stock</cbc:Line>
 *         </cac:AddressLine>
 *         <cac:Country>
 *           <cbc:IdentificationCode>DE</cbc:IdentificationCode>
 *         </cac:Country>
 *       </cac:PostalAddress>
 *       <cac:PartyTaxScheme>
 *         <cbc:CompanyID>DE999999999</cbc:CompanyID>
 *         <cac:TaxScheme>
 *           <cbc:ID>VAT</cbc:ID>
 *         </cac:TaxScheme>
 *       </cac:PartyTaxScheme>
 *       <cac:PartyLegalEntity>
 *         <cbc:RegistrationName>Theodor Est</cbc:RegistrationName>
 *       </cac:PartyLegalEntity>
 *     </cac:Party>
 *   </cac:AccountingCustomerParty>
 *
 *  */
		if (nodes.getLength() > 0) {

			for (int nodeIndex = 0; nodeIndex < nodes.getLength(); nodeIndex++) {
				//nodes.item(i).getTextContent())) {
				Node currentItemNode = nodes.item(nodeIndex);
				NodeList itemChilds = currentItemNode.getChildNodes();
				for (int itemChildIndex = 0; itemChildIndex < itemChilds.getLength(); itemChildIndex++) {
					if (itemChilds.item(itemChildIndex).getLocalName() != null) {

						if (itemChilds.item(itemChildIndex).getLocalName().equals("Party")) {
							parseFromUBL(nodes);
							return;
						}

						if (itemChilds.item(itemChildIndex).getLocalName().equals("Name")) {
							setName(itemChilds.item(itemChildIndex).getTextContent());
						}
						if (itemChilds.item(itemChildIndex).getLocalName().equals("GlobalID")) {
							if (itemChilds.item(itemChildIndex).getAttributes().getNamedItem("schemeID") != null) {
								SchemedID gid=new SchemedID().setScheme(itemChilds.item(itemChildIndex).getAttributes().getNamedItem("schemeID").getNodeValue()).setId(itemChilds.item(itemChildIndex).getTextContent());
								addGlobalID(gid);
							}

						}
						if (itemChilds.item(itemChildIndex).getLocalName().equals("DefinedTradeContact")) {
							NodeList contact = itemChilds.item(itemChildIndex).getChildNodes();
							setContact(new Contact(contact));
						}

						if (itemChilds.item(itemChildIndex).getLocalName().equals("PostalTradeAddress")) {
							NodeList postal = itemChilds.item(itemChildIndex).getChildNodes();
							for (int postalChildIndex = 0; postalChildIndex < postal.getLength(); postalChildIndex++) {
								if (postal.item(postalChildIndex).getLocalName() != null) {
									if (postal.item(postalChildIndex).getLocalName().equals("LineOne")) {
										setStreet(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("LineTwo")) {
										setAdditionalAddress(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("LineThree")) {
										setAdditionalAddressExtension(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("CityName")) {
										setLocation(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("PostcodeCode")) {
										setZIP(postal.item(postalChildIndex).getTextContent());
									}
									if (postal.item(postalChildIndex).getLocalName().equals("CountryID")) {
										setCountry(postal.item(postalChildIndex).getTextContent());
									}

								}
							}

						}

						if (itemChilds.item(itemChildIndex).getLocalName().equals("SpecifiedTaxRegistration")) {
							NodeList taxChilds = itemChilds.item(itemChildIndex).getChildNodes();
							for (int taxChildIndex = 0; taxChildIndex < taxChilds.getLength(); taxChildIndex++) {
								if (taxChilds.item(taxChildIndex).getLocalName() != null) {
									if ((taxChilds.item(taxChildIndex).getLocalName().equals("ID"))) {
										if (taxChilds.item(taxChildIndex).getAttributes().getNamedItem("schemeID") != null) {
											Node firstChild = taxChilds.item(taxChildIndex).getFirstChild();
											if (firstChild != null)
											{
												if (taxChilds.item(taxChildIndex).getAttributes()
														.getNamedItem("schemeID").getNodeValue().equals("VA")) {
													setVATID(firstChild.getNodeValue());
												}
												if (taxChilds.item(taxChildIndex).getAttributes()
														.getNamedItem("schemeID").getNodeValue().equals("FC")) {
													setTaxID(firstChild.getNodeValue());
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@Override
	public String getID() {
		return ID;
	}

	@Override
	public String getGlobalID() {
		if (globalId!=null) {
			return globalId.getID();
		}
		return null;
	}



	@Override
	public String getGlobalIDScheme() {
		if (globalId!=null) {
			return globalId.getScheme();
		}
		return null;

	}

  
	@Override
	public String getUriUniversalCommunicationID() {
		if (uriUniversalCommunicationId!=null) {
			return uriUniversalCommunicationId.getID();
		}
		return null;
	}



	@Override
	public String getUriUniversalCommunicationIDScheme() {
		if (uriUniversalCommunicationId!=null) {
			return uriUniversalCommunicationId.getScheme();
		}
		return null;

	}


	/***
	 * sets the email of the organization (not the one of the contact person)
	 * @return fluent setter
	 */
	public TradeParty setEmail(String eMail) {
		SchemedID theSchemedID=new SchemedID("EM", eMail);
		addUriUniversalCommunicationID(theSchemedID);
		return this;
	}

	/***
	 * gets the email of the organization (not the one of the contact person)
	 * will return null if not set or if the provided UriUniversalCommunicationID
	 * is not a email address
	 *
	 * @return String the email, or null
	 */
	public String getEmail() {
		if (uriUniversalCommunicationId.getScheme().equals("EM")) {
			return uriUniversalCommunicationId.getID();
		}
		return null;
	}


	/**
	 * if it's a customer, this can e.g. be the customer ID
	 *
	 * @param ID customer/seller number
	 * @return fluent setter
	 */
	public TradeParty setID(String ID) {
		this.ID = ID;
		return this;
	}

	/***
	 * (optional) a named contact person
	 * @see Contact
	 * @param c the named contact person
	 * @return fluent setter
	 */
	public TradeParty setContact(Contact c) {
		this.contact = c;
		return this;
	}

	public TradeParty addGlobalID(SchemedID schemedID) {
		globalId=schemedID;
		return this;
	}
  
	public TradeParty addUriUniversalCommunicationID(SchemedID schemedID) {
		uriUniversalCommunicationId=schemedID;
		return this;
	}

	/***
	 * required (for senders, if payment is not debit): the BIC and IBAN
	 * @param s bank credentials
	 * @return fluent setter
	 */
	public TradeParty addBankDetails(BankDetails s) {
		bankDetails.add(s);
		return this;
	}

	/**
	 * (optional)
	 *
	 * @param debitDetail e.g. containing IBAN and mandate
	 * @return fluent setter
	 */
	public TradeParty addDebitDetails(IZUGFeRDTradeSettlementDebit debitDetail) {
		debitDetails.add(debitDetail);
		return this;
	}

	@Override
	public IZUGFeRDLegalOrganisation getLegalOrganisation() {
		return legalOrg;
	}

	public TradeParty setLegalOrganisation(LegalOrganisation legalOrganisation) {
		legalOrg=legalOrganisation;
		return this;
	}

	public List<BankDetails> getBankDetails() {
		return bankDetails;
	}

	/***
	 * a general tax ID
	 * @param taxID tax number of the organisation
	 * @return fluent setter
	 */
	public TradeParty addTaxID(String taxID) {
		this.taxID = taxID;
		return this;
	}

	/***
	 * the USt-ID
	 * @param vatID Ust-ID
	 * @return fluent setter
	 */
	public TradeParty addVATID(String vatID) {
		this.vatID = vatID;
		return this;
	}

	@Override
	public String getVATID() {
		return vatID;
	}

	public TradeParty setVATID(String VATid) {
		this.vatID = VATid;
		return this;
	}

	@Override
	public String getTaxID() {
		return taxID;
	}

	public TradeParty setTaxID(String tax) {
		this.taxID = tax;
		return this;
	}

	public String getName() {
		return name;
	}


	/***
	 * required, usually done in the constructor: the complete name of the organisation
	 * @param name complete legal name
	 * @return fluent setter
	 */
	public TradeParty setName(String name) {
		this.name = name;
		return this;
	}


	public String getZIP() {
		return zip;
	}

	/***
	 * usually set in the constructor, required for recipients in german invoices: postcode
	 * @param zip postcode
	 * @return fluent setter
	 */
	public TradeParty setZIP(String zip) {
		this.zip = zip;
		return this;
	}

	@Override
	public String getStreet() {
		return street;
	}

	/***
	 * usually set in constructor, required in germany, street and house number
	 * @param street street name and number
	 * @return fluent setter
	 */
	public TradeParty setStreet(String street) {
		this.street = street;
		return this;
	}

	@Override
	public String getLocation() {
		return location;
	}

	/***
	 * usually set in constructor, usually required in germany, the city of the organisation
	 * @param location city
	 * @return fluent setter
	 */
	public TradeParty setLocation(String location) {
		this.location = location;
		return this;
	}

	@Override
	public String getCountry() {
		return country;
	}

	/***
	 * two-letter ISO code of the country
	 * @param country two-letter-code
	 * @return fluent setter
	 */
	public TradeParty setCountry(String country) {
		this.country = country;
		return this;
	}


	public String getVatID() {
		return vatID;
	}

	@Override
	public IZUGFeRDExportableContact getContact() {
		return contact;
	}

	public IZUGFeRDTradeSettlement[] getAsTradeSettlement() {
		if (bankDetails.isEmpty() && debitDetails.isEmpty()) {
			return null;
		}
		List<IZUGFeRDTradeSettlement> tradeSettlements = Stream.concat(bankDetails.stream(), debitDetails.stream())
				.map(IZUGFeRDTradeSettlement.class::cast)
				.collect(Collectors.toList());

		IZUGFeRDTradeSettlement[] result = new IZUGFeRDTradeSettlement[tradeSettlements.size()];
		for (int i = 0; i < tradeSettlements.size(); i++) {
			IZUGFeRDTradeSettlement izugFeRDTradeSettlement = tradeSettlements.get(i);
			result[i] = izugFeRDTradeSettlement;
		}
		return result;
	}

	@Override
	public String getAdditionalAddress() {
		return additionalAddress;
	}



	/***
	 * additional info of the address, e.g. which building or which floor.
	 * Street address will become "lineOne", this will become "lineTwo".
	 * (see setAdditionalAddressExtension for "lineThree")
	 * @param additionalAddress additional address description
	 * @return fluent setter
	 */
	public TradeParty setAdditionalAddress(String additionalAddress) {
		this.additionalAddress = additionalAddress;
		return this;
	}

	/***
	 * Sets two parts of additional address at once, e.g. which building and which floor
	 * (if building is not in question which floor can also be set with the first part only :-))
	 *
	 * @param additionalAddress1 first part of additional info, e.g. "Rear building"
	 * @param additionalAddress2 second part of additional address info, e.g. "2nd floor"
	 * @return fluent setter
	 */
	public TradeParty setAdditionalAddress(String additionalAddress1, String additionalAddress2) {
		this.additionalAddress = additionalAddress1;
		this.additionalAddressExtension = additionalAddress2;
		return this;
	}

	/***
	 * Sets even advanced additional address information,
	 * e.g. which floor (if LineTwo has already been used e.g. for which building)
	 * This could sometimes be BT-165?
	 *
	 * @param additionalAddress2 e.g. which floor, as String
	 * @return fluent setter
	 */
	public TradeParty setAdditionalAddressExtension(String additionalAddress2) {
		this.additionalAddressExtension = additionalAddress2;
		return this;
	}

	/***
	 * Returns even advanced additional address information,
	 * e.g. which floor (if LineTwo=setAdditionalAddress has already been used e.g. for which building)
	 * @return lineThree
	 */
	public String getAdditionalAddressExtension() {
		return this.additionalAddressExtension;
	}


}
