package com.example.azurebotsample.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://www.w3.org/2001/10/synthesis")
public class Voice {
    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name="express-as",namespace = "https://www.w3.org/2001/mstts", type = MsttsExpressAs.class)
    private MsttsExpressAs expressAs; // Field to hold the express-as element
}
