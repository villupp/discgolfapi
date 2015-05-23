package vp.jersey.rest;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import com.mysql.jdbc.Driver;

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
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            // Setting the database connection properties to the environment variables
            // given by OpenShift (deployment)
            String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
            String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
            // MySQL username and password
            String user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
            String pass = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
            // Local conf if deployed locally
            if (host == null) host = "127.0.0.1";
            if (port == null) port = "3306";
            if (user == null) user = "root";
            if (pass == null) pass = "test1234";
            // Setting the properties
            configuration.setProperty(
                    "hibernate.connection.url",
                    "jdbc:mysql://" + host + ":" + port + "/discgolfapi?autoReconnect=true"
            );
            configuration.setProperty(
                    "hibernate.connection.username",
                    user
            );
            configuration.setProperty(
                    "hibernate.connection.password",
                    pass
            );
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
