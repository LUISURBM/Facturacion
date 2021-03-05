package co.gtd.entities;

import co.gtd.entities.Detalles;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.6.1.v20150605-rNA", date="2021-03-04T18:25:50")
@StaticMetamodel(Productos.class)
public class Productos_ { 

    public static volatile SingularAttribute<Productos, Integer> precioUnitario;
    public static volatile SingularAttribute<Productos, Integer> id;
    public static volatile SingularAttribute<Productos, Integer> cantidad;
    public static volatile ListAttribute<Productos, Detalles> detallesList;
    public static volatile SingularAttribute<Productos, String> nombres;

}