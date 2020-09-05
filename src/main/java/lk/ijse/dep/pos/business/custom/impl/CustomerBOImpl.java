package lk.ijse.dep.pos.business.custom.impl;

import lk.ijse.dep.pos.business.custom.CustomerBO;
import lk.ijse.dep.pos.dao.custom.CustomerDAO;
import lk.ijse.dep.pos.db.JPAUtil;
import lk.ijse.dep.pos.entity.Customer;
import lk.ijse.dep.pos.util.CustomerTM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
@Component
public class CustomerBOImpl implements CustomerBO {

    // Field Injection
    @Autowired
    private CustomerDAO customerDAO;
    EntityManagerFactory emf = JPAUtil.getEntityManagerFactory();

    public List<CustomerTM> getAllCustomers() throws Exception {

        EntityManager em = emf.createEntityManager();
        customerDAO.setEntityManager(em);
        List<Customer> allCustomers = null;
        try{
            em.getTransaction().begin();
            allCustomers = customerDAO.findAll();
            em.getTransaction().commit();
        }catch (Throwable t){
            em.getTransaction().rollback();
            throw t;
        }finally {
            em.close();
        }

        List<CustomerTM> customers = new ArrayList<>();
        for (Customer customer : allCustomers) {
            customers.add(new CustomerTM(customer.getId(), customer.getName(), customer.getAddress()));
        }
        return customers;
    }

    public void saveCustomer(String id, String name, String address) throws Exception {

        EntityManager em = emf.createEntityManager();
        customerDAO.setEntityManager(em);
        try{
            em.getTransaction().begin();
            customerDAO.save(new Customer(id, name, address));
            em.getTransaction().commit();
        }catch (Throwable t){
            em.getTransaction().rollback();
            throw t;
        }finally {
            em.close();
        }
    }

    public void deleteCustomer(String customerId) throws Exception {

        EntityManager em = emf.createEntityManager();
        customerDAO.setEntityManager(em);
        try{
            em.getTransaction().begin();
            customerDAO.delete(customerId);
            em.getTransaction().commit();
        }catch (Throwable t){
            em.getTransaction().rollback();
            throw t;
        }finally {
            em.close();
        }
    }

    public void updateCustomer(String name, String address, String customerId) throws Exception {

        EntityManager em = emf.createEntityManager();
        customerDAO.setEntityManager(em);
        try{
            em.getTransaction().begin();
            customerDAO.update(new Customer(customerId, name, address));
            em.getTransaction().commit();
        }catch (Throwable t){
            em.getTransaction().rollback();
            throw t;
        }finally {
            em.close();
        }
    }

    public String getNewCustomerId() throws Exception {

        EntityManager em = emf.createEntityManager();
        customerDAO.setEntityManager(em);
        String lastCustomerId = null;
        try{
            em.getTransaction().begin();
            lastCustomerId = customerDAO.getLastCustomerId();
            em.getTransaction().commit();
        }catch (Throwable t){
            em.getTransaction().rollback();
            throw t;
        }finally {
            em.close();
        }

        if (lastCustomerId == null) {
            return "C001";
        } else {
            int maxId = Integer.parseInt(lastCustomerId.replace("C", ""));
            maxId = maxId + 1;
            String id = "";
            if (maxId < 10) {
                id = "C00" + maxId;
            } else if (maxId < 100) {
                id = "C0" + maxId;
            } else {
                id = "C" + maxId;
            }
            return id;
        }
    }

}
