package io.mateu.erp.model.beroni;

import io.mateu.erp.model.booking.Service;
import io.mateu.erp.model.booking.transfer.TransferService;
import io.mateu.erp.model.config.AppConfig;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Created by miguel on 1/6/17.
 */
@Getter@Setter
public class P1101 extends BeroniRecord {

    public static final char TIPOFACTURA_CARGO = 'C';
    public static final char TIPOFACTURA_ABONO = 'A';

    public static final char SEXO_VARON = 'V';
    public static final char SEXO_MUJER = 'M';

    public static final char EXTRANJERO_SI = 'S';
    public static final char EXTRANJERO_NO = 'N';

    public static final char PARTEVENTA_PAGODIRECTO = 'S';
    public static final char PARTEVENTA_FACTURA = 'N';


    private String oficina;
    private long numeroReserva;
    private int numeroFactura;
    private int numeroFacturaBSP;
    private String codigoEmpleadoVentas;
    private char tipoFactura = TIPOFACTURA_CARGO;
    private String serieFacturaRleacionada;
    private int numeroFacturaRelacionada;
    private LocalDate fechaFactura;
    private LocalDate fechaVencimiento;
    private LocalDate fechaReserva;
    private LocalDate fechaPeticionReserva;
    /* normalmente será el dni */
    private String codigoClienteEnBeroni;
    private String dni;
    private String tratamiento;
    private String primerApellido;
    private String segundoApellido;
    private String nombre;
    private String domicilio1;
    private String domicilio2;
    private String codigoPostal;
    private String Localidad;
    private String provincia;
    private String pais;
    private String telefono;
    private String otrosTelefonos;
    private String fax;
    private String movil;
    private String email;
    private char sexo = SEXO_VARON;
    private char extranjero = EXTRANJERO_NO;
    private char idioma = 'E';
    private String observaciones;
    private String observacionesCliente;
    private String contacto;
    private String nombreViajeros;
    private LocalDate fechaViaje;
    private int diasViaje;
    private int numeroPersonas;
    private String codigoProductoEnBeroni;
    private String codigoDestinoEnBeroni;
    private String codigoDepartamento;
    private String codigoSeccion;
    private String codigoSubseccion;
    private char parteVenta = PARTEVENTA_FACTURA;
    private double totalCobrado;
    private double totalFactura;
    private double porcentajeIVA;
    private String serieDepositoGarantia;
    private int numeroDepositoGarantia;
    private double importeDepositoGarantia;
    private String codigoFormaPagoEnBeroni;
    private double recargo;
    private int numeroNotaGastosComerciales;
    private double importeNotaGastosComerciales;
    private String codigoCampanyaEnBeroni;


    public P1101(EntityManager em, AppConfig appconfig, Service s) {

        setNumeroReserva(s.getBooking().getId());
        setFechaReserva(s.getBooking().getAudit().getCreated().toLocalDate());
        setFechaPeticionReserva(s.getBooking().getAudit().getCreated().toLocalDate());
        setCodigoClienteEnBeroni(s.getBooking().getAgency().getIdInInvoicingApp());
        setNombre(s.getBooking().getAgency().getName());
        setObservaciones(s.getPrivateComment());
        setObservacionesCliente(s.getComment());
        setNombreViajeros(s.getBooking().getLeadName());
        setFechaViaje(s.getStart());
        setDiasViaje((int)DAYS.between(s.getStart(), s.getFinish()));
        setNumeroPersonas((s instanceof TransferService)?((TransferService) s).getPax():0);
        setTotalFactura(s.getTotal());
        setPorcentajeIVA(10);

    }


}
