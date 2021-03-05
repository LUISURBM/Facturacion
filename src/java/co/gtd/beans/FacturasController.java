package co.gtd.beans;

import co.gtd.entities.Facturas;
import co.gtd.beans.util.JsfUtil;
import co.gtd.beans.util.PaginationHelper;
import co.gtd.entities.Detalles;
import co.gtd.services.DetallesFacade;
import co.gtd.services.FacturasFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("facturasController")
@SessionScoped
public class FacturasController implements Serializable {

    private Facturas current;
    private Detalles currentDetalles;
    private DataModel items = null;
    @EJB
    private co.gtd.services.FacturasFacade ejbFacade;
    @EJB
    private co.gtd.services.DetallesFacade ejbFacadeDetalles;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public FacturasController() {
    }

    public Facturas getSelected() {
        if (current == null) {
            current = new Facturas();
            selectedItemIndex = -1;
        }
        return current;
    }
    
    public Detalles getSelectedDetalles() {
        if (current.getDetallesList() == null || current.getDetallesList().get(0) == null) {
            currentDetalles = new Detalles();
            List<Detalles> detalles = new ArrayList<>();
            detalles.add(currentDetalles);
            current.setDetallesList(detalles);
            
        }
        return current.getDetallesList().get(0);
    }

    private FacturasFacade getFacade() {
        return ejbFacade;
    }
    
    private DetallesFacade getFacadeDetalles() {
        return ejbFacadeDetalles;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Facturas) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Facturas();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            if(current.getFecha().after(GregorianCalendar.getInstance().getTime())){
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorDateGreatterNow"));
                return null;
            }
            getFacade().create(current);
            //currentDetalles.setFacturaId(current);
            //getFacadeDetalles().create(currentDetalles);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("FacturasCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public void addProduct() {
        try {
            current.getDetallesList().add(new Detalles());
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }
    
    public String prepareEdit() {
        current = (Facturas) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            currentDetalles.setFacturaId(current);
            getFacadeDetalles().edit(currentDetalles);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("FacturasUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Facturas) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            getFacadeDetalles().remove(currentDetalles);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("FacturasDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Facturas getFacturas(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Facturas.class)
    public static class FacturasControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            FacturasController controller = (FacturasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "facturasController");
            return controller.getFacturas(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Facturas) {
                Facturas o = (Facturas) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Facturas.class.getName());
            }
        }

    }

}
