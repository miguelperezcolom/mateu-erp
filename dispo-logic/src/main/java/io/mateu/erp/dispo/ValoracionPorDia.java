package io.mateu.erp.dispo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class ValoracionPorDia {

    private CondicionesPorDia condiciones = new CondicionesPorDia();

    private ImportePorDia importes = new ImportePorDia();
}
