package io.mateu.erp.client.financial;

import io.mateu.ui.core.client.app.AbstractArea;
import io.mateu.ui.core.client.app.AbstractModule;

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
    public List<AbstractModule> getModules() {
        return Arrays.asList(new FinancialModule());
    }
}
