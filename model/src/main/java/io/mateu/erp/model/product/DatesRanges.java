package io.mateu.erp.model.product;

import io.mateu.mdd.core.util.DatesRange;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DatesRanges implements Serializable {

    List<DatesRange> ranges = new ArrayList<>();

    public DatesRanges() {

    }

    public DatesRanges(List<DatesRange> l) {
        setRanges(l);
    }
}