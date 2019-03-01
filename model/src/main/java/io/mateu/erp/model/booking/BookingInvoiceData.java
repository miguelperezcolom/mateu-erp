package io.mateu.erp.model.booking;

import io.mateu.erp.dispo.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.IOException;

@Embeddable
@Getter@Setter
public class BookingInvoiceData {

    private String companyName;
    private String vatId;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;


    @Override
    public String toString() {
        return "" + getCompanyName() + " " + getVatId() + " " + getAddress() + " " + getCity() + " " + getState() + " " + getCountry() + " " + getPostalCode();
    }

    @Override
    public boolean equals(Object obj) {
        try {
            return obj != null && Helper.toJson(this).equals(Helper.toJson(obj));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


}
