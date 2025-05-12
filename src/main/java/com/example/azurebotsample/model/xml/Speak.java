package com.example.azurebotsample.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlRootElement(name = "speak", namespace = "http://www.w3.org/2001/10/synthesis")
@XmlAccessorType(XmlAccessType.FIELD)
public class Speak {
    @XmlAttribute(name = "version")
    private String version;

    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    private String xmlLang; // Attribute with xml: prefix

    @XmlElement(name = "voice", namespace = "http://www.w3.org/2001/10/synthesis")
    private Voice voice;
}
