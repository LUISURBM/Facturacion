package co.gtd.entities;

import co.gtd.entities.Facturas;
import co.gtd.entities.Productos;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2021-03-04T18:25:50")
@StaticMetamodel(Detalles.class)
public class Detalles_ { 

    public static volatile SingularAttribute<Detalles, Integer> precioUnitario;
    public static volatile SingularAttribute<Detalles, Facturas> facturaId;
    public static volatile SingularAttribute<Detalles, Productos> productoId;
    public static volatile SingularAttribute<Detalles, Integer> id;
    public static volatile SingularAttribute<Detalles, Integer> cantidad;

}