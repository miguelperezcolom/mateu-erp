package io.mateu.erp.client.financial;

import io.mateu.mdd.core.app.AbstractArea;
import io.mateu.mdd.core.app.AbstractModule;

import java.util.Arrays;
import java.util.List;

/**
 * Created by miguel on 3/1/17.
 */
public class FinancialArea extends AbstractArea {

    public FinancialArea() {
        super("Financial");
    }

    @Override
    public List<AbstractModule> buildModules() {
        return Arrays.asList(new FinancialModule());
    }
}
