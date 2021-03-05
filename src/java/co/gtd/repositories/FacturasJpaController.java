/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gtd.repositories;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.gtd.entities.Detalles;
import co.gtd.entities.Facturas;
import co.gtd.repositories.exceptions.NonexistentEntityException;
import co.gtd.repositories.exceptions.RollbackFailureException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.transaction.UserTransaction;

/**
 *
 * @author lurbina
 */
public class FacturasJpaController implements Serializable {

    public FacturasJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    @PersistenceUnit(unitName = "FacturacionPU")
    private EntityManagerFactory emf = null;
    @Resource
    private UserTransaction utx = null;

    public FacturasJpaController() {
        try {
            InitialContext ic = new InitialContext();
            this.utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            this.emf = javax.persistence.Persistence.createEntityManagerFactory("FacturacionPU");
        } catch (NamingException ex) {
            Logger.getLogger(FacturasJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facturas facturas) throws RollbackFailureException, Exception {
        if (facturas.getDetallesList() == null) {
            facturas.setDetallesList(new ArrayList<Detalles>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Detalles> attachedDetallesList = new ArrayList<Detalles>();
            for (Detalles detallesListDetallesToAttach : facturas.getDetallesList()) {
                detallesListDetallesToAttach = em.getReference(detallesListDetallesToAttach.getClass(), detallesListDetallesToAttach.getId());
                attachedDetallesList.add(detallesListDetallesToAttach);
            }
            facturas.setDetallesList(attachedDetallesList);
            em.persist(facturas);
            for (Detalles detallesListDetalles : facturas.getDetallesList()) {
                Facturas oldFacturaIdOfDetallesListDetalles = detallesListDetalles.getFacturaId();
                detallesListDetalles.setFacturaId(facturas);
                detallesListDetalles = em.merge(detallesListDetalles);
                if (oldFacturaIdOfDetallesListDetalles != null) {
                    oldFacturaIdOfDetallesListDetalles.getDetallesList().remove(detallesListDetalles);
                    oldFacturaIdOfDetallesListDetalles = em.merge(oldFacturaIdOfDetallesListDetalles);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Facturas facturas) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facturas persistentFacturas = em.find(Facturas.class, facturas.getId());
            List<Detalles> detallesListOld = persistentFacturas.getDetallesList();
            List<Detalles> detallesListNew = facturas.getDetallesList();
            List<Detalles> attachedDetallesListNew = new ArrayList<Detalles>();
            for (Detalles detallesListNewDetallesToAttach : detallesListNew) {
                detallesListNewDetallesToAttach = em.getReference(detallesListNewDetallesToAttach.getClass(), detallesListNewDetallesToAttach.getId());
                attachedDetallesListNew.add(detallesListNewDetallesToAttach);
            }
            detallesListNew = attachedDetallesListNew;
            facturas.setDetallesList(detallesListNew);
            facturas = em.merge(facturas);
            for (Detalles detallesListOldDetalles : detallesListOld) {
                if (!detallesListNew.contains(detallesListOldDetalles)) {
                    detallesListOldDetalles.setFacturaId(null);
                    detallesListOldDetalles = em.merge(detallesListOldDetalles);
                }
            }
            for (Detalles detallesListNewDetalles : detallesListNew) {
                if (!detallesListOld.contains(detallesListNewDetalles)) {
                    Facturas oldFacturaIdOfDetallesListNewDetalles = detallesListNewDetalles.getFacturaId();
                    detallesListNewDetalles.setFacturaId(facturas);
                    detallesListNewDetalles = em.merge(detallesListNewDetalles);
                    if (oldFacturaIdOfDetallesListNewDetalles != null && !oldFacturaIdOfDetallesListNewDetalles.equals(facturas)) {
                        oldFacturaIdOfDetallesListNewDetalles.getDetallesList().remove(detallesListNewDetalles);
                        oldFacturaIdOfDetallesListNewDetalles = em.merge(oldFacturaIdOfDetallesListNewDetalles);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = facturas.getId();
                if (findFacturas(id) == null) {
                    throw new NonexistentEntityException("The facturas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facturas facturas;
            try {
                facturas = em.getReference(Facturas.class, id);
                facturas.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facturas with id " + id + " no longer exists.", enfe);
            }
            List<Detalles> detallesList = facturas.getDetallesList();
            for (Detalles detallesListDetalles : detallesList) {
                detallesListDetalles.setFacturaId(null);
                detallesListDetalles = em.merge(detallesListDetalles);
            }
            em.remove(facturas);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Facturas> findFacturasEntities() {
        return findFacturasEntities(true, -1, -1);
    }

    public List<Facturas> findFacturasEntities(int maxResults, int firstResult) {
        return findFacturasEntities(false, maxResults, firstResult);
    }

    private List<Facturas> findFacturasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facturas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Facturas findFacturas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facturas.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacturasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facturas> rt = cq.from(Facturas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
