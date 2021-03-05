/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gtd.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author lurbina
 */
@Entity
@Table(name = "DETALLES", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalles.findAll", query = "SELECT d FROM Detalles d")
    , @NamedQuery(name = "Detalles.findById", query = "SELECT d FROM Detalles d WHERE d.id = :id")
    , @NamedQuery(name = "Detalles.findByCantidad", query = "SELECT d FROM Detalles d WHERE d.cantidad = :cantidad")
    , @NamedQuery(name = "Detalles.findByPrecioUnitario", query = "SELECT d FROM Detalles d WHERE d.precioUnitario = :precioUnitario")})
public class Detalles implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "CANTIDAD")
    private Integer cantidad;
    @Column(name = "PRECIO_UNITARIO")
    private Integer precioUnitario;
    @JoinColumn(name = "FACTURA_ID", referencedColumnName = "ID")
    @ManyToOne
    private Facturas facturaId;
    @JoinColumn(name = "PRODUCTO_ID", referencedColumnName = "ID")
    @ManyToOne
    private Productos productoId;

    public Detalles() {
    }

    public Detalles(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Integer precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Facturas getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Facturas facturaId) {
        this.facturaId = facturaId;
    }

    public Productos getProductoId() {
        return productoId;
    }

    public void setProductoId(Productos productoId) {
        this.productoId = productoId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Detalles)) {
            return false;
        }
        Detalles other = (Detalles) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.gtd.entities.Detalles[ id=" + id + " ]";
    }
    
}
