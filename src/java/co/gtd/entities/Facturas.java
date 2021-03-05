/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gtd.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author lurbina
 */
@Entity
@Table(name = "FACTURAS", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Facturas.findAll", query = "SELECT f FROM Facturas f")
    , @NamedQuery(name = "Facturas.findById", query = "SELECT f FROM Facturas f WHERE f.id = :id")
    , @NamedQuery(name = "Facturas.findByNumero", query = "SELECT f FROM Facturas f WHERE f.numero = :numero")
    , @NamedQuery(name = "Facturas.findByFecha", query = "SELECT f FROM Facturas f WHERE f.fecha = :fecha")
    , @NamedQuery(name = "Facturas.findByTpoPago", query = "SELECT f FROM Facturas f WHERE f.tpoPago = :tpoPago")
    , @NamedQuery(name = "Facturas.findByDcuCliente", query = "SELECT f FROM Facturas f WHERE f.dcuCliente = :dcuCliente")
    , @NamedQuery(name = "Facturas.findByNombres", query = "SELECT f FROM Facturas f WHERE f.nombres = :nombres")
    , @NamedQuery(name = "Facturas.findBySubtotal", query = "SELECT f FROM Facturas f WHERE f.subtotal = :subtotal")
    , @NamedQuery(name = "Facturas.findByDescuento", query = "SELECT f FROM Facturas f WHERE f.descuento = :descuento")
    , @NamedQuery(name = "Facturas.findByIva", query = "SELECT f FROM Facturas f WHERE f.iva = :iva")
    , @NamedQuery(name = "Facturas.findByDescuentoTotal", query = "SELECT f FROM Facturas f WHERE f.descuentoTotal = :descuentoTotal")
    , @NamedQuery(name = "Facturas.findByImpuestoTotal", query = "SELECT f FROM Facturas f WHERE f.impuestoTotal = :impuestoTotal")
    , @NamedQuery(name = "Facturas.findByTotal", query = "SELECT f FROM Facturas f WHERE f.total = :total")})
public class Facturas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = true)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NUMERO")
    private Integer numero;
    @Column(name = "FECHA")
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @Size(max = 500)
    @Column(name = "TPO_PAGO")
    private String tpoPago;
    @Size(max = 500)
    @Column(name = "DCU_CLIENTE")
    private String dcuCliente;
    @Size(max = 2000)
    @Column(name = "NOMBRES")
    private String nombres;
    @Column(name = "SUBTOTAL")
    private Long subtotal;
    @Column(name = "DESCUENTO")
    private Long descuento;
    @Column(name = "IVA")
    private Long iva;
    @Column(name = "DESCUENTO_TOTAL")
    private Long descuentoTotal;
    @Column(name = "IMPUESTO_TOTAL")
    private Long impuestoTotal;
    @Column(name = "TOTAL")
    private Long total;
    @OneToMany(mappedBy = "facturaId", cascade = CascadeType.PERSIST)
    private List<Detalles> detallesList;

    public Facturas() {
        this.fecha = GregorianCalendar.getInstance().getTime();
    }

    public Facturas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumero() {
        return id;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTpoPago() {
        return tpoPago;
    }

    public void setTpoPago(String tpoPago) {
        this.tpoPago = tpoPago;
    }

    public String getDcuCliente() {
        return dcuCliente;
    }

    public void setDcuCliente(String dcuCliente) {
        this.dcuCliente = dcuCliente;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public Long getSubtotal() {
        try {

            Optional<Long> subTotal = this.getDetallesList().stream().map(
                    d -> Long.valueOf(d.getPrecioUnitario())
            ).reduce((accumulator, diff) -> diff + accumulator);

            subtotal = subTotal.isPresent() ? subTotal.get() : 0;
            return subtotal;
        } catch (Exception e) {
            return 0l;
        }
    }

    public void setSubtotal(Long subtotal) {
        this.subtotal = subtotal;
    }

    public Long getDescuento() {
        return descuento;
    }

    public void setDescuento(Long descuento) {
        this.descuento = descuento;
    }

    public Long getIva() {
        return iva;
    }

    public void setIva(Long iva) {
        this.iva = iva;
    }

    public Long getDescuentoTotal() {
        descuentoTotal = getSubtotal();
        descuentoTotal = ((descuentoTotal * descuento) / 100);
        return descuentoTotal;
    }

    public void setDescuentoTotal(Long descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
    }

    public Long getImpuestoTotal() {
        impuestoTotal = ((getSubtotal() - getDescuentoTotal()) * iva) / 100;
        return impuestoTotal;
    }

    public void setImpuestoTotal(Long impuestoTotal) {
        this.impuestoTotal = impuestoTotal;
    }

    public Long getTotal() {
        total = getSubtotal() - getDescuentoTotal() + getImpuestoTotal();
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    @XmlTransient
    public List<Detalles> getDetallesList() {
        return detallesList;
    }

    public void setDetallesList(List<Detalles> detallesList) {
        this.detallesList = detallesList;
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
        if (!(object instanceof Facturas)) {
            return false;
        }
        Facturas other = (Facturas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.gtd.entities.Facturas[ id=" + id + " ]";
    }

}
