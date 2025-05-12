// Ensure this file is in the same package as your JAXB classes
@XmlSchema(
        xmlns = {
                // This line defines the prefix "mstts" for the mstts namespace
                @XmlNs(prefix = "mstts", namespaceURI = "https://www.w3.org/2001/mstts"),
                // This line defines the default namespace (empty prefix "")
                @XmlNs(prefix = "", namespaceURI = "http://www.w3.org/2001/10/synthesis")
        },
        // This ensures elements defined in the schema (like speak, voice) use the declared namespaces
        elementFormDefault = XmlNsForm.QUALIFIED,
        // Usually attributes don't need prefixes unless explicitly required
        attributeFormDefault = XmlNsForm.UNQUALIFIED
)
package com.example.azurebotsample.model.xml;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;
import jakarta.xml.bind.annotation.XmlSchema;