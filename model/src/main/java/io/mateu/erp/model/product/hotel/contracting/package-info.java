@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(type=LocalDate.class,
                value=LocalDateAdapter.class),
})
package io.mateu.erp.model.product.hotel.contracting;

import io.mateu.erp.model.LocalDateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.LocalDate;