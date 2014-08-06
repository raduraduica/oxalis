package eu.peppol.document.parsers;

import eu.peppol.document.PlainUBLParser;
import eu.peppol.identifier.ParticipantId;
import eu.peppol.identifier.SchemeId;
import org.w3c.dom.Element;

/**
 * Parser to retrieves information from PEPPOL Catalogue scenarios.
 * Should be able to decode Catalogue and catalogue response (ApplicationResponse)
 *
 * @author thore
 */
public class OrderDocumentParser implements PEPPOLDocumentParser {

    private PlainUBLParser parser;

    public OrderDocumentParser(PlainUBLParser parser) {
        this.parser = parser;
    }

    @Override
    public ParticipantId getSender() {
        String xpath = "//cac:BuyerCustomerParty/cac:Party/cbc:EndpointID";
        if ("OrderResponse".equalsIgnoreCase(parser.localName())) {
            xpath = "//cac:SellerSupplierParty/cac:Party/cbc:EndpointID";
        }
        return participantId(xpath);
    }

    @Override
    public ParticipantId getReceiver() {
        String xpath = "//cac:SellerSupplierParty/cac:Party/cbc:EndpointID";
        if ("OrderResponse".equalsIgnoreCase(parser.localName())) {
            xpath = "//cac:BuyerCustomerParty/cac:Party/cbc:EndpointID";
        }
        return participantId(xpath);
    }

    /**
     * Retrieves the ParticipantId which is held in an XML element, retrieved using the supplied XPath.
     *
     * @param xPathExpr
     * @return
     */
    private ParticipantId participantId(String xPathExpr) {
        Element element = parser.retrieveElementForXpath(xPathExpr);
        String schemeIdTextValue = element.getAttribute("schemeID").trim();
        String companyId = element.getFirstChild().getNodeValue().trim();
        if (companyId == null) throw new IllegalStateException("Unable to locate participant from xpath : " + xPathExpr);
        if (schemeIdTextValue.length() > 0) companyId = SchemeId.parse(schemeIdTextValue).getIso6523Icd() + ":" + companyId;
        return new ParticipantId(companyId);
    }

}