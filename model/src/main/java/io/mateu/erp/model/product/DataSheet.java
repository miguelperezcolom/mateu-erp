package io.mateu.erp.model.product;

import io.mateu.erp.model.multilanguage.Literal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 1/10/16.
 */
public class DataSheet {

    private Literal description;

    private List<FeatureValue> features = new ArrayList<>();
}
