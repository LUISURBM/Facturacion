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
import co.gtd.entities.Productos;
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
public class ProductosJpaController implements Serializable {

    public ProductosJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    @PersistenceUnit(unitName = "FacturacionPU")
    private EntityManagerFactory emf = null;
    @Resource
    private UserTransaction utx = null;

    public ProductosJpaController() {
        try {
            InitialContext ic = new InitialContext();
            this.utx = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            this.emf = javax.persistence.Persistence.createEntityManagerFactory("FacturacionPU");
        } catch (NamingException ex) {
            Logger.getLogger(ProductosJpaController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Productos productos) throws RollbackFailureException, Exception {
        if (productos.getDetallesList() == null) {
            productos.setDetallesList(new ArrayList<Detalles>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Detalles> attachedDetallesList = new ArrayList<Detalles>();
            for (Detalles detallesListDetallesToAttach : productos.getDetallesList()) {
                detallesListDetallesToAttach = em.getReference(detallesListDetallesToAttach.getClass(), detallesListDetallesToAttach.getId());
                attachedDetallesList.add(detallesListDetallesToAttach);
            }
            productos.setDetallesList(attachedDetallesList);
            em.persist(productos);
            for (Detalles detallesListDetalles : productos.getDetallesList()) {
                Productos oldProductoIdOfDetallesListDetalles = detallesListDetalles.getProductoId();
                detallesListDetalles.setProductoId(productos);
                detallesListDetalles = em.merge(detallesListDetalles);
                if (oldProductoIdOfDetallesListDetalles != null) {
                    oldProductoIdOfDetallesListDetalles.getDetallesList().remove(detallesListDetalles);
                    oldProductoIdOfDetallesListDetalles = em.merge(oldProductoIdOfDetallesListDetalles);
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

    public void edit(Productos productos) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Productos persistentProductos = em.find(Productos.class, productos.getId());
            List<Detalles> detallesListOld = persistentProductos.getDetallesList();
            List<Detalles> detallesListNew = productos.getDetallesList();
            List<Detalles> attachedDetallesListNew = new ArrayList<Detalles>();
            for (Detalles detallesListNewDetallesToAttach : detallesListNew) {
                detallesListNewDetallesToAttach = em.getReference(detallesListNewDetallesToAttach.getClass(), detallesListNewDetallesToAttach.getId());
                attachedDetallesListNew.add(detallesListNewDetallesToAttach);
            }
            detallesListNew = attachedDetallesListNew;
            productos.setDetallesList(detallesListNew);
            productos = em.merge(productos);
            for (Detalles detallesListOldDetalles : detallesListOld) {
                if (!detallesListNew.contains(detallesListOldDetalles)) {
                    detallesListOldDetalles.setProductoId(null);
                    detallesListOldDetalles = em.merge(detallesListOldDetalles);
                }
            }
            for (Detalles detallesListNewDetalles : detallesListNew) {
                if (!detallesListOld.contains(detallesListNewDetalles)) {
                    Productos oldProductoIdOfDetallesListNewDetalles = detallesListNewDetalles.getProductoId();
                    detallesListNewDetalles.setProductoId(productos);
                    detallesListNewDetalles = em.merge(detallesListNewDetalles);
                    if (oldProductoIdOfDetallesListNewDetalles != null && !oldProductoIdOfDetallesListNewDetalles.equals(productos)) {
                        oldProductoIdOfDetallesListNewDetalles.getDetallesList().remove(detallesListNewDetalles);
                        oldProductoIdOfDetallesListNewDetalles = em.merge(oldProductoIdOfDetallesListNewDetalles);
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
                Integer id = productos.getId();
                if (findProductos(id) == null) {
                    throw new NonexistentEntityException("The productos with id " + id + " no longer exists.");
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
            Productos productos;
            try {
                productos = em.getReference(Productos.class, id);
                productos.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productos with id " + id + " no longer exists.", enfe);
            }
            List<Detalles> detallesList = productos.getDetallesList();
            for (Detalles detallesListDetalles : detallesList) {
                detallesListDetalles.setProductoId(null);
                detallesListDetalles = em.merge(detallesListDetalles);
            }
            em.remove(productos);
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

    public List<Productos> findProductosEntities() {
        return findProductosEntities(true, -1, -1);
    }

    public List<Productos> findProductosEntities(int maxResults, int firstResult) {
        return findProductosEntities(false, maxResults, firstResult);
    }

    private List<Productos> findProductosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Productos.class));
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

    public Productos findProductos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Productos.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Productos> rt = cq.from(Productos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
