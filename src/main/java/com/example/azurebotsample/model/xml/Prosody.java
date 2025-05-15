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
public class Prosody {

    @XmlAttribute
    private String rate;

    @XmlAttribute
    private String pitch;

    @XmlAttribute
    private String volume;

    @XmlValue
    private String content; // The inner text to be spoken

}
