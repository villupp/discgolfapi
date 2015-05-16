package vp.jersey.rest;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Class for MYSQL database connection.
 * @author Ville Piirainen
 */
public class DbConnection {
    /** SessionFactory object for creating sessions. */
    private static SessionFactory sessionFactory;

    /**
     * Initializes Hibernate, loads config from hibernate.cfg.xml.
     */
    public static void initHibernate() {
        try
        {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                    configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            System.out.println("Database connection initialized");
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Initializes connection if not initialized and returns session.
     *
     * @return Returns open session object if Hibernate initialized.
     */
    public static Session getSession() throws HibernateException {
        if (sessionFactory == null)
            initHibernate();
        return sessionFactory.openSession();
    }
}
