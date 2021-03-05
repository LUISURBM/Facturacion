/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gtd.services;

import co.gtd.entities.Detalles;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author lurbina
 */
@Stateless
public class DetallesFacade extends AbstractFacade<Detalles> {

    @PersistenceContext(unitName = "FacturacionPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallesFacade() {
        super(Detalles.class);
    }
    
}
