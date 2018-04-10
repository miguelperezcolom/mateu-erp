package io.mateu.erp.model.financials;

import lombok.Getter;
import lombok.Setter;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
public class CurrencyExchange implements Serializable {

    @Id
    @ManyToOne
    @NotNull
    private Currency from;

    @Id
    @ManyToOne
    @NotNull
    private Currency to;

    private double rate;

}
