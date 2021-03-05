/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.gtd.repositories;

import co.gtd.entities.Detalles;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import co.gtd.entities.Facturas;
import co.gtd.entities.Productos;
import co.gtd.repositories.exceptions.NonexistentEntityException;
import co.gtd.repositories.exceptions.RollbackFailureException;
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
public class DetallesJpaController implements Serializable {

    public DetallesJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    @PersistenceUnit(unitName = "FacturacionPU")
    private EntityManagerFactory emf = null;
    @Resource
    private UserTransaction utx = null;

    public DetallesJpaController() {
        try {
            InitialContext ic = new InitialContext();
            this.utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            this.emf = javax.persistence.Persistence.createEntityManagerFactory("FacturacionPU");
        } catch (NamingException ex) {
            Logger.getLogger(DetallesJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detalles detalles) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facturas facturaId = detalles.getFacturaId();
            if (facturaId != null) {
                facturaId = em.getReference(facturaId.getClass(), facturaId.getId());
                detalles.setFacturaId(facturaId);
            }
            Productos productoId = detalles.getProductoId();
            if (productoId != null) {
                productoId = em.getReference(productoId.getClass(), productoId.getId());
                detalles.setProductoId(productoId);
            }
            em.persist(detalles);
            if (facturaId != null) {
                facturaId.getDetallesList().add(detalles);
                facturaId = em.merge(facturaId);
            }
            if (productoId != null) {
                productoId.getDetallesList().add(detalles);
                productoId = em.merge(productoId);
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

    public void edit(Detalles detalles) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Detalles persistentDetalles = em.find(Detalles.class, detalles.getId());
            Facturas facturaIdOld = persistentDetalles.getFacturaId();
            Facturas facturaIdNew = detalles.getFacturaId();
            Productos productoIdOld = persistentDetalles.getProductoId();
            Productos productoIdNew = detalles.getProductoId();
            if (facturaIdNew != null) {
                facturaIdNew = em.getReference(facturaIdNew.getClass(), facturaIdNew.getId());
                detalles.setFacturaId(facturaIdNew);
            }
            if (productoIdNew != null) {
                productoIdNew = em.getReference(productoIdNew.getClass(), productoIdNew.getId());
                detalles.setProductoId(productoIdNew);
            }
            detalles = em.merge(detalles);
            if (facturaIdOld != null && !facturaIdOld.equals(facturaIdNew)) {
                facturaIdOld.getDetallesList().remove(detalles);
                facturaIdOld = em.merge(facturaIdOld);
            }
            if (facturaIdNew != null && !facturaIdNew.equals(facturaIdOld)) {
                facturaIdNew.getDetallesList().add(detalles);
                facturaIdNew = em.merge(facturaIdNew);
            }
            if (productoIdOld != null && !productoIdOld.equals(productoIdNew)) {
                productoIdOld.getDetallesList().remove(detalles);
                productoIdOld = em.merge(productoIdOld);
            }
            if (productoIdNew != null && !productoIdNew.equals(productoIdOld)) {
                productoIdNew.getDetallesList().add(detalles);
                productoIdNew = em.merge(productoIdNew);
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
                Integer id = detalles.getId();
                if (findDetalles(id) == null) {
                    throw new NonexistentEntityException("The detalles with id " + id + " no longer exists.");
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
            Detalles detalles;
            try {
                detalles = em.getReference(Detalles.class, id);
                detalles.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalles with id " + id + " no longer exists.", enfe);
            }
            Facturas facturaId = detalles.getFacturaId();
            if (facturaId != null) {
                facturaId.getDetallesList().remove(detalles);
                facturaId = em.merge(facturaId);
            }
            Productos productoId = detalles.getProductoId();
            if (productoId != null) {
                productoId.getDetallesList().remove(detalles);
                productoId = em.merge(productoId);
            }
            em.remove(detalles);
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

    public List<Detalles> findDetallesEntities() {
        return findDetallesEntities(true, -1, -1);
    }

    public List<Detalles> findDetallesEntities(int maxResults, int firstResult) {
        return findDetallesEntities(false, maxResults, firstResult);
    }

    private List<Detalles> findDetallesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalles.class));
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

    public Detalles findDetalles(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalles.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetallesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalles> rt = cq.from(Detalles.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
