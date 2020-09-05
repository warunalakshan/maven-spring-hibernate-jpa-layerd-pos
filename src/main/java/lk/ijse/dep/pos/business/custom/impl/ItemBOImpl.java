package lk.ijse.dep.pos.business.custom.impl;

import lk.ijse.dep.pos.business.custom.ItemBO;
import lk.ijse.dep.pos.dao.DAOFactory;
import lk.ijse.dep.pos.dao.DAOType;
import lk.ijse.dep.pos.dao.custom.ItemDAO;
import lk.ijse.dep.pos.db.JPAUtil;
import lk.ijse.dep.pos.entity.Item;
import lk.ijse.dep.pos.util.ItemTM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Component
public class ItemBOImpl implements ItemBO {

    // Dependency Declaration
    @Autowired
    private final ItemDAO itemDAO;
    EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    public ItemBOImpl() {
        // Constructor Injection
        this.itemDAO = DAOFactory.getInstance().getDAO(DAOType.ITEM);
    }

    public String getNewItemCode() throws Exception {

        EntityManager en = emf.createEntityManager();
        itemDAO.setEntityManager(en);
        String lastItemCode = null;
        try {
            en.getTransaction().begin();
            lastItemCode = itemDAO.getLastItemCode();
            en.getTransaction().commit();

            if (lastItemCode == null) {
                return "I001";
            } else {
                int maxId = Integer.parseInt(lastItemCode.replace("I", ""));
                maxId = maxId + 1;
                String id = "";
                if (maxId < 10) {
                    id = "I00" + maxId;
                } else if (maxId < 100) {
                    id = "I0" + maxId;
                } else {
                    id = "I" + maxId;
                }
                return id;
            }
        } catch (Throwable t) {
            en.getTransaction().rollback();
            throw t;
        } finally {
            en.close();
        }


    }

    public List<ItemTM> getAllItems() throws Exception {
        EntityManager en = emf.createEntityManager();
        itemDAO.setEntityManager(en);
        List<Item> allItems = null;

        try {
            en.getTransaction().begin();
            allItems = itemDAO.findAll();
            en.getTransaction().commit();
        } catch (Throwable t) {
            en.getTransaction().rollback();
            throw t;
        } finally {
            en.close();
        }

        List<ItemTM> items = new ArrayList<>();
        for (Item item : allItems) {
            items.add(new ItemTM(item.getCode(), item.getDescription(), item.getQtyOnHand(),
                    item.getUnitPrice().doubleValue()));
        }
        return items;
    }

    public void saveItem(String code, String description, int qtyOnHand, double unitPrice) throws Exception {
        EntityManager en = emf.createEntityManager();;
        itemDAO.setEntityManager(en);

        try {
            en.getTransaction().begin();
            itemDAO.save(new Item(code, description, BigDecimal.valueOf(unitPrice), qtyOnHand));
            en.getTransaction().commit();
        } catch (Throwable t) {
            en.getTransaction().rollback();
            throw t;
        } finally {
            en.close();
        }
    }

    public void deleteItem(String itemCode) throws Exception {
        EntityManager en = emf.createEntityManager();
        itemDAO.setEntityManager(en);

        try {
            en.getTransaction().begin();
            itemDAO.delete(itemCode);
            en.getTransaction().commit();
        } catch (Throwable t) {
            en.getTransaction().rollback();
            throw t;
        } finally {
            en.close();
        }
    }

    public void updateItem(String description, int qtyOnHand, double unitPrice, String itemCode) throws Exception {
        EntityManager en = emf.createEntityManager();
        itemDAO.setEntityManager(en);
        try {
            en.getTransaction().begin();
            itemDAO.update(new Item(itemCode, description,
                    BigDecimal.valueOf(unitPrice), qtyOnHand));
            en.getTransaction().commit();
        } catch (Throwable t) {
            en.getTransaction().rollback();
            throw t;
        } finally {
            en.close();
        }
    }
}
