package io.mateu.erp.model.beroni;

import io.mateu.erp.model.booking.PurchaseOrder;
import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.mdd.core.util.Helper;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import java.time.LocalDate;

/**
 * Created by miguel on 1/6/17.
 */
@Getter
@Setter
public class P1105 extends BeroniRecord {

    public static final char CODIGOIVA_DENTROCEE = 'D';
    public static final char CODIGOIVA_FUERACEE = 'F';
    public static final char CODIGOIVA_MIXTO = 'M';

    public static final char REGIMENGENERAL_SI = 'S';
    public static final char REGIMENGENERAL_NO = 'N';

    private String serie;
    private long numeroReserva;
    private String codigoProveedorEnBeroni;
    private String nombreProveedor;
    private LocalDate fechaServicio;
    private String nombreViajeros;
    private int numeroPersonas;
    private String codigoProducto;
    private String codigoDestino;
    private String numeroBono;
    private double importeNeto;
    private char codigoIVA;
    private double totalDentroCEE;
    private char regimenGeneral = REGIMENGENERAL_SI;
    private double baseImponibleCosteSiRegimenGeneral;
    private double porcentajeIVACosteSiRegimenGeneral;
    private double baseImponibleVentaSiRegimenGeneral;
    private double porcentajeIVAVentaSiRegimenGeneral;
    private double IVARepercutido;
    private double totalVentaNoCEE;
    private double porcentajeDescuento;
    private double descuento;
    private double recargo;
    private double importeCuotaGestion;


    public P1105(EntityManager em, io.mateu.erp.model.config.AppConfig appconfig, Service s, PurchaseOrder po) {

        setNumeroReserva(s.getBooking().getId());
        setCodigoProveedorEnBeroni(po.getProvider().getIdInInvoicingApp());
        setNombreProveedor(po.getProvider().getName());
        setFechaServicio(s.getStart());
        setNombreViajeros(s.getBooking().getLeadName());
        setNumeroPersonas((s instanceof TransferService)?((TransferService) s).getPax():0);
        setNumeroBono("" + po.getId());
        setImporteNeto(po.getTotal());
        setTotalDentroCEE(po.getTotal());

        double baseCompra = Helper.roundOffEuros(po.getTotal() / (1d + 10d / 100d));
        double ivaCompra = Helper.roundOffEuros(po.getTotal() - baseCompra);

        /*
        double baseVenta = Helper.roundOffEuros(s.getTotalNetValue() / (1d + 10d / 100d));
        double ivaVenta = Helper.roundOffEuros(s.getTotalNetValue() - baseVenta);


        setBaseImponibleCosteSiRegimenGeneral(baseCompra);
        setPorcentajeIVACosteSiRegimenGeneral(10);
        setBaseImponibleVentaSiRegimenGeneral(baseVenta);
        setPorcentajeIVAVentaSiRegimenGeneral(10);
        setIVARepercutido(Helper.roundOffEuros(ivaVenta - ivaCompra));
        */


    }
}
