package com.example.azurebotsample.model.xml;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
// Define the element type and its namespace
@XmlType(namespace = "https://www.w3.org/2001/mstts")
public class MsttsExpressAs {
    /*@XmlAttribute(name="role")
    private String role;*/
    @XmlAttribute(name = "style")
    private String style;

    @XmlAttribute(name = "styledegree")
    private String styledegree; // Changed from styleDegree for attribute name matching

    @XmlValue // Maps this field to the text content of the element
    private String content;
}
