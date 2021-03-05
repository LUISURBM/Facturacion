package co.gtd.entities;

import co.gtd.entities.Detalles;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2021-03-04T18:25:50")
@StaticMetamodel(Facturas.class)
public class Facturas_ { 

    public static volatile SingularAttribute<Facturas, Integer> numero;
    public static volatile SingularAttribute<Facturas, Long> impuestoTotal;
    public static volatile SingularAttribute<Facturas, Long> descuento;
    public static volatile SingularAttribute<Facturas, Long> descuentoTotal;
    public static volatile SingularAttribute<Facturas, String> nombres;
    public static volatile SingularAttribute<Facturas, String> dcuCliente;
    public static volatile SingularAttribute<Facturas, Date> fecha;
    public static volatile SingularAttribute<Facturas, Long> total;
    public static volatile SingularAttribute<Facturas, Long> iva;
    public static volatile SingularAttribute<Facturas, Long> subtotal;
    public static volatile SingularAttribute<Facturas, Integer> id;
    public static volatile ListAttribute<Facturas, Detalles> detallesList;
    public static volatile SingularAttribute<Facturas, String> tpoPago;

}