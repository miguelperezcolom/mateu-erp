package io.mateu.erp.dispo;

import io.mateu.erp.dispo.interfaces.product.IBoard;
import lombok.Getter;
import lombok.Setter;
import org.easytravelapi.common.Amount;
import org.easytravelapi.hotel.BoardPrice;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class DesglosePrecios {

    private IBoard board;
    private boolean onRequest;
    private String onRequestText;

    private Map<LineaReserva, ValoracionLineaReserva> valoracionLineas = new HashMap<>();

    private double total;

    public DesglosePrecios(IBoard board) {
        this.board = board;
    }


    public BoardPrice toBoardPrice() {

        setTotal(totalizar());


        // registrar precio
        BoardPrice p = new BoardPrice();

        p.setBoardBasisId(board.getCode());
        p.setBoardBasisName(board.getName());
        p.setNetPrice(new Amount("EUR", Helper.roundEuros(total)));

        p.setOnRequest(onRequest);
        p.setOnRequestText(onRequestText);

        return p;
    }

    private double totalizar() {
        double t = 0;

        for (ValoracionLineaReserva v : getValoracionLineas().values()) {
            v.totalizar();
            t += v.getTotal();
        }

        return Helper.roundEuros(t);
    }
}
